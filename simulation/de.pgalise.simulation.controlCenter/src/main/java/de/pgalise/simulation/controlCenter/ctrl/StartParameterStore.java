/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter;
import de.pgalise.simulation.controlCenter.model.StartParameterOriginEnum;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.Part;

/**
 * Provides a store for different {@link ControlCenterStartParameter} to be
 * offered in the init dialog. The <tt>chosenStartParameter</tt> property holds
 * the start parameter which has been chosen in the init dialog, the
 * <tt>startParameterOriginEnum</tt> property indicates the origin of the start
 * parameter in the init dialog.
 *
 * @author richter
 */
@ManagedBean
@SessionScoped
public class StartParameterStore
{

	private List<ControlCenterStartParameter> predefinedStartParameters = new LinkedList<>();

	private List<ControlCenterStartParameter> recentlyStartedSimulations = new LinkedList<>();
	private ControlCenterStartParameter chosenStartParameter;
	private StartParameterOriginEnum startParameterOriginEnum;

	/**
	 * Creates a new instance of StartParameterStore
	 */
	public StartParameterStore() {
	}

	@PostConstruct
	public void init() {
		getPredefinedStartParameters().add(
						new ControlCenterStartParameter());
	}

	/**
	 * @return the predefinedStartParameters
	 */
	public List<ControlCenterStartParameter> getPredefinedStartParameters() {
		return predefinedStartParameters;
	}

	/**
	 * @param predefinedStartParameters the predefinedStartParameters to set
	 */
	public void setPredefinedStartParameters(
					List<ControlCenterStartParameter> predefinedStartParameters) {
		this.predefinedStartParameters = predefinedStartParameters;
	}

	/**
	 * @return the recentlyStartedSimulations
	 */
	public List<ControlCenterStartParameter> getRecentlyStartedSimulations() {
		return recentlyStartedSimulations;
	}

	/**
	 * @param recentlyStartedSimulations the recentlyStartedSimulations to set
	 */
	public void setRecentlyStartedSimulations(
					List<ControlCenterStartParameter> recentlyStartedSimulations) {
		this.recentlyStartedSimulations = recentlyStartedSimulations;
	}

	private Part file;

	public void upload() {
		try {
			String fileContent = new Scanner(file.getInputStream())
							.useDelimiter("\\A").next();
			throw new UnsupportedOperationException(
							"implement import (deserialization)");
		} catch (IOException e) {
			// Error handling
		}
	}

	public Part getFile() {
		return file;
	}

	public void setFile(Part file) {
		this.file = file;
	}

	/**
	 * @return the chosenStartParameter
	 */
	public ControlCenterStartParameter getChosenStartParameter() {
		return chosenStartParameter;
	}

	/**
	 * @param chosenStartParameter the chosenStartParameter to set
	 */
	public void setChosenStartParameter(
					ControlCenterStartParameter chosenStartParameter) {
		this.chosenStartParameter = chosenStartParameter;
	}

	/**
	 * @return the startParameterOriginEnum
	 */
	public StartParameterOriginEnum getStartParameterOriginEnum() {
		return startParameterOriginEnum;
	}

	/**
	 * @param startParameterOriginEnum the startParameterOriginEnum to set
	 */
	public void setStartParameterOriginEnum(
					StartParameterOriginEnum startParameterOriginEnum) {
		this.startParameterOriginEnum = startParameterOriginEnum;
	}

}
