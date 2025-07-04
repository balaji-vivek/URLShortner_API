package com.urlshortner.urlshortner.controller;

import com.google.zxing.WriterException;
import com.urlshortner.urlshortner.dto.ShortenResponse;
import com.urlshortner.urlshortner.service.UrlShornerService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("api/v1/url")
public class UrlShortnerController {

    private final UrlShornerService urlShornerService;

    public UrlShortnerController(UrlShornerService urlShornerService){
        this.urlShornerService=urlShornerService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<?> shorten(@RequestBody Map<String, String> request) throws IOException, WriterException {
        String longUrl = request.get("longUrl");
        if(longUrl==null || longUrl.trim().isEmpty()){
            return ResponseEntity.badRequest().body("Error: 'longUrl' is required in the request body.");
        }
        ShortenResponse response = urlShornerService.shortenUrl(longUrl);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{code}")
    public ResponseEntity<?> redirect(@PathVariable String code, HttpServletResponse response) throws IOException{
        String longUrl = urlShornerService.getLongUrl(code);
        if (longUrl == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Short URL not found");
        }
        response.sendRedirect(longUrl);
        return null;
    }
}
