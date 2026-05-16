package com.mechalikh.pureedgesim.gui;

import com.mechalikh.pureedgesim.scenariomanager.SimulationParameters;
import com.mechalikh.pureedgesim.simulationmanager.Simulation;
import com.mechalikh.pureedgesim.taskorchestrator.DefaultOrchestrator;

/**
 * A default scenario class that users can run to see the GUI in action.
 */
public class DefaultGUIScenario {
    public static void main(String[] args) {
        // Setup shared data
        SimulationData sharedData = new SimulationData();
        sharedData.status = "IDLE";
        sharedData.currentScenario = "Default GUI Scenario";

        // Start Server
        try {
            GUIServer server = new GUIServer(sharedData, 8080, () -> {
                System.out.println("Restarting Simulation...");
                sharedData.logs.clear();
                sharedData.history.clear();
                sharedData.status = "RESTARTING";
                
                // Re-launch
                Simulation sim = new Simulation();
                sim.setCustomSimulationManager(GUISimulationManager.class);
                sim.launchSimulation();
            });
            server.start();

            // Initial Launch
            GUISimulationManager.setSharedData(sharedData);
            Simulation sim = new Simulation();
            sim.setCustomSimulationManager(GUISimulationManager.class);
            sim.setCustomEdgeOrchestrator(DefaultOrchestrator.class);

            // Configure for a nice visual demo
            SimulationParameters.displayRealTimeCharts = true;
            SimulationParameters.parallelism_enabled = false;
            SimulationParameters.simulationDuration = 6000;
            SimulationParameters.minNumberOfEdgeDevices = 50;
            SimulationParameters.maxNumberOfEdgeDevices = 150;
            SimulationParameters.edgeDevicesIncrementationStepSize = 50;

            System.out.println("Starting Default GUI Scenario...");
            sim.launchSimulation();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
