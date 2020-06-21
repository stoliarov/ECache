package ru.ecache.service.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import ru.ecache.model.Tables;
import ru.ecache.service.ECacheService;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class EtagBuilder {

    private ECacheService<String, Long> cacheStoreService;

    public String buildETag(String methodId, Tables tables) {

        List<Long> changeIds = new ArrayList<>();

        for (String tableId : tables.getTableIds()) {
            changeIds.add(cacheStoreService.get(tableId));
        }

        String preETag = StringUtils.join(changeIds, methodId, "");

        return "\"" + DigestUtils.sha256Hex(preETag) + "\"";
    }
}
