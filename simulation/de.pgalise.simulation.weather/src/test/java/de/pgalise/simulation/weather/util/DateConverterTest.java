/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.util;

import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
public class DateConverterTest {
	
	public DateConverterTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	/**
	 * Test of convertDate method, of class DateConverter.
	 */
//	@Test
//	public void testConvertDate() throws Exception {
//		System.out.println("convertDate");
//		String dateString = "";
//		String pattern = "";
//		Date expResult = null;
//		Date result = DateConverter.convertDate(dateString, pattern);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}

	/**
	 * Test of convertTime method, of class DateConverter.
	 */
//	@Test
//	public void testConvertTime() throws Exception {
//		System.out.println("convertTime");
//		String timeString = "";
//		String pattern = "";
//		Time expResult = null;
//		Time result = DateConverter.convertTime(timeString, pattern);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}

	/**
	 * Test of convertTimestamp method, of class DateConverter.
	 */
//	@Test
//	public void testConvertTimestamp() throws Exception {
//		System.out.println("convertTimestamp");
//		String timeString = "";
//		String pattern = "";
//		Timestamp expResult = null;
//		Timestamp result = DateConverter.convertTimestamp(timeString, pattern);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}

	/**
	 * Test of convertTimestampToMidnight method, of class DateConverter.
	 */
	@Test
	public void testConvertTimestampToMidnight() {
		System.out.println("convertTimestampToMidnight");
		long timestamp = System.currentTimeMillis();
//		GregorianCalendar timestampDate = new GregorianCalendar(timestamp);
//		Date date = new Date(timestamp);
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTimeInMillis(timestamp);
		int year = cal.get(Calendar.YEAR), mouth = cal.get(Calendar.MONTH), date = cal.get(Calendar.DATE);
		cal.clear();
		cal.set(year, mouth, date, 0, 0, 0);
		long expResult = cal.getTimeInMillis();
		long result = DateConverter.convertTimestampToMidnight(timestamp);
		Time x = new Time(23);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getCleanDate method, of class DateConverter.
	 */
	@Test
	public void testGetCleanDate_Calendar() {
		System.out.println("getCleanDate");
		long timestamp = System.currentTimeMillis();
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(timestamp);
		GregorianCalendar cal0 = new GregorianCalendar();
		cal0.setTimeInMillis(timestamp);
		int year = cal0.get(Calendar.YEAR), mouth = cal0.get(Calendar.MONTH), date = cal0.get(Calendar.DAY_OF_MONTH);
		cal0.clear();
		cal0.set(year, mouth, date, 0, 0, 0);
		long expResult = cal0.getTimeInMillis();
		DateConverter.cleanDate(cal);
		long result = cal.getTimeInMillis();
		Time x = new Time(23);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getCleanDate method, of class DateConverter.
	 */
	@Test
	public void testGetCleanDate_long() {
		System.out.println("getCleanDate");
		long timestamp = System.currentTimeMillis();
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(timestamp);
		int year = cal.get(Calendar.YEAR), mouth = cal.get(Calendar.MONTH), date = cal.get(Calendar.DAY_OF_MONTH);
		cal.clear();
		cal.set(year, mouth, date, 0, 0, 0);
		long expResult = cal.getTimeInMillis();
		long result = DateConverter.getCleanDate(timestamp);
		assertEquals(expResult, result);
	}

	/**
	 * Test of getHourOfDay method, of class DateConverter.
	 */
//	@Test
//	public void testGetHourOfDay() {
//		System.out.println("getHourOfDay");
//		long time = 0L;
//		int expResult = 0;
//		int result = DateConverter.getHourOfDay(time);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of getMonthOfYear method, of class DateConverter.
//	 */
//	@Test
//	public void testGetMonthOfYear() {
//		System.out.println("getMonthOfYear");
//		long time = 0L;
//		int expResult = 0;
//		int result = DateConverter.getMonthOfYear(time);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of getNextDay method, of class DateConverter.
//	 */
//	@Test
//	public void testGetNextDay() {
//		System.out.println("getNextDay");
//		long timestamp = 0L;
//		long expResult = 0L;
//		long result = DateConverter.getNextDay(timestamp);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of getNumberWithNull method, of class DateConverter.
//	 */
//	@Test
//	public void testGetNumberWithNull() {
//		System.out.println("getNumberWithNull");
//		int value = 0;
//		String expResult = "";
//		String result = DateConverter.getNumberWithNull(value);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of getPreviousDay method, of class DateConverter.
//	 */
//	@Test
//	public void testGetPreviousDay() {
//		System.out.println("getPreviousDay");
//		long timestamp = 0L;
//		long expResult = 0L;
//		long result = DateConverter.getPreviousDay(timestamp);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of getSeason method, of class DateConverter.
//	 */
//	@Test
//	public void testGetSeason() {
//		System.out.println("getSeason");
//		long time = 0L;
//		int expResult = 0;
//		int result = DateConverter.getSeason(time);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of hasSeconds method, of class DateConverter.
//	 */
//	@Test
//	public void testHasSeconds() {
//		System.out.println("hasSeconds");
//		Calendar cal = null;
//		boolean expResult = false;
//		boolean result = DateConverter.hasSeconds(cal);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
//
//	/**
//	 * Test of isTheSameDay method, of class DateConverter.
//	 */
//	@Test
//	public void testIsTheSameDay() {
//		System.out.println("isTheSameDay");
//		long timestamp1 = 0L;
//		long timestamp2 = 0L;
//		boolean expResult = false;
//		boolean result = DateConverter.isTheSameDay(timestamp1, timestamp2);
//		assertEquals(expResult, result);
//		// TODO review the generated test code and remove the default call to fail.
//		fail("The test case is a prototype.");
//	}
}