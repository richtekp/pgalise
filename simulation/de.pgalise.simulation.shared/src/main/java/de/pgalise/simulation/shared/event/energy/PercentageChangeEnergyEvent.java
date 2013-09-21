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
import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.shared.event.EventTypeEnum;

/**
 * Implementation of change energy consumption event with percentage.
 * It will change the energy consumption value in the given radius by the
 * given percentage value.
 * 
 * @author Timo
 */
public class PercentageChangeEnergyEvent extends ChangeEnergyConsumptionEvent {

	/**
	 * Percentage value that will change the energy consumption
	 */
	private double percentage;

	/**
	 * Constructor
	 * 
	 * @param position
	 *            Position
	 * @param measureRadiusInMeter
	 *            Measure radius (in meter)
	 * @param startTimestamp
	 *            Start timestamp of the event
	 * @param endTimestamp
	 *            End timestamp of the event
	 * @param percentage
	 *            percentage value that will change the energy consumption. Must be >= 0.0. 0.0 == 0%, 1.0 == 100%.
	 */
	public PercentageChangeEnergyEvent(Coordinate position, int measureRadiusInMeter,
			long startTimestamp, long endTimestamp, double percentage) {
		super( position,
				measureRadiusInMeter, startTimestamp, endTimestamp);
		if (this.percentage < 0.0) {
			throw new IllegalArgumentException("percentage is < 0.0 !");
		}
		this.percentage = percentage;
	}

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -6809783231672099218L;

	@Override
	public double changeEnergyConsumption(EnergyProfileEnum energyProfile, double currentEnergyConsumption) {
		return currentEnergyConsumption * this.percentage;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		long temp;
		temp = Double.doubleToLongBits(percentage);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		PercentageChangeEnergyEvent other = (PercentageChangeEnergyEvent) obj;
		if (Double.doubleToLongBits(percentage) != Double.doubleToLongBits(other.percentage)) {
			return false;
		}
		return true;
	}

	@Override
	public EventType getType() {
		return EnergyEventTypeEnum.PERCENTAGE_CHANGE_ENERGY_CONSUMPTION_EVENT;
	}
}
