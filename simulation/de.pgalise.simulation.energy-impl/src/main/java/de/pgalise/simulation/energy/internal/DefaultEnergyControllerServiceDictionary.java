/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.energy.internal;

import de.pgalise.simulation.energy.EnergyControllerServiceDictionary;
import de.pgalise.simulation.service.Service;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.service.AbstractServiceDictionary;
import de.pgalise.simulation.service.manager.ServiceHandler;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author richter
 */
@Singleton(mappedName = "de.pgalise.simulation.energy.EnergyControllerServiceDictionary", name = "de.pgalise.simulation.energy.EnergyControllerServiceDictionary")
public class DefaultEnergyControllerServiceDictionary extends AbstractServiceDictionary implements EnergyControllerServiceDictionary {
	private static final Logger log = LoggerFactory.getLogger(DefaultEnergyControllerServiceDictionary.class);

	@Override
	protected void initBeforeRead(List<ServiceHandler<Service>> list) {
		
		 list.add(new ServiceHandler<Service>() {

			@Override
			public String getName() {
				return ENERGY_CONTROLLER;
			}

			@Override
			public void handle(String server,
				Service service) {
				log.info(String.format("Using %s on server %s", getName(), server));
				getServices().put(getName(), service);
			}
		});
	}
}
