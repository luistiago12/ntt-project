package com.example.ntt.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.context.ApplicationEventPublisher;

import com.example.ntt.handler.OrderCheckEvent;
import com.example.ntt.model.entity.Request;
import com.example.ntt.queue.KafkaTopics;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, Request> kafkaTemplate;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void sendOrder(Request request) {
        eventPublisher.publishEvent(new OrderCheckEvent(this, request));
    }

    public void sendToKafka(Request request) {
        try {
            kafkaTemplate.send(KafkaTopics.REQUEST_TOPIC_POST, request).get();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar pedido: " + e.getMessage());
        }
    }
}
