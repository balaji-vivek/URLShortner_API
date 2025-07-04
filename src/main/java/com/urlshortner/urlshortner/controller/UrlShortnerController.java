package com.urlshortner.urlshortner.controller;

import com.google.zxing.WriterException;
import com.urlshortner.urlshortner.dto.ShortenResponse;
import com.urlshortner.urlshortner.service.UrlShornerService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("api/v1/url")
public class UrlShortnerController {

    private final UrlShornerService urlShornerService;
    private static final Logger log = LoggerFactory.getLogger(UrlShortnerController.class);


    public UrlShortnerController(UrlShornerService urlShornerService){
        this.urlShornerService=urlShornerService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<?> shorten(@RequestBody Map<String, String> request) throws IOException, WriterException {
        String longUrl = request.get("longUrl");
        String customCode = request.get("customCode");
        if (longUrl == null || longUrl.trim().isEmpty()) {
            log.warn("Missing or empty longUrl in request");
            throw new IllegalArgumentException("'longUrl' is required in the request body.");
        }
        log.info("Received URL to shorten: {}", longUrl);
        ShortenResponse response = urlShornerService.shortenUrl(longUrl,customCode);
        log.info("Generated short URL: {}", response.getShortUrl());
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{code}")
    public ResponseEntity<?> redirect(@PathVariable String code, HttpServletResponse response) throws IOException{
        log.info("Redirect request for short code: {}", code);
        String longUrl = urlShornerService.getLongUrl(code);
        if (longUrl == null) {
            log.error("No mapping found for code: {}", code);
            throw new RuntimeException("Short URL not found");
        }
        log.info("Redirecting to: {}", longUrl);
        response.sendRedirect(longUrl);
        return null;
    }
}
