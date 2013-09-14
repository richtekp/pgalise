/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.it;

import de.pgalise.simulation.weather.model.MutableStationData;
import de.pgalise.simulation.weather.model.StationDataNormal;
import de.pgalise.simulation.weather.util.DateConverter;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author richter
 */
public class TestUtils {
	private final static Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);
	private static EJBContainer container;
	
	public static EJBContainer getContainer() {
		if(container == null) {
			Properties p;
			p = new Properties();
			p.setProperty("weatherData", "new://Resource?type=DataSource");
			p.setProperty("weatherData.JdbcDriver", "org.postgresql.Driver");
			p.setProperty("weatherData.JdbcUrl", "jdbc:postgresql://127.0.0.1:5201/pgalise");
			p.setProperty("weatherData.UserName", "postgis");
			p.setProperty("weatherData.Password", "postgis");
			p.setProperty("weatherData.JtaManaged",	"true");
			
			p.setProperty("weatherDataTest", "new://Resource?type=DataSource");
			p.setProperty("weatherDataTest.JdbcDriver", "org.postgresql.Driver");
			p.setProperty("weatherDataTest.JdbcUrl", "jdbc:postgresql://127.0.0.1:5201/pgalise_test");
			p.setProperty("weatherDataTest.UserName", "postgis");
			p.setProperty("weatherDataTest.Password", "postgis");
			p.setProperty("weatherDataTest.JtaManaged",	"true");

			p.setProperty(
				"hibernate.dialect",
				"org.hibernate.spatial.dialect.postgis.PostgisDialect"
			);
			p.setProperty("openejb.classloader.forced-skip", "org.xml.sax");		
			p.setProperty("openejb.validation.output.level", "VERBOSE");
			container= EJBContainer.createEJBContainer(p);
		}
		return container;
	}
	
	public static EntityManagerFactory createEntityManagerFactory(String unitName) {
		return createEntityManagerFactory(unitName,
			null);
	}
	
	public static EntityManagerFactory createEntityManagerFactory(String unitName, Properties initialProperties) {
		Properties p = initialProperties;		
		if(p ==  null) {
			p = new Properties();
		}
			
//		p.setProperty(
//			"hibernate.dialect",
//			"org.hibernate.spatial.dialect.postgis.PostgisDialect"
//		);
//		p.setProperty("javax.persistence.jdbc.driver", "org.postgresql.Driver");
//		p.setProperty("javax.persistence.jdbc.url", "jdbc:postgresql://127.0.0.1:5201/weather_data");
//		p.setProperty("javax.persistence.jdbc.user", "postgis");
//		p.setProperty("javax.persistence.jdbc.password", "postgis");		
//		p.setProperty("hibernate.hbm2ddl.auto", "create-drop");
//		p.setProperty("hibernate.show_sql", "false");
		EntityManagerFactory retValue = Persistence.createEntityManagerFactory(unitName, p);
		return retValue;
	}
	
	/**
	 * saves {@link StationDataNormal} for the day of timestamp at the actual tima and at 00:00, the 
	 * preceeding day at 00:00 and the following day at 00:00 and returns the 
	 * persisted entities, which are supposed to be removed from the 
	 * database/entity manager by the user (possibly using {@link #tearDownWeatherData(java.util.Collection, javax.transaction.UserTransaction, javax.persistence.EntityManagerFactory) }
	 * @param startTimestamp 
	 * @param endTimestamp 
	 * @param utx 
	 * @param entityManagerFactory 
	 * @return 
	 * @throws NotSupportedException 
	 * @throws SystemException 
	 * @throws RollbackException 
	 * @throws HeuristicMixedException 
	 * @throws HeuristicRollbackException 
	 * @throws IllegalStateException 
	 * @see #tearDownWeatherData(java.util.Collection, javax.transaction.UserTransaction, javax.persistence.EntityManagerFactory) 
	 */
	public static Collection<StationDataNormal> setUpWeatherData(long startTimestamp, long endTimestamp, UserTransaction utx, EntityManagerFactory entityManagerFactory) throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
		long startTimestampMidnight = DateConverter.convertTimestampToMidnight(
			startTimestamp);
		long endTimestampMidnight = DateConverter.convertTimestampToMidnight(
			endTimestamp);
		utx.begin();
		EntityManager em = entityManagerFactory.createEntityManager();
		StationDataNormal serviceWeather = new StationDataNormal(new Date(startTimestampMidnight),
			new Time(startTimestampMidnight),
			1,
			1,
			1.0f,
			Measure.valueOf(1.0f, SI.CELSIUS),
			1.0f,
			1,
			1.0f,
			1.0f,
			1.0f);
		
		long preceedingDayTimestamp = startTimestampMidnight-DateConverter.ONE_DAY_IN_MILLIS;
		StationDataNormal serviceWeatherPreviousDay = new StationDataNormal(new Date(preceedingDayTimestamp),
			new Time(preceedingDayTimestamp),
			1,
			1,
			1.0f,
			Measure.valueOf(1.0f, SI.CELSIUS),
			1.0f,
			1,
			1.0f,
			1.0f,
			1.0f); //for the DatabaseWeatherLoader (loads current, previous and following day)
		StationDataNormal serviceWeatherForecast = new StationDataNormal(new Date(endTimestampMidnight),
			new Time(endTimestampMidnight),
			1,
			1,
			1.0f,
			Measure.valueOf(1.0f, SI.CELSIUS),
			1.0f,
			1,
			1.0f,
			1.0f,
			1.0f);
		StationDataNormal weather = new StationDataNormal(new Date(startTimestamp),
			new Time(startTimestamp),
			1,
			1,
			1.0f,
			Measure.valueOf(1.0f, SI.CELSIUS),
			1.0f,
			1,
			1.0f,
			1.0f,
			1.0f);
		em.joinTransaction();
		em.persist(serviceWeather);
		em.persist(serviceWeatherPreviousDay);
		em.persist(serviceWeatherForecast);
		em.persist(weather);
		utx.commit();
		em.close();
		Collection<StationDataNormal> retValue = new LinkedList<>(Arrays.asList(serviceWeather, serviceWeatherPreviousDay, serviceWeatherForecast, weather));
		LOGGER.debug(String.format("persisting %s entities for following timestamps: preceedingDay=%d (%s), startMidnight=%d (%s), start=%d (%s), endMidnight=%d (%s)", MutableStationData.class.getSimpleName(), preceedingDayTimestamp, new Timestamp(preceedingDayTimestamp).toString(), startTimestampMidnight, new Timestamp(startTimestampMidnight).toString(), startTimestamp, new Timestamp(startTimestamp).toString(), endTimestampMidnight, new Timestamp(endTimestampMidnight).toString()));
		return retValue;
	}
	
	public static void tearDownWeatherData(Collection<StationDataNormal> mutableStationDatas, UserTransaction utx, EntityManagerFactory entityManagerFactory) throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
		utx.begin();
		EntityManager em = entityManagerFactory.createEntityManager();
		em.joinTransaction();
		for(StationDataNormal mutableStationData : mutableStationDatas) {
//			em.refresh(mutableStationData);
			StationDataNormal attached = em.find(StationDataNormal.class,
				mutableStationData.getId());
			em.remove(attached);
		}
		utx.commit();
		em.close();
	}

	private TestUtils() {
	}
}
