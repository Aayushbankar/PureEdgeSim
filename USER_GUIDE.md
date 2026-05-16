# PureEdgeSim: The Complete Beginner's Guide

Welcome! If you feel like you are just running commands and seeing charts without understanding why, this guide is for you.

## 1. What is this project?
**PureEdgeSim** is a "Virtual Laboratory" for the Internet of Things (IoT). 

Imagine you want to build a system for 10,000 self-driving cars. You need to know:
- Should the cars process their own data?
- Should they send it to a server on the street corner (**Edge**)?
- Should they send it to a giant data center in another city (**Cloud**)?

Instead of buying 10,000 cars, you use PureEdgeSim to simulate them.

---

## 2. Core Concepts (The "Why")
The simulation helps you balance three competing goals:
1.  **Latency**: Getting results fast (e.g., a car needs to "see" a pedestrian in 10ms).
2.  **Energy**: Not draining the device's battery.
3.  **Cost**: Not overloading expensive servers.

### The Three Layers:
- **Mist (Edge Devices)**: The "Small guys" (Phones, Sensors). They generate data. They have batteries.
- **Edge (Edge Servers)**: The "Middle guys" (Servers in 5G towers). Fast but have limited capacity.
- **Cloud**: The "Big guys" (Amazon AWS, Google Cloud). Infinite capacity but very far away (high latency).

---

## 3. Where is everything? (Codebase Overview)

### **The "Settings" (PureEdgeSim/settings/)**
This is where you define your "World":
- `simulation_parameters.properties`: The main switchboard. Turn on/off charts, set simulation time, change network speeds.
- `edge_devices.xml`: Define your devices (e.g., "Smartphone", "Sensor"). Set their battery and CPU power.
- `edge_datacenters.xml`: Define where the Edge servers are located.
- `applications.xml`: Define the "Work". Does it need to be fast (Health app) or can it wait (Photo backup)?

### **The "Engine" (PureEdgeSim/com/mechalikh/pureedgesim/)**
- `simulationengine`: The heart. It manages the virtual "clock" and handles events.
- `simulationmanager`: Coordinates everything. It starts the visualizer and ends the simulation.
- `taskorchestrator`: **The most important part for researchers.** This is the algorithm that decides: *"Task A goes to Edge Server 1, Task B goes to Cloud."*
- `simulationvisualizer`: The code that creates the real-time charts you see on your screen.

---

## 4. How to use it (The "How")

### **Step 1: Configure your scenario**
Open `PureEdgeSim/settings/simulation_parameters.properties`.
- Want a faster simulation? Increase `update_interval`.
- Want more devices? Change `min_number_of_edge_devices`.

### **Step 2: Run the code**
Use the command we've been using:
```bash
mvn compile exec:java -Dexec.mainClass="com.mechalikh.pureedgesim.MainApplication"
```

### **Step 3: Analyze the Charts**
- **Success Rate**: If this is low, your network is too slow or your servers are too full.
- **CPU Utilization**: If Edge is at 100% and Cloud is at 0%, your orchestrator is not balanced.
- **Map**: Blue dots are your devices. Red dots are the ones currently working.

### **Step 4: Check Results**
Look in `PureEdgeSim/output/`. Every run saves a detailed CSV file and PNG charts. You can use these for reports or research papers.

---

## 5. Technical Knowledge for Developers
- **Language**: Java.
- **Build System**: Maven (handles all the libraries like XChart for the graphs).
- **Architecture**: Discrete Event Simulation (DES). It doesn't run in "real time"; it jumps from event to event (e.g., "Device 1 sends task" -> "Task arrives at Edge"). This is why it can simulate 24 hours in just 1 minute.
