/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.service;

import java.io.Serializable;

/**
 * An Entity describes a service that is located on a specific server.
 * @author Mustafa
 */
public class ServerConfigurationEntity implements Serializable {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = 5194129593797800215L;
	/**
	 * Name
	 */
	private String name;

	/**
	 * Constructor
	 */
	public ServerConfigurationEntity() {
	}

	public ServerConfigurationEntity(String name) {
		this.name = name;
	}

	/**
	 *
	 * @return name of this service/entity
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this service/entity
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
}
