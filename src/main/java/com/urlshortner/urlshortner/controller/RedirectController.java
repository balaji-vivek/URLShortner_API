package com.urlshortner.urlshortner.controller;

import com.urlshortner.urlshortner.model.UrlClick;
import com.urlshortner.urlshortner.model.UrlMapping;
import com.urlshortner.urlshortner.repository.UrlClickRepository;
import com.urlshortner.urlshortner.repository.UrlMappingRepository;
import com.urlshortner.urlshortner.service.UrlShornerService;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class RedirectController {

    private final UrlShornerService urlShornerService;
    private final UrlClickRepository urlClickRepository;
    private final UrlMappingRepository urlMappingRepository;

    public RedirectController(UrlShornerService urlShornerService,UrlClickRepository urlClickRepository,UrlMappingRepository urlMappingRepository){
        this.urlShornerService=urlShornerService;
        this.urlClickRepository=urlClickRepository;
        this.urlMappingRepository=urlMappingRepository;
    }
    @GetMapping("/{code}")
    public ResponseEntity<?> redirect(@PathVariable String code, HttpServletRequest request, HttpServletResponse response) throws IOException{
        UrlMapping mapping = urlShornerService.getByShortCode(code);
        mapping.setClickCount(mapping.getClickCount()+1);
        urlShornerService.save(mapping);

        String ip =request.getRemoteAddr();
        UrlClick click = new UrlClick();
        click.setShortCode(code);
        click.setClickedAt(LocalDateTime.now());
        click.setIpAddress(ip);
        String ua = request.getHeader("User-Agent");
        click.setUserAgent(ua);
        UserAgent userAgent =UserAgent.parseUserAgentString(ua);
        String browser = userAgent.getBrowser().getName();
        String os = userAgent.getOperatingSystem().getName();
        click.setBrowser(browser);
        click.setOs(os);
        RestTemplate rest = new RestTemplate();

        if ("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
            ip = "8.8.8.8";
        }
        Map geoData = rest.getForObject("http://ip-api.com/json/"+ ip, Map.class);
        if ("success".equals(geoData.get("status"))) {
            String country = geoData.get("country").toString();
            click.setCountry(country);
            System.out.println("Country: " + country);
        } else {
            System.out.println("Geo lookup failed: " + geoData.get("message"));
        }

        urlClickRepository.save(click);
        String longUrl = urlShornerService.getLongUrl(code);
        response.sendRedirect(longUrl);
        return null;
    }
    @GetMapping("/analytics/{code}")
    public ResponseEntity<?> getAnalytics(@PathVariable String code){
        UrlMapping mapping = urlShornerService.getByShortCode(code);
        Map<String, Object> response = new HashMap<>();
        response.put("shortCode",mapping.getShortCode());
        response.put("clickCount",mapping.getClickCount());
        response.put("longUrl",mapping.getLongUrl());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/analytics/{code}/clicks")
    public ResponseEntity<?> getClickDetails(@PathVariable String code){
        List<UrlClick> clicks = urlClickRepository.findByShortCode(code);
        return ResponseEntity.ok(clicks);
    }

    @GetMapping("/analytics/top")
    public ResponseEntity<?> getTopUrls(){
        List<UrlMapping> top = urlMappingRepository.findAll(
                Sort.by(Sort.Direction.DESC,"clickCount"))
                .stream().limit(10).toList();
        return ResponseEntity.ok(top);

    }
}
