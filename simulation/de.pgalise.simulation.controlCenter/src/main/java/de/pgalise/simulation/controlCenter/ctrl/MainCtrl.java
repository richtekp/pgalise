/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.controlCenter.InitDialogCtrlInitialType;
import de.pgalise.simulation.controlCenter.InitDialogCtrlInitialTypeEnum;
import de.pgalise.simulation.controlCenter.OSMParsedStateEnum;
import de.pgalise.simulation.controlCenter.InitDialogMessageTypeEnum;
import de.pgalise.simulation.controlCenter.internal.message.CCWebSocketMessage;
import de.pgalise.simulation.controlCenter.model.CCSimulationStartParameter;
import de.pgalise.simulation.controlCenter.model.OSMAndBusstopFileData;
import de.pgalise.simulation.service.GsonService;
import de.pgalise.simulation.service.IdGenerator;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 *
 * @author richter
 */
@ManagedBean
@SessionScoped
@Path("/mainCtrl")
public class MainCtrl implements Serializable {
	private static final long serialVersionUID = 1L;
	@EJB
	private GsonService gsonService;
	@EJB
	private IdGenerator idGenerator;
	private Map<Date, CCWebSocketMessage<?>> sentMessages = new HashMap<>();
	private OSMParsedStateEnum oSMParsedStateEnum;
	private OSMAndBusstopFileData oSMAndBusstopFileData;
	private CCSimulationStartParameter importedStartParameter;
	private InitDialogCtrlInitialType chosenInitialType = InitDialogCtrlInitialTypeEnum.CREATE;

	/**
	 * Creates a new instance of NewJSFManagedBean
	 */
	public MainCtrl() {
	}

	public void setSentMessages(
		Map<Date, CCWebSocketMessage<?>> sentMessages) {
		this.sentMessages = sentMessages;
	}

	public Map<Date, CCWebSocketMessage<?>> getSentMessages() {
		return sentMessages;
	}

	public void setChosenInitialType(InitDialogCtrlInitialType chosenInitialType) {
		this.chosenInitialType = chosenInitialType;
	}

	public InitDialogCtrlInitialType getChosenInitialType() {
		return chosenInitialType;
	}

	/**
	 * @return the oSMParsedStateEnum
	 */
	public OSMParsedStateEnum getoSMParsedStateEnum() {
		return oSMParsedStateEnum;
	}

	/**
	 * @param oSMParsedStateEnum the oSMParsedStateEnum to set
	 */
	public void setoSMParsedStateEnum(
					OSMParsedStateEnum oSMParsedStateEnum) {
		this.oSMParsedStateEnum = oSMParsedStateEnum;
	}

	/**
	 * @return the oSMAndBusstopFileData
	 */
	public OSMAndBusstopFileData getoSMAndBusstopFileData() {
		return oSMAndBusstopFileData;
	}

	/**
	 * @param oSMAndBusstopFileData the oSMAndBusstopFileData to set
	 */
	public void setoSMAndBusstopFileData(
					OSMAndBusstopFileData oSMAndBusstopFileData) {
		this.oSMAndBusstopFileData = oSMAndBusstopFileData;
	}

	/**
	 * @return the importedStartParameter
	 */
	public CCSimulationStartParameter getImportedStartParameter() {
		return importedStartParameter;
	}

	/**
	 * @param importedStartParameter the importedStartParameter to set
	 */
	public void setImportedStartParameter(
					CCSimulationStartParameter importedStartParameter) {
		this.importedStartParameter = importedStartParameter;
	}
	
	@GET()
	@Produces("text/plain")
	@Path("/parseOSMAndBusstop/")	
	public void parseOSMAndBusstop() {
		System.out.println("kfldjs222");
	}	
	
	public void export() {
		throw new UnsupportedOperationException();
	}
}
