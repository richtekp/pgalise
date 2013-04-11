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
 
package de.pgalise.weathercollector.util;

import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Logger of the WeatherCollector
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (Apr 14, 2012)
 */
public class Log {

	/**
	 * Path to the log file
	 */
	public static final String LOGFILE = "./weatherLog.txt";

	/**
	 * Global logger
	 */
	private static Logger log;

	/**
	 * Synchronize object
	 */
	private static Object obj = new Object();

	/**
	 * Configuration of the logger
	 * 
	 * @throws Exception
	 *             Will be thrown if there is any exception.
	 */
	public static synchronized void configLogging(String name) throws Exception {
		// init logger
		log = Logger.getLogger(name);

		// set log level
		log.setLevel(Level.ALL);

		// Handler
		boolean append = false; // add to the end?
		//FileHandler fh = new FileHandler(Log.class.getResource(LOGFILE).getFile(), append);
		FileHandler fh = new FileHandler(LOGFILE, append);
		
		// Format log
		fh.setFormatter(new Formatter() {
			@Override
			public String format(LogRecord rec) {
				StringBuffer buf = new StringBuffer(1000);
				SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				buf.append(formate.format(new java.util.Date()));
				buf.append(' ');
				buf.append(rec.getLevel());
				buf.append(' ');
				buf.append(this.formatMessage(rec));
				buf.append('\n');
				return buf.toString();
			}
		});

		// add handler
		log.addHandler(fh);
	}

	/**
	 * Write a message to the log
	 * 
	 * @param message
	 *            Message
	 * @param level
	 *            log level
	 */
	public static void writeLog(String message, Level level) {
		synchronized (obj) {
			log.log(level, message);
		}
	}
}
