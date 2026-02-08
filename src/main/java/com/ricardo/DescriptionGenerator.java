package com.ricardo;

public interface DescriptionGenerator {
    // Genera la reseña completa (para la página de producto)
    String generateReview(String brand, String model, double price);
    
    // Genera una frase corta de marketing (para la portada)
    String generateTagline(String brand, String model);
}
