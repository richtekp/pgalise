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
 
package de.pgalise.simulation.shared.event.energy;

import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Changes the energy consumption in the measure radius by the given percentage.
 * 
 * @author Timo
 */
@XmlRootElement
public class ChangeEnergyConsumptionEvent extends EnergyEvent {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -1110863976317425347L;

	/**
	 * Position
	 */
	private BaseCoordinate position;

	/**
	 * Measure radius (in meter)
	 */
	private int measureRadiusInMeter;

	/**
	 * Start timestamp of the event
	 */
	private long startTimestamp;

	/**
	 * End timestamp of the event
	 */
	private long endTimestamp;

	/**
	 * Constructor
	 * 
	 * @param eventID
	 * ID of the event
	 * @param position
	 * Position
	 * @param measureRadiusInMeter
	 * Measure radius (in meter)
	 * @param startTimestamp
	 * Start timestamp of the event
	 * @param endTimestamp
	 * End timestamp of the event
	 */
	
	public ChangeEnergyConsumptionEvent(BaseCoordinate position, int measureRadiusInMeter, long startTimestamp, long endTimestamp) {
		if (measureRadiusInMeter <= 0.0) {
			throw new IllegalArgumentException("measureRadiusInMeter is negative");
		}
		this.position = position;
		this.measureRadiusInMeter = measureRadiusInMeter;
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
	}

	public BaseCoordinate getPosition() {
		return position;
	}

	public void setPosition(BaseCoordinate position) {
		this.position = position;
	}

	public int getMeasureRadiusInMeter() {
		return measureRadiusInMeter;
	}

	public void setMeasureRadiusInMeter(int measureRadiusInMeter) {
		this.measureRadiusInMeter = measureRadiusInMeter;
	}

	/**
	 * Changes the current energy consumption to the new value.
	 * 
	 * @param energyProfile
	 *            Energy profile
	 * @param currentEnergyConsumption
	 *            Current energy consumption
	 * @return new energy consumption
	 */
	public double changeEnergyConsumption(EnergyProfileEnum energyProfile, double currentEnergyConsumption) {
		return currentEnergyConsumption;
	}

	public long getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public long getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (int) (endTimestamp ^ (endTimestamp >>> 32));
		result = prime * result + measureRadiusInMeter;
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		result = prime * result + (int) (startTimestamp ^ (startTimestamp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ChangeEnergyConsumptionEvent other = (ChangeEnergyConsumptionEvent) obj;
		if (endTimestamp != other.endTimestamp) {
			return false;
		}
		if (measureRadiusInMeter != other.measureRadiusInMeter) {
			return false;
		}
		if (position == null) {
			if (other.position != null) {
				return false;
			}
		} else if (!position.equals(other.position)) {
			return false;
		}
		if (startTimestamp != other.startTimestamp) {
			return false;
		}
		return true;
	}

	@Override
	public EnergyEventType getType() {
		return EnergyEventTypeEnum.CHANGE_ENERGY_CONSUMPTION_EVENT;
	}
}
