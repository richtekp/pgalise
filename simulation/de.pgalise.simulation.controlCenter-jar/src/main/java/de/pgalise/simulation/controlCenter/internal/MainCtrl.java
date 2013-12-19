/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.internal;

import de.pgalise.simulation.controlCenter.internal.message.CCWebSocketMessage;
import de.pgalise.simulation.controlCenter.internal.message.OSMAndBusstopFileMessage;
import de.pgalise.simulation.controlCenter.model.CCSimulationStartParameter;
import de.pgalise.simulation.controlCenter.model.OSMAndBusstopFileData;
import de.pgalise.simulation.service.GsonService;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEventTypeEnum;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 *
 * @author richter
 */
@ManagedBean
@SessionScoped
public class MainCtrl implements Serializable {
	private static final long serialVersionUID = 1L;
	@EJB
	private GsonService gsonService;
	@EJB
	private IdGenerator idGenerator;
	private Map<Date, CCWebSocketMessage<?>> sentMessages = new HashMap<>();
	private OSMParsedStateEnum oSMParsedStateEnum;
	private OSMAndBusstopFileData oSMAndBusstopFileData;
	private WeatherEventViewData currentWeatherEventViewData = new WeatherEventViewData();
	private CCSimulationStartParameter importedStartParameter;
	private InitDialogMessageTypeEnum chosenInitialType = InitDialogMessageTypeEnum.SIMULATION_START_PARAMETER;

	/**
	 * Creates a new instance of MainCtrl
	 */
	public MainCtrl() {
	}

	public void setCurrentWeatherEventViewData(
		WeatherEventViewData currentWeatherEventViewData) {
		this.currentWeatherEventViewData = currentWeatherEventViewData;
	}

	public WeatherEventViewData getCurrentWeatherEventViewData() {
		return currentWeatherEventViewData;
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
	
	public void sendMessage(OSMAndBusstopFileMessage oSMAndBusstopFileMessage) throws IOException {
		FacesContext.getCurrentInstance().getResponseWriter().append(oSMAndBusstopFileMessage.toJson(gsonService.getGson()));
		sentMessages.put(new Date(),oSMAndBusstopFileMessage);
	}
	
	/**
	 * parses the recently imported/uploaded {@link OSMAndBusstopFileData} 
	 */
	public void parseImportedOSMAndBusstop() {
		if(importedStartParameter == null) {
			return;
		}
		throw new UnsupportedOperationException();
	}
	
	public void sendOSMAndBusstop() throws IOException {
		this.oSMParsedStateEnum = OSMParsedStateEnum.IN_PROGRESS;
		Long id = idGenerator.getNextId();
		sendMessage(
								new OSMAndBusstopFileMessage(id,
				oSMAndBusstopFileData));
	}
	
	/**
	 * updates <tt>currentWeatherEventViewData</tt> with non standard weather events from startParameter
	 * @param startParameter
	 */
	public void updateCurrentWeatherEventViewData(CCSimulationStartParameter startParameter) {
		if(startParameter == null) {
			return;
		}
		List<WeatherEvent> tmp = new LinkedList<>();
		for(WeatherEvent weatherEvent : startParameter.getWeatherEventList()) {
			if(weatherEvent.getType() != WeatherEventTypeEnum.CITYCLIMATE && weatherEvent.getType() != WeatherEventTypeEnum.REFERENCECITY) {
				tmp.add(weatherEvent);
			}
		}
		this.currentWeatherEventViewData.setEvents(tmp);
	}
}
