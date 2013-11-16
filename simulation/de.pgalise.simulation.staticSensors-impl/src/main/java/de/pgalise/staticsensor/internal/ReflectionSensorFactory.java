/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.staticsensor.internal;

import de.pgalise.simulation.staticsensor.AbstractSensorFactory;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorFactory;
import de.pgalise.simulation.sensorFramework.SensorHelper;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.sensorFramework.output.Output;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author richter
 */
public class ReflectionSensorFactory<S extends Sensor<?>> extends AbstractSensorFactory implements SensorFactory {
	private Class<S> clazz;

	public ReflectionSensorFactory(Output output, Class<S> clazz) {
		super(output);
		this.clazz = clazz;
	}

	@Override
	public S createSensor(
		SensorHelper<?> sensorHelper,
		Set<SensorType> allowedTypes) throws InterruptedException, ExecutionException {
		S retValue = null;
		try {
			retValue = clazz.getConstructor().newInstance();
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
			throw new RuntimeException(ex);
		} 
		return retValue;
	}
	
}
