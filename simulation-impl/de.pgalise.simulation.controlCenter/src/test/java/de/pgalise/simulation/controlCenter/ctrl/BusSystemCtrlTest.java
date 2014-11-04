/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.traffic.entity.BusRoute;
import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.testutils.TestUtils;
import java.util.Set;
import java.util.concurrent.FutureTask;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.faces.event.AjaxBehaviorEvent;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import org.junit.After;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.primefaces.event.FileUploadEvent;

/**
 *
 * @author richter
 */
@LocalClient
@ManagedBean
public class BusSystemCtrlTest {
  @EJB
  private IdGenerator idGenerator;
  
  public BusSystemCtrlTest() {
  }
  
  @Before
  public void setUp() throws NamingException {
    TestUtils.getContainer().getContext().bind("inject", this);
  }
  
  @After
  public void tearDown() {
  }

  /**
   * Test of useBusRouteChange method, of class BusSystemCtrl.
   */
  @Test
  public void testUseBusRouteChange() {
    System.out.println("useBusRouteChange");
    BusRoute a = new BusRoute(idGenerator.getNextId(), "routeShortName", "routeLongName");
    a.setUsed(true);   
    BusSystemCtrl instance = new BusSystemCtrl();
    instance.useBusRouteChange(a);
    Assert.assertFalse(a.getUsed());
    instance.useBusRouteChange(a);
    Assert.assertTrue(a.getUsed());
  }

  /**
   * Test of usedChanged method, of class BusSystemCtrl.
   */
  @Ignore
  @Test
  public void testUsedChanged() {
    System.out.println("usedChanged");
    AjaxBehaviorEvent event = null;
    BusSystemCtrl instance = new BusSystemCtrl();
    instance.usedChanged(event);
    // TODO review the generated test code and remove the default call to fail.
    //fail("The test case is a prototype.");
  }

  /**
   * Test of initializeBusStopParsing method, of class BusSystemCtrl.
   */
  @Test
  public void testInitializeBusStopParsing() {
    System.out.println("initializeBusStopParsing");
    BusSystemCtrl instance = new BusSystemCtrl();
    instance.initializeBusStopParsing();
    int firstCallResult = MainCtrlUtils.BUS_STOP_PARSING_CACHE.getSize();
    assertTrue(firstCallResult >= 1);
    int firstCallResultFileCache = MainCtrlUtils.BUS_STOP_FILE_CACHE.getSize();
    assertTrue(firstCallResultFileCache >= 1);
    // test multiple invocations    
    instance.initializeBusStopParsing();
    assertEquals(firstCallResult, MainCtrlUtils.BUS_STOP_PARSING_CACHE.getSize());
    assertEquals(firstCallResultFileCache, MainCtrlUtils.BUS_STOP_FILE_CACHE.getSize());
  }

  /**
   * Test of onGTFSFileUpload method, of class CityCtrl.
   */
  @Test
  @Ignore
  public void testOnGTFSFileUpload() {
    System.out.println("onGTFSFileUpload");
    FileUploadEvent event = null;
    BusSystemCtrl instance = new BusSystemCtrl();
    instance.onGTFSFileUpload(event);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }
  
}
