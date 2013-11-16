/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.staticsensor.internal;

import de.pgalise.simulation.service.Service;
import de.pgalise.simulation.service.AbstractServiceDictionary;
import de.pgalise.simulation.service.manager.ServiceHandler;
import de.pgalise.simulation.staticsensor.StaticSensorServiceDictionary;
import java.util.List;
import javax.ejb.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author richter
 */
@Singleton(mappedName = "de.pgalise.simulation.staticsensor.StaticSensorServiceDictionary", name = "de.pgalise.simulation.staticsensor.StaticSensorServiceDictionary")
public class DefaultStaticSensorServiceDictionary extends AbstractServiceDictionary implements StaticSensorServiceDictionary {
	private static final Logger log = LoggerFactory.getLogger(DefaultStaticSensorServiceDictionary.class);

	@Override
	protected void initBeforeRead(List<ServiceHandler<Service>> list) {
		super.initBeforeRead(list); 
		
		list.add(new ServiceHandler<Service>() {

			@Override
			public String getName() {
				return STATIC_SENSOR_CONTROLLER;
			}

			@Override
			public void handle(String server, Service service) {
				log.info(String.format("Using %s on server %s", getName(), server));
				getServices().put(getName(), service);
			}
		});
	}
	
}
