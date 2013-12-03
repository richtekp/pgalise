/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.controller.StartParameter;

/**
 *
 * @author richter
 */
public interface InfrastructureStartParameter extends StartParameter {

	City getCity();
	
	void setCity(City city);
	
}
