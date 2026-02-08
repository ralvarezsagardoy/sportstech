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
        return callOllama("Actúa como experto en marketing. Escribe una reseña de venta breve (max 30 palabras) para el reloj " + brand + " " + model + ". Usa etiquetas HTML <strong> y <p>. NO respondas nada más, solo el HTML. RESPONDE ÚNICAMENTE EN ESPAÑOL Y SIN EMOTICONOS Y HAZLO HUMANO, QUE NO SE NOTE QUE ES UNA IA.");
    }

    @Override
    public String generateTagline(String brand, String model) {
        return callOllama("Escribe una frase corta por la que alguien debería comprar el reloj " + brand + " " + model + ". Solo texto plano, sin comillas ni explicaciones. RESPONDE ÚNICAMENTE EN ESPAÑOL Y RECUERDA QUE DEBE SER UNA FRASE CORTA, NO UN PARRAFO.");
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
            
            // LIMPIEZA FINAL (Ya sin borrar 'nn')
            return rawText
                    .replace("u003c", "<")   
                    .replace("u003e", ">")   
                    .replace("u0026", "&")   
                    .replace("u00e1", "á").replace("u00c1", "Á")
                    .replace("u00e9", "é").replace("u00c9", "É")
                    .replace("u00ed", "í").replace("u00cd", "Í")
                    .replace("u00f3", "ó").replace("u00d3", "Ó")
                    .replace("u00fa", "ú").replace("u00da", "Ú")
                    .replace("u00f1", "ñ").replace("u00d1", "Ñ")
                    .trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error Conexión";
        }
    }

    // LECTOR JSON MEJORADO: Convierte \n en espacio correctamente
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
                    // AQUÍ ESTÁ EL ARREGLO:
                    if (c == 'n') result.append(" ");       // \n -> espacio
                    else if (c == 'r') result.append("");   // \r -> nada
                    else if (c == 't') result.append(" ");  // \t -> espacio
                    else if (c == '"') result.append('"');  // \" -> "
                    else result.append(c);
                    escape = false;
                } else {
                    if (c == '\\') escape = true; else if (c == '"') break; else result.append(c);
                }
            }
            return result.toString();
        } catch (Exception e) { return "Error procesando"; }
    }
}