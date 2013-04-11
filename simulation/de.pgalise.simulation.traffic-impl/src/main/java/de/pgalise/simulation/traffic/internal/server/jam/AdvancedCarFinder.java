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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

import de.pgalise.simulation.service.Orientation;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.jam.SurroundingCarsFinder;
import de.pgalise.util.vector.Vector2d;

/**
 * Implements the advanced car finder. Has a visibility range to verify the vehicles in front of the given vehicle.
 * Takes advantages of the registered car on the edges of the graph.
 * 
 * @author Marina
 * @author mustafa 
 */
public class AdvancedCarFinder implements SurroundingCarsFinder {
	@Override
	public List<Vehicle<? extends VehicleData>> findCars(Vehicle<? extends VehicleData> car, long time) {
		TrafficGraphExtensions trafficGraphExtensions = car.getTrafficGraphExtensions();

		List<Vehicle<? extends VehicleData>> allCarsOnEdge = new ArrayList<>();
		double visibilityRange = 0.075 * (time / 1000d);
		Node fromNode = car.getCurrentNode();
		Node toNode = car.getNextNode();
		Edge currentEdge = car.getCurrentEdge();
		Edge nextEdge = null;

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
		List<Edge> edges = new ArrayList<>();
		edges.add(currentEdge);

		double edgesLength = 0;

		// Fuege die Kanten der Kantenliste hinzu, so lange diese sich in der
		// Visibilityrange des gegebenen Vehicles befinden
		while (visibilityRange >= edgesLength) {
			try {
				nextEdge = car.getPath().getEdgePath().get(currentEdge.getIndex() + 1);
				fromNode = toNode;
				toNode = car.getPath().getNodePath().get(toNode.getIndex() + 1);
			} catch (IndexOutOfBoundsException e) {
				break;
			}
			allCarsOnEdge.addAll(trafficGraphExtensions.getVehiclesFor(nextEdge, fromNode, toNode));
			edgesLength += trafficGraphExtensions.getLength(nextEdge);
		}

		// Berechnen der Endposition (Ende der Visibilityrange auf der letzten
		// Kante)
		double diff = edgesLength - visibilityRange;
		Vector2d dir = trafficGraphExtensions.getPosition(toNode).sub(trafficGraphExtensions.getPosition(fromNode));
		dir = dir.normalize();
		dir = dir.scale(diff);
		Vector2d endPosition = trafficGraphExtensions.getPosition(toNode).sub(dir);

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
	public Vehicle<? extends VehicleData> findNearestCar(Vehicle<? extends VehicleData> car, long time) {
		List<Vehicle<? extends VehicleData>> vehicles = this.findCars(car, time);
		Vector2d carPos = car.getPosition();

		Vehicle<? extends VehicleData> nearestCar = null;
		double distance = -1;

		for (Vehicle<? extends VehicleData> v : vehicles) {
			// AdvancedCarFinder.log.debug("Checking " + v.getName() + " as potential nearest car with its distance: "
			// + v.getPosition().sub(car.getPosition()).length());
			Vector2d otherCarPos = v.getPosition();
			otherCarPos = otherCarPos.sub(carPos);
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
