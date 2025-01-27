package com.example.ntt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ntt.model.entity.Request;
import com.example.ntt.service.KafkaProducerService;
import com.example.ntt.service.RequestService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private RequestService requestService;

    @PostMapping
    public ResponseEntity<Request> createOrder(@RequestBody Request request) {
        kafkaProducerService.sendOrder(request);
        return ResponseEntity.ok(request);
    }

    @GetMapping
    public ResponseEntity<Page<Request>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Request> requests = requestService.getRequests(pageable);
        return ResponseEntity.ok(requests);
    }
}
