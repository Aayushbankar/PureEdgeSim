# PureEdgeSim: Complete Study Roadmap & Recreation Guide

## Overview
A comprehensive guide to studying the PureEdgeSim codebase (Discrete Event Simulator for Edge/IoT Computing) and architecting a recreation of the framework.

---

# Part 1: Codebase Study Roadmap

PureEdgeSim is a **Discrete Event Simulator (DES)** written in Java 8 (Maven build). It does not update the system state every millisecond; instead, it maintains a priority queue of scheduled events and fast-forwards time from one event to the next.

## Phase 1: High-Level Orientation

### Step 1.1 — Understand the Build System
Review `pom.xml` to identify key dependencies:
- **`jgrapht-core` (1.3.1):** Graph data structure used for network topology and routing.
- **`xchart` (3.8.0):** Real-time charting and visualization library.
- **`kdtree` (1.0.3):** KD-Tree for efficient spatial queries (finding nearest Edge nodes).
- **`jFuzzyLogic` (3.0):** Fuzzy logic library for custom orchestrator examples.

### Step 1.2 — Trace the Entry Point
- `MainApplication.java` — CLI entry point, prompts user for simulation mode.
- `PureEdgeSim.java` — The central engine that owns the event loop.

---

## Phase 2: The Core Engine (Discrete Event Simulation)

| Concept | File(s) | Description |
|---------|---------|-------------|
| **Event** | `simulationengine/Event.java` | Schedulable unit: a `time` (double), `type` (int), `destination` (SimEntity), and optional `data` payload (e.g., a `Task` object). |
| **Future Queue** | `simulationengine/FutureQueue.java` | Priority queue that orders events strictly by timestamp. Ensures causality (events processed in order). |
| **SimEntity** | `simulationengine/SimEntity.java` | Abstract base class for all active components. Requires implementing `processEvent(Event e)`. Provides `send()` to schedule new events. |
| **Engine Loop** | `simulationengine/PureEdgeSim.java` | Main loop: (1) pop earliest event from queue, (2) set `clock = event.time`, (3) call `destination.processEvent(event)`, (4) repeat until queue empty or `simulationDuration` reached. |

**Key Insight:** The clock jumps directly to the next event's time. Nothing happens between events. This is what makes DES fast.

---

## Phase 3: Hardware & Environment Modeling

### 3.1 Computing Node Hierarchy
All nodes inherit from a base class (typically `DefaultComputingNode.java` or abstract `ComputingNode`).

| Layer | Resource Level | Mobility | Latency | Role |
|-------|---------------|----------|---------|------|
| **Cloud DC** | Infinite | Static | High | Heavy workloads, no real-time constraints |
| **Edge DC** | Medium | Static | Low | Regional processing |
| **Mist/Device** | Constrained | Mobile | Lowest | Ultra-fast local processing |

### 3.2 Node Internal Architecture
Each `ComputingNode` tracks:
- **CPU:** Number of cores × MIPS (Million Instructions Per Second)
- **RAM & Storage:** Available vs. used
- **Battery:** Current level, drain rate
- **Location:** X/Y coordinates
- **State:** Alive/Dead

### 3.3 Configuration Parsing
`scenariomanager/` contains parsers for XML files:
- `cloud.xml` — Cloud datacenter specs
- `edge_datacenters.xml` — Edge server specs
- `edge_devices.xml` — IoT/Mist device specs
- `applications.xml` — Application task definitions
- `simulation_parameters.properties` — Global simulation tuning

---

## Phase 4: Workload Generation & Orchestration

### 4.1 Task Lifecycle
```mermaid
flowchart LR
    A[Device generates Task] --> B[REQUEST_OFFLOAD event sent to Orchestrator]
    B --> C[Orchestrator executes findComputingNode()]
    C --> D[Task assigned to target ComputingNode]
    D --> E[Node processes task, schedules TASK_COMPLETED event]
```

### 4.2 Task Generator
- `Task.java` / `DefaultTask.java` — Workload unit: `length` (instructions), `inputSize`, `outputSize`.
- `TaskGenerator.java` / `DefaultTaskGenerator.java` — Fires `TASK_GENERATION` events based on configurable intervals (Poisson or fixed).
- `Application.java` — Defines a collection of tasks and their generation pattern.

### 4.3 Orchestrator (Pluggable Decision-Maker)
- `Orchestrator.java` — Abstract base with lifecycle: `orchestrate(task)` → `filterEnabledLayers()` → `findComputingNode()` → assign.
- `DefaultOrchestrator.java` — Two built-in algorithms:

| Algorithm | Strategy |
|-----------|----------|
| **Round Robin** | Tracks `historyMap` of tasks per node, picks the least loaded. |
| **Trade-Off** | Weighted scoring: node type weights (Cloud=1.8, Edge=1.2, Mist=1.3), workload history, CPU speed, task length. |

### 4.4 Offloading Constraints
`Orchestrator.offloadingIsPossible()` checks:
- **Connectivity:** Device in range of target node?
- **Availability:** Target node alive (battery > 0)?
- **Resources:** Target node has enough RAM/CPU/Storage?

---

## Phase 5: Environmental Dynamics

### 5.1 Network Model (`network/`)
- `NetworkModel.java` — Manages the graph topology using jgrapht.
- `TransferProgress.java` — Tracks active data transfers.
- Calculates **propagation delay** (distance / speed of light) and **transmission delay** (data size / bandwidth).
- Supports peer-to-peer, multi-hop routing, and bandwidth allocation.

### 5.2 Energy Model (`energymodel/`)
- `EnergyModel.java` / `DefaultEnergyModel.java` — Calculates power consumption.
- Two states: **Idle** (static drain) and **Active** (CPU utilization × dynamic factor).
- `NODE_DEATH` event triggered when battery hits 0.

### 5.3 Mobility Model (`mobility/`)
- `Mobility.java` / `DefaultMobility.java` — Updates X/Y coordinates over time.
- `LOCATION_UPDATE` events scheduled periodically.
- Supports trajectories, waypoints, and random walk patterns.
- Network links dynamically break/reconnect based on range.

---

## Phase 6: Practical Examples

Review these in order:

| Example | Focus |
|---------|-------|
| `Example1.java` | Minimal scenario — default everything |
| `Example2.java` + `CustomMobilityModel` | Override mobility behavior |
| `Example3.java` | Custom XML configuration |
| `Example4.java` + `CustomComputingNode` | Override hardware properties |
| `Example5.java` | Custom task generator |
| `Example6.java` | Custom orchestrator with custom algorithm |
| `Example7.java` + `CustomNetworkModel` + `ClusteringDevice` + `CachingDevice` | Advanced: clustering, caching, custom networking |
| `Example8.java` + `FuzzyLogicOrchestrator` | Fuzzy logic-based orchestration using jFuzzyLogic |

---

# Part 2: Recreation Architecture Guide

## 1. The Vision

**Why build this?** Cloud-native simulators (CloudSim) are too abstract for the volatile edge/IoT environment (battery drain, mobility, spotty networks). This framework allows researchers to test orchestration algorithms without deploying to physical devices.

**What to build:** A DES framework focused on:
- **Heterogeneity** — Mixing tiny sensors with massive cloud datacenters.
- **Causality** — Accurate event timelines.
- **Pluggability** — Easy algorithm swapping for researchers.

## 2. Tech Stack Selection

| Language | Best For | DES Library |
|----------|----------|-------------|
| **Python** | AI/ML integration, rapid prototyping | `SimPy` |
| **Rust** | Performance, memory safety, massive scale | `sim-` (custom) |
| **Go** | Concurrency, moderate scale | Custom with goroutines |
| **Java/Kotlin** | OOP, academic familiarity | Custom (like PureEdgeSim) |

### Required Libraries (Language-Agnostic)
| Component | Library |
|-----------|---------|
| Priority Queue (Min-Heap) | Built-in or `heapq` (Python) |
| Graph/Topology | `NetworkX` (Python), `petgraph` (Rust) |
| Spatial Indexing | KD-Tree (various implementations) |
| Charting/Viz | `Matplotlib`, `Plotly`, or custom |
| Config Parsing | JSON/YAML library |

## 3. Core Architecture

### Step 1 — The Event Loop (DES Engine)

```python
# Pseudocode — do NOT use a time-stepped loop
class Engine:
    def __init__(self):
        self.queue = MinHeap()   # ordered by event.time
        self.clock = 0.0
        self.entities = {}
    
    def run(self, max_time):
        while self.queue and self.clock < max_time:
            event = self.queue.pop()
            self.clock = event.time   # Jump forward!
            entity = self.entities[event.target_id]
            entity.handle_event(event)
```

### Step 2 — Base Entity Definitions

```python
class SimEntity:
    def __init__(self, id, engine):
        self.id = id
        self.engine = engine
    
    def send(self, delay, type, target_id, data=None):
        """Schedule an event for future time"""
        self.engine.schedule(self.engine.clock + delay, type, target_id, data)
    
    def handle_event(self, event):
        raise NotImplementedError

class ComputingNode(SimEntity):
    def __init__(self, id, engine, mips, cores, ram, battery, x, y):
        super().__init__(id, engine)
        self.mips = mips
        self.cores = cores
        self.ram = ram
        self.battery = battery
        self.x = x
        self.y = y
        self.alive = True
```

### Step 3 — Network Graph

```python
# Use a graph library (NetworkX in Python)
import networkx as nx

class NetworkModel:
    def __init__(self):
        self.graph = nx.Graph()
    
    def add_link(self, node_a, node_b, latency, bandwidth):
        self.graph.add_edge(node_a, node_b, 
                           weight=latency, 
                           bandwidth=bandwidth)
    
    def get_transmission_delay(self, source, dest, data_size):
        path = nx.shortest_path(self.graph, source, dest, weight='weight')
        total_latency = 0
        for i in range(len(path)-1):
            edge = self.graph[path[i]][path[i+1]]
            total_latency += edge['weight']  # propagation
            total_latency += data_size / edge['bandwidth']  # transmission
        return total_latency
```

### Step 4 — Pluggable Orchestrator

```python
class Orchestrator(SimEntity):
    """Base class — users override find_computing_node()"""
    
    def handle_event(self, event):
        if event.type == 'REQUEST_OFFLOAD':
            task = event.data
            target = self.find_computing_node(task)
            self.send(0, 'ASSIGN_TASK', target.id, task)
    
    def find_computing_node(self, task):
        """Override this with custom algorithm"""
        raise NotImplementedError

class RoundRobinOrchestrator(Orchestrator):
    def __init__(self, id, engine, nodes):
        super().__init__(id, engine)
        self.nodes = nodes
        self.counter = 0
    
    def find_computing_node(self, task):
        node = self.nodes[self.counter % len(self.nodes)]
        self.counter += 1
        return node
```

### Step 5 — Hardware Physics

```python
# Processing time
class EdgeServer(ComputingNode):
    def handle_event(self, event):
        if event.type == 'ASSIGN_TASK':
            task = event.data
            duration = task.instructions / (self.mips * self.cores)
            self.send(duration, 'TASK_COMPLETED', self.id, task)

# Battery drain
class BatteryPoweredDevice(ComputingNode):
    def on_clock_advance(self, old_time, new_time):
        elapsed = new_time - old_time
        drain = self.idle_power * elapsed
        if self.active:
            drain += self.active_power * elapsed
        self.battery -= drain
        if self.battery <= 0:
            self.alive = False
            self.send(0, 'NODE_DEATH', self.id)

# Mobility
class MobileDevice(ComputingNode):
    def handle_event(self, event):
        if event.type == 'LOCATION_UPDATE':
            self.x += self.speed_x * self.update_interval
            self.y += self.speed_y * self.update_interval
            self.send(self.update_interval, 'LOCATION_UPDATE', self.id)
```

### Step 6 — Configuration & Telemetry

#### Configuration (YAML recommended over XML)
```yaml
# scenario.yaml
simulation:
  duration: 86400  # 24 hours in seconds
  seed: 42

devices:
  - type: iot_sensor
    count: 500
    mips: 100
    cores: 1
    battery: 5000  # mAh
    mobility: random_walk
    speed: 1.2  # m/s

  - type: edge_server
    count: 10
    mips: 32000
    cores: 16
    battery: -1  # plugged in (infinite)

cloud:
  mips: 1000000
  cores: 128
  latency: 100  # ms base latency
```

#### Metrics Collector
```python
class MetricsCollector:
    def __init__(self):
        self.data = []  # list of dicts
    
    def listen(self, event):
        if event.type in ('TASK_COMPLETED', 'TASK_FAILED', 'NODE_DEATH'):
            self.data.append({
                'time': event.engine.clock,
                'type': event.type,
                'node_id': event.target_id,
                'details': str(event.data)
            })
    
    def to_csv(self, path):
        import csv
        with open(path, 'w', newline='') as f:
            writer = csv.DictWriter(f, fieldnames=self.data[0].keys())
            writer.writeheader()
            writer.writerows(self.data)
    
    def to_dataframe(self):
        import pandas as pd
        return pd.DataFrame(self.data)
```

---

## 4. Summary: Key Design Decisions

| Decision | PureEdgeSim Approach | Recommendation for Recreation |
|----------|---------------------|------------------------------|
| **Time progression** | Discrete Event (event-driven) | Same — DES is essential for performance |
| **Language** | Java 8 | Python (if ML focus) or Rust (if scale focus) |
| **Config format** | XML | YAML or JSON (simpler, more readable) |
| **Network model** | jgrapht graph | NetworkX (Python) or petgraph (Rust) |
| **Viz** | xchart (embedded) | Plotly Dash or Grafana streaming |
| **Plugin system** | Class extension | Strategy pattern / decorator / dynamic import |
| **Spatial queries** | KD-Tree | KD-Tree library or GeoHash |
| **Concurrency** | Multi-thread (SimulationThread) | Async or multiprocessing pools |
| **Testing** | JUnit 5 + Mockito | pytest + unittest.mock |

---

## 5. Implementation Milestone Plan

```
Milestone 1: Core Engine (DES)
  - Event class, FutureQueue (min-heap), SimEntity base, engine loop
  - Milestone: "Hello World" — engine processes 3 events and exits

Milestone 2: Single Node Simulation
  - ComputingNode base class, simple task generation
  - Milestone: One device generates a task, processes it, logs completion

Milestone 3: Multi-Node & Orchestration
  - Network graph, Orchestrator base, RoundRobin implementation
  - Milestone: 10 devices, 2 edge servers, tasks distributed via orchestrator

Milestone 4: Environmental Models
  - Energy drain, mobility with location updates, network link dynamics
  - Milestone: Devices move, lose connectivity, drain battery, "die"

Milestone 5: Configuration System
  - YAML/JSON parser, scenario builder from config files
  - Milestone: Load scenario from file, run simulation, output telemetry

Milestone 6: Telemetry & Visualization
  - Metrics collector, CSV export, live charts
  - Milestone: Real-time graph of CPU utilization and latency

Milestone 7: Example Suite
  - 3-5 example scenarios demonstrating plugin architecture
  - Milestone: Others can fork and implement custom orchestrators

Milestone 8: Performance Tuning
  - Profiling, optimization, parallel scenario runs
  - Milestone: Support 50,000+ devices in single-threaded mode
```

---

*This document serves as both a study guide for the existing PureEdgeSim codebase and an architectural blueprint for building a similar simulation framework.*
