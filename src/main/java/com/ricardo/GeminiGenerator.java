package com.ricardo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;

public class GeminiGenerator implements DescriptionGenerator {

    // ✅ TU CLAVE (La mantengo)
    private static final String API_KEY = "API_KEY_AQUI"; 
    
    // ✅ CAMBIO CLAVE: Usamos 'gemini-flash-lite-latest'
    // Este modelo aparece explícitamente en tu lista y no debería compartir el límite gastado del 2.5.
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-lite-latest:generateContent?key=" + API_KEY.trim();

    @Override
    public String generateReview(String brand, String model, double price) {
        try {
            String safeBrand = brand.replace("\"", "");
            String safeModel = model.replace("\"", "");
            
            // Prompt pidiendo HTML limpio
            String prompt = "Escribe una reseña de venta muy breve (max 30 palabras) para el reloj " 
                          + safeBrand + " " + safeModel + ". "
                          + "Usa etiquetas HTML <strong> y <p>. NO USES MARKDOWN. Solo el código HTML.";
            
            String jsonBody = "{"
                    + "\"contents\": [{"
                    + "\"parts\":[{\"text\": \"" + prompt + "\"}]"
                    + "}]"
                    + "}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("❌ ERROR GOOGLE (" + response.statusCode() + "): " + response.body());
                return "Error API"; 
            }

            // Extraer texto
            String rawText = simpleExtract(response.body());
            
            // Limpieza Final
            return rawText.replace("```html", "")
                          .replace("```", "")
                          .replace("\\u003c", "<")
                          .replace("\\u003e", ">")
                          .replace("u003c", "<") 
                          .replace("u003e", ">")
                          .trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error Conexión";
        }
    }

    private String simpleExtract(String json) {
        try {
            String marker = "\"text\": \""; 
            int start = json.indexOf(marker);
            if (start == -1) {
                marker = "\"text\":\"";
                start = json.indexOf(marker);
            }
            if (start == -1) return "Reseña generada.";
            
            start += marker.length();
            StringBuilder result = new StringBuilder();
            boolean escape = false;
            
            for (int i = start; i < json.length(); i++) {
                char c = json.charAt(i);
                if (escape) {
                    switch (c) {
                        case 'n': result.append(" "); break;
                        case 'r': break; 
                        case 't': result.append(" "); break;
                        case '"': result.append('"'); break;
                        case '\\': result.append('\\'); break;
                        default: result.append(c);
                    }
                    escape = false;
                } else {
                    if (c == '\\') { escape = true; }
                    else if (c == '"') { break; }
                    else { result.append(c); }
                }
            }
            return result.toString();
        } catch (Exception e) {
            return "Error procesando texto.";
        }
    }
}