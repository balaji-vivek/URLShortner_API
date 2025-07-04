package com.urlshortner.urlshortner.repository;

import com.urlshortner.urlshortner.model.UrlClick;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UrlClickRepository extends JpaRepository<UrlClick, Long> {
    List<UrlClick> findByShortCode(String shortCode);
}

