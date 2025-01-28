package com.example.ntt.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.example.ntt.model.entity.Product;
import com.example.ntt.model.entity.Request;
import com.example.ntt.model.repository.RequestRepository;
import com.example.ntt.handler.CustomExceptionHandler.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collections;
import java.util.UUID;

public class RequestServiceTest {

    @InjectMocks
    private RequestService requestService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    public void testSaveOrder() {
        Request request = new Request();
        request.setId(UUID.randomUUID());
        request.setStatus("PENDING");

        Product product = new Product();
        product.setName("product 1");
        product.setPrice(100.00);
        product.setRequest(request);

        request.setProducts(Collections.singletonList(product));

        when(requestRepository.save(request)).thenReturn(request);

        Request savedRequest = requestService.saveOrder(request);
        assertEquals(request, savedRequest);
    }

    @Test
    public void testSaveOrder_Duplicate() {
        Request request = new Request();
        request.setId(UUID.randomUUID());
        request.setStatus("PENDING");

        Product product = new Product();
        product.setName("product 1");
        product.setPrice(100.00);
        product.setRequest(request);

        request.setProducts(Collections.singletonList(product));

        when(requestRepository.existsById(request.getId())).thenReturn(true);

        assertThrows(CustomException.class, () -> requestService.saveOrder(request));
    }

    @Test
    public void testGetRequests() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Request> requests = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(requestRepository.findAll(pageable)).thenReturn(requests);

        Page<Request> result = requestService.getRequests(pageable);
        assertEquals(requests, result);
    }
}
