package com.mechalikh.pureedgesim.gui;

import com.mechalikh.pureedgesim.scenariomanager.SimulationParameters;
import com.mechalikh.pureedgesim.simulationmanager.Simulation;
import com.mechalikh.pureedgesim.simulationmanager.SimulationManager;
import com.mechalikh.pureedgesim.simulationmanager.SimLog;
import com.mechalikh.pureedgesim.simulationvisualizer.SimulationVisualizer;

import java.awt.Desktop;
import java.net.URI;
import java.util.List;

/**
 * Main application class to launch PureEdgeSim with the premium Web GUI.
 */
public class PureEdgeGUIApp {
    private static SimulationData sharedData = new SimulationData();

    public static void main(String[] args) {
        try {
            System.out.println("Initializing PureEdgeSim Premium GUI...");
            sharedData.status = "IDLE";

            // Start Server
            GUIServer server = new GUIServer(sharedData, 8080, null);
            server.start();

            // Open browser
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI("http://localhost:8080"));
            }

            // Configure Simulation
            GUISimulationManager.setSharedData(sharedData);
            Simulation sim = new Simulation();
            sim.setCustomSimulationManager(GUISimulationManager.class);

            // Disable default GUI
            SimulationParameters.displayRealTimeCharts = true; // Needs to be true for manager to call visualizer
            SimulationParameters.parallelism_enabled = false; // Real-time charts only work in sequential mode
            
            System.out.println("Simulation starting...");
            sim.launchSimulation();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
