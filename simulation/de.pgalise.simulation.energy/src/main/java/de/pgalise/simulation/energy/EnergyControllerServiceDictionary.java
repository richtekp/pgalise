/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.energy;

import de.pgalise.simulation.service.ServiceDictionary;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author richter
 */
public interface EnergyControllerServiceDictionary extends ServiceDictionary {

	String ENERGY_CONTROLLER = EnergyController.class.getName();
	final static Set<String> ENERGY_CONTROLLER_SERVICES = new HashSet<String>(
		ServiceDictionary.SERVICES) {
			{
				add(ENERGY_CONTROLLER);
			}
		};

}
