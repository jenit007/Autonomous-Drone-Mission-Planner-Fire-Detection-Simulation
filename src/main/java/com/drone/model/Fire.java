package com.drone.model;

import java.awt.Rectangle;

public class Fire {
    private int x, y;
    private int size = 30;

    public Fire(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, size, size);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return size; }
}
