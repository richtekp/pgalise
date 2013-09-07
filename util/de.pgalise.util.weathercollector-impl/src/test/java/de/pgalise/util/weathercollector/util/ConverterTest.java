/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
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
public class ConverterTest {
	
	public ConverterTest() {
	}

	/**
	 * Test of convertDate method, of class Converter.
	 */
	@Test
	public void testConvertDate() throws Exception {
		System.out.println("convertDate");
		String dateString = "Sat, 07 Sep 2013 5:18 am CEST";
		String pattern = "E, dd MMM yyyy h:mm a z";
		Calendar calendar = new GregorianCalendar(2013,
			9,
			7,
			5,
			18);
		Date expResult = new Date(calendar.getTimeInMillis());
		Date result = Converter.convertDate(dateString,
			pattern);
		assertEquals(expResult,
			result);
	}

	/**
	 * Test of convertDateFromWeekday method, of class Converter.
	 */
	@Test
	public void testConvertDateFromWeekday() throws Exception {
		System.out.println("convertDateFromWeekday");
		String dateString = "Sat, 07 Sep 2013 5:18 AM CEST";
		Calendar calendar = new GregorianCalendar(2013,
			9,
			8);
		Date expResult = new Date(calendar.getTimeInMillis());
		Date result = Converter.convertDateFromWeekday(dateString);
		assertEquals(expResult,
			result);
	}

	/**
	 * Test of convertTime method, of class Converter.
	 */
	@Test
	public void testConvertTime() throws Exception {
		System.out.println("convertTime");
		String timeString = "Sat, 07 Sep 2013 5:18 AM CEST";
		String pattern = "E, dd MMM yyyy h:mm a z";
		Calendar calendar = new GregorianCalendar(2013,
			9,
			7,
			5,
			18);
		Time expResult = new Time(calendar.getTime().getTime());
		Time result = Converter.convertTime(timeString,
			pattern);
		assertEquals(expResult,
			result);
	}

	/**
	 * Test of convertTimestamp method, of class Converter.
	 */
	@Test
	public void testConvertTimestamp() throws Exception {
		System.out.println("convertTimestamp");
		String timestampString = "Sat, 07 Sep 2013 5:18 AM CEST";
		String pattern = "E, dd MMM yyyy h:mm a z";
		Calendar calendar = new GregorianCalendar(2013,
			9,
			7,
			5,
			18);
		Timestamp expResult = new Timestamp(calendar.getTimeInMillis());
		Timestamp result = Converter.convertTimestamp(timestampString,
			pattern);
		assertEquals(expResult,
			result);
	}

	/**
	 * Test of convertToCelsius method, of class Converter.
	 */
	@Test
	public void testConvertToCelsius() {
		System.out.println("convertToCelsius");
		float fahrenheit = 0.0F;
		float expResult = -17.7777777F;
		float result = Converter.convertToCelsius(fahrenheit);
		assertEquals(expResult,
			result,
			0.0);
		fahrenheit = 32.0F;
		expResult = 0.0F;
		result = Converter.convertToCelsius(fahrenheit);
		assertEquals(expResult,
			result, 0.0);
	}

	/**
	 * Test of getNumberWithNull method, of class Converter.
	 */
	@Test
	public void testGetNumberWithNull() {
		System.out.println("getNumberWithNull");
		int value = 0;
		String expResult = "00";
		String result = Converter.getNumberWithNull(value);
		assertEquals(expResult,
			result);
		value = 10;
		expResult = "10";
		result = Converter.getNumberWithNull(value);
		assertEquals(expResult,
			result);
	}
}