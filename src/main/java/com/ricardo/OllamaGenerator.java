package com.ricardo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;

public class OllamaGenerator implements DescriptionGenerator {

    // Apunta a tu ordenador local (Puerto por defecto de Ollama)
    private static final String API_URL = "http://localhost:11434/api/generate";
    
    // 🔴 ASEGÚRATE DE USAR EL MODELO QUE DESCARGASTE
    // Si usaste 'ollama run mistral', pon "mistral". Si fue llama3, pon "llama3".
    private static final String MODEL_NAME = "mistral"; 

    @Override
    public String generateReview(String brand, String model, double price) {
        return callOllama("Escribe una reseña de venta muy breve (max 30 palabras) para el reloj " + brand + " " + model + ". Usa etiquetas HTML <strong> y <p>. NO respondas nada más, solo el HTML.");
    }

    @Override
    public String generateTagline(String brand, String model) {
        return callOllama("Escribe un eslogan de marketing de MÁXIMO 5 PALABRAS para el reloj " + brand + " " + model + ". Solo texto plano, sin comillas ni explicaciones.");
    }

    private String callOllama(String promptText) {
        try {
            // "stream": false es VITAL para que Java espere la respuesta completa
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
                System.out.println("❌ ERROR OLLAMA (" + response.statusCode() + "): Asegúrate de tener la app de Ollama abierta.");
                return "Error Local";
            }

            // Extraemos y LIMPIAMOS los códigos raros
            String rawText = extractResponse(response.body());
            
            return rawText
                    .replace("\\n", "")      // Quitar saltos de línea escapados
                    .replace("u003c", "<")   // Arreglar <
                    .replace("u003e", ">")   // Arreglar >
                    .replace("u0026", "&")   // Arreglar &
                    .trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error Conexión";
        }
    }

    // Extractor manual sencillo
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
                    // Simplemente ignoramos el escape y seguimos, luego lo limpiamos con .replace
                    if (c == '"') result.append('"');
                    else result.append(c);
                    escape = false;
                } else {
                    if (c == '\\') escape = true;
                    else if (c == '"') break; // Fin del JSON
                    else result.append(c);
                }
            }
            return result.toString();
        } catch (Exception e) {
            return "Error procesando";
        }
    }
}