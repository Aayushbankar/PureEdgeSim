# How to Run PureEdgeSim

This guide provides the necessary commands and configuration details to run your first simulation and customize it to your needs.

## 1. Prerequisites
- **Java Development Kit (JDK) 8 or higher**: Ensure `java -version` works in your terminal.
- **Apache Maven**: Ensure `mvn -version` works in your terminal.

---

## 2. Compiling the Project
Before running, you must compile the source code and download dependencies.
Run this command from the root directory:

```bash
mvn clean install -DskipTests
```

---

## 3. Running the Simulation
There are two main ways to run PureEdgeSim:

### Option A: Interactive CLI (Recommended)
This launches a menu where you can quickly select the architecture and device count.
```bash
mvn exec:java -Dexec.mainClass="com.mechalikh.pureedgesim.MainApplication"
```

### Option B: Direct Simulation (Uses Properties File)
This runs the simulation strictly based on your settings in `simulation_parameters.properties`.
```bash
mvn exec:java -Dexec.mainClass="com.mechalikh.pureedgesim.simulationmanager.Simulation"
```

---

## 4. Where to Change Parameters?

### **Global Settings**
**File**: `PureEdgeSim/settings/simulation_parameters.properties`
| Parameter | Description |
| :--- | :--- |
| `simulation_time` | Total simulation duration (in minutes). |
| `orchestration_architectures` | `CLOUD_ONLY`, `EDGE_ONLY`, `MIST_ONLY`, `ALL`, etc. |
| `orchestration_algorithms` | `TRADE_OFF` or `ROUND_ROBIN`. |
| `min_number_of_edge_devices` | Starting count of devices for the scenario. |
| `display_real_time_charts` | Set to `true` to see live performance graphs. |
| `wan_bandwidth` | Change speed of the Cloud connection (Mbps). |

### **Device Characteristics**
**File**: `PureEdgeSim/settings/edge_devices.xml`
- Change CPU power (MIPS).
- Change battery capacity and energy consumption rates.

### **Application Workloads**
**File**: `PureEdgeSim/settings/applications.xml`
- **Length**: CPU cycles required (MI).
- **Network In/Out**: Data size for transfer.
- **Latency Sensitivity**: If the task "fails" if it takes too long.

### **Infrastructure**
**Files**: `edge_datacenters.xml` and `cloud.xml`
- Define the location (x, y) of Edge servers.
- Define the processing power of the Cloud.

---

## 5. Viewing Results
After the simulation finishes, check the `PureEdgeSim/output/` folder.
- **CSV Results**: `Sequential_simulation.csv` contains all the data you saw in the charts but in a format suitable for Excel or Python analysis.
- **Text Logs**: Detailed step-by-step event logs are saved in the same timestamped folder.
