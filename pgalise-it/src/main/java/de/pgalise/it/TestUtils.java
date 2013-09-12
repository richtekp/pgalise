/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.it;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
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
//			p.loadFromXML(propInFile);
//			p.setProperty("javax.persistence.transactionType", "RESOURCE_LOCAL");
			p.setProperty("weatherData", "new://Resource?type=DataSource");
//			p.setProperty("weatherData.JdbcDriver", "org.hsqldb.jdbcDriver");
//			p.setProperty("weatherData.JdbcUrl", "jdbc:hsqldb:hsql://127.0.0.1:5201/pgalise");
//			p.setProperty("weatherData.userName", "pgalise");
//			p.setProperty("weatherData.password", "somepw");
			p.setProperty("weatherData.JdbcDriver", "org.postgresql.Driver");
			p.setProperty("weatherData.JdbcUrl", "jdbc:postgresql://127.0.0.1:5201/weather_data");
			p.setProperty("weatherData.UserName", "postgis");
			p.setProperty("weatherData.Password", "postgis");
			p.setProperty("weatherData.JtaManaged",
				"true");
			
//			p.setProperty("weatherDataTest", "new://Resource?type=javax.sql.DataSource");
////			p.setProperty("weatherDataTest.JdbcDriver", "org.hsqldb.jdbcDriver");
////			p.setProperty("weatherDataTest.JdbcUrl", "jdbc:hsqldb:hsql://127.0.0.1:5201/pgalise_test");
////			p.setProperty("weatherDataTest.userName", "pgalise");
////			p.setProperty("weatherDataTest.password", "somepw");
//			p.setProperty("weatherDataTest.JdbcDriver", "org.postgresql.Driver");
//			p.setProperty("weatherDataTest.JdbcUrl", "jdbc:postgresql://127.0.0.1:5201/weather_data");
//			p.setProperty("weatherDataTest.userName", "postgis");
//			p.setProperty("weatherDataTest.password", "postgis");
//			p.setProperty("weatherDataTest.JtaManaged",
//				"false");
			
//			p.setProperty("eclipselink.logging.level", "ALL");
//			p.setProperty("eclipselink.logging.session",
//				"true");
			
//		p.put("hibernate.dialect",
//			"org.hibernate.dialect.HSQLDialect");
		//for parser issues:
			p.setProperty("openejb.classloader.forced-skip", "org.xml.sax");		
			
			p.setProperty("openejb.validation.output.level", "VERBOSE");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}	
		EJBContainer container= EJBContainer.createEJBContainer(p);
		return container;
	}
	
	public static EntityManagerFactory createEntityManagerFactory(String unitName) {
		
		Properties p = new Properties();
		p.setProperty(
			"hibernate.dialect",
//			"org.hibernate.dialect.PostgreSQLDialect"
			"org.hibernate.spatial.dialect.postgis.PostgisDialect"
		);
//		p.setProperty("weatherDataTest", "new://Resource?type=DataSource");
//		p.setProperty("weatherDataTest.JdbcDriver", "org.hsqldb.jdbcDriver");
//		p.setProperty("weatherDataTest.JdbcUrl", "jdbc:hsqldb:hsql://127.0.0.1:5201/pgalise_test");
//		p.setProperty("weatherDataTest.userName", "pgalise");
//		p.setProperty("weatherDataTest.password", "somepw");
//		p.setProperty("weatherDataTest.JtaManaged",
//			"false");
		
//		p.setProperty("javax.persistence.jdbc.driver", "org.hsqldb.jdbcDriver");
//		p.setProperty("javax.persistence.jdbc.url", "jdbc:hsqldb:hsql://127.0.0.1:5201/pgalise_test");
//		p.setProperty("javax.persistence.jdbc.user", "pgalise");
//		p.setProperty("javax.persistence.jdbc.password", "somepw");		
		p.setProperty("javax.persistence.jdbc.driver", "org.postgresql.Driver");
		p.setProperty("javax.persistence.jdbc.url", "jdbc:postgresql://127.0.0.1:5201/weather_data");
		p.setProperty("javax.persistence.jdbc.user", "postgis");
		p.setProperty("javax.persistence.jdbc.password", "postgis");		
		p.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		p.setProperty("hibernate.show_sql", "false");
		EntityManagerFactory retValue = Persistence.createEntityManagerFactory(unitName, p);
		return retValue;
	}

	private TestUtils() {
	}
}
