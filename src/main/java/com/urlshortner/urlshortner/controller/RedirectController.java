package com.urlshortner.urlshortner.controller;

import com.urlshortner.urlshortner.model.UrlClick;
import com.urlshortner.urlshortner.model.UrlMapping;
import com.urlshortner.urlshortner.repository.UrlClickRepository;
import com.urlshortner.urlshortner.repository.UrlMappingRepository;
import com.urlshortner.urlshortner.service.UrlShornerService;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@RestController
public class RedirectController {

    private static final Logger logger = LoggerFactory.getLogger(RedirectController.class);

    private final UrlShornerService urlShornerService;
    private final UrlClickRepository urlClickRepository;
    private final UrlMappingRepository urlMappingRepository;

    public RedirectController(UrlShornerService urlShornerService,
                              UrlClickRepository urlClickRepository,
                              UrlMappingRepository urlMappingRepository) {
        this.urlShornerService = urlShornerService;
        this.urlClickRepository = urlClickRepository;
        this.urlMappingRepository = urlMappingRepository;
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> redirect(@PathVariable String code, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("Received redirect request for short code: {}", code);

        UrlMapping mapping = urlShornerService.getByShortCode(code);
        if (mapping == null) {
            logger.warn("No mapping found for short code: {}", code);
            return ResponseEntity.notFound().build();
        }

        mapping.setClickCount(mapping.getClickCount() + 1);
        urlShornerService.save(mapping);

        String ip = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
            ip = "8.8.8.8"; // fallback for localhost testing
        }

        UrlClick click = new UrlClick();
        click.setShortCode(code);
        click.setClickedAt(LocalDateTime.now());
        click.setIpAddress(ip);

        String ua = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(ua);
        click.setUserAgent(ua);
        click.setBrowser(userAgent.getBrowser().getName());
        click.setOs(userAgent.getOperatingSystem().getName());

        logger.info("Tracking click: IP={}, Browser={}, OS={}", ip, click.getBrowser(), click.getOs());

        // Get geo location info
        RestTemplate rest = new RestTemplate();
        try {
            Map<?, ?> geoData = rest.getForObject("http://ip-api.com/json/" + ip, Map.class);
            if ("success".equals(Objects.requireNonNull(geoData).get("status"))) {
                String country = geoData.get("country").toString();
                click.setCountry(country);
                logger.info("Resolved country for IP {}: {}", ip, country);
            } else {
                logger.warn("Geo lookup failed for IP {}: {}", ip, geoData.get("message"));
            }
        } catch (Exception e) {
            logger.error("Geo lookup exception for IP {}: {}", ip, e.getMessage());
        }

        urlClickRepository.save(click);

        String longUrl = mapping.getLongUrl();
        logger.info("Redirecting short code {} to URL: {}", code, longUrl);
        response.sendRedirect(longUrl);
        return null;
    }

    @GetMapping("/analytics/{code}")
    public ResponseEntity<?> getAnalytics(@PathVariable String code) {
        logger.info("Fetching analytics summary for short code: {}", code);
        UrlMapping mapping = urlShornerService.getByShortCode(code);
        if (mapping == null) {
            logger.warn("No URL mapping found for short code: {}", code);
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("shortCode", mapping.getShortCode());
        response.put("clickCount", mapping.getClickCount());
        response.put("longUrl", mapping.getLongUrl());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/analytics/{code}/clicks")
    public ResponseEntity<?> getClickDetails(@PathVariable String code) {
        logger.info("Fetching click details for short code: {}", code);
        List<UrlClick> clicks = urlClickRepository.findByShortCode(code);
        return ResponseEntity.ok(clicks);
    }

    @GetMapping("/analytics/top")
    public ResponseEntity<?> getTopUrls() {
        logger.info("Fetching top 10 most clicked short URLs");
        List<UrlMapping> top = urlMappingRepository.findAll(Sort.by(Sort.Direction.DESC, "clickCount"))
                .stream().limit(10).toList();
        return ResponseEntity.ok(top);
    }
}
