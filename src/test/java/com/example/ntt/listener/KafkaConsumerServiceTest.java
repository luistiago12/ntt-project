package com.example.ntt.listener;

import static org.mockito.Mockito.verify;

import com.example.ntt.model.entity.Product;
import com.example.ntt.model.entity.Request;
import com.example.ntt.service.RequestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.UUID;

public class KafkaConsumerServiceTest {

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Mock
    private RequestService requestService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessOrder() {

        var product = Product.builder()
        .name("product 1")
        .price(100.00)
        .build();

        var request = Request.builder()
        .id(UUID.randomUUID())
        .status("PENDING")
        .build();

        product.setRequest(request);
        request.setProducts(Collections.singletonList(product));

        kafkaConsumerService.processOrder(request);

        verify(requestService).saveOrder(request);
    }
}
