/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.internal.ejb;

import de.pgalise.simulation.controlCenter.internal.util.service.StartParameterSerializerService;
import de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter;
import de.pgalise.testutils.TestUtils;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.naming.NamingException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author richter
 */
@LocalBean
public class XMLStartParameterSerializerServiceTest {

  @EJB
  private StartParameterSerializerService instance;

  public XMLStartParameterSerializerServiceTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContext().bind("inject",
      this);
  }

  /**
   * Test of deserialize method, of class XMLStartParameterSerializerService.
   */
  @Test
  @Ignore
  public void testDeserialize_String() {
    System.out.println("deserialize");
    String content = "";
    ControlCenterStartParameter expResult = null;
    ControlCenterStartParameter result = instance.deserialize(content);
    assertEquals(expResult,
      result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of deserialize method, of class XMLStartParameterSerializerService.
   */
  @Test
  @Ignore
  public void testDeserialize_InputStream() {
    System.out.println("deserialize");
    InputStream inputStream = null;
    ControlCenterStartParameter expResult = null;
    ControlCenterStartParameter result = instance.deserialize(inputStream);
    assertEquals(expResult,
      result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of serialize method, of class XMLStartParameterSerializerService.
   */
  @Test
  @Ignore
  public void testSerialize_ControlCenterStartParameter_String() {
    System.out.println("serialize");
    ControlCenterStartParameter cCSimulationStartParameter = null;
    String fileName = "";
    String expResult = "";
    String result = instance.serialize(cCSimulationStartParameter,
      fileName);
    assertEquals(expResult,
      result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of serialize method, of class XMLStartParameterSerializerService.
   */
  @Test
  @Ignore
  public void testSerialize_ControlCenterStartParameter_OutputStream() {
    System.out.println("serialize");
    ControlCenterStartParameter controlCenterStartParameter = new ControlCenterStartParameter();
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    instance.serialize(controlCenterStartParameter,
      outputStream);
    String expResult = "";
    String result = outputStream.toString();
    assertEquals(expResult,
      result);
  }

}
