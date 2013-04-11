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

/**
 * Helper to convert specific date formats
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Jun 22, 2012)
 */
public final class DateConverter {

	/**
	 * One hour in millis
	 */
	public static final long HOUR = 3600000L;

	/**
	 * One minute in millis
	 */
	public static final long MINUTE = 60000L;

	/**
	 * 24 hours in millis
	 */
	public static final long NEXT_DAY_IN_MILLIS = 86400000L;

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
		if (pattern == null) {
			throw new RuntimeException(ExceptionMessages.getMessageForNotNull("pattern"));
		}
		if (dateString == null) {
			throw new RuntimeException(ExceptionMessages.getMessageForNotNull("dateString"));
		}

		DateFormat formatter = new SimpleDateFormat(pattern);
		Date date = new Date(formatter.parse(dateString).getTime());

		return date;
	}

	/**
	 * Returns a time object to the given time string
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
		if (pattern == null) {
			throw new RuntimeException(ExceptionMessages.getMessageForNotNull("pattern"));
		}
		if (timeString == null) {
			throw new RuntimeException(ExceptionMessages.getMessageForNotNull("timeString"));
		}

		DateFormat formatter = new SimpleDateFormat(pattern);
		Time time = new Time(formatter.parse(timeString).getTime());

		return time;
	}

	/**
	 * Returns a timestamp object to the given string
	 * 
	 * @param timeString
	 *            Timestamp string
	 * @param pattern
	 *            Pattern for the string
	 * @return Timestamp object
	 * @throws ParseException
	 *             The string can not be interpreted
	 */
	public static Timestamp convertTimestamp(String timeString, String pattern) throws ParseException {
		if (pattern == null) {
			throw new RuntimeException(ExceptionMessages.getMessageForNotNull("pattern"));
		}
		if (timeString == null) {
			throw new RuntimeException(ExceptionMessages.getMessageForNotNull("timeString"));
		}

		DateFormat formatter = new SimpleDateFormat(pattern);
		Timestamp timestamp = new Timestamp(formatter.parse(timeString).getTime());

		return timestamp;
	}

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
		return DateConverter.getCleanDate(cal);
	}

	/**
	 * Calculate fahrenheit to celsius
	 * 
	 * @param fahrenheit
	 *            value in fahrenheit
	 * @return value in celsius
	 */
	public static float convertToCelsius(float fahrenheit) {
		return (((fahrenheit - 32) * 5) / 9);
	}

	/**
	 * Returns a clean timestamp
	 * 
	 * @param cal
	 *            Calendar with timestamp
	 * @return timestamp without hours, minutes, seconds and milliseconds
	 */
	public static long getCleanDate(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTimeInMillis();
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
		return DateConverter.getCleanDate(cal);
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
	 * Returns the next day
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @return Next day as timestamp
	 */
	public static long getNextDay(long timestamp) {
		return timestamp + DateConverter.NEXT_DAY_IN_MILLIS;
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
	 * Returns the previous day
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @return Previous day as timestamp
	 */
	public static long getPreviousDay(long timestamp) {
		return timestamp - DateConverter.NEXT_DAY_IN_MILLIS;
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

}
