package com.urlshortner.urlshortner.service;

import com.google.zxing.WriterException;
import com.urlshortner.urlshortner.dto.ShortenResponse;
import com.urlshortner.urlshortner.model.UrlMapping;
import com.urlshortner.urlshortner.repository.UrlMappingRepository;
import com.urlshortner.urlshortner.util.QrCodeUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(UrlShornerService.class);



    public UrlShornerService(UrlMappingRepository urlMappingRepository){
        this.urlMappingRepository=urlMappingRepository;
    }

    public ShortenResponse shortenUrl(String longUrl,String customCode) throws IOException, WriterException {
        logger.info("shortenUrl {}", longUrl);
        String shortCode;
        if (!customCode.matches("^[a-zA-Z0-9-_]{3,20}$")) {
            throw new IllegalArgumentException("Custom code must be alphanumeric and between 3 to 20 characters.");
        }
        if(customCode!=null && !customCode.trim().isEmpty()){
            if(urlMappingRepository.findByShortCodeIgnoreCase(customCode).isPresent()){
                throw new IllegalArgumentException("Custom Code is Already taken. Please Choose Another.");
            }
            shortCode = customCode;
        } else {
            shortCode = generateCode();
        }
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
