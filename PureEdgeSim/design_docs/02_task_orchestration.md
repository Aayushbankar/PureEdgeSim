# Design Document: Task Orchestration

Task Orchestration is the process of deciding which computing node (Mist device, Edge server, or Cloud) should execute a specific task. This is the primary module where researchers implement their algorithms.

## 1. The Orchestrator Lifecycle
When a device generates a task, it doesn't execute it immediately. Instead, it "asks" an Orchestrator for a placement decision.

1. **Submission**: Task is passed to `Orchestrator.orchestrate(task)`.
2. **Filtering**: The orchestrator checks which layers are enabled (e.g., `MIST_AND_EDGE`).
3. **Algorithm Execution**: The `findComputingNode()` method is called to pick the best destination.
4. **Assignment**: The task is assigned to the selected `ComputingNode`.

## 2. Default Algorithms (`DefaultOrchestrator.java`)
PureEdgeSim provides two baseline algorithms:

### 2.1 Round Robin
Distributes tasks sequentially across all available nodes. It keeps a `historyMap` to track how many tasks were assigned to each node and picks the one with the lowest count.

### 2.2 Trade-Off
A more sophisticated baseline that weighs multiple factors:
- **Node Type**: Assigns different "priority weights" to Cloud (1.8), Edge Server (1.2), and Mist Device (1.3).
- **Workload**: Uses the `historyMap` to avoid overloading a single node.
- **CPU Speed**: Considers the `mipsPerCore` of the target node.
- **Task Length**: The total instruction count of the task.

## 3. Extending the Orchestrator
To implement a custom algorithm (e.g., based on Fuzzy Logic or Reinforcement Learning):
1. Create a new class extending `Orchestrator`.
2. Implement the `findComputingNode` method.
3. In your simulation setup, call `sim.setCustomEdgeOrchestrator(YourClass.class)`.

## 4. Offloading Constraints
The base class `Orchestrator` provides `offloadingIsPossible()`, which validates:
- **Connectivity**: Is the device within range of the target node?
- **Availability**: Is the target node still alive (not drained of battery)?
- **Resources**: Does the node have the required RAM/CPU/Storage?
