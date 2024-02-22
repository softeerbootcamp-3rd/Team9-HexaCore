package com.hexacore.tayo.config;

import com.hexacore.tayo.lock.InMemoryRangeLockManager;
import com.hexacore.tayo.lock.RangeLockManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RangeLockManager lockManager() {
        return new InMemoryRangeLockManager();
    }
}
