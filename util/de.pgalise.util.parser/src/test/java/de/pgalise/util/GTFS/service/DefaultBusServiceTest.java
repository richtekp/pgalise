/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.GTFS.service;

import de.pgalise.simulation.traffic.BusRoute;
import java.util.ArrayList;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author richter
 */
public class DefaultBusServiceTest {
	
	public DefaultBusServiceTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}

	/**
	 * Test of getBusLineData method, of class DefaultBusService.
	 */
	@Test
	public void testGetBusLineData() throws Exception {
		System.out.println("getBusLineData");
		String busRoute = "";
		long timeInMs = 0L;
		DefaultBusService instance = new DefaultBusService();
		List expResult = null;
		List result = instance.getBusLineData(busRoute,
			timeInMs);
		assertEquals(expResult,
			result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getTotalNumberOfBusTrips method, of class DefaultBusService.
	 */
	@Test
	public void testGetTotalNumberOfBusTrips() throws Exception {
		System.out.println("getTotalNumberOfBusTrips");
		List<BusRoute> busRoutes = null;
		long timeInMs = 0L;
		DefaultBusService instance = new DefaultBusService();
		int expResult = 0;
		int result = instance.getTotalNumberOfBusTrips(busRoutes,
			timeInMs);
		assertEquals(expResult,
			result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of getAllBusRoutes method, of class DefaultBusService.
	 */
	@Test
	public void testGetAllBusRoutes() throws Exception {
		DefaultBusService instance = new DefaultBusService();
		List<BusRoute> expResult = new ArrayList<>(1);
		List<BusRoute> result = instance.getAllBusRoutes();
		assertEquals(expResult,
			result);
		
		BusRoute busRoute = new DefaultBusRoute
		List<BusRoute> expResult = new ArrayList<>;List<BusRoute> expResult = new ArrayList<>;
		assertEquals(expResult,
			result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
}