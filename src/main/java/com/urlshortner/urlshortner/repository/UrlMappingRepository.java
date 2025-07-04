package com.urlshortner.urlshortner.repository;

import com.urlshortner.urlshortner.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    Optional<UrlMapping> findByShortCodeIgnoreCase(String shortCode);

}

