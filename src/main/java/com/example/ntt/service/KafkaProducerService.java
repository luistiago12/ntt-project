package com.example.ntt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

import com.example.ntt.handler.OrderCheckEvent;
import com.example.ntt.model.entity.Request;
import com.example.ntt.queue.KafkaTopics;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, Request> kafkaTemplate;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void sendOrder(Request request) {
        log.info("Publicando para o pedido: {}", request);
        eventPublisher.publishEvent(new OrderCheckEvent(this, request));
    }

    public void sendToKafka(Request request) {
        try {
            log.info("Enviando pedido para o Kafka: {}", request);
            kafkaTemplate.send(KafkaTopics.REQUEST_TOPIC_POST, request).get();
            log.info("Pedido enviado para o Kafka com sucesso: {}", request);
        } catch (Exception e) {
            log.error("Erro ao enviar pedido para o Kafka: {}", e.getMessage());
            throw new RuntimeException("Erro ao enviar pedido: " + e.getMessage());
        }
    }
}
