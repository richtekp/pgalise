/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.weather.persistence;

import de.pgalise.simulation.persistence.PersistenceHelper;
import de.pgalise.simulation.weather.entity.AbstractStationData;
import de.pgalise.simulation.weather.entity.ServiceDataCurrent;
import javax.persistence.EntityManager;

/**
 *
 * @author richter
 */
/*
internal implementation notes:
- saveOrUpdate routine for AbstractStationData and subclasses 
doesn't make sense because it doesn't have relations
*/
public interface WeatherPersistenceHelper extends PersistenceHelper {
  
  void saveOrUpdateServiceDataCurrent(EntityManager entityManager,
   ServiceDataCurrent serviceDataCurrent);
}
