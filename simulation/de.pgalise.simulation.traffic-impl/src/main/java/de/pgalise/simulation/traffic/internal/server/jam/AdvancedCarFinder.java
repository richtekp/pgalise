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
 
package de.pgalise.simulation.traffic.internal.server.jam;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.Orientation;
import de.pgalise.simulation.shared.JaxbVector2d;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.jam.SurroundingCarsFinder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateful;

/**
 * Implements the advanced car finder. Has a visibility range to verify the vehicles in front of the given vehicle.
 * Takes advantages of the registered car on the edges of the graph.
 * 
 * @author Marina
 * @author mustafa 
 */
@Stateful
public class AdvancedCarFinder implements SurroundingCarsFinder {
  @EJB
  private IdGenerator idGenerator;
  
  public AdvancedCarFinder() {
  }
  
	@Override
	public Set<Vehicle<?>> findCars(Vehicle<?> car, long time) {
		TrafficGraphExtensions trafficGraphExtensions = car.getTrafficGraphExtensions();

		Set<Vehicle<?>> allCarsOnEdge;
		double visibilityRange = 0.075 * (time / 1000d);
		TrafficNode fromNode = car.getCurrentNode();
		TrafficNode toNode = car.getNextNode();
		TrafficEdge currentEdge = car.getCurrentEdge();
		TrafficEdge nextEdge = null;

		// Visibility range erhoehen, da der Startpunkt auf der Edge liegt
		// visibilityRange += car.getPosition().length();

		// AdvancedCarFinder.log.debug("Startposition: " + car.getPosition().toString());
		// Autos, die sich auf der ersten bzw. aktuellen Kante befinden
		allCarsOnEdge = trafficGraphExtensions.getVehiclesFor(currentEdge, fromNode, toNode);

		// Autos die sich hinter dem gegebenen Vehicle befinden werden aus der
		// Liste geloescht
		for (Iterator<Vehicle<? extends VehicleData>> i = allCarsOnEdge.iterator(); i.hasNext();) {
			Vehicle<? extends VehicleData> v = i.next();
			Orientation orientation = Orientation.getOrientation(v.getDirection());
			if (Orientation.isBeyond(orientation, car.getPosition(), v.getPosition())) {
				i.remove();
				// AdvancedCarFinder.log.debug("Removing " + v.getName()
				// + ", cause it is behind / on the same position as " + car.getName());
			}
		}

		// AdvancedCarFinder.log.debug("Visibility range: " + visibilityRange);

		// Liste mit Kanten
		List<TrafficEdge> edges = new ArrayList<>();
		edges.add(currentEdge);

		double edgesLength = 0;

		// Fuege die Kanten der Kantenliste hinzu, so lange diese sich in der
		// Visibilityrange des gegebenen Vehicles befinden
		while (visibilityRange >= edgesLength) {
			try {
				nextEdge = car.getPath().get(car.getPath().indexOf(currentEdge) + 1);
				fromNode = toNode;
				toNode = car.getNodePath().get(car.getNodePath().indexOf(toNode) + 1);
			} catch (IndexOutOfBoundsException e) {
				break;
			}
			allCarsOnEdge.addAll(trafficGraphExtensions.getVehiclesFor(nextEdge, fromNode, toNode));
			edgesLength += trafficGraphExtensions.getLength(nextEdge);
		}

		// Berechnen der Endposition (Ende der Visibilityrange auf der letzten
		// Kante)
		double diff = edgesLength - visibilityRange;
		BaseCoordinate toNodePosition = trafficGraphExtensions.getPosition(toNode);
		JaxbVector2d dir = new JaxbVector2d(toNodePosition.getX(), toNodePosition.getY());
		BaseCoordinate fromNodePosition = trafficGraphExtensions.getPosition(fromNode);
		JaxbVector2d fromNodeVector = new JaxbVector2d(fromNodePosition.getX(), fromNodePosition.getY());
		dir.sub(fromNodeVector);
		dir.normalize();
		dir.scale(diff);
		JaxbVector2d endPositionVector = new JaxbVector2d(toNodePosition.getX(), toNodePosition.getY());
		endPositionVector.sub(dir);
		BaseCoordinate endPosition = new BaseCoordinate( endPositionVector.getX(), endPositionVector.getY());

		// AdvancedCarFinder.log.debug("Endposition: " + endPosition.toString());

		// Vehicles vor der Endposition der letzten betrachteten Kante aus der
		// Liste loeschen
		for (Iterator<Vehicle<? extends VehicleData>> i = allCarsOnEdge.iterator(); i.hasNext();) {
			Vehicle<? extends VehicleData> v = i.next();
			// log.debug("All cars on Edge: " + v.getName());
			Orientation orientation = Orientation.getOrientation(v.getDirection());
			if (!Orientation.isBeyond(orientation, endPosition, v.getPosition()) && (v.getCurrentEdge() == nextEdge)) {
				i.remove();
				// log.debug("Removing " + v.getName() + ", cause it is too far ahead of "
				// + car.getName());
			}
		}

		return allCarsOnEdge;
	}

	@Override
	public Vehicle<?> findNearestCar(Vehicle<?> car, long time) {
		Set<Vehicle<? extends VehicleData>> vehicles = this.findCars(car, time);
		JaxbVector2d carPos = new JaxbVector2d(car.getPosition().getX(), car.getPosition().getY());

		Vehicle<? extends VehicleData> nearestCar = null;
		double distance = -1;

		for (Vehicle<? extends VehicleData> v : vehicles) {
			// AdvancedCarFinder.log.debug("Checking " + v.getName() + " as potential nearest car with its distance: "
			// + v.getPosition().sub(car.getPosition()).length());
			JaxbVector2d otherCarPos = new JaxbVector2d(v.getPosition().getX(), v.getPosition().getY());
			otherCarPos.sub(carPos);
			if (distance == -1) {
				nearestCar = v;
				distance = otherCarPos.length();
			} else if (otherCarPos.length() < distance) {
				nearestCar = v;
				distance = otherCarPos.length();
			}
		}

		return nearestCar;
	}
}
