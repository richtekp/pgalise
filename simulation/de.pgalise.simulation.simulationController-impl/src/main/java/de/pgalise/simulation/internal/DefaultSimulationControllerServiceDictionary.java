/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.internal;

import de.pgalise.simulation.SimulationControllerServiceDictionary;
import de.pgalise.simulation.service.Service;
import de.pgalise.simulation.service.AbstractServiceDictionary;
import de.pgalise.simulation.service.manager.ServiceHandler;
import java.util.List;
import javax.ejb.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author richter
 */
@Singleton(mappedName = "de.pgalise.simulation.SimulationControllerServiceDictionary", name = "de.pgalise.simulation.SimulationControllerServiceDictionary")
public class DefaultSimulationControllerServiceDictionary extends AbstractServiceDictionary implements SimulationControllerServiceDictionary {
	private static final Logger log = LoggerFactory.getLogger(DefaultSimulationControllerServiceDictionary.class);

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
