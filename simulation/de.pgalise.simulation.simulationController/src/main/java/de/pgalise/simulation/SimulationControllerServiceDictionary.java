/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation;

import com.google.common.collect.Sets;
import de.pgalise.simulation.service.Service;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.service.internal.DefaultServiceDictionary;
import de.pgalise.simulation.service.manager.ServiceHandler;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author richter
 */
public class SimulationControllerServiceDictionary extends DefaultServiceDictionary {
	private static final Logger log = LoggerFactory.getLogger(SimulationControllerServiceDictionary.class);
	public static final String SIMULATION_CONTROLLER = SimulationController.class.getName();
	
	public final static Set<String> SIMULATION_CONTROLLER_SERVICES = Sets.union(ServiceDictionary.SERVICES,
		new HashSet<>(Arrays.asList(SIMULATION_CONTROLLER)));

	@Override
	protected void initBeforeRead(List<ServiceHandler<Service>> list) {
		super.initBeforeRead(list); //To change body of generated methods, choose Tools | Templates.
		list.add(new ServiceHandler<Service>() {

			@Override
			public String getName() {
				return SIMULATION_CONTROLLER;
			}

			@Override
			public void handle(String server, Service service) {
				log.info(String.format("Using %s on server %s", getName(), server));
				getServices().put(getName(), service);
			}
		});
	}

	
	
}
