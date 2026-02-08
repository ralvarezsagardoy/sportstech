package com.ricardo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CheckModels {
    public static void main(String[] args) {
        String key = "API_KEY_AQUÍ"; 
        String url = "https://generativelanguage.googleapis.com/v1beta/models?key=" + key;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("=== TUS MODELOS DISPONIBLES ===");
            System.out.println(response.body());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
