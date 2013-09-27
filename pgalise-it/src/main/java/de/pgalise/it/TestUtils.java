/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.it;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.shared.persistence.Identifiable;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.TrafficGraph;
import de.pgalise.simulation.weather.model.DefaultServiceDataCurrent;
import de.pgalise.simulation.weather.model.DefaultServiceDataForecast;
import de.pgalise.simulation.weather.model.DefaultWeatherCondition;
import de.pgalise.simulation.weather.model.StationDataNormal;
import de.pgalise.simulation.weather.util.DateConverter;
import java.sql.Date;
import java.sql.Time;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
			p.setProperty("pgalise", "new://Resource?type=DataSource");
			p.setProperty("pgalise.JdbcDriver", "org.postgresql.Driver");
			p.setProperty("pgalise.JdbcUrl", "jdbc:postgresql://127.0.0.1:5201/pgalise_test");
			p.setProperty("pgalise.UserName", "postgis");
			p.setProperty("pgalise.Password", "postgis");
			p.setProperty("pgalise.JtaManaged",	"true");
			
//			p.setProperty("pgaliseTest", "new://Resource?type=DataSource");
//			p.setProperty("pgaliseTest.JdbcDriver", "org.postgresql.Driver");
//			p.setProperty("pgaliseTest.JdbcUrl", "jdbc:postgresql://127.0.0.1:5201/pgalise_test");
//			p.setProperty("pgaliseTest.UserName", "postgis");
//			p.setProperty("pgaliseTest.Password", "postgis");
//			p.setProperty("pgaliseTest.JtaManaged",	"true");

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
	 * sets up entities of type <tt>DefaultServiceDataForecast</tt> for startTimestamp at midnight, the day before startTimestamp at midnight and endTimestamp at midnight
	 * @param startTimestamp
	 * @param endTimestamp
	 * @param city
	 * @param utx
	 * @param entityManagerFactory
	 * @return
	 * @throws NotSupportedException
	 * @throws SystemException
	 * @throws HeuristicMixedException
	 * @throws HeuristicRollbackException
	 * @throws IllegalStateException
	 * @throws RollbackException 
	 */
	public static Map<Date, DefaultServiceDataForecast> setUpWeatherServiceDataForecast(long startTimestamp, long endTimestamp, City city, UserTransaction utx, EntityManagerFactory entityManagerFactory) throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
		long preceedingDayTimestampMidnight = DateConverter.convertTimestampToMidnight(
			startTimestamp)-DateConverter.ONE_DAY_IN_MILLIS;
		long endTimestampMidnight = DateConverter.convertTimestampToMidnight(
			endTimestamp);
		utx.begin();
		EntityManager em = entityManagerFactory.createEntityManager();
		em.joinTransaction();
		em.merge(city);
		long start = preceedingDayTimestampMidnight;
		Map<Date, DefaultServiceDataForecast> retValue = new HashMap<>();
		while(start < endTimestampMidnight+DateConverter.ONE_DAY_IN_MILLIS) {
			DefaultServiceDataForecast serviceDataForecast = new DefaultServiceDataForecast(new Date(start),
				new Time(start),
				city,
				Measure.valueOf(10.0f,
				SI.CELSIUS),
				Measure.valueOf(1.0f, SI.CELSIUS),
				1.0f,
				1.0f,
				1.0f,
				DefaultWeatherCondition.UNKNOWN_CONDITION
			);
			em.merge(serviceDataForecast);
			retValue.put(new Date(start), serviceDataForecast);
			start += DateConverter.ONE_DAY_IN_MILLIS;
		}
		em.close();
		utx.commit();
//		LOGGER.debug(String.format("persisting %s entities for following timestamps: preceedingDay=%d (%s), startMidnight=%d (%s), endMidnight=%d (%s)", DefaultServiceDataForecast.class.getName(), preceedingDayTimestamp, new Timestamp(preceedingDayTimestamp).toString(), startTimestampMidnight, new Timestamp(startTimestampMidnight).toString(), startTimestamp, new Timestamp(startTimestamp).toString(), endTimestampMidnight, new Timestamp(endTimestampMidnight).toString()));
		return retValue;
	}
	
	/**
	 * sets up entities of type <tt>DefaultServiceDataCurrent</tt> for startTimestamp at midnight, the day before startTimestamp at midnight and endTimestamp at midnight
	 * @param startTimestamp
	 * @param endTimestamp
	 * @param city
	 * @param utx
	 * @param entityManagerFactory
	 * @return
	 * @throws NotSupportedException
	 * @throws SystemException
	 * @throws HeuristicMixedException
	 * @throws HeuristicRollbackException
	 * @throws IllegalStateException
	 * @throws RollbackException 
	 */
	public static Map<Date, DefaultServiceDataCurrent> setUpWeatherServiceDataCurrent(long startTimestamp, long endTimestamp, City city, UserTransaction utx, EntityManagerFactory entityManagerFactory) throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
		long preceedingDayTimestampMidnight = DateConverter.convertTimestampToMidnight(
			startTimestamp)-DateConverter.ONE_DAY_IN_MILLIS;
		long endTimestampMidnight = DateConverter.convertTimestampToMidnight(
			endTimestamp);
		utx.begin();
		EntityManager em = entityManagerFactory.createEntityManager();
		em.joinTransaction();
		em.merge(city);
		long start = preceedingDayTimestampMidnight;
		Map<Date, DefaultServiceDataCurrent> retValue = new HashMap<>();
		while(start < endTimestampMidnight+DateConverter.ONE_DAY_IN_MILLIS) {
			DefaultServiceDataCurrent serviceDataCurrent = new DefaultServiceDataCurrent(new Date(start),
				new Time(start),
				city,
				1.0f,
				Measure.valueOf(1.0f, SI.CELSIUS),
				1.0f,
				1.0f,
				DefaultWeatherCondition.UNKNOWN_CONDITION
			);
			em.merge(serviceDataCurrent);
			retValue.put(new Date(start), serviceDataCurrent);
			start += DateConverter.ONE_DAY_IN_MILLIS;
		}
		em.close();
		utx.commit();
//		LOGGER.debug(String.format("persisting %s entities for following timestamps: preceedingDay=%d (%s), startMidnight=%d (%s), endMidnight=%d (%s)", DefaultServiceDataCurrent.class.getName(), preceedingDayTimestamp, new Timestamp(preceedingDayTimestamp).toString(), startTimestampMidnight, new Timestamp(startTimestampMidnight).toString(), endTimestampMidnight, new Timestamp(endTimestampMidnight).toString()));
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
	public static Map<Date, StationDataNormal> setUpWeatherStationData(long startTimestamp, long endTimestamp, UserTransaction utx, EntityManagerFactory entityManagerFactory) throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
		long preceedingDayTimestampMidnight = DateConverter.convertTimestampToMidnight(
			startTimestamp)-DateConverter.ONE_DAY_IN_MILLIS;
		long endTimestampMidnight = DateConverter.convertTimestampToMidnight(
			endTimestamp);
		utx.begin();
		EntityManager em = entityManagerFactory.createEntityManager();
		em.joinTransaction();
		long start = preceedingDayTimestampMidnight;
		Map<Date, StationDataNormal> retValue = new HashMap<>();
		while(start < endTimestampMidnight+DateConverter.ONE_DAY_IN_MILLIS) {
			StationDataNormal stationDataNormal = new StationDataNormal(new Date(start),
			new Time(start),
				1,
				1,
				1.0f,
				Measure.valueOf(1.0f, SI.CELSIUS),
				1.0f,
				1,
				1.0f,
				1.0f,
				1.0f);
			em.merge(stationDataNormal);
			retValue.put(new Date(start), stationDataNormal);
			start += DateConverter.ONE_DAY_IN_MILLIS;
		}
		StationDataNormal stationDataNormal = new StationDataNormal(new Date(startTimestamp),
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
		em.merge(stationDataNormal);
		retValue.put(new Date(startTimestamp), stationDataNormal);
		em.close();
		utx.commit();
//		LOGGER.debug(String.format("persisting %s entities for following timestamps: preceedingDay=%d (%s), startMidnight=%d (%s), start=%d (%s), endMidnight=%d (%s)", StationDataNormal.class.getName(), preceedingDayTimestamp, new Timestamp(preceedingDayTimestamp).toString(), startTimestampMidnight, new Timestamp(startTimestampMidnight).toString(), startTimestamp, new Timestamp(startTimestamp).toString(), endTimestampMidnight, new Timestamp(endTimestampMidnight).toString()));
		return retValue;
	}
	
	public static <T extends Identifiable> void tearDownWeatherData(Map<?, T> mutableStationDatas, Class<T> clazz, UserTransaction utx, EntityManagerFactory entityManagerFactory) throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
		utx.begin();
		EntityManager em = entityManagerFactory.createEntityManager();
		em.joinTransaction();
		for(T mutableStationData : mutableStationDatas.values()) {
//			em.refresh(mutableStationData);
			T attached = em.find(clazz,
				mutableStationData.getId());
			em.remove(attached);
		}
		utx.commit();
		em.close();
	}
	
	public static City createDefaultTestCityInstance() {
		
		Coordinate referencePoint = new Coordinate(52.516667, 13.4);
		Polygon referenceArea = GeoToolsBootstrapping.getGEOMETRY_FACTORY().createPolygon(
			new Coordinate[] {
				new Coordinate(referencePoint.x-1, referencePoint.y-1), 
				new Coordinate(referencePoint.x-1, referencePoint.y), 
				new Coordinate(referencePoint.x, referencePoint.y), 
				new Coordinate(referencePoint.x, referencePoint.y-1),
				new Coordinate(referencePoint.x-1, referencePoint.y-1)
			}
		);
		TrafficGraph<?> trafficGraph = new TrafficGraph<>(NavigationEdge.class);
		City city = new DefaultCity("Berlin",
			3375222,
			80,
			true,
			true,
			referenceArea, 
			trafficGraph);
		return city;
	}

	private TestUtils() {
	}
}
