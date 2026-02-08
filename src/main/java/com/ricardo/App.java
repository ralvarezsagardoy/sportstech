package com.ricardo;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) {
        
        String url = "jdbc:mysql://localhost:3306/sportstech_db";
        String user = "root";
        String password = "PASSWORDMYSQL"; // 🔴 PASSWORD

        DescriptionGenerator ia = new OllamaGenerator(); 
        List<IndexTemplate.ProductData> productList = new ArrayList<>();

        try {
            Connection conexion = DriverManager.getConnection(url, user, password);
            System.out.println("🚀 Iniciando generación PRO...");

            String sql = "SELECT b.name, p.model_name, p.price, p.seo_slug, p.image_url FROM products p JOIN brands b ON p.brand_id = b.brand_id";
            Statement statement = conexion.createStatement();
            ResultSet resultados = statement.executeQuery(sql);

            while (resultados.next()) {
                String marca = resultados.getString("name");
                String modelo = resultados.getString("model_name");
                double precio = resultados.getDouble("price");
                String slug = resultados.getString("seo_slug");
                String imageUrl = resultados.getString("image_url");
                if (imageUrl == null || imageUrl.isEmpty()) imageUrl = "https://via.placeholder.com/800";

                // 1. Generar REVIEW (Larga) para la página interna
                System.out.println("🤖 IA (Review): " + modelo);
                String reviewIA = ia.generateReview(marca, modelo, precio);
                
                // 2. Generar TAGLINE (Corta) para la portada
                System.out.println("🤖 IA (Tagline): " + modelo);
                String taglineIA = ia.generateTagline(marca, modelo);

                // Guardar datos para el Index
                productList.add(new IndexTemplate.ProductData(marca, modelo, precio, imageUrl, slug, taglineIA));

                // Crear página individual
                createProductPage(slug + ".html", marca, modelo, precio, reviewIA, imageUrl);
                
                System.out.println("⏳ Esperando 5s...");
                Thread.sleep(5000); 
            }
            
            // Generar Index
            System.out.println("🎨 Creando portada con efectos...");
            saveFile("index.html", IndexTemplate.getIndexPage(productList));
            System.out.println("✅ ¡LISTO!");

            conexion.close();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private static void createProductPage(String fileName, String brand, String model, double price, String review, String imageUrl) {
        String htmlContent = WebTemplate.getPage(brand, model, price, review, imageUrl);
        saveFile(fileName, htmlContent);
    }

    private static void saveFile(String fileName, String content) {
        try {
            FileWriter writer = new FileWriter("web_output/" + fileName);
            writer.write(content);
            writer.close();
        } catch (IOException e) { System.out.println("Error: " + fileName); }
    }
}