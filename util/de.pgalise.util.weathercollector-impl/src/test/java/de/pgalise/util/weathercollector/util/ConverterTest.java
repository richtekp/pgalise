/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.util;

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
public class ConverterTest {
	
	public ConverterTest() {
	}

	/**
	 * Test of convertDate method, of class Converter.
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
		Date result = Converter.convertDate(dateString,
			pattern);
		assertEquals(expResult,
			result);
	}

	/**
	 * Test of convertDateFromWeekday method, of class Converter. It just checks that the date is equal, hour, minute, second and millisecond of the result are ignored.
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
		Date result = Converter.convertDateFromWeekday(dateString);
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
	 * Test of convertTime method, of class Converter.
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
		Time result = Converter.convertTime(timeString,
			pattern);
		assertEquals(expResult,
			result);
	}

	/**
	 * Test of convertTimestamp method, of class Converter.
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