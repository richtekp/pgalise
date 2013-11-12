/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
 
package de.pgalise.simulation.sensorFramework;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.SimulationComponent;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.shared.persistence.Identifiable;
import javax.persistence.Embedded;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract super class of a Sensor. To create a concrete sensor instantiate a SensorDomain and use the add-method to
 * pass the class object into the SensorDomain so that it can be created.
 * 
 * @param <E> 
 * @author Marcus
 * @version 1.0
 */
public interface Sensor<E extends Event> extends Identifiable, SimulationComponent<E> {

	/**
	 * returns the number of measured values of the sensor
	 * 
	 * @return measuredValues
	 */
	public long getMeasuredValues();

	/**
	 * returns the output of the sensor
	 * 
	 * @return output
	 */
	public Output getOutput() ;

	/**
	 * Returns the position of the sensor in the environment
	 * 
	 * @return position
	 */
	public Coordinate getPosition();
	
	void setPosition(Coordinate position);

	/**
	 * Returns the sensortype
	 * 
	 * @return sensortype
	 */
	public abstract SensorType getSensorType();

	/**
	 * determines whether the sensor is activated
	 * 
	 * @return activated
	 */
	public boolean isActivated();
	
	void setActivated(boolean activated);

	/**
	 * Makes the sensor measure its environment and doing the transmission sequence if it is activated.
	 * 
	 * @param eventList
	 *            List with SimulationEvents
	 */
	@Override
	public void update(final EventList<E> eventList) ;

	/**
	 * subclasses are supposed to implement this method to send their usage data through their output
	 * 
	 * @param eventList
	 *            List with SimulationEvents
	 */
	void transmitUsageData(EventList<E> eventList);
}