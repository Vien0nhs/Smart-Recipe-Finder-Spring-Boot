package com.vien.smart_recipe_finder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiService {
    @Value("${gemini.api-key}")
    private String apiKey;
    @Value("${gemini.base-url}")
    private String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateContent(String prompt) throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestBody = objectMapper.writeValueAsString(
                new GeminiRequest(
                        new Content[] { new Content(new Part[] { new Part(prompt) }) }
                )
        );
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "?key=" + apiKey, HttpMethod.POST, entity, String.class
        );
        return response.getBody();
    }
    static class GeminiRequest {
        private Content[] contents;
        public GeminiRequest(Content[] contents) {
            this.contents = contents;
        }

        public Content[] getContents() {
            return contents;
        }
    }
    static class Content {
        private Part[] parts;

        public Content(Part[] parts) {
            this.parts = parts;
        }

        public Part[] getParts() {
            return parts;
        }
    }
    static class Part {
        private String text;

        public Part(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
