/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.testutils.traffic;

import de.pgalise.simulation.traffic.TrafficCity;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author richter
 */
public class TrafficTestUtilsTest {

	public TrafficTestUtilsTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	/**
	 * Test of createDefaultTestCityInstance method, of class TrafficTestUtils.
	 */
	@Test
	public void testCreateDefaultTestCityInstance() {
		System.out.println("createDefaultTestCityInstance");
		Long id = 1L;
		TrafficCity result = TrafficTestUtils.createDefaultTestCityInstance(id);
		Assert.assertNotNull(result);
	}

}
