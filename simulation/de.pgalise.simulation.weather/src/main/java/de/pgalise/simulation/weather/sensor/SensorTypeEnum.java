/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.sensor;

import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import java.util.Set;

/**
 *
 * @author richter
 */
public enum SensorTypeEnum implements SensorType {

	;

	@Override
	public int getSensorTypeId() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Class<?> getSensorTypeClass() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String getUnit() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Set<SensorInterfererType> getSensorInterfererTypeSet() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
