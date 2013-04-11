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
 
package de.pgalise.simulation.internal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import de.pgalise.simulation.SpentTimeLogger;
import de.pgalise.simulation.service.configReader.ConfigReader;
import de.pgalise.simulation.service.configReader.Identifier;
import de.pgalise.simulation.shared.controller.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.controller.internal.AbstractController;
import de.pgalise.simulation.shared.event.SimulationEventList;
import de.pgalise.simulation.shared.exception.InitializationException;

@Lock(LockType.READ)
@Singleton(name="de.pgalise.simulation.SpentTimeLogger")
@Local
/**
 * Logs the time has been past executing a process (e.g. starting all controller).
 * Another scenario is to log the duration of one update step.<br/>
 * The log file is written to the directory specified by the config property 
 * {@link de.pgalise.simulation.service.configReader.Identifier#SCALE_TEST}.<br/>
 */
public class DefaultSpentTimeLogger extends AbstractController implements SpentTimeLogger {
	private static SimpleDateFormat sdf;
	@EJB
	private ConfigReader configReader;
	private Map<String, Long> map;
	private BufferedWriter writer;
	private boolean logEnabled;
	
	static {
		sdf = new SimpleDateFormat();
		sdf.applyPattern("dd-MM-yy_HH:mm:ss" );
	}
	
	public DefaultSpentTimeLogger() {
		map = new HashMap<>();
	}
	
	@PostConstruct
	private void postConstruct() {

	}
	
	public void begin(String name) {		
		if(logEnabled)  {
			this.map.put(name, System.currentTimeMillis());
		}
	}
	
	public void end(String name) {
		if(logEnabled) {
			Long begin = this.map.get(name);
			long elapsedTime = System.currentTimeMillis()-begin;
			this.map.remove(name);
			
			synchronized(writer) {
				try {
					// writer could be closed meanwhile
					if(logEnabled) {
						writer.write(name+";"+elapsedTime);
						writer.newLine();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public String getName() {
		return "SpentTimeLogger";
	}

	@Override
	protected void onInit(InitParameter param) throws InitializationException {
		map.clear();
		try {
			File file = new File(configReader.getProperty(Identifier.SCALE_PATH));
			if(file.exists() && configReader.getProperty(Identifier.SCALE_TEST)!=null && 
					configReader.getProperty(Identifier.SCALE_TEST).equals("true")) {
					writer = new BufferedWriter(
							new FileWriter(file.getPath()+"/spentTimeLog_"
									+sdf.format(new Date(System.currentTimeMillis()))
									+".csv"));
					logEnabled = true;
			}
		}
		catch (Exception e) {
			writer=null;
			logEnabled = false;
			e.printStackTrace();
		}
	}

	@Override
	protected void onReset() {
		if(logEnabled) {
			synchronized(writer) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				logEnabled=false;
			}
		}
	}

	@Override
	protected void onStart(StartParameter param) {
	}

	@Override
	protected void onStop() {
	}

	@Override
	protected void onResume() {
	}

	@Override
	protected void onUpdate(SimulationEventList simulationEventList) {
	}
}
