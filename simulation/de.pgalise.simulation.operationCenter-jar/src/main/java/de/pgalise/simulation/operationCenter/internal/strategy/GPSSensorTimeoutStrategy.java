
package de.pgalise.simulation.operationCenter.internal.strategy;

import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import java.util.Collection;

public interface GPSSensorTimeoutStrategy {
	void init(long interval, int missedGPSUpdateStepsBeforeTimeout);
	
	Collection<Sensor<?,?>> processUpdateStep(long timestamp, Collection<GpsSensor> sensorHelpers) ;
}