package com.drone.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import com.drone.database.DatabaseConnection;

public class FireLogExporter {

    public static void exportToTextFile(String filename) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             FileWriter writer = new FileWriter(filename)) {

            writer.write("🔥 Drone & Fire Logs 🔥\n");
            writer.write("========================\n\n");

            // Drones
            writer.write("=== Drones ===\n");
            ResultSet dronesRS = stmt.executeQuery("SELECT * FROM drones");
            while (dronesRS.next()) {
                writer.write("ID: " + dronesRS.getInt("id") +
                             " | Name: " + dronesRS.getString("name") +
                             " | Battery: " + dronesRS.getInt("battery") +
                             " | X: " + dronesRS.getInt("x_position") +
                             " | Y: " + dronesRS.getInt("y_position") +
                             " | Last Updated: " + dronesRS.getString("last_updated") + "\n");
            }

            writer.write("\n=== Fires ===\n");
            ResultSet fireRS = stmt.executeQuery("SELECT * FROM fire_events");
            while (fireRS.next()) {
                writer.write("ID: " + fireRS.getInt("id") +
                             " | X: " + fireRS.getInt("x_position") +
                             " | Y: " + fireRS.getInt("y_position") +
                             " | Detected Time: " + fireRS.getString("detected_time") + "\n");
            }

            writer.write("========================\n");
            writer.flush();
            System.out.println("Drone & Fire logs exported successfully to " + filename);

        } catch (IOException | java.sql.SQLException e) {
            e.printStackTrace();
        }
    }
}
