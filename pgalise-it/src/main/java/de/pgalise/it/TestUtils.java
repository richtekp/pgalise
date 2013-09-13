/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.it;

import de.pgalise.simulation.weather.model.MutableStationData;
import de.pgalise.simulation.weather.model.StationDataNormal;
import de.pgalise.simulation.weather.util.DateConverter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.Time;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.ejb.embeddable.EJBContainer;
import javax.measure.Measure;
import javax.measure.unit.SI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

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
		p.setProperty(
			"hibernate.dialect",
//			"org.hibernate.dialect.PostgreSQLDialect"
			"org.hibernate.spatial.dialect.postgis.PostgisDialect"
		);
			
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
	
	/**
	 * saves {@link StationDataNormal} for the day of timestamp at 00:00, the 
	 * preceeding day at 00:00 and the following day at 00:00 and returns the 
	 * persisted entities, which are supposed to be removed from the 
	 * database/entity manager by the user
	 * @param timestamp
	 * @return 
	 */
	public static Collection<MutableStationData> setUpWeatherData(long timestamp, UserTransaction utx, EntityManagerFactory entityManagerFactory) throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
		long startTimestampMidnight = DateConverter.convertTimestampToMidnight(
			timestamp);
		long endTimestampMidnight = DateConverter.convertTimestampToMidnight(
			timestamp+DateConverter.ONE_DAY_IN_MILLIS);
		MutableStationData serviceWeather = new StationDataNormal(new Date(startTimestampMidnight),
			new Time(DateConverter.convertTimestampToMidnight(startTimestampMidnight)),
			1,
			1,
			1.0f,
			Measure.valueOf(1.0f, SI.CELSIUS),
			1.0f,
			1,
			1.0f,
			1.0f,
			1.0f);
		
		MutableStationData serviceWeatherPreviousDay = new StationDataNormal(new Date(startTimestampMidnight),
			new Time(DateConverter.convertTimestampToMidnight(startTimestampMidnight-DateConverter.ONE_DAY_IN_MILLIS)),
			1,
			1,
			1.0f,
			Measure.valueOf(1.0f, SI.CELSIUS),
			1.0f,
			1,
			1.0f,
			1.0f,
			1.0f); //for the DatabaseWeatherLoader (loads current, previous and following day)
		MutableStationData serviceWeatherForecast = new StationDataNormal(new Date(endTimestampMidnight),
			new Time(DateConverter.convertTimestampToMidnight(endTimestampMidnight)),
			1,
			1,
			1.0f,
			Measure.valueOf(1.0f, SI.CELSIUS),
			1.0f,
			1,
			1.0f,
			1.0f,
			1.0f);
		MutableStationData weather = new StationDataNormal(new Date(timestamp),
			new Time(DateConverter.convertTimestampToMidnight(timestamp)),
			1,
			1,
			1.0f,
			Measure.valueOf(1.0f, SI.CELSIUS),
			1.0f,
			1,
			1.0f,
			1.0f,
			1.0f);
		utx.begin();
		EntityManager em = entityManagerFactory.createEntityManager();
		em.joinTransaction();
		em.persist(serviceWeather);
		em.persist(serviceWeatherPreviousDay);
		em.persist(serviceWeatherForecast);
		em.persist(weather);
		utx.commit();
		Collection<MutableStationData> retValue = new LinkedList<>(Arrays.asList(serviceWeather, serviceWeatherPreviousDay, serviceWeatherForecast, weather));
		return retValue;
	}
	
	public static void tearDownWeatherData(Collection<MutableStationData> mutableStationDatas, UserTransaction utx, EntityManagerFactory entityManagerFactory) throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
		utx.begin();
		EntityManager em = entityManagerFactory.createEntityManager();
		em.joinTransaction();
		for(MutableStationData mutableStationData : mutableStationDatas) {
			em.remove(mutableStationData);
		}
		utx.commit();
		em.close();
	}

	private TestUtils() {
	}
}
