package com.ricardo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AdminTask {

    private static final String DB_USER = "root";
    private static final String DB_PASS = "PASSWORDMYSQL";

    public static void main(String[] args) {
        try {
            System.out.println("🛠️  INICIANDO TAREAS ADMINISTRATIVAS...");

            // 1. CONECTAR AL SERVIDOR (SIN BASE DE DATOS) PARA CREARLA SI NO EXISTE
            Connection serverConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", DB_USER, DB_PASS);
            Statement stmt = serverConn.createStatement();
            
            System.out.println(">> Creando Base de Datos si no existe...");
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS sportstech_db");
            serverConn.close(); // Cerramos conexión al servidor

            // 2. CONECTAR A LA BASE DE DATOS
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sportstech_db", DB_USER, DB_PASS);
            Statement dbStmt = conn.createStatement();

            // 3. CREAR TABLAS
            System.out.println(">> Creando Tablas...");
            
            // Tabla MARCAS
            dbStmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS brands (
                    brand_id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(50) NOT NULL UNIQUE,
                    website VARCHAR(255)
                )
            """);

            // Tabla PRODUCTOS
            dbStmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS products (
                    product_id INT AUTO_INCREMENT PRIMARY KEY,
                    brand_id INT,
                    model_name VARCHAR(100) NOT NULL UNIQUE,
                    price DECIMAL(10, 2),
                    battery_hours INT,
                    water_resistance_atm INT,
                    has_gps BOOLEAN,
                    activity_type VARCHAR(50),
                    seo_slug VARCHAR(150),
                    image_url VARCHAR(500),
                    affiliate_link VARCHAR(500),
                    ai_description TEXT,
                    FOREIGN KEY (brand_id) REFERENCES brands(brand_id)
                )
            """);

            // 4. IMPORTAR CSV
            System.out.println(">> Importando 'productos.csv'...");
            importarCSV(conn);

            System.out.println("✅ ¡TODO LISTO! Base de datos actualizada.");
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void importarCSV(Connection conn) {
        String csvFile = "productos.csv";
        String line;
        String cvsSplitBy = ";"; // IMPORTANTE: Separador punto y coma

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            
            // Saltamos la primera línea (cabeceras)
            br.readLine(); 

            while ((line = br.readLine()) != null) {
                String[] data = line.split(cvsSplitBy);

                // CSV COLUMNAS: 
                // 0:Brand, 1:Model, 2:Price, 3:Battery, 4:Water, 5:GPS, 6:Activity, 7:Slug, 8:Image
                
                String brandName = data[0].trim();
                String modelName = data[1].trim();
                double price = Double.parseDouble(data[2].trim().replace(",", ".")); // Asegurar decimal con punto
                int battery = Integer.parseInt(data[3].trim());
                int water = Integer.parseInt(data[4].trim());
                boolean gps = data[5].trim().equals("1");
                String activity = data[6].trim();
                String slug = data[7].trim();
                String image = data.length > 8 ? data[8].trim() : "";

                // A. INSERTAR MARCA (Si no existe)
                PreparedStatement psBrand = conn.prepareStatement("INSERT IGNORE INTO brands (name) VALUES (?)");
                psBrand.setString(1, brandName);
                psBrand.executeUpdate();

                // B. OBTENER ID DE LA MARCA
                PreparedStatement psGetId = conn.prepareStatement("SELECT brand_id FROM brands WHERE name = ?");
                psGetId.setString(1, brandName);
                ResultSet rs = psGetId.executeQuery();
                int brandId = 0;
                if (rs.next()) brandId = rs.getInt("brand_id");

                // C. INSERTAR PRODUCTO (Si no existe)
                // Usamos INSERT IGNORE para que si ya existe el modelo, no dé error y siga con el siguiente
                String sqlProduct = """
                    INSERT IGNORE INTO products 
                    (brand_id, model_name, price, battery_hours, water_resistance_atm, has_gps, activity_type, seo_slug, image_url) 
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
                
                PreparedStatement psProd = conn.prepareStatement(sqlProduct);
                psProd.setInt(1, brandId);
                psProd.setString(2, modelName);
                psProd.setDouble(3, price);
                psProd.setInt(4, battery);
                psProd.setInt(5, water);
                psProd.setBoolean(6, gps);
                psProd.setString(7, activity);
                psProd.setString(8, slug);
                psProd.setString(9, image);
                
                int rows = psProd.executeUpdate();
                if (rows > 0) System.out.println("   -> Añadido: " + modelName);
            }

        } catch (Exception e) {
            System.out.println("⚠️ Error leyendo CSV (¿Está el archivo 'productos.csv' en la carpeta raíz?): " + e.getMessage());
        }
    }
}
