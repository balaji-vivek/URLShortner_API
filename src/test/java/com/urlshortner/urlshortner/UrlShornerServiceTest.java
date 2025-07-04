package com.urlshortner.urlshortner;

import com.urlshortner.urlshortner.model.UrlMapping;
import com.urlshortner.urlshortner.repository.UrlMappingRepository;
import com.urlshortner.urlshortner.service.UrlShornerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class UrlShornerServiceTest {

    @Mock
    private UrlMappingRepository urlMappingRepository;

    @InjectMocks
    private UrlShornerService urlShornerService;


    @Test
    void testShortenUrl_ShouldSaveAndReturnShortUrl() throws Exception{
        String longUrl = "https://wwww.example.com";
        UrlMapping mockMapping = new UrlMapping(null,longUrl,"abc123", LocalDateTime.now());
        Mockito.when(urlMappingRepository.save(Mockito.any())).thenReturn(mockMapping);
        String shortCode = urlShornerService.shortenUrl(longUrl,"custom").getShortUrl();
        assertNotNull(shortCode);
    }

    @Test
    void testGetLongUrl_shouldReturnOriginalUrl(){
        String code ="abc123";
        UrlMapping mapping = new UrlMapping(1L,"http://www.example.com",code,LocalDateTime.now());
        Mockito.when(urlMappingRepository.findByShortCodeIgnoreCase(code)).thenReturn(Optional.of(mapping));

        String result = urlShornerService.getLongUrl(code);
        assertEquals("http://www.example.com",result);
    }
}
