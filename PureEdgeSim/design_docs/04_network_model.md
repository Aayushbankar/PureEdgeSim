# Design Document: Network Modeling

PureEdgeSim uses an analytical network model to simulate data transfers between computing nodes. This approach prioritizes simulation speed and scalability while maintaining high accuracy for Edge/IoT scenarios.

## 1. Network Link Types
The simulator creates a graph of `NetworkLink` objects connecting the nodes:
- **WiFi**: Used for local device-to-device (D2D) and device-to-access-point (D2AP) links.
- **Cellular (4G/5G)**: Used for devices connected to the WAN through base stations.
- **MAN (Metropolitan Area Network)**: High-speed links between Edge Data Centers.
- **WAN (Wide Area Network)**: High-latency links connecting the local infrastructure to the Cloud.

## 2. Bandwidth Sharing
Instead of simulating packet-by-packet (which is slow), PureEdgeSim uses **Fair Bandwidth Sharing**:
1. When multiple transfers occur on the same link, they share the total bandwidth.
2. If the link capacity is 100Mbps and 4 tasks are transferring data, each gets 25Mbps.
3. This is calculated dynamically: every time a transfer starts or ends, the remaining time for all active transfers on that link is recalculated.

## 3. Data Transfer Lifecycle
A task offloading involves two transfers:
- **Request**: Transferring the input data (MBytes) from Source to Destination.
- **Response**: Transferring the result data from Destination back to Source.

The `TransferProgress` class tracks the status of these transfers.

## 4. Latency Calculation
Total latency for a task is calculated as:
`Total Latency = Transfer_Delay + Queuing_Delay + Execution_Time`

Where `Transfer_Delay = Data_Size / (Total_Bandwidth / Active_Transfers) + Link_Latency`.

## 5. Extensibility
The `DefaultNetworkModel` can be overridden by users wanting to implement custom routing protocols or complex congestion control algorithms by extending the `NetworkModel` class.
