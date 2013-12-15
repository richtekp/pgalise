/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.model.vehicle;

import de.pgalise.simulation.service.RandomSeedService;

/**
 *
 * @author richter
 */
public interface VehicleFactory {
	

	RandomSeedService getRandomSeedService();
}
