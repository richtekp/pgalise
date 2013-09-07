/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import org.hsqldb.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author richter
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
		{WeatherCollectorTest.class, WeatherStationManagerTest.class, WeatherServiceManagerTest.class})
public class DatabaseTestSuite {
	private static Server SERVER;

	@BeforeClass
	@SuppressWarnings("SleepWhileInLoop")
	public static void setUpClass() throws Exception {		
		SERVER = new Server();
		SERVER.setAddress("127.0.0.1");
		SERVER.setPort(5201);
		SERVER.setDatabaseName(0,
			"pgalise");
		SERVER.setDatabasePath(0,
			"target/pgalise");
		SERVER.setNoSystemExit(true);
		SERVER.start();
		while(SERVER.getState() != 1) {
			Thread.sleep(100);
		}
		try (Connection conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:5201/pgalise;create=true", "sa", "")) {
			ResultSet users = conn.createStatement().executeQuery("select * from information_schema.system_users where USER_NAME = 'pgalise'");
			if(!users.next()) {
				conn.createStatement().execute("create user \"pgalise\" password \"somepw\"");
			}
			ResultSet schemata = conn.getMetaData().getSchemas(null,
				"PGALISE");
			if(!schemata.next()) {
				conn.createStatement().execute("create schema pgalise authorization \"pgalise\"");
			}
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		SERVER.shutdown();
	}

	private DatabaseTestSuite() {
	}
		
}