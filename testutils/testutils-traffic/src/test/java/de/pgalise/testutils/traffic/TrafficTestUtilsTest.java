/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.testutils.traffic;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.traffic.entity.TrafficCity;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author richter
 */
public class TrafficTestUtilsTest {

  public TrafficTestUtilsTest() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  /**
   * Test of createDefaultTestCityInstance method, of class TrafficTestUtils.
   */
  @Test
  public void testCreateDefaultTestCityInstance() {
    System.out.println("createDefaultTestCityInstance");
    IdGenerator idGenerator = EasyMock.createNiceMock(IdGenerator.class);
    final Long id = 1L;
    EasyMock.expect(idGenerator.getNextId()).andReturn(id);
    TrafficCity result = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);
    Assert.assertNotNull(result);
    Assert.assertEquals(id,
      result.getId());
  }

}
