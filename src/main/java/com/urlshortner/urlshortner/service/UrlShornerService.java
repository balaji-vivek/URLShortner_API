package com.urlshortner.urlshortner.service;

import com.google.zxing.WriterException;
import com.urlshortner.urlshortner.dto.ShortenResponse;
import com.urlshortner.urlshortner.model.UrlMapping;
import com.urlshortner.urlshortner.repository.UrlMappingRepository;
import com.urlshortner.urlshortner.util.QrCodeUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class UrlShornerService {
    private final UrlMappingRepository urlMappingRepository;
    @Value("${app.base-url}")
    private String baseUrl;


    public UrlShornerService(UrlMappingRepository urlMappingRepository){
        this.urlMappingRepository=urlMappingRepository;
    }

    public ShortenResponse shortenUrl(String longUrl) throws IOException, WriterException {
        String shortCode = generateCode();
        UrlMapping mapping = new UrlMapping(null, longUrl, shortCode, LocalDateTime.now());
        String shortUrl = baseUrl + "/" + shortCode;
        String qrCodeBase64 = QrCodeUtil.generateQrCodeBase64(shortUrl, 250, 250);

        urlMappingRepository.save(mapping);
        return new ShortenResponse(shortUrl, qrCodeBase64);
    }
    public String getLongUrl(String shortCode){
        System.out.print(shortCode);
        return urlMappingRepository.findByShortCodeIgnoreCase(shortCode)
                .orElseThrow(() ->
                 new ResponseStatusException(HttpStatus.NOT_FOUND, "Short URL not found"))
                .getLongUrl();

    }

    private String generateCode() {
        return RandomStringUtils.randomAlphanumeric(6);
    }

    public UrlMapping getByShortCode(String code){
        return urlMappingRepository.findByShortCodeIgnoreCase(code)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Short URL Not Found"));
    }
    public void save(UrlMapping mapping){
        urlMappingRepository.save(mapping);
    }

}
