/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.ejb.embeddable.EJBContainer;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author richter
 */
public class TestUtils {

	public static EJBContainer createContainer() {
		Properties p;
		try (InputStream propInFile = TestUtils.class.getResourceAsStream("/database.properties")) {
			p = new Properties();
			p.loadFromXML(propInFile);
			p.setProperty("javax.persistence.transactionType", "RESOURCE_LOCAL");
			p.put("weatherData", "new://Resource?type=DataSource");
			p.put("weatherData.JdbcDriver", "org.hsqldb.jdbcDriver");
			p.put("weatherData.JdbcUrl", "jdbc:hsqldb:hsql://127.0.0.1:5201/pgalise");
			p.put("weatherData.userName", "pgalise");
			p.put("weatherData.password", "somepw");
			p.put("weatherData.JtaManaged",
				"true");
			
			p.put("weatherDataTest", "new://Resource?type=DataSource");
			p.put("weatherDataTest.JdbcDriver", "org.hsqldb.jdbcDriver");
			p.put("weatherDataTest.JdbcUrl", "jdbc:hsqldb:hsql://127.0.0.1:5201/pgalise_test");
			p.put("weatherDataTest.userName", "pgalise");
			p.put("weatherDataTest.password", "somepw");
			p.put("weatherDataTest.JtaManaged",
				"false");
			
			p.setProperty("eclipselink.logging.level", "ALL");
			p.setProperty("eclipselink.logging.session",
				"true");
			
		p.put("hibernate.dialect",
			"org.hibernate.dialect.HSQLDialect");

		} catch (IOException e) {
			throw new RuntimeException(e);
		}	
		EJBContainer container= EJBContainer.createEJBContainer(p);
		return container;
	}
	
	public static EntityManagerFactory createEntityManagerFactory(String unitName) {
		Properties p = new Properties();
		p.put(
			"hibernate.dialect",
//			"org.hibernate.dialect.PostgreSQLDialect"
			"org.hibernate.spatial.dialect.postgis.PostgisDialect"
		);
//		p.put("weatherDataTest", "new://Resource?type=DataSource");
//		p.put("weatherDataTest.JdbcDriver", "org.hsqldb.jdbcDriver");
//		p.put("weatherDataTest.JdbcUrl", "jdbc:hsqldb:hsql://127.0.0.1:5201/pgalise_test");
//		p.put("weatherDataTest.userName", "pgalise");
//		p.put("weatherDataTest.password", "somepw");
//		p.put("weatherDataTest.JtaManaged",
//			"false");
		
//		p.put("javax.persistence.jdbc.driver", "org.hsqldb.jdbcDriver");
//		p.put("javax.persistence.jdbc.url", "jdbc:hsqldb:hsql://127.0.0.1:5201/pgalise_test");
//		p.put("javax.persistence.jdbc.user", "pgalise");
//		p.put("javax.persistence.jdbc.password", "somepw");		
		p.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
		p.put("javax.persistence.jdbc.url", "jdbc:postgresql://127.0.0.1:5201/weather_data");
		p.put("javax.persistence.jdbc.user", "postgis");
		p.put("javax.persistence.jdbc.password", "postgis");		
		p.put("hibernate.hbm2ddl.auto", "create-drop");
		EntityManagerFactory retValue = Persistence.createEntityManagerFactory(unitName, p);
		return retValue;
	}

	private TestUtils() {
	}
}
