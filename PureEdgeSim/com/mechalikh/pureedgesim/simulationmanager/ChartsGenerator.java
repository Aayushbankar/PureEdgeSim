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
 *     @author Charafeddine Mechalikh
 **/
package com.mechalikh.pureedgesim.simulationmanager;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.CategorySeries;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler.ChartTheme;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

import com.mechalikh.pureedgesim.scenariomanager.SimulationParameters;

public class ChartsGenerator {

	protected List<String[]> records = new ArrayList<>(50);
	protected String fileName;
	protected String folder;
	protected List<String> energyChartsList = List.of("Energy consumption of computing nodes (Wh)",
			"Average energy consumption (Wh/Computing node)", "Cloud energy consumption (Wh)",
			"Average Cloud energy consumption (Wh/Data center)", "Edge energy consumption (Wh)",
			"Average Edge energy consumption (Wh/Data center)", "Mist energy consumption (Wh)",
			"Average Mist energy consumption (Wh/Device)", "WAN energy consumption (Wh)", "MAN energy consumption (Wh)",
			"LAN energy consumption (Wh)", "WiFi energy consumption (Wh)", "LTE energy consumption (Wh)",
			"Ethernet energy consumption (Wh)");

	protected List<String> cpuChartsList = List.of("Average CPU usage (%)", "Average CPU usage (Cloud) (%)",
			"Average CPU usage (Edge) (%)", "Average CPU usage (Mist) (%)");

	protected List<String> tasksChartsList = List.of("Tasks successfully executed", "Tasks failed (delay)",
			"Tasks failed (device dead)", "Tasks failed (mobility)", "Tasks not generated due to the death of devices",
			"Total tasks executed (Cloud)", "Tasks successfully executed (Cloud)", "Total tasks executed (Edge)",
			"Tasks successfully executed (Edge)", "Total tasks executed (Mist)", "Tasks successfully executed (Mist)");
	protected List<String> delaysChartsList = List.of("Average waiting time (s)", "Average execution delay (s)",
			"Average service time (s)");

	protected List<String> networkChartsList = List.of("Network usage (s)", "Wan usage (s)", "Containers wan usage (s)",
			"Containers lan usage (s)");

	public ChartsGenerator(String fileName) {
		this.fileName = fileName;
		loadFile();
	}

	protected void loadFile() {
		try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = file.readLine()) != null) {
				records.add(line.split(","));
			} 
		} catch (Exception e) {
			SimLog.println("%s - Problem reading CSV file.",this.getClass().getSimpleName());
		}
	}

	protected int getColumnIndex(String name) {
		if (records.isEmpty() || records.get(0).length == 0) return -1;
		for (int j = 0; j < records.get(0).length; j++) {
			if (records.get(0)[j].trim().equals(name.trim())) {
				return j;
			}
		}
		return -1;
	}

	public void displayChart(String y_series, String y_series_label, String folder) {
		this.folder = folder;
		// Create the charts filtered by algorithms (byAlgorithm = true), in order to
		// compare the orchestration algorithms
		generateChart("Edge devices count", y_series, y_series_label, true);
		// Create charts that are filtered by architectures (byAlgorithm = false)
		generateChart("Edge devices count", y_series, y_series_label, false);

	}

	public void generateChart(String x_series, String y_series, String y_series_label, boolean byAlgorithms) {
		XYChart chart;
		for (int i = 0; i < (byAlgorithms ? SimulationParameters.orchestrationAlgorithms.length
				: SimulationParameters.orchestrationArchitectures.length); i++) {
			chart = initChart(x_series, y_series, y_series_label, getArray(byAlgorithms)[i]);
			for (int j = 0; j < (byAlgorithms ? SimulationParameters.orchestrationArchitectures.length
					: SimulationParameters.orchestrationAlgorithms.length); j++) {
				double[] xData = toArray(
						getColumn(x_series, SimulationParameters.orchestrationArchitectures[(byAlgorithms ? j : i)],
								SimulationParameters.orchestrationAlgorithms[(byAlgorithms ? i : j)]));
				double[] yData = toArray(
						getColumn(y_series, SimulationParameters.orchestrationArchitectures[(byAlgorithms ? j : i)],
								SimulationParameters.orchestrationAlgorithms[(byAlgorithms ? i : j)]));

				XYSeries series = chart.addSeries(getArray(!byAlgorithms)[j], xData, yData);
				series.setMarker(SeriesMarkers.CIRCLE); // Marker type: circle,rectangle, diamond..
				series.setLineStyle(new BasicStroke());
			}
			// Save the chart
			saveBitmap(chart, (byAlgorithms ? "Architectures" : "Algorithms") + folder + "/",
					y_series + "__" + getArray(byAlgorithms)[i]);
		}
	}

	protected String[] getArray(boolean byAlgorithms) {
		return (byAlgorithms ? SimulationParameters.orchestrationAlgorithms
				: SimulationParameters.orchestrationArchitectures);
	}

	protected XYChart initChart(String x_series, String y_series, String y_series_label, String title) {
		XYChart chart = new XYChartBuilder().height(400).width(600).theme(ChartTheme.Matlab).xAxisTitle(x_series)
				.yAxisTitle(y_series_label).build();
		chart.setTitle(y_series + " (" + title + ")");
		chart.getStyler().setLegendVisible(true);
		return chart;
	}

	protected void saveBitmap(XYChart chart, String folder, String name) {
		try {
			File file = new File(new File(fileName).getParent() + "/Final results/" + folder);
			file.mkdirs();
			BitmapEncoder.saveBitmapWithDPI(chart, file.getPath() + "/" + name.replace("/", " per "), BitmapFormat.PNG,
					300);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Overload of saveBitmap that accepts CategoryChart (or any xchart Chart). */
	protected void saveBitmap(org.knowm.xchart.internal.chartpart.Chart<?, ?> chart, String folder, String name) {
		try {
			File file = new File(new File(fileName).getParent() + "/Final results/" + folder);
			file.mkdirs();
			BitmapEncoder.saveBitmapWithDPI(chart, file.getPath() + "/" + name.replace("/", " per "), BitmapFormat.PNG,
					300);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected List<Double> getColumn(String name, String orch, String alg) {
		List<Double> list = new ArrayList<>();
		int column = getColumnIndex(name);
		if (column < 0) return list;
		for (int line = 1; line < records.size(); line++) {
			String[] row = records.get(line);
			if (row.length <= Math.max(1, column)) continue;
			if (row[0].trim().equals(orch.trim()) && row[1].trim().equals(alg.trim())) {
				list.add(Double.parseDouble(row[column]));
			}
		}
		return list;
	}

	/**
	 * Returns the edge-device-count values (as category label strings) for a given
	 * architecture + algorithm pair, in the order they appear in the CSV.
	 */
	protected List<String> getDeviceCountsAsStrings(String orch, String alg) {
		List<String> list = new ArrayList<>();
		int col = getColumnIndex("Edge devices count");
		if (col < 0) return list;
		for (int line = 1; line < records.size(); line++) {
			String[] row = records.get(line);
			if (row.length <= Math.max(1, col)) continue;
			if (row[0].trim().equals(orch.trim()) && row[1].trim().equals(alg.trim())) {
				list.add(row[col].trim());
			}
		}
		return list;
	}

	protected double[] toArray(List<Double> list) {
		double[] results = new double[list.size()];
		for (int i = 0; i < list.size(); i++)
			results[i] = list.get(i);
		return results;
	}

	public void generate() {
		generateTasksCharts();
		generateNetworkCharts();
		generateCpuCharts();
		generateEnergyCharts();
		generateServiceTimeChart();
	}

	protected void generateEnergyCharts() {
		for (String value : energyChartsList)
			displayChart(value, "Consumed energy (Wh)", "/Energy");
	}

	protected void generateCpuCharts() {
		for (String value : cpuChartsList)
			displayChart(value, "CPU utilization (%)", "/CPU Utilization");
	}

	protected void generateNetworkCharts() {
		for (String value : networkChartsList)
			displayChart(value, "Utilization (s)", "/Network");
	}

	protected void generateTasksCharts() {
		for (String value : tasksChartsList)
			displayChart(value, "Number of tasks", "/Tasks");

		for (String value : delaysChartsList)
			displayChart(value, "Time (s)", "/Tasks");
	}
	/**
	 * Generates one stacked bar chart per orchestration architecture, matching
	 * the reference paper style (Fig. 15a):
	 *   - X-axis  : Edge devices count
	 *   - Y-axis  : Service time (s)
	 *   - Stacked : Bottom = average execution delay, Top = average waiting time
	 *   - Values annotated on each segment
	 *   - One chart saved per architecture (e.g. Cloud-Only, Fog-and-Cloud, …)
	 */
	protected void generateServiceTimeChart() {
		// Colour palette matching the reference image (dark grey + light grey)
		Color execColor = new Color(80, 80, 80);    // dark grey  — execution time
		Color waitColor = new Color(190, 190, 190);  // light grey — waiting time

		Font titleFont  = new Font("SansSerif", Font.BOLD,  13);
		Font labelFont  = new Font("SansSerif", Font.PLAIN, 11);
		Font annotFont  = new Font("SansSerif", Font.PLAIN,  9);

		// One chart per architecture  (columns of the reference figure)
		for (String arch : SimulationParameters.orchestrationArchitectures) {

			String displayTitle;
			if (arch.equalsIgnoreCase("CLOUD_ONLY")) {
				displayTitle = "Cloud-Only";
			} else if (arch.equalsIgnoreCase("EDGE_ONLY")) {
				displayTitle = "Fog-and-Cloud";
			} else if (arch.equalsIgnoreCase("MIST_ONLY")) {
				displayTitle = "Proposed";
			} else {
				displayTitle = arch;
			}

			CategoryChart chart = new CategoryChartBuilder()
					.width(520).height(430)
					.title(displayTitle)
					.xAxisTitle("Edge devices count")
					.yAxisTitle("Service time (s)")
					.theme(ChartTheme.Matlab)
					.build();

			// Style
			chart.getStyler().setStacked(true);              // stacked bars
			chart.getStyler().setHasAnnotations(true);       // value labels on bars
			chart.getStyler().setAnnotationsFont(annotFont);
			chart.getStyler().setLegendPosition(LegendPosition.OutsideS);
			chart.getStyler().setLegendVisible(true);
			chart.getStyler().setAvailableSpaceFill(0.55);   // bar-to-gap ratio
			chart.getStyler().setChartTitleFont(titleFont);
			chart.getStyler().setAxisTitleFont(labelFont);
			chart.getStyler().setAxisTickLabelsFont(labelFont);
			chart.getStyler().setLegendFont(labelFont);
			chart.getStyler().setChartBackgroundColor(Color.WHITE);
			chart.getStyler().setPlotBackgroundColor(Color.WHITE);
			chart.getStyler().setPlotBorderVisible(true);

			boolean anyData = false;

			// One series-pair per algorithm  (grouped side-by-side within each device count)
			for (String alg : SimulationParameters.orchestrationAlgorithms) {

				List<String> deviceCounts = getDeviceCountsAsStrings(arch, alg);
				if (deviceCounts.isEmpty()) continue;

				List<Double> execData = getColumn("Average execution delay (s)", arch, alg);
				List<Double> waitData = getColumn("Average waiting time (s)",   arch, alg);

				// Waiting-time segment (bottom, light)
				CategorySeries waitSeries = chart.addSeries(
						alg + " - Waiting Time", deviceCounts, waitData);
				waitSeries.setFillColor(waitColor);

				// Execution-time segment (top, dark)
				CategorySeries execSeries = chart.addSeries(
						alg + " - Execution Time", deviceCounts, execData);
				execSeries.setFillColor(execColor);

				anyData = true;
			}

			if (anyData) {
				saveBitmap(chart, "Service Time/",
						"service_time__" + arch);
			}
		}
	}

}
