package com.urlshortner.urlshortner.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ShortenResponse {

    private String shortUrl;
    private String qrCodeBase64;

    public ShortenResponse(String shortUrl,String qrCodeBase64){
        this.shortUrl=shortUrl;
        this.qrCodeBase64=qrCodeBase64;
    }
}
