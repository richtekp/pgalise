/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.service;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.entity.BusRoute;
import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.simulation.traffic.entity.CityInfrastructureData;
import de.pgalise.simulation.traffic.entity.gtfs.GTFSBusAgency;
import de.pgalise.simulation.traffic.entity.gtfs.GTFSBusCalendar;
import de.pgalise.testutils.TestUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipInputStream;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author richter
 */
@LocalClient
@ManagedBean
public class GTFSPublicTransportDataServiceTest {
  @EJB
  private IdGenerator idGenerator;
  
  public GTFSPublicTransportDataServiceTest() {
  }
  
  @Before
  public void setUp() throws NamingException {
    TestUtils.getContainer().getContext().bind("inject",
      this);
  }

  /**
   * Test of parse method, of class GTFSPublicTransportDataService.
   */
  @Test
  public void testParse() throws Exception {
    ZipInputStream zis = new ZipInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("verkehrsverbund_berlin_brandenburg-gtfs.zip"));
    DefaultGTFSPublicTransportDataService instance = new DefaultGTFSPublicTransportDataService(idGenerator);
    instance.parse(zis);
    assertEquals(51, instance.getBusAgencys().size());
    assertEquals(12845, instance.getBusStops().size());
    assertEquals(1882,instance.getBusCalendars().size());
    assertEquals(1363,
      instance.getBusRoutes());
  }

  /**
   * Test of insertBusStops method, of class GTFSPublicTransportDataService.
   */
  @Test
  public void testInsertBusStops() throws IOException {
    ZipInputStream zis = new ZipInputStream(Thread.currentThread().getContextClassLoader().getResourceAsStream("verkehrsverbund_berlin_brandenburg-gtfs.zip"));
    DefaultGTFSPublicTransportDataService instance = new DefaultGTFSPublicTransportDataService(idGenerator);
    instance.parse(zis);
    assertEquals(12845, instance.getBusStops().size());
    CityInfrastructureData cityInfrastructureData = null;
    Set<BusStop> busStops = new HashSet<>(Arrays.asList(new BusStop("somename",
      null,
      null), new BusStop(
        "someothername",
        null,
        null)));
    TrafficGraph trafficGraph = null;
    instance.insertBusStops(cityInfrastructureData,
      busStops,
      trafficGraph);
    assertEquals(12845+busStops.size(), instance.getBusStops().size());
  }
  
}
