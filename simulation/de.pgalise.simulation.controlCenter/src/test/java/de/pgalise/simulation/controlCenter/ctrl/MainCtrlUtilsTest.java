/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import net.sf.ehcache.Cache;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author richter
 */
public class MainCtrlUtilsTest {

	public MainCtrlUtilsTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@Test
	public void testContants() {
		Cache cache1 = MainCtrlUtils.OSM_FILE_CACHE;
		Assert.assertEquals(cache1.getSize(),
			MainCtrlUtils.INITIAL_OSM_FILE_PATHS.size());
		Cache cache = MainCtrlUtils.BUS_STOP_FILE_CACHE;
		Assert.assertEquals(cache.getSize(),
			MainCtrlUtils.INITIAL_BUS_STOP_FILE_PATHS.size());
	}

}
