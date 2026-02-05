package com.fintech.trading.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class PrecisionConfig {

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        // Forces JSON to treat BigDecimal as plain numbers, not scientific notation
        return builder.createXmlMapper(false)
                .featuresToEnable(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN)
                .build();
    }
}