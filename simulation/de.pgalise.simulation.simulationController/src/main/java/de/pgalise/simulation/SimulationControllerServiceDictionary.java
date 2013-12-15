/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation;

import de.pgalise.simulation.service.ServiceDictionary;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author richter
 */
public interface SimulationControllerServiceDictionary extends ServiceDictionary {

	public static final String SIMULATION_CONTROLLER = SimulationController.class.
		getName();

	public final static Set<String> SIMULATION_CONTROLLER_SERVICES = new HashSet<String>(
		ServiceDictionary.SERVICES) {
			{
				add(SIMULATION_CONTROLLER);
			}
		};
}
