package ru.ecache.aspect;

import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import ru.ecache.annotations.ECache;
import ru.ecache.annotations.ECacheEvict;
import ru.ecache.model.Tables;
import ru.ecache.service.ECacheService;
import ru.ecache.service.utils.EtagBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Aspect
@AllArgsConstructor
public class ECacheAspect {

    private final EtagBuilder eTagBuilder;

    private final ECacheService<String, Long> cacheStoreService;

    /**
     * Для методов контроллеров. Вычисляет eTag, подставляет его в ответы.<br/>
     * Если полученный от клиента ifNoneMatch header совпадает с вычисленным eTag,
     * то аспект не передает управление методу контроллера, а сразу отправляет ответ со статусом 304 и пустым телом.
     */
    @Around("@annotation(ru.ecache.annotations.ECache)")
    Object doCache(ProceedingJoinPoint joinPoint) throws Throwable {

        String etag = null;
        boolean etagWasCalculated = false;

        String ifNoneMatchHeader = getIfNoneMatchHeader(joinPoint);

        if (StringUtils.isNotBlank(ifNoneMatchHeader)) {

            etag = buildEtag(joinPoint);
            etagWasCalculated = true;

            if (isEtagMatched(etag, ifNoneMatchHeader)) {
                return ResponseEntity.ok()
                        .eTag(ifNoneMatchHeader)
                        .build();
            }
        }

        ResponseEntity<?> returnValue = (ResponseEntity<?>) joinPoint.proceed();

        return buildResponse(returnValue, joinPoint, etag, etagWasCalculated);
    }

    @After("@annotation(ru.ecache.annotations.ECacheEvict)")
    void updateModifiedStatus(JoinPoint joinPoint) {

        Tables tables = Optional.of(joinPoint)
                .map(s -> (MethodSignature) s.getSignature())
                .map(MethodSignature::getMethod)
                .map(method -> method.getAnnotation(ECacheEvict.class))
                .map(ECacheEvict::tables)
                .map(Arrays::asList)
                .map(Tables::new)
                .orElse(null);

        if (tables == null || CollectionUtils.isEmpty(tables.getTableIds())) {
            return;
        }

        for (String tableName : tables.getTableIds()) {
            cacheStoreService.put(tableName, System.currentTimeMillis());
        }
    }

    private Object buildResponse(ResponseEntity<?> originalResponse,
                                 ProceedingJoinPoint joinPoint,
                                 String etag,
                                 boolean etagWasCalculated) {

        if (!etagWasCalculated) {
            etag = buildEtag(joinPoint);
        }

        if (etag != null) {
            return ResponseEntity.status(getStatus(originalResponse))
                    .headers(getHeaders(originalResponse))
                    .eTag(etag)
                    .body(getBody(originalResponse));
        } else {
            return ResponseEntity.status(getStatus(originalResponse))
                    .headers(getHeaders(originalResponse))
                    .body(getBody(originalResponse));
        }
    }

    private HttpHeaders getHeaders(ResponseEntity<?> returnValue) {

        return Optional.ofNullable(returnValue)
                .map(HttpEntity::getHeaders)
                .orElse(new HttpHeaders(new LinkedMultiValueMap<>(0)));
    }

    private Object getBody(ResponseEntity<?> returnValue) {

        return Optional.ofNullable(returnValue)
                .map(HttpEntity::getBody)
                .orElse(null);
    }

    private HttpStatus getStatus(ResponseEntity<?> returnValue) {

        return Optional.ofNullable(returnValue)
                .map(ResponseEntity::getStatusCode)
                .orElse(HttpStatus.OK);
    }

    private String getIfNoneMatchHeader(JoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();

        String ifNoneMatchHeader = (String) Optional.ofNullable(args)
                .filter(it -> it.length > 0)
                .map(it -> it[0])
                .orElse(null);

        if (ifNoneMatchHeader == null || ifNoneMatchHeader.length() < 3) {
            return null;
        }

        return ifNoneMatchHeader.replace("\"", "");
    }

    private boolean isEtagMatched(String etag, String ifNoneMatchHeader) {

        if (StringUtils.isBlank(etag)) {
            return false;
        }

        return etag.equals(ifNoneMatchHeader);
    }

    @Nullable
    private String buildEtag(JoinPoint joinPoint) {

        String methodId = getMethodId(joinPoint);

        if (methodId == null) {
            return null;
        }

        ECache eCacheAnnotation = getECacheAnnotation(joinPoint);

        Tables tables = getTables(eCacheAnnotation);
        List<String> entityIds = getEntityIds(eCacheAnnotation);

        if (tables == null) {
            tables = new Tables(entityIds);
        } else {
            tables.merge(entityIds);
        }

        return eTagBuilder.buildETag(methodId, tables);
    }

    private String getMethodId(JoinPoint joinPoint) {

        return Optional.of(joinPoint)
                .map(JoinPoint::getSignature)
                .map(Signature::toString)
                .orElse(null);
    }

    private List<String> getEntityIds(ECache eCacheAnnotation) {

        return Optional.ofNullable(eCacheAnnotation)
                .map(ECache::entities)
                .map(array -> new ArrayList<>(Arrays.asList(array)))
                .map(Collection::stream)
                .orElse(Stream.empty())
                .map(Class::getName)
                .collect(Collectors.toList());
    }

    private Tables getTables(ECache eCacheAnnotation) {

        return Optional.ofNullable(eCacheAnnotation)
                .map(ECache::tables)
                .map(array -> new ArrayList<>(Arrays.asList(array)))
                .map(Tables::new)
                .orElse(null);
    }

    private ECache getECacheAnnotation(JoinPoint joinPoint) {

        return Optional.of(joinPoint)
                .map(s -> (MethodSignature) s.getSignature())
                .map(MethodSignature::getMethod)
                .map(method -> method.getAnnotation(ECache.class))
                .orElse(null);
    }
}
