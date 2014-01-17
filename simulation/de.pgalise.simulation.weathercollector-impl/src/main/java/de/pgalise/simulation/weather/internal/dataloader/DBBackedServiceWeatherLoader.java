/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.internal.dataloader;

import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.weather.dataloader.WeatherLoader;
import de.pgalise.simulation.weather.dataloader.WeatherMap;
import de.pgalise.simulation.weather.entity.ServiceDataCurrent;
import de.pgalise.simulation.weather.entity.ServiceDataForecast;
import de.pgalise.simulation.weather.entity.ServiceDataHelper;
import de.pgalise.simulation.weather.model.StationDataMap;
import de.pgalise.simulation.weathercollector.ServiceStrategyManager;
import de.pgalise.simulation.weathercollector.util.DatabaseManager;
import de.pgalise.simulation.weathercollector.weatherservice.ServiceStrategy;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang3.time.DateUtils;

/**
 * A {@link WeatherLoader} which lazily fetches weather data with a
 * {@link ServiceStrategy}, and saves the result into a {@link DatabaseManager}
 * where further requests are looked up before contacting weather service.
 *
 * This avoids the odd passive communication between weather collector and
 * weather loader which takes place by checking the database and throwing an
 * exception if it doesn't contain values.
 *
 * @TODO: in order to avoid to much API changes the methods which ought to
 * return data from weather stations currently return data from services...
 *
 * @author richter
 */
/*
 @TODO: maybe remove map cache because caching of EntityManager should be sufficient
 */
@Stateful
public class DBBackedServiceWeatherLoader implements WeatherLoader {

  @EJB
  private ServiceStrategyManager serviceStrategyManager;
  @EJB
  private DatabaseManager databaseManager;
  @PersistenceContext(unitName = "pgalise-weathercollector")
  private EntityManager entityManager;

  /**
   * saves instances of {@link ServiceDataHelper} so that
   * {@link ServiceDataCurrent} and ServiceDataForecast} can be retrieved from
   * it
   */
  private Map<Long, ServiceDataHelper> cache = new HashMap<>();

  @Override
  public boolean checkStationDataForDay(long timestamp) {
    return true;
  }

  @Override
  public ServiceDataCurrent loadCurrentServiceWeatherData(long timestamp,
    City city) {
    Date midnight = DateUtils.truncate(new Date(timestamp),
      Calendar.DATE);
    ServiceDataHelper cached = cache.get(midnight.getTime());
    if (cached == null) {
      Query query = entityManager.createNamedQuery(
        "ServiceDataCurrent.findByDate");
      query.setParameter("date",
        midnight);
      query.setParameter("city",
        city);
      try {
        cached = (ServiceDataHelper) query.getSingleResult();
      } catch (NoResultException ex) {
        cached = serviceStrategyManager.getPrimaryServiceStrategy().getWeather(
          city);
        databaseManager.saveServiceData(cached);
      }
      cache.put(midnight.getTime(),
        cached);
    }
    return cached.getCurrentCondition();
  }

  @Override
  public ServiceDataForecast loadForecastServiceWeatherData(long timestamp,
    City city) {
    Date midnight = DateUtils.truncate(new Date(timestamp),
      Calendar.DATE);
    ServiceDataHelper cached = cache.get(midnight.getTime());
    if (cached == null) {
      Query query = entityManager.createNamedQuery(
        "ServiceDataForecast.findByDate");
      query.setParameter("date",
        midnight);
      query.setParameter("city",
        city);
      try {
        cached = (ServiceDataHelper) query.getSingleResult();
      } catch (NoResultException ex) {
        cached = serviceStrategyManager.getPrimaryServiceStrategy().getWeather(
          city);
        databaseManager.saveServiceData(cached);
      }
      cache.put(midnight.getTime(),
        cached);
    }
    return cached.getForecastConditions().iterator().next();
  }

  /**
   *
   * @param timestamp
   * @return a {@link StationDataMap} which does nothing as specified
   */
  @Override
  public WeatherMap loadStationData(long timestamp) {
    return new StationDataMap();
  }

  @Override
  public void setLoadOption(boolean takeNormalData) {
    //do nothing
  }

}
