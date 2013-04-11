/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
 
package de.pgalise.simulation.weather.internal.modifier.jfreechart;

import java.awt.Color;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import de.pgalise.simulation.weather.dataloader.Weather;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;

/**
 * Testcase Helper with JFreeChart
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 13, 2012)
 */
public class TimeSeriesChart extends ApplicationFrame {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -8910462755504573764L;

	/**
	 * Create Dataset for Chart
	 * 
	 * @param list
	 *            List of timeseries object
	 * @return Dataset
	 */
	public static XYDataset createDataset(List<TimeSeries> list) {
		TimeSeriesCollection dataset = new TimeSeriesCollection();

		// Add serie
		for (TimeSeries serie : list) {
			dataset.addSeries(serie);
		}

		return dataset;
	}

	/**
	 * Returns a list with weather objects
	 * 
	 * @param map
	 *            Map with weather objects
	 * @return list with weather objects
	 */
	public static List<Weather> getList(WeatherMap map) {
		List<Weather> list = new ArrayList<Weather>();
		Vector<Long> times = new Vector<Long>(map.keySet());

		Collections.sort(times);
		for (Long time : times) {
			list.add(map.get(time));

		}
		return list;
	}

	/**
	 * Returns a timeserie
	 * 
	 * @param title
	 *            Title
	 * @param list
	 *            List of weather objects
	 * @param key
	 *            WeatherParameterEnum
	 * @return TimeSerie
	 */
	public static TimeSeries getTimeSerie(String title, List<Weather> list, WeatherParameterEnum key) {
		TimeSeries s1 = new TimeSeries(title);

		// Add weather
		for (Weather weather : list) {
			try {
				Timestamp time = new Timestamp(weather.getTimestamp() + 3600000);
				s1.add(new Millisecond(time), TimeSeriesChart.getValue(key, weather));
			} catch (Exception ex) {
				// Do nothing
			}
		}

		return s1;
	}

	/**
	 * Returns a timeserie
	 * 
	 * @param title
	 *            Title
	 * @param map
	 *            List of weather objects
	 * @param key
	 *            WeatherParameterEnum
	 * @return TimeSerie
	 */
	public static TimeSeries getTimeSerie(String title, WeatherMap map, WeatherParameterEnum key) {
		return TimeSeriesChart.getTimeSerie(title, TimeSeriesChart.getList(map), key);
	}

	/**
	 * Returns the value of weather parameter
	 * 
	 * @param key
	 *            WeatherParameterEnum
	 * @param weather
	 *            Weather object
	 * @return Value to the key
	 */
	private static double getValue(WeatherParameterEnum key, Weather weather) {
		double value;

		switch (key) {
			case AIR_PRESSURE:
				value = weather.getAirPressure();
				break;
			case LIGHT_INTENSITY:
				value = weather.getLightIntensity();
				break;
			case PERCEIVED_TEMPERATURE:
				value = weather.getPerceivedTemperature();
				break;
			case TEMPERATURE:
				value = weather.getTemperature();
				break;
			case PRECIPITATION_AMOUNT:
				value = weather.getPrecipitationAmount();
				break;
			case RADIATION:
				value = weather.getRadiation();
				break;
			case RELATIV_HUMIDITY:
				value = weather.getRelativHumidity();
				break;
			case WIND_DIRECTION:
				value = weather.getWindDirection();
				break;
			case WIND_VELOCITY:
				value = weather.getWindVelocity();
				break;
			default:
				value = -1;
				break;
		}

		return value;
	}

	/**
	 * Returns the value unit of weather parameter
	 * 
	 * @param key
	 *            WeatherParameterEnum
	 * @return Unit of the Paramter
	 */
	private static String getValueUnit(WeatherParameterEnum key) {
		switch (key) {
			case AIR_PRESSURE:
				return "hPa";
			case LIGHT_INTENSITY:
				return "Lux";
			case PERCEIVED_TEMPERATURE:
			case TEMPERATURE:
				return "C";
			case PRECIPITATION_AMOUNT:
				return "mm";
			case RADIATION:
				return "W/qm";
			case RELATIV_HUMIDITY:
				return "%";
			case WIND_DIRECTION:
				return "degree";
			case WIND_VELOCITY:
				return "m/s";
			default:
				return "?";
		}
	}

	/**
	 * Chart
	 */
	private JFreeChart chart;

	/**
	 * Date as String
	 */
	private String date;

	/**
	 * Title of the chart
	 */
	private String title;

	/**
	 * Unit of the values
	 */
	private String unit;

	/**
	 * Constructor
	 * 
	 * @param title
	 *            Title
	 * @param date
	 *            Date as Timestamp
	 * @param unit
	 *            Unit of the values
	 * @param list
	 *            List with TimeSeries
	 */
	public TimeSeriesChart(String title, long date, String unit, List<TimeSeries> series) {
		this(title, date, unit, TimeSeriesChart.createDataset(series));
	}

	/**
	 * Constructor
	 * 
	 * @param title
	 *            Title
	 * @param date
	 *            Date as Timestamp
	 * @param unit
	 *            Unit of the values
	 * @param dataset
	 *            XYDataset
	 */
	public TimeSeriesChart(String title, long date, String unit, XYDataset dataset) {
		super(title); // For frame

		// Change attributes
		this.title = title;
		this.date = new Date(date).toString();
		this.unit = unit;

		// Create chart
		this.setChart(dataset);
		this.initChart();
	}

	/**
	 * Constructor
	 * 
	 * @param title
	 *            Title
	 * @param date
	 *            Date as Timestamp
	 * @param parameter
	 *            WeatherParameterEnum
	 * @param series
	 *            List with TimeSeries
	 */
	public TimeSeriesChart(String title, long date, WeatherParameterEnum parameter, List<TimeSeries> series) {
		this(title, date, TimeSeriesChart.getValueUnit(parameter), series);
	}

	/**
	 * Repaint the chart
	 * 
	 * @param title
	 *            Title
	 * @param date
	 *            Date as String
	 * @param unit
	 *            Unit of the values
	 * @param dataset
	 *            XYDataset
	 */
	public void repaintChart(String title, String date, String unit, XYDataset dataset) {
		// Change attributes
		this.title = title;
		this.date = date;
		this.unit = unit;

		// Change chart
		this.setChart(dataset);
		this.repaint();
	}

	/**
	 * Show this chart
	 */
	public void showChart() {
		this.pack();
		RefineryUtilities.centerFrameOnScreen(this);
		this.setVisible(true);
	}

	/**
	 * Init chart
	 */
	private void initChart() {
		final ChartPanel chartPanel = new ChartPanel(this.chart, false);
		chartPanel.setPreferredSize(new java.awt.Dimension(800, 500));
		chartPanel.setMouseZoomable(true, false);

		this.setContentPane(chartPanel);
	}

	/**
	 * Set chart
	 * 
	 * @param dataset
	 *            Dataset
	 */
	private void setChart(XYDataset dataset) {

		// Create new chart
		JFreeChart chartNew = ChartFactory.createTimeSeriesChart(this.title + " (" + this.date + ")", // title
				"Time (hh:mm:ss)", // x-axis label
				"Value (" + this.unit + ")", // y-axis label
				dataset, // data
				true, // create legend?
				true, // generate tooltips?
				false // generate URLs?
				);

		// Change Plot
		chartNew.setBackgroundPaint(Color.white);
		chartNew.setPadding(new RectangleInsets(0.0, 0.0, 0.0, 0.0));

		XYPlot plot = (XYPlot) chartNew.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);

		plot.setDomainCrosshairLockedOnData(true);
		plot.setRangeCrosshairLockedOnData(true);

		// Show points for values
		// XYItemRenderer r = plot.getRenderer();
		// if (r instanceof XYLineAndShapeRenderer) {
		// XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
		// renderer.setShapesVisible(true);
		// renderer.setShapesFilled(true);
		// }

		// Change date axis
		DateAxis dateAxis = (DateAxis) plot.getDomainAxis();
		dateAxis.setDateFormatOverride(new SimpleDateFormat("HH:mm:ss")); // "dd.MM.yyyy HH:mm:ss"
		dateAxis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
		dateAxis.setVerticalTickLabels(true);
		dateAxis.setUpperMargin(0.00);
		dateAxis.setLowerMargin(0.00);

		// Set chart
		this.chart = chartNew;
	}
}
