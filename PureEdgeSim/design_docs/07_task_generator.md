# Design Document: Workload Generation

The Workload module defines the "what" and "when" of the simulation. It generates the computational demand that stresses the system.

## 1. Applications (`Application.java`)
Applications are templates for tasks. Each application has:
- **`length`**: CPU cycles required to finish (in Million Instructions, MI).
- **`inputSize`**: Data size to be sent to the offloader (MBytes).
- **`outputSize`**: Data size returned as a result (MBytes).
- **`latencySensitivity`**: The maximum allowed time (seconds) for the task to finish. If exceeded, it is a failure.

## 2. Tasks (`Task.java`)
A `Task` is a runtime instance of an `Application`.
- **Generation Time**: When the task is created.
- **Source**: The device that generated it.
- **Destination**: The node that will execute it (decided by Orchestrator).
- **Status**: Can be `SUCCESS`, `FAILURE_LATENCY`, `FAILURE_MOBILITY`, etc.

## 3. The Generator (`DefaultTaskGenerator.java`)
The generator handles the spawning of tasks.
- **Poisson Distribution**: Tasks are typically generated following a Poisson process to simulate realistic, bursty traffic.
- **Batching**: In v5.0+, tasks are scheduled in batches (e.g., 100 at a time) rather than all at once. This drastically reduces the memory usage of the event queue.

## 4. Task Lifecycle
1. **CREATED**: Spawned by the generator.
2. **ORCHESTRATED**: A destination is assigned.
3. **TRANSFER_IN**: Data is moving from source to destination.
4. **EXECUTING**: Running on the destination CPU.
5. **TRANSFER_OUT**: Result is moving back to the source.
6. **FINISHED**: Success or Failure is recorded in `SimLog`.
