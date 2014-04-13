package de.pgalise.simulation.controlCenter.ctrl;

import com.vividsolutions.jts.geom.Envelope;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.traffic.entity.CityInfrastructureData;
import de.pgalise.simulation.traffic.entity.TrafficCity;
import de.pgalise.testutils.TestUtils;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import org.easymock.EasyMock;
import org.geotools.data.DataStore;
import org.geotools.filter.text.cql2.CQLException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.postgresql.util.PSQLException;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author richter
 */
@LocalClient
@ManagedBean
public class CityCtrlTest implements Serializable {

  private static final long serialVersionUID = 1L;

  @EJB
  private IdGenerator idGenerator;

  public CityCtrlTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContainer().getContext().bind("inject",
      this);
  }

  @Test
  public void testInitialize() {
    CityCtrl instance = new CityCtrl(true,
      true,
      null,
      idGenerator);
    instance.initialize();
    assertTrue(MainCtrlUtils.OSM_PARSING_CACHE.getSize() >= 1);
  }

  /**
   * Test of retrieveOsmFileCacheKeys method, of class CityCtrl.
   */
  @Test
  @Ignore
  public void testRetrieveOsmFileCacheKeys() {
    System.out.println("retrieveOsmFileCacheKeys");
    CityCtrl instance = new CityCtrl();
    List<String> expResult = null;
    List<String> result = instance.retrieveOsmFileCacheKeys();
    assertEquals(expResult,
      result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of retrieveBusStopFileCacheKeys method, of class CityCtrl.
   */
  @Test
  @Ignore
  public void testRetrieveBusStopFileCacheKeys() {
    System.out.println("retrieveBusStopFileCacheKeys");
    CityCtrl instance = new CityCtrl();
    List<String> expResult = null;
    List<String> result = instance.retrieveBusStopFileCacheKeys();
    assertEquals(expResult,
      result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of onOSMFileUpload method, of class CityCtrl.
   */
  @Test
  @Ignore
  public void testOnOSMFileUpload() {
    System.out.println("onOSMFileUpload");
    FileUploadEvent event = null;
    CityCtrl instance = new CityCtrl();
    instance.onOSMFileUpload(event);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of onOsmFileSelectionChanged method, of class CityCtrl.
   */
  @Test
  @Ignore
  public void testOnOsmFileSelectionChanged() throws InterruptedException, ExecutionException {
    System.out.println("onOsmFileSelectionChanged");
    AjaxBehaviorEvent event = null;
    CityCtrl instance = new CityCtrl();
    UIComponent uIComponentMock = EasyMock.createNiceMock(UIComponent.class);
    ValueChangeEvent valueChangeEvent = new ValueChangeEvent(uIComponentMock,
      null,
      "someValue");
    instance.onOsmFileSelectionChanged(valueChangeEvent);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of cityNameAutocomplete method, of class CityCtrl.
   */
  @Test
  @Ignore
  public void testCityNameAutocomplete() throws CQLException {
    System.out.println("cityNameAutocomplete");
    String input = "";
    CityCtrl instance = new CityCtrl();
    List<TrafficCity> expResult = null;
    List<TrafficCity> result = instance.cityNameAutocomplete(input);
    assertEquals(expResult,
      result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of onCityNameSelectionChange method, of class CityCtrl.
   */
  @Test
  @Ignore
  public void testOnCityNameSelectionChange() {
    System.out.println("onCityNameSelectionChange");
    SelectEvent event = null;
    CityCtrl instance = new CityCtrl();
    instance.onCityNameSelectionChange(event);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of retrieveDataStore method, of class CityCtrl.
   */
  @Test
  public void testRetrieveDataStore() throws IOException, PSQLException {
    //test valid
    CityCtrl instance = new CityCtrl();    
    instance.setDbDatabase("postgis");
    instance.setDbHost("localhost");
    instance.setDbPort(5204);
    instance.setDbUser("postgis");
    instance.setDbPassword("postgis");
    DataStore result = instance.retrieveDataStore();
    assertNotNull(      result);
    //invalid parameters (so far tested database name and database user) don't 
    //cause exception
  }

  /**
   * Test of retrieveEnvelope method, of class CityCtrl.
   */
  @Test
  @Ignore
  public void testRetrieveEnvelope() {
    System.out.println("retrieveBoundaries");
    CityCtrl instance = new CityCtrl();
    Envelope expResult = null;
    Envelope result = instance.retrieveEnvelope();
    assertEquals(expResult,
      result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of generateOsmParseTask method, of class CityCtrl.
   */
  @Test
  @Ignore
  public void testGenerateOsmParseTask() {
    System.out.println("generateOsmParseTask");
    String osmFileName = "";
    CityCtrl instance = new CityCtrl();
    FutureTask<TrafficCity> expResult = null;
    FutureTask<TrafficCity> result = instance.generateOsmParseTask(
      osmFileName);
    assertEquals(expResult,
      result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

}
