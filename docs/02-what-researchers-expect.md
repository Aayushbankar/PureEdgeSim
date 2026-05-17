# What Researchers Expect from Edge Simulation Software

If you are building a new simulator to compete with tools like PureEdgeSim, iFogSim, or EdgeCloudSim, researchers and engineers will evaluate your software against a strict set of criteria. This document outlines those expectations — learned from surveying academic papers, open-source simulator GitHub issues, and user feedback.

---

## 1. Scalability and Performance

Researchers are pushing boundaries. They do not want to simulate 10 nodes; they want to simulate a "Smart City" with 100,000+ nodes over 24 hours.

| Requirement | Why It Matters |
|-------------|----------------|
| **Discrete Event Engine** | Must use event-driven simulation, not time-stepped loops. Stepping through every millisecond is computationally infeasible at scale. |
| **Low Memory Footprint** | Each simulated device should be a lightweight data structure (not a heavyweight thread). 100,000 devices should fit comfortably in 2-4 GB of RAM. |
| **Parallel Scenario Runs** | Researchers often run the same scenario 20+ times with different random seeds (Monte Carlo analysis). The ability to run scenarios in parallel across CPU cores is highly valued. |

---

## 2. Massive Heterogeneity

Unlike Cloud datacenters where servers are uniform, the Edge is chaotic.

**What researchers expect to model in a single scenario:**

| Device Type | CPU | RAM | Battery | Mobility | Connectivity |
|-------------|-----|-----|---------|----------|--------------|
| Temperature sensor | 10 MIPS | 64 KB | Coin cell (200 mAh) | Static | BLE (10 m range) |
| Smartphone | 8-core, 24000 MIPS | 8 GB | Li-Ion (4000 mAh) | Pedestrian (1.4 m/s) | WiFi + 5G |
| Vehicle ECU | 4-core, 8000 MIPS | 512 MB | Car battery | Vehicular (20 m/s) | DSRC / 5G |
| Edge Server | 32-core, 256000 MIPS | 64 GB | Plugged (infinite) | Static | Fiber (1 Gbps) |
| Cloud Datacenter | 1000+ cores | TB-scale | Infinite | Static | Optical backbone |

**Expectation:** The software must let you define these in configuration files (XML, JSON, YAML) without writing code for each device.

---

## 3. Pluggable Architecture (Developer Experience)

Researchers are usually not expert software engineers. They want to focus on their algorithm, not the simulator's internals.

### The Orchestrator Interface
The most critical extension point. A researcher expects to write:
```python
class MyRLOrchestrator(Orchestrator):
    def find_computing_node(self, task):
        # Load the state (CPU, battery, network of all nodes)
        # Query a trained RL model
        # Return the ID of the best node
        return best_node_id
```

**Expectation:** This should require zero understanding of the event queue, the graph topology internals, or the clock mechanism.

### AI/ML Integration (The Modern Priority)
Modern researchers heavily use PyTorch, TensorFlow, and OpenAI Gym.
- **Python-first:** Simulators written in Java (PureEdgeSim, iFogSim) face adoption friction because researchers must bridge Java ↔ Python via sockets or files.
- **Gym Interface:** Offering an OpenAI Gym-compatible environment (`env.step(action)` → `observation, reward, done`) is a massive competitive advantage for RL researchers.

---

## 4. Realistic Environmental Physics

Deterministic simulation is worthless if the underlying physics are too abstract to match reality.

### Energy Models
| Feature | Expectation |
|---------|-------------|
| **Idle Drain** | Static power consumption even when the device is doing nothing. |
| **Active Drain** | CPU utilization × dynamic power factor. |
| **Transmission Drain** | Power cost for sending/receiving data over wireless interfaces. |
| **Battery Nonlinearity** | (Optional but valued) Peukert effect — batteries drain faster under high load. |

### Network Models
| Feature | Expectation |
|---------|-------------|
| **Propagation Delay** | Distance / speed of light. Two nodes 1 km apart should have ~3.3 μs propagation delay. |
| **Transmission Delay** | Data size / link bandwidth. A 1 MB file on a 100 Mbps link takes 80 ms. |
| **Multi-Hop Routing** | If A cannot talk directly to C, data must route through B. Shortest-path calculation. |
| **Dynamic Topology** | When a mobile node moves out of range, links must break and routes must recalculate. |

### Mobility Models
| Model | Description |
|-------|-------------|
| **Random Waypoint** | Device picks random destination, moves there, pauses, repeats. |
| **Random Walk** | Device changes direction at random intervals (Brownian motion). |
| **Vehicular** | Device follows road paths (requires map data or waypoints). |
| **Gaussian Markov** | Smooth, correlated movement (more realistic than pure random). |

---

## 5. Determinism (Reproducibility)

This is **non-negotiable** for academic peer review.

**Expectation:** If a researcher runs the simulation with `seed = 42`, it must produce the exact same outcome on a Macbook in Tokyo as on a Linux server in London.

**Implementation requirements:**
- A global pseudorandom number generator (PRNG) that is passed explicitly to all components.
- No reliance on system clock, `/dev/random`, or unordered hash maps for any stochastic decision.
- Every random draw (task arrival times, mobility decisions, network errors) must be traceable to the global seed.

---

## 6. Granular Telemetry and Exporting

Researchers need graphs for their papers. The software should make data extraction trivial.

### Required Metrics
| Metric | What It Tracks |
|--------|----------------|
| **Task Success Rate** | % of tasks completed within their deadline. |
| **Average Latency** | Round-trip time from task generation to result receipt. |
| **Energy Consumption** | Total mAh/μJ consumed per device. |
| **CPU Utilization** | Average % of CPU capacity used per node. |
| **Network Congestion** | Packets dropped, queue sizes, bandwidth saturation. |
| **Node Lifespan** | Time until battery depletion for each device. |

### Export Formats
| Format | Use Case |
|--------|----------|
| **CSV** | Universal, importable into Excel, R, MATLAB. |
| **Pandas DataFrame** | For Python-based analysis pipelines. |
| **Live Charts** | Real-time visualization for debugging (Matplotlib, Plotly, Grafana). |

---

## 7. Documentation and Examples

- **Quickstart:** "Run my first simulation with default settings" should work in 3 commands.
- **Step-by-step tutorials:** From "Customize configuration" to "Write a custom orchestrator."
- **API Reference:** Generated (via Sphinx, Javadoc, etc.) and cross-linked.
- **Peer-Reviewed Validation:** Papers showing the simulator reproduces results from a real testbed (important for trust).
