# Design Document: Simulation Management and Logging

The Simulation Manager module coordinates the execution of various scenarios and ensures that the results are accurately recorded and persisted.

## 1. The Manager (`Simulation.java`)
This is the top-level entry point for the entire framework.
- **File Checking**: Verifies that all XML and properties files exist and are valid.
- **Scenario Loading**: Reads `orchestration_architectures` and `orchestration_algorithms` from settings and creates a list of `Scenario` objects to run.
- **Multi-threading**: If `parallel_simulation` is enabled, it spawns multiple `SimulationThread` instances to utilize all CPU cores.

## 2. Iterations and Scenarios
A single execution of PureEdgeSim can involve hundreds of individual simulations.
- **Scenario**: A unique combination of (Architecture, Algorithm, Device Count).
- **Looping**: The manager automatically loops through all device counts (from `min` to `max` with the defined `step`).

## 3. The Logger (`SimLog.java`)
The logger is the primary data aggregator.
- **Real-time Monitoring**: Increments counters for tasks sent, failed, and executed on each layer (Cloud, Edge, Mist).
- **Network Aggregation**: Summarizes total bandwidth usage and traffic volume.
- **Energy Aggregation**: Calculates the Wh consumed by every component.
- **Persistence**: 
    - **CSV Output**: `Sequential_simulation.csv` (or Parallel). Each row represents one completed Scenario.
    - **Text Output**: `simulation_log.txt`. Contains every single `print` and `deepLog` message for debugging.

## 4. Charts and Visualization (`simulationvisualizer`)
If `display_real_time_charts` is true, the `SimulationVisualizer` component is activated.
- **Live Updates**: Every `charts_update_interval`, it pulls the latest counters from `SimLog`.
- **Chart Types**: Success Rate, CPU Utilization, WAN Usage, and Task Distribution.
- **Save Feature**: Can save the final charts as bitmap images at the end of the run.
