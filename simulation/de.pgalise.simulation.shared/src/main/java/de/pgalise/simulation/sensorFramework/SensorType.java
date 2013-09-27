/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.sensorFramework;

import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author richter
 */
public interface SensorType {
	

	/**
	 * Returns the sensor type id belonging to this enum literal.
	 * 
	 * @return the sensor type id belonging to this enum literal
	 */
	public int getSensorTypeId() ;

	public Class<?> getSensorTypeClass() ;

	public String getUnit() ;

	public Set<SensorInterfererType> getSensorInterfererTypeSet() ;
}
