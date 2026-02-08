package com.ricardo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TestConnection {
    public static void main(String[] args) {
        // PON TU API KEY AQUÍ
        String API_KEY = "AIzaSyB3AKw2iOkOF6d8o26CJIhBZ8-XrMVACn4"; 
        
        // Esta URL pregunta: "¿Qué modelos tienes disponibles?"
        String url = "https://generativelanguage.googleapis.com/v1beta/models?key=" + API_KEY.trim();

        try {
            System.out.println("🕵️ Probando llave...");
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Código de respuesta: " + response.statusCode());
            
            if (response.statusCode() == 200) {
                System.out.println("✅ ¡LA LLAVE FUNCIONA! Modelos disponibles:");
                System.out.println(response.body());
            } else {
                System.out.println("❌ ERROR: Tu llave no tiene acceso a la IA.");
                System.out.println("Mensaje de Google: " + response.body());
                System.out.println("\nSOLUCIÓN: Tienes que ACTIVAR la API 'Generative Language' en la consola de Google Cloud.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
