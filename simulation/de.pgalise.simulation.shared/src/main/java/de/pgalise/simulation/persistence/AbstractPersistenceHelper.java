/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.persistence;

import de.pgalise.simulation.shared.entity.BaseBoundary;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.entity.BaseCoordinatePK;
import de.pgalise.simulation.shared.entity.City;
import java.io.Serializable;
import javax.persistence.EntityManager;


public abstract class AbstractPersistenceHelper implements PersistenceHelper {
  private static final long serialVersionUID = 1L;

  public AbstractPersistenceHelper() {
  }

  @Override
  public void saveOrUpdate(EntityManager entityManager, Serializable instance, Class<?> clazz, Object id) {
    if (entityManager.find(clazz,
            id) == null) {
      entityManager.persist(instance);
    } else {
      entityManager.merge(instance);
    }
    entityManager.flush();
  }

  @Override
  public void saveOrUpdateCity(EntityManager entityManager,
          City city) {
    saveOrUpdate(entityManager,
            city.getBoundary().getReferencePoint(),
            BaseCoordinate.class,
            new BaseCoordinatePK(city.getBoundary().getReferencePoint().getX(), 
                    city.getBoundary().getReferencePoint().getY()));
    saveOrUpdate(entityManager, city.getBoundary(), 
            BaseBoundary.class, 
            city.getBoundary().getId()
    );
    saveOrUpdate(entityManager,
            city,
            City.class,
            city.getId());
  }
}
