# How to Run PureEdgeSim (Step-by-Step Guide)

PureEdgeSim is equipped with a powerful Command Line Interface (CLI) supporting both **Interactive Menu-based** and **Parameter-based headless** runs.

---

## Prerequisites
Ensure you have the following installed:
1. **Java Development Kit (JDK 8 or higher)**: JDK 21 is recommended.
2. **Apache Maven**: Make sure `mvn` is accessible in your system path.

To verify:
```bash
java -version
mvn -version
```

---

## Step 1: Compilation
Navigate to the root directory of the project and compile it:
```bash
mvn clean compile
```
*(If you wish to compile and skip tests, run `mvn clean install -DskipTests`)*

---

## Step 2: Running the Simulator

### Option A: Interactive Menu-based Mode (Recommended for testing settings)
Launch the simulator without arguments:
```bash
mvn exec:java -Dexec.mainClass="com.mechalikh.pureedgesim.MainApplication"
```

This starts the interactive menu:
1. Choose option `2` (General), `3` (Network), or `4` (Orchestration) to modify settings.
2. Select parameters to change. Prompts show the allowed range or options:
   * *Example*: `Enable parallel simulation (Options: true, false): `
   * *Example*: `Enter registry download strategy (Options: CACHE, CLOUD): `
3. Enter your values. Invalid entries display a validation error.
4. When ready, enter the option to return to the main menu and select `1` to **Start Simulation**.
5. Once the simulation finishes, the console loop returns to the menu, and you can exit by selecting `7`.

### Option B: Parameter-based Headless Mode (Recommended for scripting/servers)
You can directly pass configuration parameters as arguments using `-Dexec.args="..."`. This mode bypasses the interactive menu and executes immediately:
```bash
mvn exec:java -Dexec.mainClass="com.mechalikh.pureedgesim.MainApplication" \
    -Dexec.args="--time 0.05 --devices 200 --parallel false"
```

#### Available Command-Line Arguments:
*   `-c`, `--config <path>`: Properties configuration file path.
*   `-a`, `--apps <path>`: Applications XML file path.
*   `-d`, `--devices <int>`: Total device count to simulate.
*   `-t`, `--time <double>`: Simulation time (in minutes).
*   `-p`, `--parallel <true|false>`: Enable/disable parallel scenario execution.
*   `-s`, `--save-charts <true|false>`: Save final results PNG charts.
*   `-l`, `--save-log <true|false>`: Save simulation iteration logs to a file.
*   `-v`, `--verbose <true|false>`: Enable deep verbose trace logging.
*   `-b`, `--batch <int>`: Task scheduler batch size.
*   `-dc`, `--display-charts <true|false>`: Display real-time Swing Live Charts window.
*   `-rn`, `--realistic-net <true|false>`: Enable realistic network bandwidth model.

---

## Step 3: Analyzing Outputs
At the end of a simulation run:
- Detailed metrics are output directly to the terminal.
- Simulation reports (iteration summaries, CPU, network, energy usage, and tasks logs in CSV/log files) are saved to:
  ```
  PureEdgeSim/output/
  ```
- If `saveCharts` is enabled and parallelism is off, final comparison charts will be generated as PNG images in the output folder.

---

## Troubleshooting & Quirks

### 1. The Real-time Swing Window or PNG Charts did not show/save
*   **Cause**: You have `Parallel Simulation` enabled (`true`). To prevent concurrent thread rendering/writing locks, the framework automatically disables Swing live views and saved output charts when running in parallel.
*   **Solution**: Disable parallel execution (set `Parallel Simulation` to `false`) to view or save charts.

### 2. Typo in boolean inputs
*   **Cause**: Inputs for booleans (`true`/`false`) are strictly validated. Typing `yes`, `no`, `t`, or `f` results in a validation exception rather than silently defaulting to `false`.
*   **Solution**: Type exactly `true` or `false` (case-insensitive).
