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
 
package de.pgalise.util.weathercollector.exceptions;

/**
 * Service data could not be read
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Mar 16, 2012)
 */
public class ReadServiceDataException extends Exception {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -3210493856731108208L;

	/**
	 * Constructor
	 */
	public ReadServiceDataException() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param text
	 *            Error
	 */
	public ReadServiceDataException(String text) {
		super(text);
	}

}
