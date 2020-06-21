package ru.ecache.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "ecache.store")
public class ECacheProperties {

    private String redisHost;

    private String redisPort;
}
