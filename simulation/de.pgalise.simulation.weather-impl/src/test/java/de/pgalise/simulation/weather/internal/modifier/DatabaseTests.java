/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.internal.modifier;

import de.pgalise.simulation.weather.internal.service.DefaultWeatherServiceTest;
import java.sql.Connection;
import java.sql.DriverManager;
//import org.apache.derby.drda.NetworkServerControl;
//import org.hsqldb.Server;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 *
 * @author richter
 */
@RunWith(Suite.class)
@SuiteClasses({ CityClimateTest.class, ColdDayEventTest.class , HotDayEventTest.class, RainDayEventTest.class, ReferenceCityTest.class, StormDayEventTest.class, DefaultWeatherServiceTest.class})
public class DatabaseTests {
//	private final static NetworkServerControl NETWORK_SERVER_CONTROL;
//	static {
//		try {
//			NETWORK_SERVER_CONTROL = new NetworkServerControl(Inet4Address.getByName("127.0.0.1"), 5201);
//		} catch (Exception ex) {
//			throw new ExceptionInInitializerError(ex);
//		}
//	}
//	private final static Server SERVER = new Server();
	
	/**
	 * makes setup usable outside test suite
	 * @see DatabaseTests#ensureDatabaseShutDown() 
	 * @throws Exception 
	 */
	public static void ensureDatabaseReady() throws Exception {
		try {
//			Class.forName("org.apache.derby.jdbc.ClientDriver");
//			Class.forName("org.hsqldb.jdbcDriver");
			Class.forName("org.postgresql.Driver");
		}catch(ClassNotFoundException ex) {
			throw new RuntimeException(ex);
		}
//		try {
////			Connection connection = DriverManager.getConnection("jdbc:derby://127.0.0.1:5201/");
//			Connection connection = DriverManager.getConnection("jdbc:hsqldb:hsql://127.0.0.1:5201/database"); //connect to database "database" regardless whether it exists in order to avoid SQLException "database alias does not exist"
//			connection.close();
//		}catch(SQLException ex) {
////			NETWORK_SERVER_CONTROL.start(new PrintWriter(System.out));
////			boolean connected = false;
////			while(!connected) {
////				try {
////					NETWORK_SERVER_CONTROL.ping();
////					connected = true;
////				}catch(Exception ex0) {
////					Thread.sleep(100);
////				}
////			}
//			SERVER.setAddress("127.0.0.1");
//			SERVER.setPort(5201);
//			SERVER.setNoSystemExit(true);
//			SERVER.setDatabasePath(0, "./database");
//			SERVER.setDatabaseName(0, "database");
//			SERVER.setTrace(false);
//			SERVER.start();
//		}
//		Connection connection = DriverManager.getConnection("jdbc:derby://127.0.0.1:5201/weather_database;create=true");
//		Connection connection = DriverManager.getConnection("jdbc:hsqldb:hsql://127.0.0.1:5201/database");
//		Connection connection = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5201/postgres", "postgis", "postgis");
//		if(connection.getSchema() == null) {
//			connection.createStatement().execute("CREATE SCHEMA database");
//		}
//		connection.close();
	}
	
	/**
	 * makes shutdown usable outside test suite
	 * @see DatabaseTests#ensureDatabaseShutDown() 
	 * @throws Exception 
	 */
	public static void ensureDatabaseShutDown() throws Exception {
//		NETWORK_SERVER_CONTROL.shutdown();
//		SERVER.shutdown();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ensureDatabaseReady();
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception {
		ensureDatabaseShutDown();
	}

	protected DatabaseTests() {
	}
}
