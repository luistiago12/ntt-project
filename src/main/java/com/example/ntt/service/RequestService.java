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

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
        log.info("Recebendo OrderCheckEvent para o pedido: {}", request);

        if (request.getId() != null) {
            String redisKey = "order:" + request.getId().toString();
            if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
                log.warn("Pedido duplicado detectado: {}", request);
                throw new CustomException("Pedido duplicado.");
            }
        }
        kafkaProducerService.sendToKafka(request);
    }

    @CircuitBreaker(name = "requestService", fallbackMethod = "fallbackProcessOrder")
    @Retry(name = "requestService")
    public Request saveOrder(Request request) {
        log.info("Salvando pedido: {}", request);

        if (request.getId() != null && existsById(request.getId())) {
            log.warn("Pedido duplicado detectado ao salvar: {}", request);
            throw new CustomException("Pedido duplicado.");
        }

        if (request.getProducts() != null) {
            for (Product product : request.getProducts()) {
                product.setRequest(request);
            }
        }

        var requestSaved = requestRepository.save(request);
        redisTemplate.opsForValue().set("order:" + requestSaved.getId().toString(), "processed", TTL_HOURS, TimeUnit.HOURS);
        log.info("Pedido salvo com sucesso: {}", requestSaved);

        return requestSaved;
    }

    public Page<Request> getRequests(Pageable pageable) {
        log.info("Buscando pedidos com paginação: {}", pageable);
        return requestRepository.findAll(pageable);
    }

    public Boolean existsById(UUID id) {
        log.info("Verificando existência do pedido com ID: {}", id);
        return requestRepository.existsById(id);
    }

    public void fallbackProcessOrder(Request request, Throwable t) {
        log.error("Método de fallback chamado devido a: {}", t.getMessage());
        throw new RuntimeException("Erro ao processar pedido: " + t.getMessage());
    }
}
