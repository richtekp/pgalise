/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.shared;

import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.entity.BaseCoordinatePK;
import de.pgalise.simulation.shared.entity.City;
import javax.persistence.EntityManager;

/**
 *
 * @author richter
 */
public class PersistenceUtil {
  
  
  public static <T> void saveOrUpdate(EntityManager entityManager, T instance, Class<T> clazz, Object id) {
    if(entityManager.find(clazz,
      id) == null) {
      entityManager.persist(instance);
    }else {
      entityManager.merge(instance);
    }
  }

  public static void saveOrUpdateCity(EntityManager entityManager,
    City city) {    
    PersistenceUtil.saveOrUpdate(entityManager,
      city.getBoundary().getReferencePoint(),
      BaseCoordinate.class,
      new BaseCoordinatePK(city.getBoundary().getReferencePoint().getX(), city.getBoundary().getReferencePoint().getY()));
    for(BaseCoordinate boundaryCoordinate : city.getBoundary().getBoundaryCoordinates()) {      
      PersistenceUtil.saveOrUpdate(entityManager,
        boundaryCoordinate,
        BaseCoordinate.class,
        new BaseCoordinatePK(boundaryCoordinate.getX(), boundaryCoordinate.getY()));
    }
    PersistenceUtil.saveOrUpdate(entityManager,
      city,
      City.class,
      city.getId());
  }

  private PersistenceUtil() {
  }
}
