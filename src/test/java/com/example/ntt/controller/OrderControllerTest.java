package com.example.ntt.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ntt.model.entity.Product;
import com.example.ntt.model.entity.Request;
import com.example.ntt.service.KafkaProducerService;
import com.example.ntt.service.RequestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.ntt.model.repository.RequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.redis.core.RedisTemplate;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import com.example.ntt.config.TestConfig;

import java.util.Collections;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = OrderController.class)
@Import({TestConfig.class})
@MockitoSettings(strictness = Strictness.LENIENT)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private KafkaTemplate<String, Request> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(OrderControllerTest.class);

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateOrder() throws Exception {
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

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(request);

        logger.info("Request JSON: {}", requestJson);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetOrders() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Request> requests = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(requestService.getRequests(pageable)).thenReturn(requests);

        mockMvc.perform(get("/api/orders")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk());
    }
}
