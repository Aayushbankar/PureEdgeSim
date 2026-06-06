/**
 * PureEdgeSim:  A Simulation Framework for Performance Evaluation of Cloud, Edge and Mist Computing Environments 
 * 
 * This file is part of the PureEdgeSim Project.
 * 
 * PureEdgeSim is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * PureEdgeSim is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with PureEdgeSim. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package com.mechalikh.pureedgesim;

import java.util.Arrays;
import java.util.Scanner;
import com.mechalikh.pureedgesim.scenariomanager.SimulationParameters;
import com.mechalikh.pureedgesim.simulationmanager.Simulation;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

/**
 * The MainApplication class is the entry point for launching simulations.
 * It has been updated to support a powerful CLI that works in both
 * parameter-based and menu-based modes.
 * 
 * @author Charafeddine Mechalikh
 * @since PureEdgeSim 5.0
 */
public class MainApplication {

	// Device overrides
	private static java.util.Map<Integer, java.util.Map<String, String>> deviceOverrides = new java.util.HashMap<>();

	// File Paths
	private static String configPath = null;
	private static String appsPath = null;
	private static String devicesFilePath = null;
	private static String datacentersFilePath = null;
	private static String cloudFilePath = null;
	private static String outputPath = null;

	// General Parameters
	private static Double simulationTime = null;
	private static Integer devicesCount = null;
	private static Integer minDevices = null;
	private static Integer maxDevices = null;
	private static Integer stepSize = null;
	private static String[] archs = null;
	private static String[] algos = null;
	private static Boolean parallelSim = null;
	private static Boolean saveCharts = null;
	private static Boolean saveLog = null;
	private static Boolean verbose = null;
	private static Integer batchSize = null;
	private static Boolean displayCharts = null;

	// Network & Map Parameters
	private static Boolean realisticNet = null;
	private static Double netUpdate = null;
	private static Integer mapLength = null;
	private static Integer mapWidth = null;
	private static Integer d2dRange = null;
	private static Integer apRange = null;
	private static Double wanBw = null;
	private static Double wanLat = null;
	private static Double wifiBw = null;
	private static Double wifiLat = null;
	private static Double cellularBw = null;
	private static Double cellularLat = null;

	// Registry & Orchestration Parameters
	private static Boolean enableRegistry = null;
	private static String registryMode = null;
	private static Boolean enableOrch = null;
	private static String deployOrch = null;

	public static void main(final String[] args) {
		if (args.length > 0) {
			// Parameter-based mode
			boolean parsed = parseArgs(args);
			if (parsed) {
				System.out.println("Launching PureEdgeSim with CLI arguments...");
				startSimulation();
			}
		} else {
			// Menu-based mode
			runInteractiveMenu();
		}
	}

	private static boolean parseArgs(String[] args) {
		try {
			for (int i = 0; i < args.length; i++) {
				String arg = args[i];
				switch (arg) {
					case "-h":
					case "--help":
						printHelp();
						return false;
					case "-c":
					case "--config":
						configPath = args[++i];
						break;
					case "-a":
					case "--apps":
						appsPath = args[++i];
						break;
					case "-d":
					case "--devices-file":
						devicesFilePath = args[++i];
						break;
					case "-e":
					case "--datacenters-file":
						datacentersFilePath = args[++i];
						break;
					case "-l":
					case "--cloud-file":
						cloudFilePath = args[++i];
						break;
					case "-o":
					case "--output":
						outputPath = args[++i];
						break;
					case "-t":
					case "--time":
						simulationTime = Double.parseDouble(args[++i]);
						break;
					case "-n":
					case "--devices":
						devicesCount = Integer.parseInt(args[++i]);
						break;
					case "--min-devices":
						minDevices = Integer.parseInt(args[++i]);
						break;
					case "--max-devices":
						maxDevices = Integer.parseInt(args[++i]);
						break;
					case "--step":
						stepSize = Integer.parseInt(args[++i]);
						break;
					case "--arch":
						archs = args[++i].split(",");
						for (int j = 0; j < archs.length; j++) {
							archs[j] = archs[j].trim().toUpperCase();
						}
						break;
					case "--algo":
						algos = args[++i].split(",");
						for (int j = 0; j < algos.length; j++) {
							algos[j] = algos[j].trim().toUpperCase();
						}
						break;
					case "--parallel":
						parallelSim = Boolean.parseBoolean(args[++i]);
						break;
					case "--save-charts":
						saveCharts = Boolean.parseBoolean(args[++i]);
						break;
					case "--save-log":
						saveLog = Boolean.parseBoolean(args[++i]);
						break;
					case "--verbose":
						verbose = Boolean.parseBoolean(args[++i]);
						break;
					case "--batch":
						batchSize = Integer.parseInt(args[++i]);
						break;
					case "--display-charts":
						displayCharts = Boolean.parseBoolean(args[++i]);
						break;
					case "--realistic-net":
						realisticNet = Boolean.parseBoolean(args[++i]);
						break;
					case "--net-update":
						netUpdate = Double.parseDouble(args[++i]);
						break;
					case "--map-length":
						mapLength = Integer.parseInt(args[++i]);
						break;
					case "--map-width":
						mapWidth = Integer.parseInt(args[++i]);
						break;
					case "--d2d-range":
						d2dRange = Integer.parseInt(args[++i]);
						break;
					case "--ap-range":
						apRange = Integer.parseInt(args[++i]);
						break;
					case "--wan-bw":
						wanBw = Double.parseDouble(args[++i]);
						break;
					case "--wan-lat":
						wanLat = Double.parseDouble(args[++i]);
						break;
					case "--wifi-bw":
						wifiBw = Double.parseDouble(args[++i]);
						break;
					case "--wifi-lat":
						wifiLat = Double.parseDouble(args[++i]);
						break;
					case "--cellular-bw":
						cellularBw = Double.parseDouble(args[++i]);
						break;
					case "--cellular-lat":
						cellularLat = Double.parseDouble(args[++i]);
						break;
					case "--enable-registry":
						enableRegistry = Boolean.parseBoolean(args[++i]);
						break;
					case "--registry-mode":
						registryMode = args[++i].trim();
						break;
					case "--enable-orch":
						enableOrch = Boolean.parseBoolean(args[++i]);
						break;
					case "--deploy-orch":
						deployOrch = args[++i].trim().toUpperCase();
						break;
					case "--device-mobility": {
						int idx = Integer.parseInt(args[++i]);
						String val = args[++i];
						deviceOverrides.computeIfAbsent(idx, k -> new java.util.HashMap<>()).put("mobility", val.toLowerCase());
						break;
					}
					case "--device-speed": {
						int idx = Integer.parseInt(args[++i]);
						String val = args[++i];
						deviceOverrides.computeIfAbsent(idx, k -> new java.util.HashMap<>()).put("speed", val);
						break;
					}
					case "--device-battery": {
						int idx = Integer.parseInt(args[++i]);
						String val = args[++i];
						deviceOverrides.computeIfAbsent(idx, k -> new java.util.HashMap<>()).put("battery", val.toLowerCase());
						break;
					}
					case "--device-tasks": {
						int idx = Integer.parseInt(args[++i]);
						String val = args[++i];
						deviceOverrides.computeIfAbsent(idx, k -> new java.util.HashMap<>()).put("generateTasks", val.toLowerCase());
						break;
					}
					case "--device-percent": {
						int idx = Integer.parseInt(args[++i]);
						String val = args[++i];
						deviceOverrides.computeIfAbsent(idx, k -> new java.util.HashMap<>()).put("percentage", val);
						break;
					}
					case "--device-battery-cap": {
						int idx = Integer.parseInt(args[++i]);
						String val = args[++i];
						deviceOverrides.computeIfAbsent(idx, k -> new java.util.HashMap<>()).put("batteryCapacity", val);
						break;
					}
					case "--device-idle": {
						int idx = Integer.parseInt(args[++i]);
						String val = args[++i];
						deviceOverrides.computeIfAbsent(idx, k -> new java.util.HashMap<>()).put("idleConsumption", val);
						break;
					}
					case "--device-max-consumption": {
						int idx = Integer.parseInt(args[++i]);
						String val = args[++i];
						deviceOverrides.computeIfAbsent(idx, k -> new java.util.HashMap<>()).put("maxConsumption", val);
						break;
					}
					case "--device-mips": {
						int idx = Integer.parseInt(args[++i]);
						String val = args[++i];
						deviceOverrides.computeIfAbsent(idx, k -> new java.util.HashMap<>()).put("mips", val);
						break;
					}
					case "--device-cores": {
						int idx = Integer.parseInt(args[++i]);
						String val = args[++i];
						deviceOverrides.computeIfAbsent(idx, k -> new java.util.HashMap<>()).put("cores", val);
						break;
					}
					case "--device-ram": {
						int idx = Integer.parseInt(args[++i]);
						String val = args[++i];
						deviceOverrides.computeIfAbsent(idx, k -> new java.util.HashMap<>()).put("ram", val);
						break;
					}
					case "--device-storage": {
						int idx = Integer.parseInt(args[++i]);
						String val = args[++i];
						deviceOverrides.computeIfAbsent(idx, k -> new java.util.HashMap<>()).put("storage", val);
						break;
					}
					default:
						System.err.println("Unknown argument: " + arg);
						printHelp();
						return false;
				}
			}
		} catch (Exception e) {
			System.err.println("Error parsing command line arguments: " + e.getMessage());
			printHelp();
			return false;
		}
		return true;
	}

	private static void printHelp() {
		System.out.println("Usage: java MainApplication [options]");
		System.out.println("Options:");
		System.out.println("  -h, --help                        Display this help message");
		System.out.println("  -c, --config <path>               Properties file path override");
		System.out.println("  -a, --apps <path>                 Applications XML file path override");
		System.out.println("  -d, --devices-file <path>         Edge devices XML file path override");
		System.out.println("  -e, --datacenters-file <path>     Edge datacenters XML file path override");
		System.out.println("  -l, --cloud-file <path>           Cloud XML file path override");
		System.out.println("  -o, --output <path>               Output folder path override");
		System.out.println("  -t, --time <minutes>              Simulation duration in minutes");
		System.out.println("  -n, --devices <count>             Sets min and max devices count");
		System.out.println("  --min-devices <count>             Minimum number of edge devices");
		System.out.println("  --max-devices <count>             Maximum number of edge devices");
		System.out.println("  --step <count>                    Device count incrementation step size");
		System.out.println("  --arch <architectures>            Orchestration architectures (comma-separated)");
		System.out.println("  --algo <algorithms>               Orchestration algorithms (comma-separated)");
		System.out.println("  --parallel <true|false>           Enable/disable parallel simulation");
		System.out.println("  --save-charts <true|false>        Enable/disable saving final charts");
		System.out.println("  --save-log <true|false>           Enable/disable saving log file");
		System.out.println("  --verbose <true|false>            Enable/disable deep logging");
		System.out.println("  --batch <size>                    Workload scheduler batch size");
		System.out.println("  --display-charts <true|false>     Enable/disable real-time charts window");
		System.out.println("  --realistic-net <true|false>      Enable/disable realistic network model");
		System.out.println("  --net-update <seconds>            Network model update interval");
		System.out.println("  --map-length <meters>             Simulation map length");
		System.out.println("  --map-width <meters>              Simulation map width");
		System.out.println("  --d2d-range <meters>              Device-to-device WiFi range");
		System.out.println("  --ap-range <meters>               Access point / base station range");
		System.out.println("  --wan-bw <Mbps>                   WAN link bandwidth");
		System.out.println("  --wan-lat <seconds>               WAN link latency");
		System.out.println("  --wifi-bw <Mbps>                  WiFi bandwidth");
		System.out.println("  --wifi-lat <seconds>              WiFi latency");
		System.out.println("  --cellular-bw <Mbps>              Cellular link bandwidth");
		System.out.println("  --cellular-lat <seconds>          Cellular link latency");
		System.out.println("  --enable-registry <true|false>    Enable container registry model");
		System.out.println("  --registry-mode <mode>            Registry cache/download strategy");
		System.out.println("  --enable-orch <true|false>        Enable custom orchestrators");
		System.out.println("  --deploy-orch <location>          Orchestrator deployment strategy");
		System.out.println("  --device-mobility <index> <val>   Mobility (true/false) for device type index");
		System.out.println("  --device-speed <index> <val>      Speed (m/s) for device type index");
		System.out.println("  --device-battery <index> <val>     Battery-powered (true/false) for device type index");
		System.out.println("  --device-tasks <index> <val>      Generate tasks (true/false) for device type index");
		System.out.println("  --device-percent <index> <val>    Percentage of devices (%) for device type index");
		System.out.println("  --device-battery-cap <index> <val> Battery capacity (Wh) for device type index");
		System.out.println("  --device-idle <index> <val>       Idle energy consumption rate (W) for device type index");
		System.out.println("  --device-max-consumption <index>  Max active energy consumption rate (W) for device type index");
		System.out.println("  --device-mips <index> <val>       CPU MIPS (1 GIPS = 1000 MIPS) for device type index");
		System.out.println("  --device-cores <index> <val>      CPU Cores for device type index");
		System.out.println("  --device-ram <index> <val>        RAM (MB) for device type index");
		System.out.println("  --device-storage <index> <val>    Storage (MB) for device type index");
	}

	private static void runInteractiveMenu() {
		Scanner scanner = new Scanner(System.in);
		while (true) {
			printHeader();
			System.out.println("\n===== PureEdgeSim CLI Configuration Menu =====");
			System.out.println("1. Start Simulation with active configuration");
			System.out.println("2. Configure General Settings (time, devices count, logging, etc.)");
			System.out.println("3. Configure Network & Map Settings (bandwidth, latencies, range, etc.)");
			System.out.println("4. Configure Orchestration & Registry Settings");
			System.out.println("5. Configure Edge Devices Parameters (mobility, battery, CPU, RAM, etc.)");
			System.out.println("6. Configure File Paths & Folders");
			System.out.println("7. View Current Configurations");
			System.out.println("8. Exit");
			System.out.print("\nSelect an option (1-8): ");

			String choice = scanner.nextLine().trim();
			if (choice.equals("8")) {
				System.out.println("Exiting. Goodbye!");
				break;
			} else if (choice.equals("1")) {
				System.out.println("\nLaunching simulation...");
				startSimulation();
			} else if (choice.equals("2")) {
				configureGeneralSettings(scanner);
			} else if (choice.equals("3")) {
				configureNetworkMapSettings(scanner);
			} else if (choice.equals("4")) {
				configureOrchRegistrySettings(scanner);
			} else if (choice.equals("5")) {
				configureEdgeDevicesSettings(scanner);
			} else if (choice.equals("6")) {
				configureFilePaths(scanner);
			} else if (choice.equals("7")) {
				printCurrentConfiguration();
				System.out.println("\nPress Enter to return to main menu...");
				scanner.nextLine();
			} else {
				System.out.println("Invalid option. Please try again.");
			}
		}
		scanner.close();
	}

	private static void configureGeneralSettings(Scanner scanner) {
		while (true) {
			System.out.println("\n--- General Settings ---");
			System.out.println("1. Simulation Time (minutes): " + (simulationTime != null ? simulationTime : "Default"));
			System.out.println("2. Devices Count: " + (devicesCount != null ? devicesCount : "Default"));
			System.out.println("3. Min Devices Count: " + (minDevices != null ? minDevices : "Default"));
			System.out.println("4. Max Devices Count: " + (maxDevices != null ? maxDevices : "Default"));
			System.out.println("5. Increment Step Size: " + (stepSize != null ? stepSize : "Default"));
			System.out.println("6. Parallel Simulation: " + (parallelSim != null ? parallelSim : "Default"));
			System.out.println("7. Save Results Charts: " + (saveCharts != null ? saveCharts : "Default"));
			System.out.println("8. Save Log File: " + (saveLog != null ? saveLog : "Default"));
			System.out.println("9. Deep Verbose Log: " + (verbose != null ? verbose : "Default"));
			System.out.println("10. Batch Size: " + (batchSize != null ? batchSize : "Default"));
			System.out.println("11. Display Real-time Swing Charts (Live View): " + (displayCharts != null ? displayCharts : "Default"));
			System.out.println("12. Back to Main Menu");
			System.out.print("\nSelect parameter to edit (1-12): ");
			String choice = scanner.nextLine().trim();
			if (choice.equals("12")) break;

			String promptMsg = "Enter new value: ";
			switch (choice) {
				case "1": promptMsg = "Enter simulation time in minutes (Double > 0, e.g., 60.0): "; break;
				case "2": promptMsg = "Enter devices count (Integer >= 1, e.g., 200): "; break;
				case "3": promptMsg = "Enter minimum devices count (Integer >= 1, e.g., 100): "; break;
				case "4": promptMsg = "Enter maximum devices count (Integer >= 1, e.g., 500): "; break;
				case "5": promptMsg = "Enter devices count increment step size (Integer >= 1, e.g., 50): "; break;
				case "6": promptMsg = "Enable parallel simulation (Options: true, false): "; break;
				case "7": promptMsg = "Save final results charts (Options: true, false): "; break;
				case "8": promptMsg = "Save simulation log file (Options: true, false): "; break;
				case "9": promptMsg = "Enable deep verbose logging (Options: true, false): "; break;
				case "10": promptMsg = "Enter task scheduler batch size (Integer >= 1, e.g., 100): "; break;
				case "11": promptMsg = "Display real-time Swing charts (Options: true, false): "; break;
				default:
					System.out.println("Invalid field.");
					continue;
			}

			System.out.print(promptMsg);
			String val = scanner.nextLine().trim();
			try {
				switch (choice) {
					case "1":
						double timeVal = Double.parseDouble(val);
						if (timeVal <= 0) throw new IllegalArgumentException("Simulation time must be greater than 0.");
						simulationTime = timeVal;
						break;
					case "2":
						int countVal = Integer.parseInt(val);
						if (countVal < 1) throw new IllegalArgumentException("Devices count must be >= 1.");
						devicesCount = countVal;
						break;
					case "3":
						int minVal = Integer.parseInt(val);
						if (minVal < 1) throw new IllegalArgumentException("Min devices count must be >= 1.");
						minDevices = minVal;
						break;
					case "4":
						int maxVal = Integer.parseInt(val);
						if (maxVal < 1) throw new IllegalArgumentException("Max devices count must be >= 1.");
						maxDevices = maxVal;
						break;
					case "5":
						int stepVal = Integer.parseInt(val);
						if (stepVal < 1) throw new IllegalArgumentException("Step size must be >= 1.");
						stepSize = stepVal;
						break;
					case "6": parallelSim = parseStrictBoolean(val); break;
					case "7": saveCharts = parseStrictBoolean(val); break;
					case "8": saveLog = parseStrictBoolean(val); break;
					case "9": verbose = parseStrictBoolean(val); break;
					case "10":
						int batchVal = Integer.parseInt(val);
						if (batchVal < 1) throw new IllegalArgumentException("Batch size must be >= 1.");
						batchSize = batchVal;
						break;
					case "11": displayCharts = parseStrictBoolean(val); break;
				}
			} catch (Exception e) {
				System.out.println("Error editing value: " + e.getMessage());
			}
		}
	}

	private static void configureNetworkMapSettings(Scanner scanner) {
		while (true) {
			System.out.println("\n--- Network & Map Settings ---");
			System.out.println("1. Realistic Network Model: " + (realisticNet != null ? realisticNet : "Default"));
			System.out.println("2. Network Update Interval (s): " + (netUpdate != null ? netUpdate : "Default"));
			System.out.println("3. Map Length (meters): " + (mapLength != null ? mapLength : "Default"));
			System.out.println("4. Map Width (meters): " + (mapWidth != null ? mapWidth : "Default"));
			System.out.println("5. WiFi D2D Range (meters): " + (d2dRange != null ? d2dRange : "Default"));
			System.out.println("6. Access Point Coverage Range (meters): " + (apRange != null ? apRange : "Default"));
			System.out.println("7. WAN Bandwidth (Mbps): " + (wanBw != null ? wanBw : "Default"));
			System.out.println("8. WAN Latency (seconds): " + (wanLat != null ? wanLat : "Default"));
			System.out.println("9. WiFi Bandwidth (Mbps): " + (wifiBw != null ? wifiBw : "Default"));
			System.out.println("10. WiFi Latency (seconds): " + (wifiLat != null ? wifiLat : "Default"));
			System.out.println("11. Cellular Bandwidth (Mbps): " + (cellularBw != null ? cellularBw : "Default"));
			System.out.println("12. Cellular Latency (seconds): " + (cellularLat != null ? cellularLat : "Default"));
			System.out.println("13. Back to Main Menu");
			System.out.print("\nSelect parameter to edit (1-13): ");
			String choice = scanner.nextLine().trim();
			if (choice.equals("13")) break;

			String promptMsg = "Enter new value: ";
			switch (choice) {
				case "1": promptMsg = "Enable realistic network model (Options: true, false): "; break;
				case "2": promptMsg = "Enter network model update interval in seconds (Double >= 0.1, e.g., 1.0): "; break;
				case "3": promptMsg = "Enter simulation map length in meters (Integer > 0, e.g., 200): "; break;
				case "4": promptMsg = "Enter simulation map width in meters (Integer > 0, e.g., 200): "; break;
				case "5": promptMsg = "Enter device-to-device WiFi range in meters (Integer >= 0, e.g., 10): "; break;
				case "6": promptMsg = "Enter access point coverage range in meters (Integer >= 0, e.g., 200): "; break;
				case "7": promptMsg = "Enter WAN link bandwidth in Mbps (Double > 0, e.g., 20.0): "; break;
				case "8": promptMsg = "Enter WAN link latency in seconds (Double >= 0, e.g., 0.06): "; break;
				case "9": promptMsg = "Enter local WiFi bandwidth in Mbps (Double > 0, e.g., 1300.0): "; break;
				case "10": promptMsg = "Enter local WiFi latency in seconds (Double >= 0, e.g., 0.005): "; break;
				case "11": promptMsg = "Enter cellular link bandwidth in Mbps (Double > 0, e.g., 100.0): "; break;
				case "12": promptMsg = "Enter cellular link latency in seconds (Double >= 0, e.g., 0.03): "; break;
				default:
					System.out.println("Invalid field.");
					continue;
			}

			System.out.print(promptMsg);
			String val = scanner.nextLine().trim();
			try {
				switch (choice) {
					case "1": realisticNet = parseStrictBoolean(val); break;
					case "2":
						double netVal = Double.parseDouble(val);
						if (netVal < 0.1) throw new IllegalArgumentException("Network update interval must be >= 0.1 seconds.");
						netUpdate = netVal;
						break;
					case "3":
						int lenVal = Integer.parseInt(val);
						if (lenVal <= 0) throw new IllegalArgumentException("Map length must be > 0.");
						mapLength = lenVal;
						break;
					case "4":
						int widVal = Integer.parseInt(val);
						if (widVal <= 0) throw new IllegalArgumentException("Map width must be > 0.");
						mapWidth = widVal;
						break;
					case "5":
						int d2dVal = Integer.parseInt(val);
						if (d2dVal < 0) throw new IllegalArgumentException("D2D range must be >= 0.");
						d2dRange = d2dVal;
						break;
					case "6":
						int apVal = Integer.parseInt(val);
						if (apVal < 0) throw new IllegalArgumentException("AP coverage range must be >= 0.");
						apRange = apVal;
						break;
					case "7":
						double wanBwVal = Double.parseDouble(val);
						if (wanBwVal <= 0) throw new IllegalArgumentException("WAN bandwidth must be > 0.");
						wanBw = wanBwVal;
						break;
					case "8":
						double wanLatVal = Double.parseDouble(val);
						if (wanLatVal < 0) throw new IllegalArgumentException("WAN latency must be >= 0.");
						wanLat = wanLatVal;
						break;
					case "9":
						double wifiBwVal = Double.parseDouble(val);
						if (wifiBwVal <= 0) throw new IllegalArgumentException("WiFi bandwidth must be > 0.");
						wifiBw = wifiBwVal;
						break;
					case "10":
						double wifiLatVal = Double.parseDouble(val);
						if (wifiLatVal < 0) throw new IllegalArgumentException("WiFi latency must be >= 0.");
						wifiLat = wifiLatVal;
						break;
					case "11":
						double cellularBwVal = Double.parseDouble(val);
						if (cellularBwVal <= 0) throw new IllegalArgumentException("Cellular bandwidth must be > 0.");
						cellularBw = cellularBwVal;
						break;
					case "12":
						double cellularLatVal = Double.parseDouble(val);
						if (cellularLatVal < 0) throw new IllegalArgumentException("Cellular latency must be >= 0.");
						cellularLat = cellularLatVal;
						break;
				}
			} catch (Exception e) {
				System.out.println("Error editing value: " + e.getMessage());
			}
		}
	}

	private static void configureOrchRegistrySettings(Scanner scanner) {
		while (true) {
			System.out.println("\n--- Orchestration & Registry Settings ---");
			System.out.println("1. Orchestration Architectures: " + (archs != null ? Arrays.toString(archs) : "Default"));
			System.out.println("2. Orchestration Algorithms: " + (algos != null ? Arrays.toString(algos) : "Default"));
			System.out.println("3. Enable Container Registry: " + (enableRegistry != null ? enableRegistry : "Default"));
			System.out.println("4. Registry Download Mode: " + (registryMode != null ? registryMode : "Default"));
			System.out.println("5. Enable Custom Orchestrators: " + (enableOrch != null ? enableOrch : "Default"));
			System.out.println("6. Deploy Orchestrator Location: " + (deployOrch != null ? deployOrch : "Default"));
			System.out.println("7. Back to Main Menu");
			System.out.print("\nSelect parameter to edit (1-7): ");
			String choice = scanner.nextLine().trim();
			if (choice.equals("7")) break;

			String promptMsg = "Enter new value: ";
			switch (choice) {
				case "1": promptMsg = "Enter orchestration architectures (comma-separated, Options: CLOUD_ONLY, EDGE_ONLY, MIST_ONLY, MIST_AND_CLOUD, EDGE_AND_CLOUD, MIST_AND_EDGE, ALL): "; break;
				case "2": promptMsg = "Enter orchestration algorithms (comma-separated, Options: TRADE_OFF, ROUND_ROBIN): "; break;
				case "3": promptMsg = "Enable container registry model (Options: true, false): "; break;
				case "4": promptMsg = "Enter registry download strategy (Options: CACHE, CLOUD): "; break;
				case "5": promptMsg = "Enable custom orchestrators (Options: true, false): "; break;
				case "6": promptMsg = "Enter orchestrator deployment location (Options: CLOUD, EDGE, MIST): "; break;
				default:
					System.out.println("Invalid field.");
					continue;
			}

			System.out.print(promptMsg);
			String val = scanner.nextLine().trim();
			try {
				switch (choice) {
					case "1":
						String[] tempArchs = val.split(",");
						for (int j = 0; j < tempArchs.length; j++) {
							String trimmed = tempArchs[j].trim().toUpperCase();
							if (!trimmed.equals("CLOUD_ONLY") && !trimmed.equals("EDGE_ONLY") && !trimmed.equals("MIST_ONLY")
								&& !trimmed.equals("MIST_AND_CLOUD") && !trimmed.equals("EDGE_AND_CLOUD") && !trimmed.equals("MIST_AND_EDGE")
								&& !trimmed.equals("ALL")) {
								throw new IllegalArgumentException("Invalid architecture: " + trimmed);
							}
							tempArchs[j] = trimmed;
						}
						archs = tempArchs;
						break;
					case "2":
						String[] tempAlgos = val.split(",");
						for (int j = 0; j < tempAlgos.length; j++) {
							String trimmed = tempAlgos[j].trim().toUpperCase();
							if (!trimmed.equals("TRADE_OFF") && !trimmed.equals("ROUND_ROBIN")) {
								throw new IllegalArgumentException("Invalid algorithm: " + trimmed);
							}
							tempAlgos[j] = trimmed;
						}
						algos = tempAlgos;
						break;
					case "3": enableRegistry = parseStrictBoolean(val); break;
					case "4":
						String regMode = val.trim().toUpperCase();
						if (!regMode.equals("CACHE") && !regMode.equals("CLOUD")) {
							throw new IllegalArgumentException("Invalid download strategy. Must be CACHE or CLOUD.");
						}
						registryMode = regMode;
						break;
					case "5": enableOrch = parseStrictBoolean(val); break;
					case "6":
						String depLoc = val.trim().toUpperCase();
						if (!depLoc.equals("CLOUD") && !depLoc.equals("EDGE") && !depLoc.equals("MIST")) {
							throw new IllegalArgumentException("Invalid orchestrator deployment location. Must be CLOUD, EDGE, or MIST.");
						}
						deployOrch = depLoc;
						break;
				}
			} catch (Exception e) {
				System.out.println("Error editing value: " + e.getMessage());
			}
		}
	}

	private static void configureFilePaths(Scanner scanner) {
		while (true) {
			System.out.println("\n--- File Paths & Folders ---");
			System.out.println("1. Configuration file properties path: " + (configPath != null ? configPath : "Default"));
			System.out.println("2. Applications XML path: " + (appsPath != null ? appsPath : "Default"));
			System.out.println("3. Edge devices XML path: " + (devicesFilePath != null ? devicesFilePath : "Default"));
			System.out.println("4. Edge datacenters XML path: " + (datacentersFilePath != null ? datacentersFilePath : "Default"));
			System.out.println("5. Cloud datacenters XML path: " + (cloudFilePath != null ? cloudFilePath : "Default"));
			System.out.println("6. Output directory path: " + (outputPath != null ? outputPath : "Default"));
			System.out.println("7. Back to Main Menu");
			System.out.print("\nSelect parameter to edit (1-7): ");
			String choice = scanner.nextLine().trim();
			if (choice.equals("7")) break;

			System.out.print("Enter new path: ");
			String val = scanner.nextLine().trim();
			switch (choice) {
				case "1": configPath = val; break;
				case "2": appsPath = val; break;
				case "3": devicesFilePath = val; break;
				case "4": datacentersFilePath = val; break;
				case "5": cloudFilePath = val; break;
				case "6": outputPath = val; break;
				default: System.out.println("Invalid field.");
			}
		}
	}

	private static void printCurrentConfiguration() {
		System.out.println("\n--- Current Custom Configuration ---");
		System.out.println("Config File Path: " + (configPath != null ? configPath : "Default"));
		System.out.println("Apps XML Path: " + (appsPath != null ? appsPath : "Default"));
		System.out.println("Devices XML Path: " + (devicesFilePath != null ? devicesFilePath : "Default"));
		System.out.println("Datacenters XML Path: " + (datacentersFilePath != null ? datacentersFilePath : "Default"));
		System.out.println("Cloud XML Path: " + (cloudFilePath != null ? cloudFilePath : "Default"));
		System.out.println("Output Folder: " + (outputPath != null ? outputPath : "Default"));
		System.out.println("Simulation Duration (m): " + (simulationTime != null ? simulationTime : "Default"));
		System.out.println("Devices Count: " + (devicesCount != null ? devicesCount : "Default"));
		System.out.println("Min Devices: " + (minDevices != null ? minDevices : "Default"));
		System.out.println("Max Devices: " + (maxDevices != null ? maxDevices : "Default"));
		System.out.println("Step Size: " + (stepSize != null ? stepSize : "Default"));
		System.out.println("Parallel Sim: " + (parallelSim != null ? parallelSim : "Default"));
		System.out.println("Save Charts: " + (saveCharts != null ? saveCharts : "Default"));
		System.out.println("Save Log File: " + (saveLog != null ? saveLog : "Default"));
		System.out.println("Deep Logging: " + (verbose != null ? verbose : "Default"));
		System.out.println("Display Charts (Live View): " + (displayCharts != null ? displayCharts : "Default"));
		System.out.println("Realistic Network Model: " + (realisticNet != null ? realisticNet : "Default"));
		System.out.println("WAN Bandwidth (Mbps): " + (wanBw != null ? wanBw : "Default"));
		System.out.println("WAN Latency (s): " + (wanLat != null ? wanLat : "Default"));
		System.out.println("WiFi Bandwidth (Mbps): " + (wifiBw != null ? wifiBw : "Default"));
		System.out.println("WiFi Latency (s): " + (wifiLat != null ? wifiLat : "Default"));
		System.out.println("Cellular Bandwidth (Mbps): " + (cellularBw != null ? cellularBw : "Default"));
		System.out.println("Cellular Latency (s): " + (cellularLat != null ? cellularLat : "Default"));
		System.out.println("Orchestration Architectures: " + (archs != null ? Arrays.toString(archs) : "Default"));
		System.out.println("Orchestration Algorithms: " + (algos != null ? Arrays.toString(algos) : "Default"));
		System.out.println("Enable Registry: " + (enableRegistry != null ? enableRegistry : "Default"));
		System.out.println("Registry Mode: " + (registryMode != null ? registryMode : "Default"));
		System.out.println("Enable Orchestrators: " + (enableOrch != null ? enableOrch : "Default"));
		System.out.println("Deploy Orchestrator: " + (deployOrch != null ? deployOrch : "Default"));
	}

	private static void startSimulation() {
		new Simulation() {
			@Override
			protected boolean checkFiles() {
				// Apply custom file paths before super.checkFiles()
				if (configPath != null) SimulationParameters.simulationParametersFile = configPath;
				if (appsPath != null) SimulationParameters.applicationFile = appsPath;
				if (devicesFilePath != null) SimulationParameters.edgeDevicesFile = devicesFilePath;
				if (datacentersFilePath != null) SimulationParameters.edgeDataCentersFile = datacentersFilePath;
				if (cloudFilePath != null) SimulationParameters.cloudDataCentersFile = cloudFilePath;
				if (outputPath != null) SimulationParameters.outputFolder = outputPath;

				applyDeviceOverridesAndSave();

				boolean result = super.checkFiles();

				if (result) {
					// Apply all custom overrides here, after properties file has been parsed
					if (simulationTime != null) {
						SimulationParameters.simulationDuration = simulationTime * 60.0;
					}
					if (devicesCount != null) {
						SimulationParameters.minNumberOfEdgeDevices = devicesCount;
						SimulationParameters.maxNumberOfEdgeDevices = devicesCount;
					}
					if (minDevices != null) {
						SimulationParameters.minNumberOfEdgeDevices = minDevices;
					}
					if (maxDevices != null) {
						SimulationParameters.maxNumberOfEdgeDevices = maxDevices;
					}
					if (stepSize != null) {
						SimulationParameters.edgeDevicesIncrementationStepSize = stepSize;
					}
					if (archs != null) {
						SimulationParameters.orchestrationArchitectures = archs;
					}
					if (algos != null) {
						SimulationParameters.orchestrationAlgorithms = algos;
					}
					if (parallelSim != null) {
						SimulationParameters.parallelism_enabled = parallelSim;
					}
					if (saveCharts != null) {
						SimulationParameters.saveCharts = saveCharts;
					}
					if (saveLog != null) {
						SimulationParameters.saveLog = saveLog;
					}
					if (verbose != null) {
						SimulationParameters.deepLoggingEnabled = verbose;
					}
					if (batchSize != null) {
						SimulationParameters.batchSize = batchSize;
					}
					if (displayCharts != null) {
						SimulationParameters.displayRealTimeCharts = displayCharts;
					}
					if (realisticNet != null) {
						SimulationParameters.realisticNetworkModel = realisticNet;
					}
					if (netUpdate != null) {
						SimulationParameters.networkUpdateInterval = netUpdate;
					}
					if (mapLength != null) {
						SimulationParameters.simulationMapLength = mapLength;
					}
					if (mapWidth != null) {
						SimulationParameters.simulationMapWidth = mapWidth;
					}
					if (d2dRange != null) {
						SimulationParameters.edgeDevicesRange = d2dRange;
					}
					if (apRange != null) {
						SimulationParameters.edgeDataCentersRange = apRange;
					}
					if (wanBw != null) {
						SimulationParameters.wanBandwidthBitsPerSecond = wanBw * 1000000.0;
					}
					if (wanLat != null) {
						SimulationParameters.wanLatency = wanLat;
					}
					if (wifiBw != null) {
						SimulationParameters.wifiBandwidthBitsPerSecond = wifiBw * 1000000.0;
					}
					if (wifiLat != null) {
						SimulationParameters.wifiLatency = wifiLat;
					}
					if (cellularBw != null) {
						SimulationParameters.cellularBandwidthBitsPerSecond = cellularBw * 1000000.0;
					}
					if (cellularLat != null) {
						SimulationParameters.cellularLatency = cellularLat;
					}
					if (enableRegistry != null) {
						SimulationParameters.enableRegistry = enableRegistry;
					}
					if (registryMode != null) {
						SimulationParameters.registryMode = registryMode;
					}
					if (enableOrch != null) {
						SimulationParameters.enableOrchestrators = enableOrch;
					}
					if (deployOrch != null) {
						SimulationParameters.deployOrchestrators = deployOrch;
					}
				}
				return result;
			}
		}.launchSimulation();
	}

	private static void printHeader() {
		System.out.println("**************************************************");
		System.out.println("*                                                *");
		System.out.println("*            PureEdgeSim CLI Engine             *");
		System.out.println("*                                                *");
		System.out.println("**************************************************");
	}

	private static boolean parseStrictBoolean(String val) {
		String lower = val.toLowerCase();
		if (lower.equals("true")) return true;
		if (lower.equals("false")) return false;
		throw new IllegalArgumentException("Invalid boolean value. Must be 'true' or 'false'.");
	}

	private static void applyDeviceOverridesAndSave() {
		if (deviceOverrides.isEmpty()) {
			return;
		}
		try {
			File inputFile = new File(devicesFilePath != null ? devicesFilePath : SimulationParameters.edgeDevicesFile);
			if (!inputFile.exists()) {
				System.err.println("Warning: Edge devices file not found at " + inputFile.getAbsolutePath() + ". Could not apply overrides.");
				return;
			}
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("device");
			for (Integer idx : deviceOverrides.keySet()) {
				if (idx >= 0 && idx < nList.getLength()) {
					Element deviceElement = (Element) nList.item(idx);
					java.util.Map<String, String> overrides = deviceOverrides.get(idx);
					for (java.util.Map.Entry<String, String> entry : overrides.entrySet()) {
						String param = entry.getKey();
						String value = entry.getValue();
						NodeList paramList = deviceElement.getElementsByTagName(param);
						if (paramList.getLength() > 0) {
							paramList.item(0).setTextContent(value);
						} else {
							Element newEl = doc.createElement(param);
							newEl.setTextContent(value);
							deviceElement.appendChild(newEl);
						}
					}
				} else {
					System.err.println("Warning: Device index " + idx + " is out of bounds (0 to " + (nList.getLength() - 1) + "). Override ignored.");
				}
			}

			// Save back to a custom file
			String customPath = "PureEdgeSim/settings/edge_devices_custom.xml";
			File customFile = new File(customPath);
			customFile.getParentFile().mkdirs();
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(customFile);
			transformer.transform(source, result);

			// Update path to point to custom XML file
			SimulationParameters.edgeDevicesFile = customPath;
			System.out.println("Applied " + deviceOverrides.size() + " device overrides. Saved to " + customPath);
		} catch (Exception e) {
			System.err.println("Error applying device overrides: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static String getTagValueOrOverride(int devIdx, String tagName, Element devEl) {
		if (deviceOverrides.containsKey(devIdx) && deviceOverrides.get(devIdx).containsKey(tagName)) {
			return deviceOverrides.get(devIdx).get(tagName) + " (modified)";
		}
		NodeList nl = devEl.getElementsByTagName(tagName);
		if (nl.getLength() > 0) {
			return nl.item(0).getTextContent();
		}
		return "N/A";
	}

	private static void configureEdgeDevicesSettings(Scanner scanner) {
		while (true) {
			System.out.println("\n--- Edge Devices Parameters ---");
			File inputFile = new File(devicesFilePath != null ? devicesFilePath : SimulationParameters.edgeDevicesFile);
			if (!inputFile.exists()) {
				System.out.println("Active edge devices XML file not found: " + inputFile.getAbsolutePath());
				break;
			}
			NodeList nList = null;
			try {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(inputFile);
				doc.getDocumentElement().normalize();
				nList = doc.getElementsByTagName("device");
			} catch (Exception e) {
				System.out.println("Error parsing edge devices XML: " + e.getMessage());
				break;
			}

			if (nList == null || nList.getLength() == 0) {
				System.out.println("No devices defined in the XML file.");
				break;
			}

			System.out.println("Available Device Types in XML:");
			for (int i = 0; i < nList.getLength(); i++) {
				Element devEl = (Element) nList.item(i);
				String percentage = getTagValueOrOverride(i, "percentage", devEl);
				String mips = getTagValueOrOverride(i, "mips", devEl);
				String battery = getTagValueOrOverride(i, "battery", devEl);
				String cores = getTagValueOrOverride(i, "cores", devEl);
				System.out.println(String.format("  [%d] Device Type %d (Percentage: %s%%, CPU: %s MIPS, Cores: %s, Battery-powered: %s)", 
					i, i + 1, percentage, mips, cores, battery));
			}
			System.out.println("  [" + nList.getLength() + "] Back to Main Menu");
			System.out.print("\nSelect a device type to configure (0-" + nList.getLength() + "): ");
			String devChoiceStr = scanner.nextLine().trim();
			int devIdx;
			try {
				devIdx = Integer.parseInt(devChoiceStr);
			} catch (NumberFormatException e) {
				System.out.println("Invalid index.");
				continue;
			}

			if (devIdx == nList.getLength()) {
				break;
			}
			if (devIdx < 0 || devIdx > nList.getLength()) {
				System.out.println("Index out of bounds.");
				continue;
			}

			Element devEl = (Element) nList.item(devIdx);
			configureSpecificDevice(scanner, devIdx, devEl);
		}
	}

	private static void configureSpecificDevice(Scanner scanner, int devIdx, Element devEl) {
		String[] fields = {"mobility", "speed", "battery", "generateTasks", "percentage", "batteryCapacity", 
						   "idleConsumption", "maxConsumption", "mips", "cores", "ram", "storage"};
		String[] fieldLabels = {
			"Mobility (true/false)",
			"Speed (m/s)",
			"Battery-powered (true/false)",
			"Generate Tasks (true/false)",
			"Percentage of devices (%)",
			"Battery capacity (Wh)",
			"Idle energy consumption rate (W)",
			"Max active energy consumption rate (W)",
			"CPU MIPS (1 GIPS = 1000 MIPS)",
			"CPU Cores",
			"RAM (MB)",
			"Storage (MB)"
		};

		while (true) {
			System.out.println("\n--- Configure Device Type " + (devIdx + 1) + " ---");
			for (int i = 0; i < fields.length; i++) {
				String val = getTagValueOrOverride(devIdx, fields[i], devEl);
				System.out.println(String.format("  %d. %s: %s", i + 1, fieldLabels[i], val));
			}
			System.out.println("  " + (fields.length + 1) + ". Back to Devices Menu");
			System.out.print("\nSelect parameter to edit (1-" + (fields.length + 1) + "): ");
			String choiceStr = scanner.nextLine().trim();
			int choice;
			try {
				choice = Integer.parseInt(choiceStr);
			} catch (NumberFormatException e) {
				System.out.println("Invalid selection.");
				continue;
			}

			if (choice == fields.length + 1) {
				break;
			}
			if (choice < 1 || choice > fields.length) {
				System.out.println("Selection out of bounds.");
				continue;
			}

			String fieldName = fields[choice - 1];
			String fieldLabel = fieldLabels[choice - 1];
			System.out.print("Enter new value for " + fieldLabel + ": ");
			String newVal = scanner.nextLine().trim();

			try {
				if (fieldName.equals("mobility") || fieldName.equals("battery") || fieldName.equals("generateTasks")) {
					if (!newVal.equalsIgnoreCase("true") && !newVal.equalsIgnoreCase("false")) {
						throw new IllegalArgumentException("Must be true or false.");
					}
					newVal = newVal.toLowerCase();
				} else if (fieldName.equals("cores")) {
					int val = Integer.parseInt(newVal);
					if (val < 1) throw new IllegalArgumentException("Cores must be >= 1.");
				} else {
					double val = Double.parseDouble(newVal);
					if (val < 0) throw new IllegalArgumentException("Value must be >= 0.");
				}

				deviceOverrides.computeIfAbsent(devIdx, k -> new java.util.HashMap<>()).put(fieldName, newVal);
				System.out.println("Modified parameter stored successfully.");
			} catch (Exception e) {
				System.out.println("Invalid value: " + e.getMessage());
			}
		}
	}

}
