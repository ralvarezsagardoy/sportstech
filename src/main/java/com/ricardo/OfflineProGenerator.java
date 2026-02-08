package com.ricardo;

import java.util.Random;

public class OfflineProGenerator implements DescriptionGenerator {

    @Override
    public String generateReview(String brand, String model, double price) {
        String[] templates = {
            "<strong>Rendimiento puro.</strong> El " + model + " redefine lo que esperábamos de " + brand + ". Su sensor óptico de última generación y su chasis ultraligero lo convierten en la opción número uno para triatletas. Por $" + price + ", es una inversión en tu salud.",
            
            "<strong>¿El rey de la montaña?</strong> Probamos el " + brand + " " + model + " durante 30 días y el resultado es claro: batería infinita y precisión milimétrica. Si te tomas el trail running en serio, este es tu reloj.",
            
            "<strong>Elegancia y potencia.</strong> No solo sirve para correr; el " + model + " destaca por su diseño premium. " + brand + " ha logrado un equilibrio perfecto entre smartwatch de estilo de vida y herramienta de alto rendimiento.",
            
            "<strong>Análisis Técnico:</strong> A diferencia de sus competidores, el " + model + " ofrece métricas de recuperación avanzadas sin suscripción mensual. Su pantalla AMOLED brilla incluso bajo luz solar directa. Una compra maestra por $" + price + "."
        };

        // Elegir una frase al azar para que parezca dinámico
        return templates[new Random().nextInt(templates.length)];
    }
}
