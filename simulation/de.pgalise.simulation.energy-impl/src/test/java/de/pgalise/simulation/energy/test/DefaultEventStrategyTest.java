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
 
package de.pgalise.simulation.energy.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.pgalise.simulation.energy.internal.DefaultEnergyEventStrategy;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.event.energy.EnergyEvent;
import de.pgalise.simulation.shared.event.energy.PercentageChangeEnergyEvent;
import de.pgalise.simulation.shared.city.Coordinate;

/**
 * JUnit test for the {@link DefaultEventStrategy}.<br />
 * <br />
 * Tests the event strategy without event, with event, with event inside event boundary and with event and not inside
 * event boundary.
 * 
 * @author Timo
 */
public class DefaultEventStrategyTest {
	private static final long testTimestampBeforeEvent = 1000;
	private static final long testTimestampDuringEvent = 5000;
	private static final long testTimestampAfterEvent = 8000;
	private static final long eventStartTimestamp = 4000;
	private static final long eventEndTimestamp = 7000;
	private static final EnergyProfileEnum energyProfile = EnergyProfileEnum.HOUSEHOLD;
	private static final Coordinate testGeoLocation = new Coordinate(50, 50);
	private static final Coordinate testGeoLocationOutsideEvent = new Coordinate(90, 90);
	private static final int energyConsumption = 50;
	private static final double assertEqualsDelta = 0.01;

	@Test
	public void testWithPercentageEvent() {
		DefaultEnergyEventStrategy eventStrategy = new DefaultEnergyEventStrategy();

		/* before event */
		eventStrategy.update(DefaultEventStrategyTest.testTimestampBeforeEvent, new ArrayList<EnergyEvent>());
		double beforeEvent = eventStrategy.getEnergyConsumptionInKWh(DefaultEventStrategyTest.testTimestampBeforeEvent,
				DefaultEventStrategyTest.energyProfile, DefaultEventStrategyTest.testGeoLocation,
				DefaultEventStrategyTest.energyConsumption);
		Assert.assertEquals(DefaultEventStrategyTest.energyConsumption, beforeEvent,
				DefaultEventStrategyTest.assertEqualsDelta);

		/* with event */
		double percentage = 0.5;
		List<EnergyEvent> eventList = new ArrayList<>();
		eventList.add(new PercentageChangeEnergyEvent(DefaultEventStrategyTest.testGeoLocation, 50,
				DefaultEventStrategyTest.eventStartTimestamp, DefaultEventStrategyTest.eventEndTimestamp, percentage));
		eventStrategy.update(DefaultEventStrategyTest.eventStartTimestamp, eventList);

		/* Inside event boundary */
		double withEventInsideBoundary = eventStrategy.getEnergyConsumptionInKWh(
				DefaultEventStrategyTest.testTimestampDuringEvent, DefaultEventStrategyTest.energyProfile,
				DefaultEventStrategyTest.testGeoLocation, DefaultEventStrategyTest.energyConsumption);
		Assert.assertEquals(beforeEvent * percentage, withEventInsideBoundary,
				DefaultEventStrategyTest.assertEqualsDelta);
		/* Outside event boundary */
		double withEventOutsideBoundary = eventStrategy.getEnergyConsumptionInKWh(
				DefaultEventStrategyTest.testTimestampDuringEvent, DefaultEventStrategyTest.energyProfile,
				DefaultEventStrategyTest.testGeoLocationOutsideEvent, DefaultEventStrategyTest.energyConsumption);
		Assert.assertEquals(beforeEvent, withEventOutsideBoundary, DefaultEventStrategyTest.assertEqualsDelta);

		/* after event */
		eventStrategy.update(DefaultEventStrategyTest.testTimestampAfterEvent, new ArrayList<EnergyEvent>());
		double afterEvent = eventStrategy.getEnergyConsumptionInKWh(DefaultEventStrategyTest.testTimestampAfterEvent,
				DefaultEventStrategyTest.energyProfile, DefaultEventStrategyTest.testGeoLocation,
				DefaultEventStrategyTest.energyConsumption);
		Assert.assertEquals(DefaultEventStrategyTest.energyConsumption, afterEvent,
				DefaultEventStrategyTest.assertEqualsDelta);
	}
}
