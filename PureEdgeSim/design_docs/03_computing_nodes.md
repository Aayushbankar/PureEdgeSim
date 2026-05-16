# Design Document: Computing Infrastructure

PureEdgeSim models the hardware environment using a hierarchical collection of computing nodes. Each node represents a physical or virtual entity capable of processing data.

## 1. Computing Node Hierarchy
All nodes inherit from `ComputingNode.java` (typically implemented as `DefaultComputingNode.java`).

### 1.1 Cloud Data Centers
- **Characteristics**: Infinite computing resources, fixed location, highest network latency.
- **Role**: Backend for heavy workloads that don't have strict real-time requirements.

### 1.2 Edge Data Centers (Cloudlets/Fog Nodes)
- **Characteristics**: Medium resources, fixed location, low latency.
- **Role**: Regional processing to reduce Cloud traffic.

### 1.3 Mist/Edge Devices
- **Characteristics**: Constrained resources (smartphone/sensor), mobile, battery-powered, lowest latency.
- **Role**: Local processing for ultra-fast response times.

## 2. Internal Architecture
A `ComputingNode` is composed of several layers:
- **Host**: The physical hardware.
- **CPU Cores**: Defined by MIPS (Million Instructions Per Second).
- **Virtual Machines (VMs)**: Abstract layers where tasks actually run.
- **Resource Management**: Tracks available RAM, Storage, and CPU utilization.

## 3. The Lifecycle of a Node
1. **Generation**: Created by `ComputingNodesGenerator` based on the XML configurations (`edge_devices.xml`, `cloud.xml`, etc.).
2. **Task Processing**: Receives a `Task`, calculates the execution time based on `task.length / node.mips`, and schedules a completion event.
3. **Energy Depletion**: If battery-powered, the node continuously subtracts energy for idle and active states.
4. **Death**: If energy hits 0, `isDead` becomes true. The node stops processing and all network links to it are broken.

## 4. Heterogeneity
One of PureEdgeSim's strengths is handling massive heterogeneity. By adjusting the XML files, you can simulate a mix of:
- High-end smartphones (8 cores, large battery).
- Low-power IoT sensors (1 core, tiny battery).
- Industrial Edge servers (32 cores, fixed power).
