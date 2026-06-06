# PureEdgeSim Development Log (June 5-6, 2026)

This document provides a detailed architectural log of all the features, refactorings, bug fixes, and improvements implemented in PureEdgeSim over the past two days.

---

## 1. Overview of Tasks Accomplished

1. **Output Directory Restructuring & Target Run Co-location**:
   * Previously, chart files (PNGs) were stored globally in a separate folder (`PureEdgeSim/output/charts/`), making it difficult to link a specific simulation run's results with its generated charts.
   * Restructured the charting system so that all charts (`nodes_energy_consumption.png`, `tasks_success_rate.png`, etc.) are saved directly inside the unique timestamped output folder of the respective simulation run (e.g. `PureEdgeSim/output/2026-06-06_09-51-26/`).

2. **Energy Consumption Histograms**:
   * Improved the comparison visualization of energy consumption across different architectures.
   * Enhanced `EnergyChart` to dynamically plot co-simulation runs (like `--arch ALL`), creating side-by-side comparison bars for **Mist**, **Edge**, and **Cloud** energy consumption metrics.

3. **Task Service Time Metrics & Visualizer Dashboard**:
   * Integrated task service time tracking (accumulated delay from task creation to completion).
   * Developed `ServiceTimeChart.java` to plot average task service times over simulation minutes.
   * Updated `SimulationVisualizer` to display a 5-chart dashboard (expanding from the previous 4-chart layout) to accommodate the new service time metric.

4. **Multi-tier Node Count Personalization (General Settings / Option 2)**:
   * Redesigned CLI general settings (Option 2) to configure individual counts for:
     1. **Edge/Mist Devices Count (Mist nodes)**
     2. **Edge Datacenters Count (Edge servers)**
     3. **Cloud Datacenters Count (Cloud servers)**
   * Added corresponding command line argument flags: `--edge-datacenters` (`-edc`) and `--cloud-datacenters` (`-cdc`).
   * Implemented XML template parsing and replication to output custom XML config files (`cloud_custom.xml`, `edge_datacenters_custom.xml`) on-the-fly.

5. **Robust Dynamic Network Topology Scaling**:
   * Fixed topology creator exceptions when scaling beyond 1 cloud datacenter or multiple edge servers without manual link configs.
   * Updated `DefaultTopologyCreator` to automatically and dynamically link all instantiated Edge and Cloud servers to the shared WAN/metroRouter structure.

---

## 2. File-by-File Code Changes

### [com/mechalikh/pureedgesim/simulationvisualizer/Chart.java](file:///mnt/work/projects/PureEdgeSim/PureEdgeSim/com/mechalikh/pureedgesim/simulationvisualizer/Chart.java)
* **Goal**: Co-locate output PNG charts inside the timestamped simulation run folder.
* **Changes**:
  * Modified `save(String path, int width, int height)` to dynamically retrieve the active timestamped folder from `SimulationParameters.outputFolder` (which is updated per-run) and output the PNGs directly into it.

---

### [com/mechalikh/pureedgesim/simulationmanager/SimLog.java](file:///mnt/work/projects/PureEdgeSim/PureEdgeSim/com/mechalikh/pureedgesim/simulationmanager/SimLog.java)
* **Goal**: Accumulate delay stats to compute average task service times.
* **Changes**:
  * Declared a new metric accumulator: `protected Double totalServiceTime = 0.0;`.
  * Updated `getTasksExecutionInfos(Task task)` to increment `totalServiceTime` using `task.getTotalDelay()`.
  * Added getter `getAverageServiceTime()` to compute running average task service times.

---

### [com/mechalikh/pureedgesim/simulationvisualizer/ServiceTimeChart.java](file:///mnt/work/projects/PureEdgeSim/PureEdgeSim/com/mechalikh/pureedgesim/simulationvisualizer/ServiceTimeChart.java) [NEW]
* **Goal**: Create a line chart plotting task service times.
* **Implementation**:
  * Inherits from `Chart`.
  * Configures the layout using an X-axis for simulation time (minutes) and a Y-axis for average service time (seconds).
  * Subscribes to time series updates from the active simulation manager logger.

---

### [com/mechalikh/pureedgesim/simulationvisualizer/SimulationVisualizer.java](file:///mnt/work/projects/PureEdgeSim/PureEdgeSim/com/mechalikh/pureedgesim/simulationvisualizer/SimulationVisualizer.java)
* **Goal**: Embed the new `ServiceTimeChart` into the active dashboard.
* **Changes**:
  * Increased the default charts list array capacity from `4` to `5`.
  * Instantiated and added `ServiceTimeChart` to the layout manager, producing a cohesive 5-chart dashboard frame.

---

### [com/mechalikh/pureedgesim/MainApplication.java](file:///mnt/work/projects/PureEdgeSim/PureEdgeSim/com/mechalikh/pureedgesim/MainApplication.java)
* **Goal**: Add specific options to Option 2, register CLI arguments, and handle XML replication.
* **Changes**:
  * Added fields `edgeDataCentersCount` and `cloudDataCentersCount`.
  * Configured `--edge-datacenters` and `--cloud-datacenters` argument options.
  * Overhauled Option 2 (`Configure General Settings`) in the interactive menu loops to accept specific entity overrides.
  * Added `applyDatacenterOverridesAndSave()` which parses template XML files and dynamically replicates datacenter node nodes based on user configurations.
  * Updated `checkFiles()` inside the simulation initializer to reset static count variables and trigger custom XML overrides.

---

### [com/mechalikh/pureedgesim/datacentersmanager/DefaultTopologyCreator.java](file:///mnt/work/projects/PureEdgeSim/PureEdgeSim/com/mechalikh/pureedgesim/datacentersmanager/DefaultTopologyCreator.java)
* **Goal**: Support multiple Edge and Cloud datacenters without pathfinder exceptions.
* **Changes**:
  * Refactored `createWanLink()` to loop over all parsed Cloud nodes, establishing reciprocal WAN links to the central metroRouter.
  * Refactored `generateTopologyGraph()` to dynamically connect all active Edge datacenters to the WAN node, ensuring fully connected and navigable topologies out-of-the-box.

---

## 3. Verification & Execution Guide

### Localized Safe Run Environment
To run compiling and execution processes without altering the global OS system variables, use inline environment variable scoping:

```bash
# Clean Compile
JAVA_HOME=/home/legion/tools/jdk-17.0.19+10 PATH=/home/legion/tools/jdk-17.0.19+10/bin:/home/legion/tools/apache-maven-3.9.6/bin:$PATH mvn clean compile

# Execute Interactive CLI Menu
JAVA_HOME=/home/legion/tools/jdk-17.0.19+10 PATH=/home/legion/tools/jdk-17.0.19+10/bin:/home/legion/tools/apache-maven-3.9.6/bin:$PATH mvn exec:java -Dexec.mainClass="com.mechalikh.pureedgesim.MainApplication"

# Execute Custom Headless Simulation (5 Devices, 3 Edge servers, 2 Cloud servers)
JAVA_HOME=/home/legion/tools/jdk-17.0.19+10 PATH=/home/legion/tools/jdk-17.0.19+10/bin:/home/legion/tools/apache-maven-3.9.6/bin:$PATH mvn exec:java -Dexec.mainClass="com.mechalikh.pureedgesim.MainApplication" -Dexec.args="-t 2 -n 5 --edge-datacenters 3 --cloud-datacenters 2 --arch ALL"
```
