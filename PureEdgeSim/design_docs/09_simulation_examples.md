# Design Document: Simulation Examples

The `examples/` package is the best starting point for researchers to understand how to use and extend PureEdgeSim. Each example demonstrates a specific feature or customization.

## 1. Overview of Examples

### Example 1: Basic Simulation
- **Goal**: Run a simple simulation with default models.
- **Key Learning**: How to use the `Simulation` class and `launchSimulation()`.

### Example 2: Custom Mobility
- **Goal**: Replace the default Random Waypoint model.
- **Key Learning**: How to use `sim.setCustomMobilityModel()`.

### Example 3: Custom Energy Model
- **Goal**: Implement a specialized battery drain logic.
- **Key Learning**: How to use `sim.setCustomEnergyModel()`.

### Example 6: Cooperative Caching & Clustering
- **Goal**: Simulate a complex scenario where devices share data replicas.
- **Key Learning**: Advanced networking and peer-to-peer (P2P) interactions.

### Example 8: Fuzzy Logic Orchestration
- **Goal**: Use an AI-based decision engine for task offloading.
- **Key Learning**: Integrating external libraries (jFuzzyLogic) and custom orchestrators.

## 2. How to Create Your Own Scenario
To build a new experiment:
1. Create a new Java class.
2. Instantiate `Simulation sim = new Simulation();`.
3. (Optional) Set your custom models using the `setCustom...` methods.
4. Call `sim.launchSimulation();`.
5. Run the class via your IDE or `mvn exec:java -Dexec.mainClass="examples.YourClassName"`.

## 3. Best Practices
- **Do not modify the core**: Avoid changing files in `com.mechalikh.pureedgesim.simulationengine` etc. Instead, extend them in your own package.
- **Use the Scenario Manager**: Change parameters via the XML/Properties files rather than hardcoding values.
- **Check the Logs**: Use `deepLog` in your custom classes to see exactly why a decision was made.
