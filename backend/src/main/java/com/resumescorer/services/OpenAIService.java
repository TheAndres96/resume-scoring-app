package com.resumescorer.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OpenAIService {
    @Value("${openai.api.key}")
    private String openAiApiKey;

    private final String API_URL = "https://api.openai.com/v1/chat/completions";

    public String scoreResume(String resumeText) {
        RestTemplate restTemplate = new RestTemplate();

        //Prepare API request payload
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo"); //GPT model
        requestBody.put("messages", new Object[] {
            Map.of("role","system","content", "You are an AI assistant. Score this resume from 0 to 100."),
            Map.of("role","user","content", resumeText)
        });

        //Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, Map.class);
            
            // Read Rate Limit Headers
            HttpHeaders responseHeaders = response.getHeaders();
            int remainingRequests = Integer.parseInt(responseHeaders.getFirst("x-ratelimit-remaining-requests"));
            int remainingTokens = Integer.parseInt(responseHeaders.getFirst("x-ratelimit-remaining-tokens"));
            int resetRequestsInSeconds = Integer.parseInt(responseHeaders.getFirst("x-ratelimit-reset-requests").replace("s", ""));
            int resetTokensInSeconds = Integer.parseInt(responseHeaders.getFirst("x-ratelimit-reset-tokens").replace("s", ""));

            // Log limits
            System.out.println("Requests Remaining: " + remainingRequests);
            System.out.println("Tokens Remaining: " + remainingTokens);
            System.out.println("Request Limit Resets in: " + resetRequestsInSeconds + " seconds");
            System.out.println("Token Limit Resets in: " + resetTokensInSeconds + " seconds");

            // **Check if we are close to hitting the request limit**
            if (remainingRequests <= 1) {
                System.out.println("⚠️ WARNING: Approaching request limit. Waiting for reset...");
                Thread.sleep((resetRequestsInSeconds + 1) * 1000L); // Wait until limit resets
            }

            // **Check if we are close to hitting the token limit**
            if (remainingTokens <= 1000) { // Adjust this based on model
                System.out.println("⚠️ WARNING: Approaching token limit. Waiting for reset...");
                Thread.sleep((resetTokensInSeconds + 1) * 1000L); // Wait until limit resets
            }

            return response.getBody().toString(); // Returns full response for now
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error: Request Interrupted";
        } catch (Exception e) {
            return "Error calling OpenAI: " + e.getMessage();
        }
    }
    
}
