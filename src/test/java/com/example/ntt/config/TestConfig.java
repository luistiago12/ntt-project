package com.example.ntt.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import com.example.ntt.model.entity.Request;
import com.example.ntt.model.repository.RequestRepository;
import com.example.ntt.service.KafkaProducerService;
import com.example.ntt.service.RequestService;

@TestConfiguration
public class TestConfig {
    @SuppressWarnings("unchecked")
    @Bean
    public KafkaTemplate<String, Request> kafkaTemplate() {
        return Mockito.mock(KafkaTemplate.class);
    }

    @Bean
    public KafkaProducerService kafkaProducerService(KafkaTemplate<String, Request> kafkaTemplate) {
        return new KafkaProducerService(kafkaTemplate);
    }

    @Bean
    public RequestService requestService() {
        return Mockito.mock(RequestService.class);
    }

    @Bean
    public RequestRepository requestRepository() {
        return Mockito.mock(RequestRepository.class);
    }

    @SuppressWarnings("unchecked")
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        return Mockito.mock(RedisTemplate.class);
    }
}
