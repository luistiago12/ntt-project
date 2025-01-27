package com.example.ntt.model.entity;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Product {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private Double price;

    @ManyToOne
    @JoinColumn(name = "request_id")
    @JsonIgnore
    private Request request;
}
