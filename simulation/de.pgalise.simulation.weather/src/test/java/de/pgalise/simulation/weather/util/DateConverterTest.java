/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author richter
 */
public class DateConverterTest {
	
	public DateConverterTest() {
	}

	/**
	 * Test of convertDate method, of class DateConverter.
	 * 
	 * @throws ParseException 
	 */
	@Test
	public void testConvertDate() throws ParseException {
		String dateString = "Sat, 07 Sep 2013 5:18 am CEST";
		String pattern = "E, dd MMM yyyy h:mm a z";
		Calendar calendar = new GregorianCalendar(2013,
			8, //0-based
			7,
			5,
			18);
		Date expResult = new Date(calendar.getTimeInMillis());
		Date result = DateConverter.convertDate(dateString,
			pattern);
		assertEquals(expResult,
			result);
	}

	/**
	 * Test of convertDateFromWeekday method, of class DateConverter. It just checks that the date is equal, hour, minute, second and millisecond of the result are ignored.
	 * 
	 * @throws ParseException 
	 */
	@Test
	public void testConvertDateFromWeekday() throws ParseException  {
		String dateString = "Sat";
		Calendar calendar = new GregorianCalendar();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		int daysToSat = dayOfWeek == Calendar.SUNDAY ? 6 : 7-dayOfWeek ;
 		calendar.add(Calendar.DAY_OF_WEEK,
			daysToSat);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date expResult = new Date(calendar.getTimeInMillis());
		Date result = DateConverter.convertDateFromWeekday(dateString);
		Calendar resultCalendar = new GregorianCalendar();
		resultCalendar.setTimeInMillis(result.getTime());
		resultCalendar.set(Calendar.HOUR, 0);
		resultCalendar.set(Calendar.MINUTE, 0);
		resultCalendar.set(Calendar.SECOND, 0);
		resultCalendar.set(Calendar.MILLISECOND, 0);
		result = new Date(resultCalendar.getTimeInMillis());
		assertEquals(expResult,
			result);
	}

	/**
	 * Test of convertTime method, of class DateConverter.
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testConvertTime() throws Exception {
		String timeString = "Sat, 07 Sep 2013 5:18 AM CEST";
		String pattern = "E, dd MMM yyyy h:mm a z";
		Calendar calendar = new GregorianCalendar(2013,
			8, //0-based
			7,
			5,
			18);
		Time expResult = new Time(calendar.getTime().getTime());
		Time result = DateConverter.convertTime(timeString,
			pattern);
		assertEquals(expResult,
			result);
	}

	/**
	 * Test of convertTimestamp method, of class DateConverter.
	 * 
	 * @throws ParseException 
	 */
	@Test
	public void testConvertTimestamp() throws ParseException  {
		String timestampString = "Sat, 07 Sep 2013 5:18 AM CEST";
		String pattern = "E, dd MMM yyyy h:mm a z";
		Calendar calendar = new GregorianCalendar(2013,
			8, //0-based
			7,
			5,
			18);
		Timestamp expResult = new Timestamp(calendar.getTimeInMillis());
		Timestamp result = DateConverter.convertTimestamp(timestampString,
			pattern);
		assertEquals(expResult,
			result);
	}

	/**
	 * Test of convertToCelsius method, of class DateConverter.
	 */
	@Test
	public void testConvertToCelsius() {
		float fahrenheit = 0.0F;
		float expResult = -17.7777777F;
		float result = DateConverter.convertToCelsius(fahrenheit);
		assertEquals(expResult,
			result,
			0.0);
		fahrenheit = 32.0F;
		expResult = 0.0F;
		result = DateConverter.convertToCelsius(fahrenheit);
		assertEquals(expResult,
			result, 0.0);
	}

	/**
	 * Test of getNumberWithNull method, of class DateConverter.
	 */
	@Test
	public void testGetNumberWithNull() {
		int value = 0;
		String expResult = "00";
		String result = DateConverter.getNumberWithNull(value);
		assertEquals(expResult,
			result);
		value = 10;
		expResult = "10";
		result = DateConverter.getNumberWithNull(value);
		assertEquals(expResult,
			result);
	}

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

}