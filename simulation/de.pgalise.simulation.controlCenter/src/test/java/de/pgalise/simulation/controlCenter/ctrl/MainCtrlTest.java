/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.controlCenter.internal.message.ControlCenterMessage;
import de.pgalise.simulation.controlCenter.internal.util.service.StartParameterSerializerService;
import de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.testutils.TestUtils;
import java.io.IOException;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

/**
 *
 * @author richter
 */
@LocalClient
@ManagedBean
public class MainCtrlTest {

  @EJB
  private StartParameterSerializerService startParameterSerializerService;

  public MainCtrlTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContainer().bind("inject",
      this);
  }

  /**
   * Test of parseOSMAndBusstop method, of class MainCtrl.
   */
  @Test
  @Ignore
  public void testParseOSMAndBusstop() {
    System.out.println("parseOSMAndBusstop");
    MainCtrl instance = new MainCtrl();
    instance.parseOSMAndBusstop();
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of prepareExport method, of class MainCtrl.
   */
  @Test
  @Ignore
  public void testPrepareExport() {
    System.out.println("prepareExport");
    MainCtrl instance = new MainCtrl();
    instance.prepareExport();
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of deleteUncommittedEvent method, of class MainCtrl.
   */
  @Test
  @Ignore
  public void testDeleteUncommittedEvent() {
    System.out.println("deleteUncommittedEvent");
    Event event = null;
    MainCtrl instance = new MainCtrl();
    instance.deleteUncommittedEvent(event);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of deleteUnSentMessage method, of class MainCtrl.
   */
  @Test
  @Ignore
  public void testDeleteUnSentMessage() {
    System.out.println("deleteUnSentMessage");
    ControlCenterMessage<?> unsentMessage = null;
    MainCtrl instance = new MainCtrl();
    instance.deleteUnSentMessage(unsentMessage);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of sendMessages method, of class MainCtrl.
   */
  @Test
  @Ignore
  public void testSendMessages() {
    System.out.println("sendMessages");
    MainCtrl instance = new MainCtrl();
    instance.sendMessages();
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of generateTree method, of class MainCtrl.
   */
  @Test
  @Ignore
  public void testGenerateTree() {
    System.out.println("generateTree");
    MainCtrl instance = new MainCtrl();
    instance.generateTree();
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of getStartParameterTreeRoot method, of class MainCtrl.
   */
  @Test
  @Ignore
  public void testGetStartParameterTreeRoot() {
    System.out.println("getStartParameterTreeRoot");
    MainCtrl instance = new MainCtrl();
    TreeNode expResult = null;
    TreeNode result = instance.getStartParameterTreeRoot();
    assertEquals(expResult,
      result);
    // TODO review the generated test code and remove the default call to fail.
    fail("The test case is a prototype.");
  }

  /**
   * Test of retrieveExportDownloadLink method, of class MainCtrl.
   *
   * Only test that the result is not null and returns without exception because
   * serialization is handled by {@link StartParameterSerializerService} and
   * tested there.
   */
  @Test
  public void testRetrieveExportDownloadLink() {
    System.out.println("retrieveExportDownloadLink");
    MainCtrl instance = new MainCtrl();
    instance.setStartParameterSerializerService(startParameterSerializerService);
    ControlCenterStartParameter startParameter
      = new ControlCenterStartParameter();
    instance.setStartParameter(startParameter);
    StreamedContent result = instance.retrieveExportDownloadLink();
    assertNotNull(result);
  }

  @Test
  @Ignore
  public void testStartSimulation() throws IOException {
    MainCtrl instance = new MainCtrl();
    instance.startSimulation();
  }
}
