# Design Document: Mobility and Location

The Location Manager defines the spatial environment of the simulation and how mobile devices interact within that 2D space.

## 1. Simulation Map
The environment is a 2D plane defined by `length` and `width` (in meters). 
- **Fixed Entities**: Cloud servers (conceptual location) and Edge Data Centers (fixed x, y coordinates).
- **Mobile Entities**: Edge devices (smartphones, vehicles) that move over time.

## 2. Mobility Model (`MobilityModel.java`)
PureEdgeSim uses a **Random Waypoint** style model by default, but optimized for performance.

### 2.1 Movement States
A device can be in two states:
- **Moving**: Travelling toward a target destination at a specific `speed` (m/s).
- **Paused**: Staying at a location for a random duration (defined by `minPauseDuration` and `maxPauseDuration`).

### 2.2 Update Logic
Instead of calculating every step, the `DefaultMobilityModel` works on demand:
1. Every `updateInterval`, the engine asks the model: "Where is device X now?".
2. The model calculates the new (x, y) based on the time elapsed and speed.
3. If the device reaches its destination, it enters the pause state.

## 3. Impact on Tasks
Mobility is a primary cause of task failure in Edge computing:
- **Range Check**: A device must be within `edge_devices_range` or `edge_datacenters_coverage` to offload to a local peer or server.
- **Mobility Failure**: If a task is being processed on an Edge Server and the device moves out of that server's coverage area before the result is returned, the task fails due to `MOBILITY_LOSS`.

## 4. Location Awareness
The `LocationManager` provides utility methods to calculate distances between any two nodes. This distance is used by the `Orchestrator` to find the "nearest" server and by the `NetworkModel` to calculate latency (if distance-based latency is enabled).
