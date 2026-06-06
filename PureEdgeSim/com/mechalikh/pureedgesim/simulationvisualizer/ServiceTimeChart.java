/**
 *     PureEdgeSim:  A Simulation Framework for Performance Evaluation of Cloud, Edge and Mist Computing Environments 
 *
 *     This file is part of PureEdgeSim Project.
 *
 *     PureEdgeSim is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     PureEdgeSim is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with PureEdgeSim. If not, see <http://www.gnu.org/licenses/>.
 *     
 *     @author Antigravity
 **/
package com.mechalikh.pureedgesim.simulationvisualizer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.markers.SeriesMarkers;

import com.mechalikh.pureedgesim.simulationmanager.SimulationManager;

/**
 * This class represents a chart of average task service time over the course of simulation.
 * It extends the Chart class.
 */
public class ServiceTimeChart extends Chart {

	protected List<Double> serviceTimeList = new ArrayList<>();

	/**
	 * Constructs a ServiceTimeChart object.
	 *
	 * @param title             the title of the chart
	 * @param xAxisTitle        the title of the x-axis
	 * @param yAxisTitle        the title of the y-axis
	 * @param simulationManager the SimulationManager object
	 */
	public ServiceTimeChart(String title, String xAxisTitle, String yAxisTitle, SimulationManager simulationManager) {
		super(title, xAxisTitle, yAxisTitle, simulationManager);
		((XYChart) getChart()).getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Line);
		updateSize(0.0, null, null, null);
	}

	/**
	 * Updates the chart with the latest average service time.
	 */
	@Override
	public void update() {
		int currentClock = (int) simulationManager.getSimulation().clockInMinutes();
		if (currentClock != clock) {
			clock = currentClock;
			double averageServiceTime = simulationManager.getSimulationLogger().getAverageServiceTime();
			double[] time = new double[clock + 1];
			for (int i = 0; i <= clock; i++) {
				time[i] = i;
			}
			serviceTimeList.add(averageServiceTime);
			updateSeries(getChart(), "Service time", time, toArray(serviceTimeList), SeriesMarkers.NONE, Color.MAGENTA);
		}
	}
}
