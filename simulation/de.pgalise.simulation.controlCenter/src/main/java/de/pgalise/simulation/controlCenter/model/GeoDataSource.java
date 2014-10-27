/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.model;

import de.pgalise.simulation.traffic.entity.CityInfrastructureData;


/**
 *
 * @author richter
 */
public interface GeoDataSource {

	CityInfrastructureData generateInfrastructureData();
}
