package com.example.Order.config;

import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public Filter loggingFilter() {
        return new LoggingFilter();
    }
}
