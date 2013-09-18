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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.jfree.data.time.TimeSeries;

import de.pgalise.simulation.weather.internal.modifier.simulationevents.ReferenceCityModifier;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;

/**
 * Test of ReferenceCityDecorator
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 10, 2012)
 */
public class ReferenceCityChart extends AbstractChartTest {

	/**
	 * For tests
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// Start
		Calendar cal = new GregorianCalendar();
		cal.set(2012, 8, 20, 0, 0, 0);
		long startTimestamp = cal.getTimeInMillis();

		// End
		cal.set(2012, 8, 21, 0, 0, 0);
		long endTimestamp = cal.getTimeInMillis();
		WeatherParameterEnum parameter = WeatherParameterEnum.TEMPERATURE;
		// WeatherParameterEnum parameter = WeatherParameterEnum.PERCEIVED_TEMPERATURE;
		// WeatherParameterEnum parameter = WeatherParameterEnum.RELATIV_HUMIDITY;
		// WeatherParameterEnum parameter = WeatherParameterEnum.WIND_VELOCITY;
		String title = "Test of ReferenceCityDecorator";

		// Test class
		AbstractChartTest chart = new ReferenceCityChart(startTimestamp, endTimestamp, parameter);

		// Add timeseries to list
		final List<TimeSeries> series = new ArrayList<>();
		series.add(chart.getReferenceTimeSerie());
		series.add(chart.getDecoratorTimeSerie());

		// Show chart
		TimeSeriesChart demo = new TimeSeriesChart(title, startTimestamp, parameter, series);
		demo.showChart();
	}

	/**
	 * Constructor
	 * 
	 * @param startdate
	 *            Date as Timestamp
	 * @param enddate
	 *            Date as Timestamp
	 * @param parameter
	 *            WeatherParameterEnum
	 */
	public ReferenceCityChart(long startdate, long enddate, WeatherParameterEnum parameter) throws Exception {
		super(startdate, enddate, parameter);
	}

	@Override
	protected TimeSeries getDecoratorTimeSerie() throws Exception {
		// Deploy strategy
		this.getService().deployStrategy(new ReferenceCityModifier(this.getRandom().getSeed(ReferenceCityChart.class.toString()), this.getProps(), this.getLoader()));

		// Create timeserie for JFreeChart
		return TimeSeriesChart.getTimeSerie(AbstractChartTest.DECORATOR_TITLE, this.getService().getReferenceValues(), this.getParameter());
	}

	@Override
	protected TimeSeries getReferenceTimeSerie() {
		// Create timeserie for JFreeChart
		return TimeSeriesChart.getTimeSerie(AbstractChartTest.REFERENCE_TITLE, this.getService().getReferenceValues(), this.getParameter());
	}
}
