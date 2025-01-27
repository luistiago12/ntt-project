package com.example.ntt.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.ntt.model.entity.Request;
import com.example.ntt.service.RequestService;
import com.example.ntt.queue.KafkaTopics;

@Service
public class KafkaConsumerService {

    @Autowired
    private RequestService requestService;

    @KafkaListener(topics = KafkaTopics.REQUEST_TOPIC_POST, groupId = "order-group")
    public void processOrder(Request request) {
        requestService.saveOrder(request);
    }
}
