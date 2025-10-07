package com.drone.model;

import java.awt.Rectangle;

public class Drone {
    private int x, y;
    private int size = 30;
    private String name;
    private int battery; // battery percentage

    public Drone(String name, int battery, int x, int y) {
        this.name = name;
        this.battery = battery;
        this.x = x;
        this.y = y;
    }

    public void moveUp(int step) { y = Math.max(y - step, 0); }
    public void moveDown(int step, int panelHeight) { y = Math.min(y + step, panelHeight - size); }
    public void moveLeft(int step) { x = Math.max(x - step, 0); }
    public void moveRight(int step, int panelWidth) { x = Math.min(x + step, panelWidth - size); }

    public Rectangle getBounds() { return new Rectangle(x, y, size, size); }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }
    public String getName() { return name; }
    public int getBattery() { return battery; }
    public void setBattery(int battery) { this.battery = battery; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }
}
