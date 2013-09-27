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
 
package de.pgalise.simulation.weather.internal.positionconverter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import java.sql.Timestamp;
import java.text.ParseException;

import org.junit.Assert;
import org.junit.Test;

import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.util.DateConverter;

/**
 * Tests the linear weather grid converter
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 22, 2012)
 */
public class LinearWeatherPositionConverterTest {

	public LinearWeatherPositionConverterTest() {
	}

	@Test
	public void testGetValue() throws ParseException {
		/*
		 * Test preparations
		 */
		Timestamp testTime = DateConverter.convertTimestamp("2012-10-08 12:00:00", "YYYY-MM-dd HH:mm:ss");
		Coordinate testPosition = new Coordinate(1, 1);
		double value;
		/*
		 * Test: Temperature
		 */
		Coordinate referencePoint = new Coordinate(20, 20);
		Geometry referenceArea = GeoToolsBootstrapping.getGEOMETRY_FACTORY().createPolygon(
			new Coordinate[] {
				new Coordinate(referencePoint.x-1, referencePoint.y-1), 
				new Coordinate(referencePoint.x-1, referencePoint.y), 
				new Coordinate(referencePoint.x, referencePoint.y), 
				new Coordinate(referencePoint.x, referencePoint.y-1),
				new Coordinate(referencePoint.x-1, referencePoint.y-1)
			}
		);
		LinearWeatherPositionConverter instance = new LinearWeatherPositionConverter(referenceArea);
		value = instance.getValue(WeatherParameterEnum.TEMPERATURE,
				testTime.getTime(), testPosition, 20.0);
		Assert.assertEquals(22, value, 1);

		/*
		 * Test: Air pressure
		 */
		value = instance.getValue(WeatherParameterEnum.AIR_PRESSURE,
				testTime.getTime(), testPosition, 1034);
		Assert.assertEquals(1034, value, 1);

		/*
		 * Test: Light intensity
		 */
		value = instance.getValue(WeatherParameterEnum.LIGHT_INTENSITY,
				testTime.getTime(), testPosition, 77000.0);
		Assert.assertEquals(80000, value, 1000);

		/*
		 * Test: Precipitation amount
		 */
		value = instance.getValue(WeatherParameterEnum.PRECIPITATION_AMOUNT,
				testTime.getTime(), testPosition, 0.0);
		Assert.assertEquals(0, value, 0);

		/*
		 * Test: Precipitation amount
		 */
		value = instance.getValue(WeatherParameterEnum.PRECIPITATION_AMOUNT,
				testTime.getTime(), testPosition, 2.0);
		Assert.assertEquals(1, value, 1);

		/*
		 * Test: Radiation
		 */
		value = instance.getValue(WeatherParameterEnum.RADIATION,
				testTime.getTime(), testPosition, 427.0);
		Assert.assertEquals(415, value, 3);

		/*
		 * Test: Relativ humidity
		 */
		value = instance.getValue(WeatherParameterEnum.RELATIV_HUMIDITY,
				testTime.getTime(), testPosition, 62.0);
		Assert.assertEquals(62, value, 1);

		/*
		 * Test: Wind direction
		 */
		value = instance.getValue(WeatherParameterEnum.WIND_DIRECTION,
				testTime.getTime(), testPosition, 217.0);
		Assert.assertEquals(217, value, 1);

		/*
		 * Test: Wind velocity
		 */
		value = instance.getValue(WeatherParameterEnum.WIND_VELOCITY,
				testTime.getTime(), testPosition, 2.0);
		Assert.assertEquals(3, value, 0.5);

		/*
		 * Test: Wind strength
		 */
		value = instance.getValue(WeatherParameterEnum.WIND_STRENGTH,
				testTime.getTime(), testPosition, 2.0);
		Assert.assertEquals(2, value, 0);
	}
}
