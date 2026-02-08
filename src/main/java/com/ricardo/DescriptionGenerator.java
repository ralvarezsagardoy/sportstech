package com.ricardo;

// Esto define QUÉ hace nuestro generador, no CÓMO lo hace.
// Es vital para poder cambiar de "IA Gratis" a "IA de Pago" en el futuro sin romper nada.
public interface DescriptionGenerator {
    String generateReview(String brand, String model, double price);
}
