# Design Document: Energy Modeling

The Energy Model is critical for evaluating the sustainability and lifetime of Mist/Edge environments where many devices rely on batteries.

## 1. Energy States
Each `ComputingNode` maintains an `EnergyModel` that tracks consumption based on three primary states:
- **Idle State**: Consumption when the device is on but not processing or transmitting.
- **Active State (CPU)**: Consumption while the CPU is executing tasks. This is proportional to CPU utilization.
- **Active State (Network)**: Consumption during data transmission and reception (WiFi or Cellular).

## 2. Calculation Logic
Energy is measured in **Watt-Hours (Wh)**.
- **CPU Energy**: Calculated based on `maxConsumption` and `idleConsumption` parameters in the XML.
  `Energy = (Idle_Cons + Utilization * (Max_Cons - Idle_Cons)) * Time`
- **Network Energy**: Calculated per bit transferred. The XML defines `nanojoules_per_bit` for transmission and reception.

## 3. Battery Management
For mobile devices, the `batteryCapacity` (in Wh) is defined.
1. At each `updateInterval`, the total energy consumed since the last update is calculated.
2. This value is subtracted from the current battery level.
3. If `currentBattery <= 0`, the device is marked as "Dead".
4. Dead devices can no longer generate or process tasks, and any tasks currently running on them fail.

## 4. Network Layer Energy
In version 5.0, energy measurement was expanded to include infrastructure layers:
- **WAN Energy**: Consumption by routers and core network infrastructure.
- **MAN/LAN Energy**: Consumption by regional and local switches.
- **Wireless Energy**: Consumption by WiFi APs and Cellular base stations.

This allows researchers to see the "total energy footprint" of an offloading decision, including the hidden energy cost of the network itself.
