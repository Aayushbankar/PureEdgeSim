<p align="center">
  <img width="200" src="https://user-images.githubusercontent.com/46229052/196671093-21ba3438-719d-4dd4-ad79-bfddd1395663.png">
</p>

# PureEdgeSim 🌐
**A powerful simulation framework for performance evaluation of Cloud, Edge, and Mist computing environments.**

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0) 
[![Maven Central](https://img.shields.io/maven-central/v/com.mechalikh/pureedgesim.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.mechalikh%22%20AND%20a:%22pureedgesim%22)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/7bcee5c75c3741b5923e0158c6e79b37)](https://www.codacy.com/gh/CharafeddineMechalikh/PureEdgeSim/dashboard)

PureEdgeSim enables large-scale study of IoT as a distributed, dynamic, and highly heterogeneous infrastructure. It features modular design, realistic network/energy/mobility models, and support for the edge-to-cloud continuum.

---

## 🚀 Quick Start & Running the Simulation

Follow these steps to get the new CLI-based PureEdgeSim running:

### **Step 1: Compile the Project**
Navigate to the project root directory and run:
```bash
mvn clean compile
```

### **Step 2: Run the Application**

#### Mode A: Interactive Menu-based Mode (Console UI)
Launch without arguments to use the interactive menu:
```bash
mvn exec:java -Dexec.mainClass="com.mechalikh.pureedgesim.MainApplication"
```
You can dynamically modify settings categories (General, Network, Orchestration, File Paths) with interactive guidance and validation before launching.

#### Mode B: Parameter-based Mode (Headless / Scripted)
You can directly pass command-line arguments to override configurations. This is ideal for server and background runs:
```bash
mvn exec:java -Dexec.mainClass="com.mechalikh.pureedgesim.MainApplication" \
    -Dexec.args="--time 0.05 --devices 200 --parallel false"
```

For detailed instructions, argument references, and configuration guides, check out:
- [HOW_TO_RUN.md](HOW_TO_RUN.md) (Step-by-Step execution guide)
- [docs/CODEBASE_DOCUMENTATION.md](docs/CODEBASE_DOCUMENTATION.md) (Complete developer & architecture documentation)

---

## ✨ Key Features
- 📱 **Heterogeneous Support:** Model sensors, mobile devices, battery-powered nodes, and various connectivity types (WiFi, 5G, 4G, Ethernet).
- 📡 **Realistic Networking:** Advanced modeling of latency, bandwidth allocation, peer-to-peer communication, and routing.
- ⚡ **Energy & Mobility:** Integrated realistic energy consumption and device mobility models.
- 📈 **Scalability:** Supports simulations with **30,000+ devices** and long-duration scenarios (+24 hours).
- 📊 **Rich Metrics:** Detailed tracking of CPU/network utilization, delays, energy consumption, and task success rates.
- 🎨 **Live Visualization:** Debug and understand your simulation in real-time with the built-in visualizer.

<p align="center">
  <img src="https://github.com/CharafeddineMechalikh/PureEdgeSim/blob/master/PureEdgeSim/files/real%20time.gif" width="600">
</p>

---

## 🔗 Related Work
PureEdgeSim has been used in various research domains:
- **Reinforcement & Deep Learning Scenarios**
- **Satellite Edge Computing**
- **Data Caching & Placement**
- **Task Scheduling & Security**
- **Self-Organized Architectures**
- **Software-Defined Networking (SDN)**

Detailed list of publications can be found in the [Wiki](https://github.com/CharafeddineMechalikh/PureEdgeSim/wiki).

---


<p align="right">
  <a href="#pureedgesim-">Back to top ↑</a>
</p>
