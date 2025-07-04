package com.urlshortner.urlshortner.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
public class UrlMapping {
    public UrlMapping(){

    }

    public UrlMapping(Long id, String longUrl, String shortCode, LocalDateTime createdAt) {
        this.id = id;
        this.longUrl = longUrl;
        this.shortCode = shortCode;
        this.createdAt = createdAt;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String longUrl;

    public String getLongUrl() {
        return longUrl;
    }

    private String shortCode;
    private LocalDateTime createdAt;

    private int clickCount=0;

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    public Object getShortCode() {
        return shortCode;
    }
}
