package com.example.ntt.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.ntt.model.entity.Request;
import com.example.ntt.handler.OrderCheckEvent;
import com.example.ntt.handler.CustomExceptionHandler.CustomException;
import com.example.ntt.model.entity.Product;
import com.example.ntt.model.repository.RequestRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class RequestService {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    private static final long TTL_HOURS = 24;

    @EventListener
    public void handleOrderCheckEvent(OrderCheckEvent event) {
        Request request = event.getRequest();

        if (request.getId() != null) {
            String redisKey = "order:" + request.getId().toString();
            if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                throw new CustomException("Pedido duplicado.");
            }
        }
        kafkaProducerService.sendToKafka(request);
    }

    @CircuitBreaker(name = "requestService", fallbackMethod = "fallbackProcessOrder")
    @Retry(name = "requestService")
    public Request saveOrder(Request request) {

        if (request.getId() != null && existsById(request.getId())) {
            throw new CustomException("Pedido duplicado.");
        }

        if (request.getProducts() != null) {
            for (Product product : request.getProducts()) {
                product.setRequest(request);
            }
        }

        var requestSaved = requestRepository.save(request);
        redisTemplate.opsForValue().set("order:" + requestSaved.getId().toString(), "processed", TTL_HOURS, TimeUnit.HOURS);

        return requestSaved;
    }

    public Page<Request> getRequests(Pageable pageable) {
        return requestRepository.findAll(pageable);
    }

    public Boolean existsById(UUID id) {
        return requestRepository.existsById(id);
    }

    public void fallbackProcessOrder(Request request, Throwable t) {
        System.out.println("Fallback method called due to: " + t.getMessage());
    }
}
