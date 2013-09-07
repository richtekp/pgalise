/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
 
package de.pgalise.util.weathercollector.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Helper with important methods to convert dates, times and other stuff
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Jun 22, 2012)
 */
public class Converter {
	
	/**
	 * invokes {@link #convertDate(java.lang.String, java.lang.String, java.util.Locale) } with {@link Locale#US}
	 * @param dateString
	 * @param pattern
	 * @return
	 * @throws ParseException 
	 */
	public static Date convertDate(String dateString, String pattern) throws ParseException {
		Date retValue = convertDate(dateString,
			pattern, Locale.US);
		return retValue;
	}

	/**
	 * Returns a date object to the given date string
	 * 
	 * @param dateString
	 *            Date string
	 * @param pattern
	 *            Pattern for the string
	 * @param locale 
	 * @return Date object
	 * @throws ParseException
	 *             The string can not be interpreted
	 */
	public static Date convertDate(String dateString, String pattern, Locale locale) throws ParseException {
		if (dateString == null) {
			throw new IllegalArgumentException("dateString");
		} else if (pattern == null) {
			throw new IllegalArgumentException("pattern");
		}

		DateFormat formatter = new SimpleDateFormat(pattern, locale);
		Date date = new Date(formatter.parse(dateString).getTime());

		return date;
	}
	
	/**
	 * invokes {@link #convertDateFromWeekday(java.lang.String, java.util.Locale) } with {@link Locale#US}
	 * @param dateString
	 * @return
	 * @throws ParseException 
	 */
	public static Date convertDateFromWeekday(String dateString) throws ParseException {
		Date retValue = convertDateFromWeekday(dateString, Locale.US);
		return retValue;
	}

	/**
	 * Returns the date object to the next weekday with the current hour, minute, second and millisecond
	 * 
	 * @param dateString
	 *            Next weekday
	 * @param locale 
	 * @return Date object
	 * @throws ParseException
	 *             The string can not be interpreted
	 */
	public static Date convertDateFromWeekday(String dateString, Locale locale) throws ParseException {
		if (dateString == null) {
			throw new IllegalArgumentException("dateString");
		}

		Calendar cal = new GregorianCalendar();
		DateFormat formatter = new SimpleDateFormat("E", locale);

		// Current date
		if (formatter.format(cal.getTime()).equals(dateString)) {
			return new Date(cal.getTimeInMillis());
		}

		// Next days
		for (int i = 0; i < 7; i++) {
			cal.add(Calendar.DAY_OF_MONTH, 1);
			if (formatter.format(cal.getTime()).equals(dateString)) {
				return new Date(cal.getTimeInMillis());
			}
		}

		// Nothing found?
		return null;
	}
	
	/**
	 * invokes {@link #convertTime(java.lang.String, Sting) } with {@link Locale#US}
	 * @param timeString
	 * @param pattern
	 * @return
	 * @throws ParseException  
	 */
	public static Time convertTime(String timeString, String pattern) throws ParseException {
		Time retValue = convertTime(timeString,
			pattern,
			Locale.US);
		return retValue;
	}

	/**
	 * Returns a time object to the given string
	 * 
	 * @param timeString
	 *            Time string
	 * @param pattern
	 *            Pattern for the string
	 * @param locale 
	 * @return Time object
	 * @throws ParseException
	 *             The string can not be interpreted
	 */
	public static Time convertTime(String timeString, String pattern, Locale locale) throws ParseException {
		if (timeString == null) {
			throw new IllegalArgumentException("timeString");
		} else if (pattern == null) {
			throw new IllegalArgumentException("pattern");
		}

		DateFormat formatter = new SimpleDateFormat(pattern, locale);
		Time time = new Time(formatter.parse(timeString).getTime());

		return time;
	}
	
	/**
	 * invokes {@link #convertTimestamp(java.lang.String, java.lang.String, java.util.Locale) } with {@link Locale#US}
	 * @param timestampString
	 * @param pattern
	 * @return
	 * @throws ParseException 
	 */
	public static Timestamp convertTimestamp(String timestampString, String pattern) throws ParseException {
		Timestamp retValue = convertTimestamp(timestampString,
			pattern, Locale.US);
		return retValue;
	}

	/**
	 * Returns a timestamp object to the given string
	 * 
	 * @param timestampString
	 *            Timestamp string
	 * @param pattern
	 *            Pattern for the string
	 * @param locale 
	 * @return Timestamp object
	 * @throws ParseException
	 *             The string can not be interpreted
	 */
	public static Timestamp convertTimestamp(String timestampString, String pattern, Locale locale) throws ParseException {
		if (timestampString == null) {
			throw new IllegalArgumentException("timestampString");
		} else if (pattern == null) {
			throw new IllegalArgumentException("pattern");
		}

		DateFormat formatter = new SimpleDateFormat(pattern, locale);
		Timestamp timestamp = new Timestamp(formatter.parse(timestampString).getTime());

		return timestamp;
	}

	/**
	 * Convert fahrenheit to celsius
	 * 
	 * @param fahrenheit
	 *            Fahrenheit
	 * @return Degree of Celsius
	 */
	public static float convertToCelsius(float fahrenheit) {
		return (((fahrenheit - 32) * 5) / 9);
	}

	/**
	 * Returns the number as String with the property "value < 10 = "0" + value"
	 * 
	 * @param value
	 *            Number
	 * @return String with zero (if value < 10)
	 */
	public static String getNumberWithNull(int value) {
		return (value < 10) ? "0" + value : "" + value;
	}

	private Converter() {
	}

}
