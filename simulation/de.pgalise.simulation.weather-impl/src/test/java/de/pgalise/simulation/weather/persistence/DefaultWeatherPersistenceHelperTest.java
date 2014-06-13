/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.weather.persistence;

import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.weather.entity.ServiceDataCurrent;
import java.io.Serializable;
import javax.persistence.EntityManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author richter
 */
public class DefaultWeatherPersistenceHelperTest {
  
  public DefaultWeatherPersistenceHelperTest() {
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }

  /**
   * Test of saveOrUpdateServiceDataCurrent method, of class DefaultWeatherPersistenceHelper.
   */
  @Test
  public void testSaveOrUpdateServiceDataCurrent() throws Exception {
    System.out.println("saveOrUpdateServiceDataCurrent");
    EntityManager entityManager = null;
    ServiceDataCurrent serviceDataCurrent = null;
    DefaultWeatherPersistenceHelper instance = new DefaultWeatherPersistenceHelper();
    instance.saveOrUpdateServiceDataCurrent(entityManager, serviceDataCurrent);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
  
}
