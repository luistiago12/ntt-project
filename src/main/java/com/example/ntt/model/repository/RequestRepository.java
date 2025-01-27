package com.example.ntt.model.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ntt.model.entity.Request;

public interface RequestRepository extends JpaRepository<Request, UUID> {

}
