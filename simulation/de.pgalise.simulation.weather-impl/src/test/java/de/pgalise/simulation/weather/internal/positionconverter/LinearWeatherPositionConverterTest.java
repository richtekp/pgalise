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

import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.weather.parameter.WeatherParameterEnum;
import de.pgalise.simulation.weather.positionconverter.WeatherPositionConverter;
import de.pgalise.simulation.weather.positionconverter.WeatherPositionInitParameter;
import de.pgalise.simulation.weather.util.DateConverter;
import de.pgalise.testutils.TestUtils;
import java.sql.Timestamp;
import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.transaction.UserTransaction;
import org.apache.openejb.api.LocalClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests the linear weather grid converter
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Oct 22, 2012)
 */
@LocalBean
@ManagedBean
@LocalClient
public class LinearWeatherPositionConverterTest {

  @EJB
  private WeatherPositionConverter instance;

  @Resource
  private UserTransaction userTransaction;

  public LinearWeatherPositionConverterTest() {
  }

  @Before
  public void setUp() throws Exception {
    TestUtils.getContext().bind("inject",
      this);
    TestUtils.getContainer().getContext().bind("inject",
      this);
  }

  @Test
  @Ignore //why result 62?
  public void testGetValue() throws Exception {
    userTransaction.begin();
    try {
      /*
       * Test preparations
       */
      Timestamp testTime = DateConverter.convertTimestamp("2012-10-08 12:00:00",
        "YYYY-MM-dd HH:mm:ss");
      JaxRSCoordinate testPosition = new JaxRSCoordinate(1,
        1);
      double value;
      /*
       * Test: Temperature
       */
      JaxRSCoordinate referencePoint = new JaxRSCoordinate(20,
        20);
      Polygon referenceArea = GeoToolsBootstrapping.getGEOMETRY_FACTORY().
        createPolygon(
          new JaxRSCoordinate[]{
            new JaxRSCoordinate(referencePoint.getX() - 1,
              referencePoint.getY() - 1),
            new JaxRSCoordinate(referencePoint.getX() - 1,
              referencePoint.getY()),
            new JaxRSCoordinate(referencePoint.getX(),
              referencePoint.getY()),
            new JaxRSCoordinate(referencePoint.getX(),
              referencePoint.getY() - 1),
            new JaxRSCoordinate(referencePoint.getX() - 1,
              referencePoint.getY() - 1)
          }
        );
      instance.init(new WeatherPositionInitParameter(referenceArea));
      value = instance.getValue(WeatherParameterEnum.TEMPERATURE,
        testTime.getTime(),
        testPosition,
        20.0,
        referenceArea);
      Assert.assertEquals(22,
        value,
        1);

      /*
       * Test: Air pressure
       */
      value = instance.getValue(WeatherParameterEnum.AIR_PRESSURE,
        testTime.getTime(),
        testPosition,
        1034,
        referenceArea);
      Assert.assertEquals(1034,
        value,
        1);

      /*
       * Test: Light intensity
       */
      value = instance.getValue(WeatherParameterEnum.LIGHT_INTENSITY,
        testTime.getTime(),
        testPosition,
        77000.0,
        referenceArea);
      Assert.assertEquals(80000,
        value,
        1000);

      /*
       * Test: Precipitation amount
       */
      value = instance.getValue(WeatherParameterEnum.PRECIPITATION_AMOUNT,
        testTime.getTime(),
        testPosition,
        0.0,
        referenceArea);
      Assert.assertEquals(0,
        value,
        0);

      /*
       * Test: Precipitation amount
       */
      value = instance.getValue(WeatherParameterEnum.PRECIPITATION_AMOUNT,
        testTime.getTime(),
        testPosition,
        2.0,
        referenceArea);
      Assert.assertEquals(1,
        value,
        1);

      /*
       * Test: Radiation
       */
      value = instance.getValue(WeatherParameterEnum.RADIATION,
        testTime.getTime(),
        testPosition,
        427.0,
        referenceArea);
      Assert.assertEquals(415,
        value,
        3);

      /*
       * Test: Relativ humidity
       */
      value = instance.getValue(WeatherParameterEnum.RELATIV_HUMIDITY,
        testTime.getTime(),
        testPosition,
        62.0,
        referenceArea);
      Assert.assertEquals(62,
        value,
        1);

      /*
       * Test: Wind direction
       */
      value = instance.getValue(WeatherParameterEnum.WIND_DIRECTION,
        testTime.getTime(),
        testPosition,
        217.0,
        referenceArea);
      Assert.assertEquals(217,
        value,
        1);

      /*
       * Test: Wind velocity
       */
      value = instance.getValue(WeatherParameterEnum.WIND_VELOCITY,
        testTime.getTime(),
        testPosition,
        2.0,
        referenceArea);
      Assert.assertEquals(3,
        value,
        0.5);

      /*
       * Test: Wind strength
       */
      value = instance.getValue(WeatherParameterEnum.WIND_STRENGTH,
        testTime.getTime(),
        testPosition,
        2.0,
        referenceArea);
      Assert.assertEquals(2,
        value,
        0);
    } finally {
      userTransaction.commit();
    }
  }
}
