package ru.ecache.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ecache.aspect.ECacheAspect;
import ru.ecache.properties.ECacheProperties;
import ru.ecache.service.ECacheManager;
import ru.ecache.service.ECacheService;
import ru.ecache.service.EEntitiesCacheStore;
import ru.ecache.service.jpa.ECacheHibernateEventListener;
import ru.ecache.service.utils.EtagBuilder;

import javax.persistence.EntityManagerFactory;

@Configuration
@AllArgsConstructor
@EnableConfigurationProperties(ECacheProperties.class)
public class ECacheServicesConfiguration {

    private final ECacheProperties eCacheProperties;

    @Bean
    @ConditionalOnBean(ECacheService.class)
    public EtagBuilder etagBuilder(ECacheService<String, Long> eCacheService) {
        return new EtagBuilder(eCacheService);
    }

    @Bean
    @ConditionalOnBean({ECacheService.class, EtagBuilder.class})
    public ECacheAspect eCacheAspect(EtagBuilder etagBuilder, ECacheService<String, Long> eCacheService) {
        return new ECacheAspect(etagBuilder, eCacheService);
    }

    @Bean
    @ConditionalOnBean({ECacheService.class, EEntitiesCacheStore.class, EntityManagerFactory.class})
    public ECacheHibernateEventListener eCacheHibernateEventListener(EntityManagerFactory factory,
                                                                     EEntitiesCacheStore eEntitiesCacheStore,
                                                                     ECacheService<String, Long> eCacheService) {
        return new ECacheHibernateEventListener(factory, eEntitiesCacheStore, eCacheService);
    }

    @Bean
    public ECacheManager eCacheManager() {
        return new ECacheManager(eCacheProperties);
    }
}
