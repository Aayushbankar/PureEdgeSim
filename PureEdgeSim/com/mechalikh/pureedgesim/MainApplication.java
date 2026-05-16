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

import java.util.Scanner;
import com.mechalikh.pureedgesim.scenariomanager.SimulationParameters;
import com.mechalikh.pureedgesim.simulationmanager.Simulation;

/**
 * The MainApplication class is the entry point for launching simulations with
 * default settings. The Simulation class is responsible for managing the
 * simulation process and can be customized by the user.
 * 
 * @author Charafeddine Mechalikh
 * @since PureEdgeSim 5.0
 */
public class MainApplication {

	public static void main(final String[] args) {
		Scanner scanner = new Scanner(System.in);
		printHeader();

		System.out.println("Welcome to PureEdgeSim!");
		System.out.println("------------------------------------");
		System.out.println("1. Start Simulation (Default Settings)");
		System.out.println("2. Quick Configure & Start");
		System.out.println("3. Exit");
		System.out.print("\nSelect an option: ");

		String choice = scanner.nextLine();

		if (choice.equals("2")) {
			configureAndStart(scanner);
		} else if (choice.equals("1")) {
			new Simulation().launchSimulation();
		} else {
			System.out.println("Exiting...");
		}
		scanner.close();
	}

	private static void configureAndStart(Scanner scanner) {
		System.out.println("\n--- Quick Configuration ---");
		
		System.out.println("Select Orchestration Architecture:");
		System.out.println("1. Cloud Only");
		System.out.println("2. Edge Only");
		System.out.println("3. Mist Only");
		System.out.println("4. Edge and Cloud (Default)");
		System.out.print("Choice: ");
		String archChoice = scanner.nextLine();
		
		String[] arch;
		switch (archChoice) {
			case "1": arch = new String[]{"CLOUD_ONLY"}; break;
			case "2": arch = new String[]{"EDGE_ONLY"}; break;
			case "3": arch = new String[]{"MIST_ONLY"}; break;
			default: arch = new String[]{"EDGE_AND_CLOUD"}; break;
		}

		System.out.print("Enter number of devices (e.g. 100): ");
		String countInput = scanner.nextLine();
		int tempCount = 100;
		try {
			tempCount = Integer.parseInt(countInput);
		} catch (Exception e) {
			System.out.println("Invalid input, using default (100).");
		}
		final int count = tempCount;

		System.out.println("\nStarting simulation with your settings...\n");
		
		// Use a custom simulation class to ensure our settings aren't overwritten by the properties file
		new Simulation() {
			@Override
			protected boolean checkFiles() {
				boolean result = super.checkFiles();
				if (result) {
					SimulationParameters.orchestrationArchitectures = arch;
					SimulationParameters.minNumberOfEdgeDevices = count;
					SimulationParameters.maxNumberOfEdgeDevices = count;
				}
				return result;
			}
		}.launchSimulation();
	}

	private static void printHeader() {
		System.out.println("**************************************************");
		System.out.println("*                                                *");
		System.out.println("*            PureEdgeSim Visualizer              *");
		System.out.println("*                                                *");
		System.out.println("**************************************************");
	}

}
