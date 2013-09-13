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
 
package de.pgalise.simulation.weather.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.pgalise.simulation.shared.exception.ExceptionMessages;
import java.util.Locale;

/**
 * Helper to convert specific date formats
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Jun 22, 2012)
 */
public class DateConverter {

	/**
	 * One hour in millis
	 */
	public static final long ONE_HOUR_IN_MILLIS = 3600000L;

	/**
	 * One minute in millis
	 */
	public static final long ONE_MINUTE_IN_MILLIS = 60000L;

	/**
	 * 24 hours in millis
	 */
	public static final long ONE_DAY_IN_MILLIS = 86400000L;

	/**
	 * Convert the timestamp to the format 00:00:00 (removes the hours, minutes, seconds and millis)
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @return changed timestamp
	 */
	public static long convertTimestampToMidnight(long timestamp) {
		if (timestamp < 1) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotPositive("timestamp", false));
		}

		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(timestamp);

		// Date is correct
		if (!DateConverter.hasSeconds(cal)) {
			return timestamp;
		}

		// Correct the date
		DateConverter.cleanDate(cal);
		return cal.getTimeInMillis();
	}

	/**
	 * Returns a clean timestamp
	 * 
	 * @param cal
	 *            Calendar with timestamp
	 * @return timestamp without hours, minutes, seconds and milliseconds
	 */
	public static void cleanDate(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	/**
	 * Returns a clean timestamp
	 * 
	 * @param cal
	 *            Calendar with timestamp
	 * @return timestamp without hours, minutes, seconds and milliseconds
	 */
	public static long getCleanDate(long timestamp) {
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(timestamp);
		cleanDate(cal);
		return cal.getTimeInMillis();
	}

	/**
	 * Returns the hour of the day
	 * 
	 * @param time
	 *            timestamp
	 * @return hour of the day
	 */
	public static int getHourOfDay(long time) {
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(time);
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	/**
	 * Returns the month of the year
	 * 
	 * @param time
	 *            timestamp
	 * @return month of the year
	 */
	public static int getMonthOfYear(long time) {
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(time);
		return cal.get(Calendar.MONTH);
	}

	/**
	 * Returns the time of <tt>timestamp</tt> one day later
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @return Next day as timestamp
	 */
	public static long getNextDay(long timestamp) {
		return timestamp + DateConverter.ONE_DAY_IN_MILLIS;
	}

	/**
	 * Returns a number as string with the character: value < 10 = "0" + value
	 * 
	 * @param value
	 *            Number
	 * @return String with a zero added
	 */
	public static String getNumberWithNull(int value) {
		return (value < 10) ? "0" + value : "" + value;
	}

	/**
	 * Returns the time of <tt>timestamp</tt> one day before
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @return Previous day as timestamp
	 */
	public static long getPreviousDay(long timestamp) {
		return timestamp - DateConverter.ONE_DAY_IN_MILLIS;
	}

	/**
	 * Returns the season
	 * 
	 * @param time
	 *            Timestamp
	 * @return season
	 */
	public static int getSeason(long time) {
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(time);

		switch (cal.get(Calendar.MONTH)) {
			case Calendar.DECEMBER:
			case Calendar.JANUARY:
			case Calendar.FEBRUARY:
				return 0;

			case Calendar.MARCH:
			case Calendar.APRIL:
			case Calendar.MAY:
				return 1;

			case Calendar.JUNE:
			case Calendar.JULY:
			case Calendar.AUGUST:
				return 2;

			case Calendar.SEPTEMBER:
			case Calendar.OCTOBER:
			case Calendar.NOVEMBER:
				return 3;

			default:
				return 0;
		}
	}

	/**
	 * Returns true, if the date object has seconds, minutes or hours
	 * 
	 * @param cal
	 *            Calendar with timestamp
	 * @return true, if the date object has seconds, minutes or hours
	 */
	public static boolean hasSeconds(Calendar cal) {
		if ((cal.get(Calendar.HOUR_OF_DAY) != 0) || (cal.get(Calendar.MINUTE) != 0) || (cal.get(Calendar.SECOND) != 0)
				|| (cal.get(Calendar.MILLISECOND) != 0)) {
			return true;
		}

		return false;
	}

	/**
	 * Checks if the day of the timestamp2 is the same day of timestamp1
	 * 
	 * @param timestamp1
	 *            Timestamp
	 * @param timestamp2
	 *            Timestamp
	 * @return true if the day of the timestamp2 is the same day of timestamp1
	 */
	public static boolean isTheSameDay(long timestamp1, long timestamp2) {
		Calendar cal = new GregorianCalendar();
		cal.setTimeInMillis(timestamp1);
		int time1 = cal.get(Calendar.DAY_OF_YEAR);
		cal.setTimeInMillis(timestamp2);
		int time2 = cal.get(Calendar.DAY_OF_YEAR);

		return (time1 == time2) ? true : false;
	}
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
	 * invokes {@link #convertTime(java.lang.String, java.lang.String, java.util.Locale) } with {@link Locale#US}
	 * @param timeString
	 * @param pattern
	 * @return
	 * @throws ParseException 
	 */
	public static Time convertTime(String timeString, String pattern) throws ParseException {
		return convertTime(timeString,
			pattern,
			Locale.US);
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

	private DateConverter() {
	}

}
