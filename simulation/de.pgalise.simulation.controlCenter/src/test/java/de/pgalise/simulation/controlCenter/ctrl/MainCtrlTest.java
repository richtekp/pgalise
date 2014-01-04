/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.controlCenter.internal.message.ControlCenterMessage;
import de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter;
import de.pgalise.simulation.controlCenter.model.MapAndBusstopFileData;
import de.pgalise.simulation.shared.event.Event;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

/**
 *
 * @author richter
 */
public class MainCtrlTest {
	
	public MainCtrlTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
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
	 * Test of onStartParameterUpload method, of class MainCtrl.
	 */
	@Test
	@Ignore
	public void testOnStartParameterUpload() {
		System.out.println("onStartParameterUpload");
		MainCtrl instance = new MainCtrl();
		instance.onStartParameterUpload();
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
	 * Test of removeTrafficServerIP method, of class MainCtrl.
	 */
	@Test
	@Ignore
	public void testRemoveTrafficServerIP() {
		System.out.println("removeTrafficServerIP");
		String trafficServerIP = "";
		MainCtrl instance = new MainCtrl();
		instance.removeTrafficServerIP(trafficServerIP);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of addTrafficServerIP method, of class MainCtrl.
	 */
	@Test
	@Ignore
	public void testAddTrafficServerIP() {
		System.out.println("addTrafficServerIP");
		MainCtrl instance = new MainCtrl();
		instance.addTrafficServerIP();
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
	 */
	@Test
	public void testRetrieveExportDownloadLink() {
		System.out.println("retrieveExportDownloadLink");
		MainCtrl instance = new MainCtrl();
		StreamedContent expResult = null;
		StreamedContent result = instance.retrieveExportDownloadLink();
		assertEquals(expResult,
			result);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
}