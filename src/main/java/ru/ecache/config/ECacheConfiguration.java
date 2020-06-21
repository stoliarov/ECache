package ru.ecache.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ECacheServicesConfiguration.class)
public class ECacheConfiguration {
}
