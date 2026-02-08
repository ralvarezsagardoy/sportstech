package com.ricardo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;

public class OllamaGenerator implements DescriptionGenerator {

    private static final String API_URL = "http://localhost:11434/api/generate";
    private static final String MODEL_NAME = "mistral"; 

    @Override
    public String generateReview(String brand, String model, double price) {
        // ✅ CAMBIO: FORZAR ESPAÑOL
        return callOllama("Escribe una reseña de venta muy breve (max 30 palabras) para el reloj " + brand + " " + model + ". Usa etiquetas HTML <strong> y <p>. NO respondas nada más, solo el HTML. RESPONDE SIEMPRE EN ESPAÑOL.");
    }

    @Override
    public String generateTagline(String brand, String model) {
        // ✅ CAMBIO: FORZAR ESPAÑOL
        return callOllama("Escribe un eslogan de marketing de MÁXIMO 5 PALABRAS para el reloj " + brand + " " + model + ". Solo texto plano, sin comillas ni explicaciones. RESPONDE SIEMPRE EN ESPAÑOL.");
    }

    private String callOllama(String promptText) {
        try {
            String jsonBody = String.format(
                "{\"model\": \"%s\", \"prompt\": \"%s\", \"stream\": false}",
                MODEL_NAME,
                promptText.replace("\"", "'").replace("\n", " ")
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("❌ ERROR OLLAMA (" + response.statusCode() + ")");
                return "Error Local";
            }

            String rawText = extractResponse(response.body());
            
            // ✅ CAMBIO: LIMPIEZA DE "nn" Y CÓDIGOS RAROS
            return rawText
                    .replace("\\n", " ")     // Cambia salto de línea escapado por espacio
                    .replace("\n", " ")      // Cambia salto de línea real por espacio
                    .replace("nn", " ")      // ELIMINA LOS "nn" FANTASMA
                    .replace("u003c", "<")   
                    .replace("u003e", ">")   
                    .replace("u0026", "&")   
                    .replace("u00e1", "á") .replace("u00e9", "é") .replace("u00ed", "í") .replace("u00f3", "ó") .replace("u00fa", "ú") .replace("u00f1", "ñ") // Tildes básicas
                    .trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error Conexión";
        }
    }

    private String extractResponse(String json) {
        try {
            String marker = "\"response\":\"";
            int start = json.indexOf(marker);
            if (start == -1) return "Texto no encontrado";
            start += marker.length();
            StringBuilder result = new StringBuilder();
            boolean escape = false;
            for (int i = start; i < json.length(); i++) {
                char c = json.charAt(i);
                if (escape) {
                    if (c == '"') result.append('"'); else result.append(c);
                    escape = false;
                } else {
                    if (c == '\\') escape = true; else if (c == '"') break; else result.append(c);
                }
            }
            return result.toString();
        } catch (Exception e) { return "Error procesando"; }
    }
}