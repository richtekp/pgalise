/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.persistence;

import de.pgalise.simulation.shared.entity.City;
import java.io.Serializable;
import javax.persistence.EntityManager;

/**
 *
 * @author richter
 */
public interface PersistenceHelper extends Serializable {
  
  void saveOrUpdate(EntityManager entityManager, Serializable instance, Class<?> clazz, Object id) ;

  void saveOrUpdateCity(EntityManager entityManager,
    City city) ;
}
