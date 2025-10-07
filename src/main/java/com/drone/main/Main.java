package com.drone.main;

import javax.swing.SwingUtilities;
import com.drone.ui.SimulationFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimulationFrame::new);
    }
}
