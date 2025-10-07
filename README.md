# 🔥 Autonomous Drone Mission Planner — Fire Detection Simulation

This project is a Java-based simulation system that models autonomous drone operations for detecting and tracking fires in a 2D grid environment.

## 🚀 Features
- Real-time fire detection simulation using drones
- MySQL database integration for persistent data storage
- Export fire & drone logs to text files (`fire_logs.txt`)
- GUI built with Java Swing for easy control and visualization
- Auto-updating drone telemetry (position, battery, status, and timestamps)

## 🧩 Tech Stack
- **Language:** Java (JDK 17+)
- **GUI:** Java Swing
- **Database:** MySQL
- **IDE:** Visual Studio Code / Eclipse

## 📊 Database Structure
**Tables:**
1. `drones`
   - `id`, `name`, `battery`, `x_position`, `y_position`, `last_updated`
2. `fire_events`
   - `id`, `drone_id`, `x_position`, `y_position`, `detected_time`

## 🧠 How It Works
1. Drones patrol a simulated area.
2. When fire coordinates are detected, the system stores the event in the database.
3. Drones update their position, battery, and status automatically.
4. You can export all logs via the “Export Logs” button in the GUI.

## 📦 Setup
1. Import the project into your IDE.
2. Configure your MySQL connection in `DatabaseConnection.java`.
3. Run the SQL script to create tables.
4. Launch the simulation from `Main.java`.

## 📁 File Structure
src/
├── com.drone.controller/
│ ├── FireLogExporter.java
│ ├── DroneController.java
│ └── ...
├── com.drone.database/
│ ├── DatabaseConnection.java
├── com.drone.ui/
│ ├── SimulationFrame.java
├── com.drone/
│ ├── Fire.java
│ └── Main.java

---

**Developed by:** Jenit Johnson 👨‍💻  
**Purpose:** Academic Mini Simulation Project — Autonomous Systems (Java)

---
