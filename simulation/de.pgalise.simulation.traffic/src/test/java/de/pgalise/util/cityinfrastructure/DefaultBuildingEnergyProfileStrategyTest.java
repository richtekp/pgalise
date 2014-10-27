/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.cityinfrastructure;

import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.entity.Building;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author richter
 */
public class DefaultBuildingEnergyProfileStrategyTest {

  public DefaultBuildingEnergyProfileStrategyTest() {
  }

  @BeforeClass
  public static void setUpClass() {
  }

  @Test
  public void testInit() {
    DefaultBuildingEnergyProfileStrategy defaultBuildingEnergyProfileStrategy = new DefaultBuildingEnergyProfileStrategy();
  }

  /**
   * Test of getEnergyProfile method, of class
   * DefaultBuildingEnergyProfileStrategy.
   */
  @Test
  @Ignore //@TODO: implement (check for existing tests in other modules 
  //(before move) first
  public void testGetEnergyProfile() {
    System.out.println("getEnergyProfile");
    Building building = null;
    DefaultBuildingEnergyProfileStrategy instance = new DefaultBuildingEnergyProfileStrategy();
    EnergyProfileEnum expResult = null;
    EnergyProfileEnum result = instance.getEnergyProfile(building);
    assertEquals(expResult,
      result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

}
