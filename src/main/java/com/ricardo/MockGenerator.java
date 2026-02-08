package com.ricardo;

import java.util.Random;

public class MockGenerator implements DescriptionGenerator {

    @Override
    public String generateReview(String brand, String model, double price) {
        // Frases prefabricadas para mezclar
        String[] intros = {
            "Si buscas rendimiento, el " + model + " es una bestia.",
            "Analizamos a fondo el " + model + " de " + brand + ".",
            "¿Vale la pena gastar $" + price + " en este dispositivo? La respuesta es sí."
        };

        String[] middles = {
            " Destaca por su batería y precisión en el GPS.",
            " Es la opción favorita de los maratonianos este año.",
            " Aunque el precio es alto, la calidad de construcción es premium."
        };

        String[] conclusions = {
            " Compra obligada si te tomas el deporte en serio.",
            " Una alternativa sólida al Apple Watch.",
            " Definitivamente, " + brand + " ha hecho un gran trabajo."
        };

        // Seleccionamos frases al azar para que no parezcan todos iguales
        Random random = new Random();
        String p1 = intros[random.nextInt(intros.length)];
        String p2 = middles[random.nextInt(middles.length)];
        String p3 = conclusions[random.nextInt(conclusions.length)];

        return p1 + p2 + p3;
    }
}