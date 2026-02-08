package com.ricardo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CheckModels { //Clase utilizada para verificar los modelos disponibles en la API de Google Generative Language
    //Sin embargo, no se utiliza en el proyecto final, ya que se optó por usar Ollama localmente para evitar problemas de latencia y costos asociados a la API de Google.
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
