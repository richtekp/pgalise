/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.internal.dataloader;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.entity.ServiceDataCurrent;
import de.pgalise.simulation.weather.entity.ServiceDataForecast;
import de.pgalise.testutils.TestUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import org.junit.Assert;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author richter
 */
@ManagedBean
@LocalClient
public class DBBackedServiceWeatherLoaderTest {

  @EJB
  private WeatherLoader instance;
  @EJB
  private IdGenerator idGenerator;

  public DBBackedServiceWeatherLoaderTest() {
  }

  @Before
  public void setUpClass() throws NamingException {
    TestUtils.getContainer().getContext().bind("inject",
      this);
  }

  /**
   * Test of checkStationDataForDay method, of class
   * DBBackedServiceWeatherLoader.
   */
  @Test
  public void testCheckStationDataForDay() throws Exception {
    long timestamp = 0L;
    boolean expResult = false;
    try {
      instance.checkStationDataForDay(timestamp);
      Assert.fail();
    } catch (UnsupportedOperationException expected) {
    }
  }

  /**
   * Test of loadCurrentServiceWeatherData method, of class
   * DBBackedServiceWeatherLoader.
   */
  @Test
  public void testLoadCurrentServiceWeatherData() throws Exception {
    long timestamp = 0L;
    City city = TestUtils.createDefaultTestCityInstance(idGenerator);
    ServiceDataCurrent result = instance.
      loadCurrentServiceWeatherData(timestamp,
        city);
    assertNotNull(result);
  }

  /**
   * Test of loadForecastServiceWeatherData method, of class
   * DBBackedServiceWeatherLoader.
   */
  @Test
  public void testLoadForecastServiceWeatherData() throws Exception {
    long timestamp = 0L;
    City city = TestUtils.createDefaultTestCityInstance(idGenerator);
    ServiceDataForecast result = instance.loadForecastServiceWeatherData(
      timestamp,
      city);
    assertNotNull(result);
  }

  /**
   * Test of loadStationData method, of class DBBackedServiceWeatherLoader.
   */
  @Test
  public void testLoadStationData() throws Exception {
    long timestamp = 0L;
    try {
      instance.loadStationData(timestamp);
      Assert.fail();
    } catch (UnsupportedOperationException expected) {
    }
  }

  /**
   * Test of setLoadOption method, of class DBBackedServiceWeatherLoader.
   */
  @Test
  public void testSetLoadOption() throws Exception {
    boolean takeNormalData = false;
    try {
      instance.setLoadOption(takeNormalData);
      fail();
    } catch (UnsupportedOperationException ex) {
    }
  }

  private DBBackedServiceWeatherLoader lookupDBBackedServiceWeatherLoaderBean() {
    try {
      Context c = new InitialContext();
      return (DBBackedServiceWeatherLoader) c.lookup(
        "java:global/de.pgalise_simulation-ear_ear_2.0-SNAPSHOT/de.pgalise.simulation_weathercollector-impl_ejb_2.1-SNAPSHOT/DBBackedServiceWeatherLoader!de.pgalise.simulation.weather.internal.dataloader.DBBackedServiceWeatherLoader");
    } catch (NamingException ne) {
      Logger.getLogger(getClass().getName()).
        log(Level.SEVERE,
          "exception caught",
          ne);
      throw new RuntimeException(ne);
    }
  }

}
