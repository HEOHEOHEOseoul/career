package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	private final static String BASE_URL = "http://localhost:3000";
	private static final String API_MIME_TYPE = "application/json";
	private static final String USER_AGENT = "Spring 5 WebClient";
	
	@Bean
    WebClient client(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .baseUrl(BASE_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, API_MIME_TYPE)
                .defaultHeader(HttpHeaders.USER_AGENT, USER_AGENT)
                .build();
    }

    @Bean
    RestTemplate restTemplate() {
    	RestTemplate rest = new RestTemplate();
    	rest.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        return rest;
    }
}
