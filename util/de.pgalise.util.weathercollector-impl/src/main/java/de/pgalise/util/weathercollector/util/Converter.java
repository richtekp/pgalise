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

/**
 * Helper with important methods to convert dates, times and other stuff
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Jun 22, 2012)
 */
public final class Converter {

	/**
	 * Returns a date object to the given date string
	 * 
	 * @param dateString
	 *            Date string
	 * @param pattern
	 *            Pattern for the string
	 * @return Date object
	 * @throws ParseException
	 *             The string can not be interpreted
	 */
	public static Date convertDate(String dateString, String pattern) throws ParseException {
		if (dateString == null) {
			throw new IllegalArgumentException("dateString");
		} else if (pattern == null) {
			throw new IllegalArgumentException("pattern");
		}

		DateFormat formatter = new SimpleDateFormat(pattern);
		Date date = new Date(formatter.parse(dateString).getTime());

		return date;
	}

	/**
	 * Returns the date object to the next weekday
	 * 
	 * @param dateString
	 *            Next weekday
	 * @return Date object
	 * @throws ParseException
	 *             The string can not be interpreted
	 */
	public static Date convertDateFromWeekday(String dateString) throws ParseException {
		if (dateString == null) {
			throw new IllegalArgumentException("dateString");
		}

		Calendar cal = new GregorianCalendar();
		DateFormat formatter = new SimpleDateFormat("E");

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
	 * Returns a time object to the given string
	 * 
	 * @param timeString
	 *            Time string
	 * @param pattern
	 *            Pattern for the string
	 * @return Time object
	 * @throws ParseException
	 *             The string can not be interpreted
	 */
	public static Time convertTime(String timeString, String pattern) throws ParseException {
		if (timeString == null) {
			throw new IllegalArgumentException("timeString");
		} else if (pattern == null) {
			throw new IllegalArgumentException("pattern");
		}

		DateFormat formatter = new SimpleDateFormat(pattern);
		Time time = new Time(formatter.parse(timeString).getTime());

		return time;
	}

	/**
	 * Returns a timestamp object to the given string
	 * 
	 * @param timestampString
	 *            Timestamp string
	 * @param pattern
	 *            Pattern for the string
	 * @return Timestamp object
	 * @throws ParseException
	 *             The string can not be interpreted
	 */
	public static Timestamp convertTimestamp(String timestampString, String pattern) throws ParseException {
		if (timestampString == null) {
			throw new IllegalArgumentException("timestampString");
		} else if (pattern == null) {
			throw new IllegalArgumentException("pattern");
		}

		DateFormat formatter = new SimpleDateFormat(pattern);
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

}
