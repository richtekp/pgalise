/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.internal.modifier;

import com.vividsolutions.jts.geom.GeometryFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.ejb.embeddable.EJBContainer;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * a toolbox managing integration tests with embedded OpenEJB container and 
 * persistent manager which can join the JTA-managed entity manager of the 
 * container
 * @author richter
 */
public class DatabaseTestUtils {
	private static EJBContainer CONTAINER;
	private static EntityManagerFactory ENTITY_MANAGER_FACTORY ;
	private static EntityManager ENTITY_MANAGER;
	private static GeometryFactory GEOMETRY_FACTORY;

	public static GeometryFactory getGEOMETRY_FACTORY() {
		if(GEOMETRY_FACTORY == null) {
			GEOMETRY_FACTORY = new GeometryFactory();
		}
		return GEOMETRY_FACTORY;
	}

	public static EntityManager getENTITY_MANAGER() {
		Properties map = new Properties();
		map.put("openejb.classloader.forced-skip", "org.xml.sax");
		map.put("weatherDataTest", "new://Resource?type=javax.sql.DataSource");
		map.put("weatherDataTest.java.naming.factory.initial", "org.apache.openejb.client.LocalInitialContextFactory");
		map.put("weatherDataTest.JdbcDriver", "org.postgis.DriverWrapper");
		map.put("weatherDataTest.JdbcUrl", "jdbc:postgresql://127.0.0.1:5201/data");
		map.put("weatherDataTest.JtaManaged", "true");
		map.put("weatherDataTest.DefaultAutoCommit", "false");
		map.put("weatherDataTest.UserName", "postgis");
		map.put("weatherDataTest.Password", "postgis");
		map.put("java.naming.factory.initial", "org.apache.openejb.client.LocalInitialContextFactory");
		System.getProperties().putAll(map);
		if(ENTITY_MANAGER_FACTORY == null) {
			ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("weather_data_test", map);
		}else {
			ENTITY_MANAGER_FACTORY.close();
			ENTITY_MANAGER_FACTORY = Persistence.createEntityManagerFactory("weather_data_test", map);
		}
		return ENTITY_MANAGER_FACTORY.createEntityManager(map);
	}

	public static EJBContainer getCONTAINER() {
		if(CONTAINER == null) {
			try {
				Properties prop = new Properties();
				prop.load(ColdDayEventTest.class.getResourceAsStream("/META-INF/jndi.properties"));
				System.getProperties().putAll(prop);
	//			Configuration a = new Configuration().configure("/META-INF/hibernate.cfg.xml");
				CONTAINER = EJBContainer.createEJBContainer(prop);
			} catch (IOException ex) {
				throw new ExceptionInInitializerError(ex);
			}
		}
		return CONTAINER;
	}

	private DatabaseTestUtils() {
	}
	
	
//	private final static EJBContainer container;
//	static {
//		try {
//			Class.forName(
//				"org.postgis.DriverWrapper"
//				//"org.apache.derby.jdbc.EmbeddedDriver"
//			);
////			Class.forName("org.postgresql.Driver");
//		}catch(ClassNotFoundException ex) {
//			throw new RuntimeException(ex);
//		}
////		try {
//			Properties prop = new Properties();
//			// Load EJB properties
////			prop.load(DefaultWeatherServiceTest.class.getResourceAsStream("/META-INF/jndi.properties"));
//			prop.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.core.LocalInitialContextFactory");
//			prop.put("weatherData", "new://Resource?type=DataSource");
////			prop.put("weatherData.JdbcDriver", "org.postgresql.Driver");
////			prop.put("weatherData.JdbcUrl", "jdbc:postgresql://127.0.0.1:5201/weather_data");
////			prop.put("weatherData.JtaManaged", "true");
////			prop.put("weatherData.user", "postgis");
////			prop.put("weatherData.password", "postgis");
//			prop.put("weatherData.JdbcDriver", "org.apache.derby.jdbc.EmbeddedDriver");
//			prop.put("weatherData.JdbcUrl", "jdbc:derby:derbyDB;create=true");
//			prop.put("weatherData.JtaManaged", "true");
//			
////			System.setProperty("java.naming", DISABLE_CLASS_MOCKING)
////			System.getProperties().putAll(prop);
////			System.setProperties(prop);
////			Configuration a = new Configuration().configure("/META-INF/hibernate.cfg.xml");
//			container = EJBContainer.createEJBContainer(prop);
////		} catch (IOException ex) {
////			throw new ExceptionInInitializerError(ex);
////		}
//	}
}
