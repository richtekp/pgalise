/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;

/**
 *
 * @author richter
 */
public class MainCtrlUtils {
	
	public final static DataStore POSTGIS_OSM_DATA_STORE;
	static  {
		try {
			Map<String, Object> connectionParameters = new HashMap<>();
			connectionParameters.put("port",
				5204);
			connectionParameters.put("Connection timeout",
				null);
			connectionParameters.put("passwd",
				"somepw");
			connectionParameters.put("dbtype",
				"postgis");
			connectionParameters.put("host",
				"localhost");
			connectionParameters.put("Session close-up SQL",
				null);
			connectionParameters.put("encode functions",
				null);
			connectionParameters.put("validate connections",
				null);
			connectionParameters.put("max connections",
				null);
			connectionParameters.put("Primary key metadata table",
				null);
			connectionParameters.put("database",
				"postgis1");
			connectionParameters.put("namespace",
				null);
			connectionParameters.put("schema",
				null);
			connectionParameters.put("Loose bbox",
				null);
			connectionParameters.put("Expose primary keys",
				null);
			connectionParameters.put("Session startup SQL",
				null);
			connectionParameters.put("fetch size",
				null);
			connectionParameters.put("Max open prepared statements",
				null);
			connectionParameters.put("preparedStatements",
				null);
			connectionParameters.put("Estimated extends",
				null);
			connectionParameters.put("user",
				"pgalise");
			connectionParameters.put("min connections",
				null);
			POSTGIS_OSM_DATA_STORE = DataStoreFinder.getDataStore(connectionParameters);
			if (POSTGIS_OSM_DATA_STORE == null) {
				throw new RuntimeException("Could not connect - check parameters");
			}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	private MainCtrlUtils() {
	}
	
}
