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
package de.pgalise.simulation.energy.internal;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import java.util.ArrayList;
import java.util.List;

import org.khelekore.prtree.DistanceCalculator;
import org.khelekore.prtree.DistanceResult;
import org.khelekore.prtree.MBRConverter;
import org.khelekore.prtree.NodeFilter;
import org.khelekore.prtree.PRTree;
import org.khelekore.prtree.PointND;
import org.khelekore.prtree.SimplePointND;

import de.pgalise.simulation.energy.EnergyEventStrategyLocal;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.event.energy.ChangeEnergyConsumptionEvent;
import de.pgalise.simulation.shared.event.energy.EnergyEvent;
import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import javax.ejb.Stateful;

/**
 * Default implementation of energy event strategy. Considers only the first
 * found suitable energy event. It uses a PR-Tree to find suitable energy events
 * for the requested positions.
 *
 * @author Timo
 */
@Stateful
public class DefaultEnergyEventStrategy implements EnergyEventStrategyLocal {

	private static final int PR_TREE_BRANCH_FACTOR = 30;
	private static final NodeMBRConverter NODE_MBR_CONVERTER = new NodeMBRConverter();
	private DistanceCalculator<ChangeEnergyConsumptionEvent> PR_TREE_DISTANCE_CALCULATOR = new EventDistanceCalculator();
	private PRTreeEventFilter PR_EVENT_FILTER = new PRTreeEventFilter();

	private PRTree<ChangeEnergyConsumptionEvent> changeEnergyConsumptionEventDataTree;

	private List<ChangeEnergyConsumptionEvent> currentChangeEvents, futureChangeEvents;

	/**
	 * Default
	 */
	public DefaultEnergyEventStrategy() {
		this.futureChangeEvents = new ArrayList<>();
		this.currentChangeEvents = new ArrayList<>();
		this.changeEnergyConsumptionEventDataTree = new PRTree<>(NODE_MBR_CONVERTER,
			PR_TREE_BRANCH_FACTOR);
		this.changeEnergyConsumptionEventDataTree.load(this.currentChangeEvents);
	}

	@Override
	public void update(long timestamp,
		List<EnergyEvent> energyEventList) {

		boolean currentListChanged = false;

		/* Add new events: */
		List<ChangeEnergyConsumptionEvent> tmpNewCurrentEvents = new ArrayList<>();
		for (EnergyEvent event : energyEventList) {
			if (event instanceof ChangeEnergyConsumptionEvent) {

				ChangeEnergyConsumptionEvent changeEvent = (ChangeEnergyConsumptionEvent) event;

				if (changeEvent.getStartTimestamp() >= timestamp) {

					this.futureChangeEvents.add(changeEvent);

				} else if (changeEvent.getStartTimestamp() == timestamp) {

					tmpNewCurrentEvents.add(changeEvent);

				}
			}
		}

		/* Remove old events: */
		int currentChangeEventsSize = this.currentChangeEvents.size();
		for (ChangeEnergyConsumptionEvent changeEvent : new ArrayList<>(
			this.currentChangeEvents)) {
			if (changeEvent.getEndTimestamp() <= timestamp) {
				this.currentChangeEvents.remove(changeEvent);
			}
		}
		if (currentChangeEventsSize != this.currentChangeEvents.size()) {
			currentListChanged = true;
		}

		/* find new current events: */
		currentChangeEventsSize = this.currentChangeEvents.size();
		this.currentChangeEvents.addAll(tmpNewCurrentEvents);
		for (ChangeEnergyConsumptionEvent changeEvent : new ArrayList<>(
			this.futureChangeEvents)) {
			if (changeEvent.getStartTimestamp() >= timestamp) {
				this.currentChangeEvents.add(changeEvent);
				this.futureChangeEvents.remove(changeEvent);
			}
		}
		if (currentChangeEventsSize != this.currentChangeEvents.size()) {
			currentListChanged = true;
		}

		/* Reset tree: */
		if (currentListChanged) {
			this.changeEnergyConsumptionEventDataTree = new PRTree<>(
				NODE_MBR_CONVERTER,
				PR_TREE_BRANCH_FACTOR);
			this.changeEnergyConsumptionEventDataTree.load(this.currentChangeEvents);
		}
	}

	@Override
	public double getEnergyConsumptionInKWh(long timestamp,
		EnergyProfileEnum key,
		JaxRSCoordinate geoLocation,
		double consumptionWithoutEvents) {

		List<DistanceResult<ChangeEnergyConsumptionEvent>> results = this.changeEnergyConsumptionEventDataTree.
			nearestNeighbour(this.PR_TREE_DISTANCE_CALCULATOR,
				this.PR_EVENT_FILTER,
				1,
				new SimplePointND(geoLocation.getX(),
					geoLocation.getY()));

		for (DistanceResult<ChangeEnergyConsumptionEvent> result : results) {
			/* Result distance is in KM */
			if (result.getDistance() <= result.get().getMeasureRadiusInMeter() / 1000.0) {
				return result.get().changeEnergyConsumption(key,
					consumptionWithoutEvents);
			}
		}

		return consumptionWithoutEvents;
	}

	/**
	 * MBRConverter for our nodes.
	 *
	 * @author Timo
	 */
	private static class NodeMBRConverter implements
		MBRConverter<ChangeEnergyConsumptionEvent> {

		private static final long serialVersionUID = 5914867807215611947L;

		@Override
		public int getDimensions() {
			return 2;
		}

		@Override
		public double getMax(int arg0,
			ChangeEnergyConsumptionEvent arg1) {
			return arg0 == 0 ? arg1.getPosition().getX() : arg1.getPosition().getY();
		}

		@Override
		public double getMin(int arg0,
			ChangeEnergyConsumptionEvent arg1) {
			return arg0 == 0 ? arg1.getPosition().getX() : arg1.getPosition().getY();
		}
	}

	/**
	 * Distance calulator for events.
	 *
	 * @author Timo
	 */
	private static class EventDistanceCalculator implements
		DistanceCalculator<ChangeEnergyConsumptionEvent> {

		private static final long serialVersionUID = -6193813933897564417L;

		@Override
		public double distanceTo(ChangeEnergyConsumptionEvent e,
			PointND p) {
			double retValue = LatLngTool.distance(new LatLng(p.getOrd(0),
				p.getOrd(1)),
				new LatLng(e.getPosition().getX(),
					e.getPosition().getY()),
				LengthUnit.METER);
			return retValue;
		}
	}

	/**
	 * Filter for events.
	 *
	 * @author Timo
	 */
	private static class PRTreeEventFilter implements
		NodeFilter<ChangeEnergyConsumptionEvent> {

		private static final long serialVersionUID = -3646078593986310313L;

		@Override
		public boolean accept(ChangeEnergyConsumptionEvent arg0) {
			return true;
		}
	}
}
