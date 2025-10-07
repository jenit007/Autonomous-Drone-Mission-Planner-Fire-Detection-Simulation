package com.drone.ui;

import javax.swing.*;
import java.awt.*;
import com.drone.controller.FireLogExporter;

public class SimulationFrame extends JFrame {

    public SimulationFrame() {
        setTitle("Drone Simulation - Fire & Obstacle Detection");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        SimulationPanel panel = new SimulationPanel();

        JPanel topPanel = new JPanel();

        JButton fireBtn = new JButton("Place Fire");
        fireBtn.addActionListener(e -> panel.setPlacementMode("FIRE"));

        JButton obsBtn = new JButton("Place Obstacle");
        obsBtn.addActionListener(e -> panel.setPlacementMode("OBSTACLE"));

        JButton exportBtn = new JButton("Export Fire Logs");
        exportBtn.addActionListener(e -> {
            FireLogExporter.exportToTextFile("fire_logs.txt");
            JOptionPane.showMessageDialog(this, "Fire logs exported to fire_logs.txt");
        });

        JButton addDroneBtn = new JButton("Add Drone");
        addDroneBtn.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter drone name:");
            if (name != null && !name.isEmpty()) {
                String batteryStr = JOptionPane.showInputDialog(this, "Enter battery % (0-100):");
                try {
                    int battery = Integer.parseInt(batteryStr);
                    panel.addDrone(name, battery);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid battery value!");
                }
            }
        });

        JButton scanBtn = new JButton("SCAN");
        scanBtn.addActionListener(e -> {
            if (!panel.getDrones().isEmpty())
                panel.scanAndExtinguish(panel.getDrones().get(0));
            else JOptionPane.showMessageDialog(this, "No drones available!");
        });

        JButton upBtn = new JButton("↑ Up");
        upBtn.addActionListener(e -> { if (!panel.getDrones().isEmpty()) panel.moveDrone(panel.getDrones().get(0), "UP"); });

        JButton downBtn = new JButton("↓ Down");
        downBtn.addActionListener(e -> { if (!panel.getDrones().isEmpty()) panel.moveDrone(panel.getDrones().get(0), "DOWN"); });

        JButton leftBtn = new JButton("← Left");
        leftBtn.addActionListener(e -> { if (!panel.getDrones().isEmpty()) panel.moveDrone(panel.getDrones().get(0), "LEFT"); });

        JButton rightBtn = new JButton("→ Right");
        rightBtn.addActionListener(e -> { if (!panel.getDrones().isEmpty()) panel.moveDrone(panel.getDrones().get(0), "RIGHT"); });

        topPanel.add(fireBtn);
        topPanel.add(obsBtn);
        topPanel.add(exportBtn);
        topPanel.add(addDroneBtn);
        topPanel.add(scanBtn);
        topPanel.add(upBtn);
        topPanel.add(downBtn);
        topPanel.add(leftBtn);
        topPanel.add(rightBtn);

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimulationFrame::new);
    }
}
