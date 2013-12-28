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

import de.pgalise.simulation.weather.internal.modifier.events.RainDayEvent;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;

/**
 * Test of RainDayEvent
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 11, 2012)
 */
public class RainDayEventChart extends AbstractChartTest {

	/**
	 * Test timestamp
	 */
	public static long testTimestamp;

	/**
	 * Test value
	 */
	public static float testValue = 20.0f;

	/**
	 * Test duration
	 */
	public static long testDuration = 4;

	/**
	 * For tests
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		// Start
		Calendar cal = new GregorianCalendar();
		cal.set(2011, 5, 11, 0, 0, 0);
		long startTimestamp = cal.getTimeInMillis();

		// End
		cal.set(2011, 5, 12, 0, 0, 0);
		long endTimestamp = cal.getTimeInMillis();

		// Test time
		cal.set(2011, 5, 11, 18, 0, 0);
		RainDayEventChart.testTimestamp = cal.getTimeInMillis();

		WeatherParameterEnum parameter = RainDayEvent.CHANGE_PARAMETER;
		String title = "Test of RainDayEvent";

		// Test class
		AbstractChartTest chart = new RainDayEventChart(startTimestamp, endTimestamp, parameter);

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
	public RainDayEventChart(long startdate, long enddate, WeatherParameterEnum parameter) throws Exception {
		super(startdate, enddate, parameter);
	}

	@Override
	protected TimeSeries getDecoratorTimeSerie() throws Exception {
		// Deploy strategy
		this.getService().deployStrategy(new RainDayEvent(this.getRandom().getSeed(RainDayEventChart.class.toString()),
				RainDayEventChart.testTimestamp, this.getProps(), RainDayEventChart.testValue,
				RainDayEventChart.testDuration, this.getLoader()));

		// Create timeserie for JFreeChart
		return TimeSeriesChart.getTimeSerie(AbstractChartTest.DECORATOR_TITLE, this.getService().getReferenceValues(), this.getParameter());
	}

	@Override
	protected TimeSeries getReferenceTimeSerie() {
		// Create timeserie for JFreeChart
		return TimeSeriesChart.getTimeSerie(AbstractChartTest.REFERENCE_TITLE, this.getService().getReferenceValues(), this.getParameter());
	}
}
