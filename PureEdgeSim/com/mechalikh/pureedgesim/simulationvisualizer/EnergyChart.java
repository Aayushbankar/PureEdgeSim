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
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.Styler.LegendPosition;
import com.mechalikh.pureedgesim.datacentersmanager.ComputingNode;
import com.mechalikh.pureedgesim.scenariomanager.SimulationParameters;
import com.mechalikh.pureedgesim.simulationmanager.SimulationManager;

/**
 * Represents a histogram chart showing aggregated energy consumption for the
 * different computing tiers (Mist, Edge, Cloud).
 */
public class EnergyChart extends Chart {

	/**
	 * Constructs an EnergyChart object.
	 *
	 * @param title             the title of the chart
	 * @param xAxisTitle        the title of the x-axis
	 * @param yAxisTitle        the title of the y-axis
	 * @param simulationManager the simulation manager to get data from
	 */
	public EnergyChart(String title, String xAxisTitle, String yAxisTitle, SimulationManager simulationManager) {
		super(title, xAxisTitle, yAxisTitle, simulationManager);
		
		this.chart = new CategoryChartBuilder()
				.width(width)
				.height(height)
				.title(title)
				.xAxisTitle(xAxisTitle)
				.yAxisTitle(yAxisTitle)
				.theme(ChartTheme.Matlab)
				.build();

		CategoryChart categoryChart = (CategoryChart) this.chart;
		
		// Configure modern design aesthetics
		categoryChart.getStyler().setLegendPosition(LegendPosition.OutsideE);
		categoryChart.getStyler().setLegendVisible(true);
		categoryChart.getStyler().setAvailableSpaceFill(0.75); // Grouped bar gap spacing
		categoryChart.getStyler().setHasAnnotations(true);     // Draw exact values on top of bars
		
		// Clean modern fonts
		Font titleFont = new Font("SansSerif", Font.BOLD, 13);
		Font labelFont = new Font("SansSerif", Font.PLAIN, 11);
		categoryChart.getStyler().setChartTitleFont(titleFont);
		categoryChart.getStyler().setLegendFont(labelFont);
		categoryChart.getStyler().setAxisTitleFont(titleFont);
		categoryChart.getStyler().setAxisTickLabelsFont(labelFont);
		categoryChart.getStyler().setAnnotationsFont(labelFont);
	}

	/**
	 * Updates the chart with the latest aggregated energy consumption data.
	 */
	@Override
	public void update() {
		CategoryChart categoryChart = (CategoryChart) chart;
		List<ComputingNode> allNodes = computingNodesGenerator.getAllNodesList();

		double mistTotal = 0.0, mistCpu = 0.0, mistNet = 0.0;
		double edgeTotal = 0.0, edgeCpu = 0.0, edgeNet = 0.0;
		double cloudTotal = 0.0, cloudCpu = 0.0, cloudNet = 0.0;

		for (ComputingNode node : allNodes) {
			double totalEnergy = 0.0;
			double cpuEnergy = 0.0;
			double networkEnergy = 0.0;

			if (node.getEnergyModel() != null) {
				totalEnergy = node.getEnergyModel().getTotalEnergyConsumption();
				cpuEnergy = node.getEnergyModel().getCpuEnergyConsumption();
				networkEnergy = totalEnergy - cpuEnergy;
			}

			if (node.getType() == SimulationParameters.TYPES.EDGE_DEVICE) {
				mistTotal += totalEnergy;
				mistCpu += cpuEnergy;
				mistNet += networkEnergy;
			} else if (node.getType() == SimulationParameters.TYPES.EDGE_DATACENTER) {
				edgeTotal += totalEnergy;
				edgeCpu += cpuEnergy;
				edgeNet += networkEnergy;
			} else if (node.getType() == SimulationParameters.TYPES.CLOUD) {
				cloudTotal += totalEnergy;
				cloudCpu += cpuEnergy;
				cloudNet += networkEnergy;
			}
		}

		List<String> categories = List.of("Mist", "Edge", "Cloud");
		List<Double> totalValues = List.of(mistTotal, edgeTotal, cloudTotal);
		List<Double> cpuValues = List.of(mistCpu, edgeCpu, cloudCpu);
		List<Double> networkValues = List.of(mistNet, edgeNet, cloudNet);

		// Use modern soft premium colors: Golden/Yellow, soft Coral Red, Cobalt Blue
		updateCategorySeries(categoryChart, "Total Energy (Wh)", categories, totalValues, new Color(241, 196, 15));
		updateCategorySeries(categoryChart, "CPU Energy (Wh)", categories, cpuValues, new Color(231, 76, 60));
		updateCategorySeries(categoryChart, "Network Energy (Wh)", categories, networkValues, new Color(41, 128, 185));
	}

	private void updateCategorySeries(CategoryChart chart, String name, List<String> categories, List<Double> values, Color color) {
		if (chart.getSeriesMap().containsKey(name)) {
			chart.updateCategorySeries(name, categories, values, null);
		} else {
			org.knowm.xchart.CategorySeries series = chart.addSeries(name, categories, values);
			series.setFillColor(color);
		}
	}
}
