package com.mechalikh.pureedgesim.gui;

import java.util.ArrayList;
import java.util.List;

public class SimulationData {
    public double time;
    public double cpuUtilization;
    public double taskSuccessRate;
    public double networkUtilization;
    public int totalTasks;
    public int successfulTasks;
    public int failedTasks;
    public String currentScenario;
    public String status; 
    
    // Detailed Metrics
    public double energyCloud;
    public double energyEdge;
    public double energyMist;
    public double networkWan;
    public double networkMan;
    public double networkLan;
    public int failLatency;
    public int failMobility;
    public int failResources;
    public int failDeviceDead;
    public int tasksCloud;
    public int tasksEdge;
    public int tasksMist;
    
    public List<DataPoint> history = new ArrayList<>();
    public List<String> logs = new ArrayList<>();

    public void addLog(String log) {
        logs.add(log);
        if (logs.size() > 200) logs.remove(0);
    }

    public static class DataPoint {
        public double time;
        public double cpu;
        public double successRate;
        public double network;

        public DataPoint(double time, double cpu, double successRate, double network) {
            this.time = time;
            this.cpu = cpu;
            this.successRate = successRate;
            this.network = network;
        }
    }

    public void addHistory(double time, double cpu, double successRate, double network) {
        history.add(new DataPoint(time, cpu, successRate, network));
        if (history.size() > 100) history.remove(0);
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"time\":").append(time).append(",");
        sb.append("\"cpuUtilization\":").append(cpuUtilization).append(",");
        sb.append("\"taskSuccessRate\":").append(taskSuccessRate).append(",");
        sb.append("\"networkUtilization\":").append(networkUtilization).append(",");
        sb.append("\"totalTasks\":").append(totalTasks).append(",");
        sb.append("\"successfulTasks\":").append(successfulTasks).append(",");
        sb.append("\"failedTasks\":").append(failedTasks).append(",");
        sb.append("\"currentScenario\":\"").append(currentScenario).append("\",");
        sb.append("\"status\":\"").append(status).append("\",");
        
        // New metrics
        sb.append("\"energy\":{\"cloud\":").append(energyCloud).append(",\"edge\":").append(energyEdge).append(",\"mist\":").append(energyMist).append("},");
        sb.append("\"networkUsage\":{\"wan\":").append(networkWan).append(",\"man\":").append(networkMan).append(",\"lan\":").append(networkLan).append("},");
        sb.append("\"failures\":{\"latency\":").append(failLatency).append(",\"mobility\":").append(failMobility).append(",\"resources\":").append(failResources).append(",\"deviceDead\":").append(failDeviceDead).append("},");
        sb.append("\"tasksPerLevel\":{\"cloud\":").append(tasksCloud).append(",\"edge\":").append(tasksEdge).append(",\"mist\":").append(tasksMist).append("},");
        
        sb.append("\"logs\":[");
        for (int i = 0; i < logs.size(); i++) {
            sb.append("\"").append(logs.get(i).replace("\"", "\\\"").replace("\n", "").replace("\r", "")).append("\"");
            if (i < logs.size() - 1) sb.append(",");
        }
        sb.append("],");

        sb.append("\"history\":[");
        for (int i = 0; i < history.size(); i++) {
            DataPoint dp = history.get(i);
            sb.append("{\"time\":").append(dp.time).append(",\"cpu\":").append(dp.cpu).append(",\"successRate\":").append(dp.successRate).append(",\"network\":").append(dp.network).append("}");
            if (i < history.size() - 1) sb.append(",");
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }
}
