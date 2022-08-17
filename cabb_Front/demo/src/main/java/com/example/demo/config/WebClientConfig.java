package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	private static final String BASE_URL = "http://192.168.10.99:3000";
	private static final String API_MIME_TYPE = "application/json";
	
	@Bean
    public WebClient client(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, API_MIME_TYPE)
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
    	RestTemplate rest = new RestTemplate();
    	rest.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        return rest;
    }
}
