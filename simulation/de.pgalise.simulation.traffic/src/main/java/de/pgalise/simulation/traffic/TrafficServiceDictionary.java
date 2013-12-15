/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.traffic.server.TrafficServer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author richter
 */
public interface TrafficServiceDictionary extends ServiceDictionary {

	public static final String TRAFFIC_SERVER = TrafficServer.class.getName();
	public static final String TRAFFIC_CONTROLLER = TrafficController.class.
		getName();
	public final static Set<String> ENERGY_CONTROLLER_SERVICES = new HashSet<String>(
		ServiceDictionary.SERVICES) {
			{
				add(TRAFFIC_SERVER);
				add(TRAFFIC_CONTROLLER);
			}
		};
}
