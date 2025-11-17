package com.vanhuy.user_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced; // THÊM DÒNG NÀY
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {
    @Bean
    @LoadBalanced // SỬA Ở ĐÂY: Thêm @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}