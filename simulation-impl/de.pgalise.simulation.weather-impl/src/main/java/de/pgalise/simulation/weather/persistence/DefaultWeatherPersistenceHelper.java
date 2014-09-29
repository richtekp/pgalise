/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.weather.persistence;

import de.pgalise.simulation.persistence.AbstractPersistenceHelper;
import de.pgalise.simulation.weather.entity.AbstractStationData;
import de.pgalise.simulation.weather.entity.ServiceDataCurrent;
import de.pgalise.simulation.weather.entity.WeatherCondition;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;


@Stateless
public class DefaultWeatherPersistenceHelper extends AbstractPersistenceHelper implements WeatherPersistenceHelper {
  private static final long serialVersionUID = 1L;

  public DefaultWeatherPersistenceHelper() {
  }

  @Override
  public void saveOrUpdateServiceDataCurrent(EntityManager entityManager, ServiceDataCurrent serviceDataCurrent) {
    super.saveOrUpdate(entityManager, serviceDataCurrent.getCondition(), WeatherCondition.class, serviceDataCurrent.getCondition().getId());
    super.saveOrUpdateCity(entityManager, serviceDataCurrent.getCity());
    super.saveOrUpdate(entityManager, serviceDataCurrent, ServiceDataCurrent.class, serviceDataCurrent.getId());
  }  
}
