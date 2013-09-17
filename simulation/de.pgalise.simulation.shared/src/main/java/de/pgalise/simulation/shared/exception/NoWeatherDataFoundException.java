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
 
package de.pgalise.simulation.shared.exception;

import java.sql.Date;

/**
 * There are no weather data for the given date. Subclasses should specify whether service data or station data was required.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Aug 18, 2012)
 */
public abstract class NoWeatherDataFoundException extends RuntimeException {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -9210493828131108208L;

	/**
	 * Constructor
	 */
	public NoWeatherDataFoundException() {
		super("No data can load because no date is given.");
	}

	/**
	 * Constructor
	 * 
	 * @param date
	 *            Date
	 */
	public NoWeatherDataFoundException(Date date) {
		super("No weather data for the date " + date + " can be found.");
	}

	/**
	 * Constructor
	 * 
	 * @param startDate
	 *            Date
	 * @param endDate
	 *            Date
	 */
	public NoWeatherDataFoundException(Date startDate, Date endDate) {
		super("No weather data from  " + startDate + " to " + endDate + " can be found.");
	}

	/**
	 * Constructor
	 * 
	 * @param timestamp
	 *            Timestamp
	 */
	public NoWeatherDataFoundException(long timestamp) {
		super("No weather data for the date " + new Date(timestamp) + " can be found.");
	}

	/**
	 * Constructor
	 * 
	 * @param date
	 *            Message as string
	 */
	public NoWeatherDataFoundException(String date) {
		super(date);
	}
}
