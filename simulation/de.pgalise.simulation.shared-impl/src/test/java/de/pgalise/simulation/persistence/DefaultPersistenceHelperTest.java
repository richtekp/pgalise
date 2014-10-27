/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.persistence;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.BaseCoordinatePK;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.testutils.TestUtils;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author richter
 */
/*
internal implementation notes:
- if at some point the class hierarchy is changed and there's no longer 
a class without transitive properties, use the simplest one instead of 
creating an extra project (which won't be recognized correctly probably 
anyway)
*/
public class DefaultPersistenceHelperTest {
  /*
   internal implementation notes:
   - no need to test injection here as this is not EJB module
   */

  @EJB
  private IdGenerator idGenerator;
  @PersistenceContext
  private EntityManager entityManager;
  @Resource
  private UserTransaction userTransaction;
  @EJB
  private PersistenceHelper persistenceUtil;

  public DefaultPersistenceHelperTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContainer().getContext().bind("inject", this);
  }

  /**
   * Test of saveOrUpdate method, of class PersistenceHelper.
   *
   * @throws javax.transaction.NotSupportedException
   * @throws javax.transaction.SystemException
   * @throws javax.transaction.HeuristicMixedException
   * @throws javax.transaction.HeuristicRollbackException
   * @throws javax.transaction.RollbackException
   */
  @Test
  public void testSaveOrUpdate() throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
    // test instance should be an entity without transitive properties, but 
    //one property which can be changed (e.g. not BaseCoordinate as it is 
    //immuatble)
    BaseCoordinatePK id = new BaseCoordinatePK(1.3, 1.3);
    NavigationNode instance = new NavigationNode(
      1.3,1.3
    );
    instance.setOffice(false);
    userTransaction.begin();
    try {
      // test save
      persistenceUtil.saveOrUpdate(
        entityManager,
        instance, 
        NavigationNode.class, 
        id
      );
      NavigationNode result = entityManager.find(
        NavigationNode.class, 
        id
      );
      assertEquals(instance, result);
      // test update (without changes)
      persistenceUtil.saveOrUpdate(
        entityManager, 
        instance, 
        NavigationNode.class, 
        id
      );
      result = entityManager.find(
        NavigationNode.class, 
        id
      );
      assertEquals(instance, result);
      // test update (with changes)
      persistenceUtil.saveOrUpdate(
        entityManager, 
        instance, 
        NavigationNode.class, 
        id
      );
      result = entityManager.find(
        NavigationNode.class, 
        id
      );
      assertEquals(instance, result);
    } finally {
      userTransaction.commit();
    }
  }

  /**
   * Test of saveOrUpdateCity method, of class PersistenceHelper.
   *
   * @throws javax.transaction.NotSupportedException
   * @throws javax.transaction.SystemException
   * @throws javax.transaction.HeuristicMixedException
   * @throws javax.transaction.HeuristicRollbackException
   * @throws javax.transaction.RollbackException
   */
  @Test
  public void testSaveOrUpdateCity() throws NotSupportedException, SystemException, HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException {
    City city = TestUtils.createDefaultTestCityInstance(idGenerator);
    userTransaction.begin();
    try {
      // test save
      persistenceUtil.saveOrUpdateCity(entityManager, city);
      City result = entityManager.find(City.class, city.getId());
      assertEquals(city, result);
      // test update (without changes)
      persistenceUtil.saveOrUpdateCity(entityManager, city);
      result = entityManager.find(City.class, city.getId());
      assertEquals(city, result);
      // test update (with changes)
      city.setAltitude(4000);
      persistenceUtil.saveOrUpdateCity(entityManager, city);
      result = entityManager.find(City.class, city.getId());
      assertEquals(city, result);
    } finally {
      userTransaction.commit();
    }
  }

}
