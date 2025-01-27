package com.example.ntt.handler;

import org.springframework.context.ApplicationEvent;
import com.example.ntt.model.entity.Request;

public class OrderCheckEvent extends ApplicationEvent {
    private final Request request;

    public OrderCheckEvent(Object source, Request request) {
        super(source);
        this.request = request;
    }

    public Request getRequest() {
        return request;
    }
}
