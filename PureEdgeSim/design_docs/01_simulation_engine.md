# Design Document: Simulation Engine

The Simulation Engine is the "heart" of PureEdgeSim. It implements a **Discrete Event Simulation (DES)** paradigm, which allows for high-performance simulation of long-duration scenarios with thousands of devices.

## 1. Core Concepts
Unlike time-stepped simulators (which update every millisecond), a DES jumps directly between events. If nothing happens for 10 minutes, the simulation clock jumps 10 minutes instantly.

### 1.1 The Event (`Event.java`)
An `Event` represents an action scheduled to occur at a specific virtual time.
- **Attributes**:
  - `time`: The virtual timestamp of the event.
  - `type`: An integer ID representing the event type (e.g., `TASK_GENERATION`, `MOBILITY_UPDATE`).
  - `destination`: The `SimEntity` that will handle the event.
  - `data`: Optional payload (e.g., the `Task` object).

### 1.2 The Engine (`PureEdgeSim.java`)
The main orchestrator of the event loop.
- **Clock**: A `double` value representing current simulation time in seconds.
- **Entity Management**: Tracks all active entities (Orchestrators, Data Centers, etc.).
- **The Loop**:
  1. Pull the earliest event from the `FutureQueue`.
  2. Set `clock = event.time`.
  3. Call `destination.processEvent(event)`.
  4. Repeat until the queue is empty or `simulationDuration` is reached.

### 1.3 The Future Queue (`FutureQueue.java`)
A specialized priority queue optimized for time-sorted events. It ensures that events are processed in strict chronological order, which is vital for causality.

## 2. Entity Model (`SimEntity.java`)
Every active component in the simulation (e.g., an Orchestrator or a Computing Node) must extend `SimEntity`.
- **`send()`**: Allows an entity to schedule a future event for itself or another entity.
- **`processEvent()`**: The abstract method where the entity logic resides.

## 3. Performance Rationale
In version 5.0, the dependency on CloudSim Plus was removed. The new custom engine is:
- **Lightweight**: No unnecessary "Cloud" overhead (brokers, VM migrations) for Mist scenarios.
- **Thread-Safe**: Designed to allow multiple `SimulationThread` instances to run independent scenarios in parallel across CPU cores.
