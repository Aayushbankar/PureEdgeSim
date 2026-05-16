package com.mechalikh.pureedgesim.gui;

import com.mechalikh.pureedgesim.simulationmanager.DefaultSimulationManager;
import com.mechalikh.pureedgesim.simulationmanager.SimLog;
import com.mechalikh.pureedgesim.simulationengine.PureEdgeSim;
import com.mechalikh.pureedgesim.scenariomanager.Scenario;

public class GUISimulationManager extends DefaultSimulationManager {
    private static SimulationData sharedData;

    public static void setSharedData(SimulationData data) {
        sharedData = data;
    }

    public GUISimulationManager(SimLog simLog, PureEdgeSim pureEdgeSim, int simulationId, int iteration, Scenario scenario) {
        super(simLog, pureEdgeSim, simulationId, iteration, scenario);
    }

    @Override
    public void startSimulation() {
        // Inject our WebSimulationVisualizer
        this.simulationVisualizer = new WebSimulationVisualizer(this, sharedData);
        
        simLog.print("%s - Launching with Premium Web GUI", getClass().getSimpleName());
        simulation.start();
    }
}
