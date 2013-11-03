/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.staticsensor.internal;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorFactory;
import de.pgalise.simulation.sensorFramework.output.Output;

/**
 *
 * @param <S> 
 * @author richter
 */
public abstract class AbstractSensorFactory<S extends Sensor<?>> implements SensorFactory<S> {
	private Output output;

	public AbstractSensorFactory(Output output) {
		this.output = output;
	}

	protected void setOutput(Output output) {
		this.output = output;
	}

	@Override
	public Output getOutput() {
		return output;
	}
}
