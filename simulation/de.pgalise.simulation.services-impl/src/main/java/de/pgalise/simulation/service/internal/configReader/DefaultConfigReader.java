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
 
package de.pgalise.simulation.service.internal.configReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.service.configReader.ConfigReader;
import de.pgalise.simulation.service.configReader.Identifier;

@Lock(LockType.READ)
@Remote
@Singleton(name="de.pgalise.simulation.service.configReader.ConfigReader")
/**
 *  Default implementation of the ConfigReader.
 * @author mustafa
 *
 */
public class DefaultConfigReader implements ConfigReader {
	private static final Logger log = LoggerFactory.getLogger(DefaultConfigReader.class);
	private Properties prop;
	
	public DefaultConfigReader() throws IOException {
		prop = new Properties();
		
		setDefaultProperties(prop);
		
		boolean fileNotFound=false;		
		try {
			if(System.getProperties().getProperty("simulation.configuration.filepath")!=null) {
				prop.load(new FileInputStream(System.getProperties().getProperty("simulation.configuration.filepath")));
				log.info("Simulation configuration loaded: "+
						System.getProperties().getProperty("simulation.configuration.filepath"));
			}
			else {
				InputStream in = DefaultConfigReader.class.getResourceAsStream("/simulation.conf");
				prop.load(in);
				log.info("Simulation configuration loaded was found in the classpath");
			}
		} catch (Exception e) {
			log.error("Could not find simulation configuration file");
			fileNotFound=true;
		}
		
		validateProperties(prop);
		
		storeConfig(fileNotFound);
		
		printProperties();
	}
	
	private void setDefaultProperties(Properties prop) {
		if(System.getProperty("catalina.base")==null) {
			System.setProperty("catalina.base", System.getProperty("user.dir"));
		}
		// scaletest config	
		prop.put(Identifier.SCALE_TEST.getName(), "false");
	
		if(System.getProperty(Identifier.SCALE_PATH.getName())!=null) {
			prop.put(Identifier.SCALE_PATH.getName(),
					System.getProperties().getProperty(Identifier.SCALE_PATH.getName()));
		}

		if(prop.get(Identifier.SCALE_PATH.getName())==null) {
			prop.put(Identifier.SCALE_PATH.getName(), System.getProperty("catalina.base"));
		}
		
		// server config
		prop.put(Identifier.SENSOR_OUTPUT_SERVER.getName(), "127.0.0.1:6666");	
		
		prop.put(Identifier.MOCK_PERSISTENCE_SERVICE.getName(), "true");		
		
		prop.put(Identifier.SERVER_IP.getName(), "127.0.0.1");
		
		prop.put(Identifier.SERVER_HTTP_PORT.getName(), "8080");
		
		prop.put(Identifier.SERVER_EJBD_PORT.getName(), "4201");
		
		prop.put(Identifier.MOCK_CONTROL_CENTER_CONTROLLER.getName(), "false");
		prop.put(Identifier.MOCK_OPERATION_CENTER_CONTROLLER.getName(), "false");
		
		prop.put(Identifier.OUTPUT_DECORATOR.getName(), "");
	}
	
	
	private void validateProperties(Properties prop) {
		if(System.getProperty("openejb.service.manager.class")!=null && 
				System.getProperty("openejb.service.manager.class").equals("org.apache.openejb.server.SimpleServiceManager")) {
			prop.put(Identifier.EJBD_PROTOCOL_ENABLED.getName(), "true");	
		}
		else {
			prop.put(Identifier.EJBD_PROTOCOL_ENABLED.getName(), "false");	
		}
		
		if(prop.get(Identifier.SCALE_TEST.getName()).equals("true") && 
				prop.get(Identifier.SCALE_PATH.getName())==null) {
			log.error("Tried to enable logging for scale test but no output path is specified");
		}
		
		if(prop.get(Identifier.EJBD_PROTOCOL_ENABLED.getName()).equals("true")) {
			prop.put(Identifier.SERVER_HOST.getName(),
					prop.get(Identifier.SERVER_IP.getName())+":"+prop.get(Identifier.SERVER_EJBD_PORT.getName()));
		}
		else {
			prop.put(Identifier.SERVER_HOST.getName(),
					prop.get(Identifier.SERVER_IP.getName())+":"+prop.getProperty(Identifier.SERVER_HTTP_PORT.getName()));
		}
	}
	
	private void storeConfig(boolean store) throws IOException {
		if(!store) {
			return;
		}
		
		log.info("Simulation configuration file not found. Creating one under "+System.getProperty("catalina.base")+"/applib");
		File file = new File(System.getProperty("catalina.base")+"/applib");
		if(file.exists()) {
			
			FileOutputStream out = new FileOutputStream(file.getPath()+"/simulation.conf");
			prop.store(out, "Simulation configuration file");
		}
		else {
			log.error("Could not create simulation configuration file: Path does not exist");
		}
	}
	
	private void printProperties() {	
		for(Identifier id : Identifier.ALL) {
			log.info(String.format("Set %s to %s", id.getName(),
					prop.getProperty(id.getName())));	
		}
	}
	
	/**
	 * For testing purpose only. Passed parameter is the InputStream of
	 * a property file which will be used to find the properties of the simulation.
	 * @param input
	 * @throws IOException 
	 */
	public DefaultConfigReader(InputStream input) throws IOException {
		prop = new Properties();
		prop.load(input);
	}
	
	@Override
	public String getProperty(Identifier propName) {
		return prop.getProperty(propName.getName());
	}
}
