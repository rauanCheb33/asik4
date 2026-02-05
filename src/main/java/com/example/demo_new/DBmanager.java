package com.example.demo_new;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBmanager {
    private static String URL;
    private static String USER;
    private static String PASS;

    static {
        Map<String, String> env = loadEnv(".env");
        URL = env.get("DB_URL");
        USER = env.get("DB_USER");
        PASS = env.get("DB_PASS");
    }

    private static Map<String, String> loadEnv(String filePath) {
        Map<String, String> env = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("=", 2);
                if (parts.length == 2) env.put(parts[0].trim(), parts[1].trim());
            }
        } catch (IOException e) {
            System.err.println("Error: File .env not found in project!");
        }
        return env;
    }


    public static void setupDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS movies (" +
                "id SERIAL PRIMARY KEY, " +
                "title VARCHAR(255) NOT NULL, " +
                "description VARCHAR(255), " +
                "duration INT, " +
                "age_restriction INT, " +
                "price DOUBLE PRECISION);";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("[DB] Connection established. Tables verified.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // WRITE: добавление фильмов
    public void addMovieToDB(String title, String description, int duration, int age, double price) {
        String sql = "INSERT INTO movies (title, description, duration, age_restriction, price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setInt(3, duration);
            pstmt.setInt(4, age);
            pstmt.setDouble(5, price);
            pstmt.executeUpdate();
            System.out.println("[DB] Данные записаны.");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // READ: получение списка фильмов
    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                movies.add(new Movie(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("duration"),
                        rs.getInt("age_restriction"),
                        rs.getDouble("price")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return movies;
    }

    // UPDATE:  изменение цены
    public void updateMoviePrice(int id, double newPrice) {
        String sql = "UPDATE movies SET price = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newPrice);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            System.out.println("[DB] Данные обновлены.");
        } catch (SQLException e) { e.printStackTrace(); }
    }
    public void updateMovieDescription(int id, String newDescription){
        String sql = "UPDATE movies SET description = ? WHERE id = ?";
        try (Connection conn  = DriverManager.getConnection(URL, USER, PASS);
            PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, newDescription);
            pstmt.setInt(2,id);
            int rowsAffected = pstmt.executeUpdate();
            if(rowsAffected > 0){
                System.out.println("[DB] description was updated");
            }else{
                System.out.println("[DB] film was not found");
            }

        } catch (SQLException e) { e.printStackTrace(); }

    }

    // DELETE: удаление фильма
    public void deleteMovieFromDB(int id) {
        String sql = "DELETE FROM movies WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("[DB] Данные удалены.");
        } catch (SQLException e) { e.printStackTrace(); }
    }
}