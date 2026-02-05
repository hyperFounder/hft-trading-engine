package com.fintech.trading.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "tradeExecutor")
    public Executor tradeExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);     // Minimum threads active
        executor.setMaxPoolSize(100);     // Maximum burst threads
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("HFT-Exec-");
        executor.initialize();
        return executor;
    }
}