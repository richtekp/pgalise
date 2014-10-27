package de.pgalise.simulation.operationCenter.internal.strategy;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import java.util.Set;

public interface GPSSensorTimeoutStrategy {

	void init(long interval,
		int missedGPSUpdateStepsBeforeTimeout);

	/**
	 *
	 * @param timestamp
	 * @param sensorHelpers
	 * @return a {@link Set} of {@link Sensor}s which have been missed since last
	 * update step ??
	 */
	Set<GpsSensor> processUpdateStep(long timestamp,
		Set<GpsSensor> sensorHelpers);
}
