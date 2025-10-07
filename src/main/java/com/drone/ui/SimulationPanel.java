package com.drone.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import com.drone.model.*;
import com.drone.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class SimulationPanel extends JPanel {

    private ArrayList<Drone> drones;
    private ArrayList<Fire> fires;
    private ArrayList<Obstacle> obstacles;

    private String placementMode = null; // FIRE, OBSTACLE, or null

    public SimulationPanel() {
        setBackground(Color.WHITE);
        setFocusable(true);
        drones = new ArrayList<>();
        fires = new ArrayList<>();
        obstacles = new ArrayList<>();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if ("FIRE".equals(placementMode)) addFire(e.getX(), e.getY());
                else if ("OBSTACLE".equals(placementMode)) addObstacle(e.getX(), e.getY());
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw fires
        g.setColor(Color.RED);
        for (Fire fire : fires) g.fillRect(fire.getX(), fire.getY(), fire.getSize(), fire.getSize());

        // Draw obstacles
        g.setColor(Color.GRAY);
        for (Obstacle obs : obstacles) g.fillRect(obs.getX(), obs.getY(), obs.getSize(), obs.getSize());

        // Draw drones
        g.setColor(Color.BLUE);
        for (Drone drone : drones) {
            g.fillRect(drone.getX(), drone.getY(), drone.getSize(), drone.getSize());
            g.setColor(Color.BLACK);
            g.drawString(drone.getName(), drone.getX(), drone.getY() - 5);
            g.setColor(Color.BLUE);
        }
    }

    // Add a new drone
    public void addDrone(String name, int battery) {
        Random r = new Random();
        Drone drone = new Drone(name, battery, r.nextInt(getWidth() - 30), r.nextInt(getHeight() - 30));
        drones.add(drone);
        logDroneToDB(drone);
        repaint();
    }

    // Move drone manually
    public void moveDrone(Drone drone, String direction) {
        switch (direction) {
            case "UP": drone.moveUp(5); break;
            case "DOWN": drone.moveDown(5, getHeight()); break;
            case "LEFT": drone.moveLeft(5); break;
            case "RIGHT": drone.moveRight(5, getWidth()); break;
        }
        checkCollisions(drone);
        logDroneToDB(drone);
        repaint();
    }

    // Check collisions
    private void checkCollisions(Drone drone) {
        // Fire collision
        for (int i = 0; i < fires.size(); i++) {
            Fire fire = fires.get(i);
            if (drone.getBounds().intersects(fire.getBounds())) {
                JOptionPane.showMessageDialog(this, "🔥 Fire detected at (" + fire.getX() + "," + fire.getY() + ")");
                logFireToDB(fire, drone); // ✅ fixed: link to drone
                fires.remove(fire);
                break;
            }
        }

        // Obstacle collision
        for (Obstacle obs : obstacles) {
            if (drone.getBounds().intersects(obs.getBounds())) {
                JOptionPane.showMessageDialog(this, "⚠️ Obstacle detected! Drone repositioned.");
                repositionDrone(drone);
                break;
            }
        }
    }

    // Reposition drone safely
    private void repositionDrone(Drone drone) {
        Random r = new Random();
        int newX, newY;
        boolean safe;
        do {
            newX = r.nextInt(getWidth() - drone.getSize());
            newY = r.nextInt(getHeight() - drone.getSize());
            Rectangle newBounds = new Rectangle(newX, newY, drone.getSize(), drone.getSize());
            safe = true;
            for (Obstacle obs : obstacles) {
                if (newBounds.intersects(obs.getBounds())) {
                    safe = false;
                    break;
                }
            }
        } while (!safe);
        drone.setPosition(newX, newY);
        logDroneToDB(drone);
        repaint();
    }

    // Logging
    private void logDroneToDB(Drone drone) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO drones (name, battery, x_position, y_position) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, drone.getName());
            ps.setInt(2, drone.getBattery());
            ps.setInt(3, drone.getX());
            ps.setInt(4, drone.getY());
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ✅ FIXED VERSION — properly links fires to drones
    private void logFireToDB(Fire fire, Drone drone) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO fire_events (drone_id, x_position, y_position) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, getLatestDroneId(conn, drone)); // link to that drone
            ps.setInt(2, fire.getX());
            ps.setInt(3, fire.getY());
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Helper — find last inserted drone ID by name
    private int getLatestDroneId(Connection conn, Drone drone) {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id FROM drones WHERE name=? ORDER BY id DESC LIMIT 1")) {
            ps.setString(1, drone.getName());
            var rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    // Fire & obstacle placement
    public void addFire(int x, int y) { fires.add(new Fire(x, y)); repaint(); }
    public void addObstacle(int x, int y) { obstacles.add(new Obstacle(x, y)); repaint(); }

    public void setPlacementMode(String mode) { placementMode = mode; }

    // SCAN & extinguish nearest fire
    public void scanAndExtinguish(Drone drone) {
        if (fires.isEmpty()) { JOptionPane.showMessageDialog(this, "No fires to scan!"); return; }

        Fire nearest = null;
        double minDist = Double.MAX_VALUE;
        for (Fire fire : fires) {
            double dist = Math.hypot(drone.getX() - fire.getX(), drone.getY() - fire.getY());
            if (dist < minDist) { minDist = dist; nearest = fire; }
        }
        if (nearest == null) return;

        Fire targetFire = nearest;
        new Thread(() -> {
            boolean reached = false;
            while (!reached) {
                if (targetFire == null || fires.isEmpty()) break;

                // Horizontal move
                if (drone.getX() < targetFire.getX()) drone.moveRight(Math.min(5, targetFire.getX() - drone.getX()), getWidth());
                else if (drone.getX() > targetFire.getX()) drone.moveLeft(5);

                // Vertical move
                if (drone.getY() < targetFire.getY()) drone.moveDown(Math.min(5, targetFire.getY() - drone.getY()), getHeight());
                else if (drone.getY() > targetFire.getY()) drone.moveUp(5);

                checkCollisions(drone);

                if (drone.getBounds().intersects(targetFire.getBounds())) {
                    reached = true;
                    fires.remove(targetFire);
                    logFireToDB(targetFire, drone); // ✅ fixed here too
                    JOptionPane.showMessageDialog(this, "🔥 Fire at (" + targetFire.getX() + "," + targetFire.getY() + ") put down!");
                }

                repaint();
                try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }).start();
    }

    public ArrayList<Drone> getDrones() { return drones; }
}
