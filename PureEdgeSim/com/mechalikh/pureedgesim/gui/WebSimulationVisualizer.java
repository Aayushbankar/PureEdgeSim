package com.mechalikh.pureedgesim.gui;

import com.mechalikh.pureedgesim.simulationmanager.SimulationManager;
import com.mechalikh.pureedgesim.simulationmanager.SimLog;
import com.mechalikh.pureedgesim.simulationvisualizer.SimulationVisualizer;
import com.mechalikh.pureedgesim.scenariomanager.SimulationParameters;
import java.util.List;

/**
 * A simulation visualizer that redirects updates to a SimulationData object
 * for the web-based GUI.
 */
public class WebSimulationVisualizer extends SimulationVisualizer {

    private SimulationData data;

    public WebSimulationVisualizer(SimulationManager simulationManager, SimulationData data) {
        super(simulationManager);
        this.data = data;
    }

    @Override
    public void updateCharts() {
        // Capture stats from simulation manager
        data.time = simulationManager.getSimulation().clock();
        data.taskSuccessRate = 100 - simulationManager.getFailureRate();
        
        SimLog log = simulationManager.getSimulationLogger();
        data.totalTasks = log.getGeneratedTasks();
        data.successfulTasks = simulationManager.getFinishedTaskList().size();
        data.failedTasks = data.totalTasks - data.successfulTasks;
        
        // Detailed metrics
        data.failLatency = log.getTasksFailedLatency();
        data.failMobility = log.getTasksFailedMobility();
        data.failResources = log.getTasksFailedRessourcesUnavailable();
        data.failDeviceDead = log.getTasksFailedBeacauseDeviceDead();
        
        data.networkWan = log.getTotalWanUsage();
        data.networkMan = log.getTotalManUsage();
        data.networkLan = log.getTotalLanUsage();
        
        data.tasksCloud = log.getTasksExecutedOnCloud();
        data.tasksEdge = log.getTasksExecutedOnEdge();
        data.tasksMist = log.getTasksExecutedOnMist();

        // Update logs
        List<String> currentLogs = log.getLog();
        if (currentLogs.size() > data.logs.size()) {
            for (int i = data.logs.size(); i < currentLogs.size(); i++) {
                data.addLog(currentLogs.get(i));
            }
        }

        data.currentScenario = simulationManager.getScenario().toString();
        data.status = "RUNNING";
        
        data.cpuUtilization = Math.random() * 100; // Placeholder
        data.networkUtilization = (data.networkWan + data.networkMan + data.networkLan) / (data.time + 1);
        
        data.addHistory(data.time, data.cpuUtilization, data.taskSuccessRate, data.networkUtilization);
    }

    @Override
    public void close() {
        data.status = "FINISHED";
    }

    @Override
    public void saveCharts() {
        // No-op for web visualizer as we don't use XCharts
    }

    @Override
    public void repaint() {
        // No-op for web
    }
}
