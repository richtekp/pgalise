/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.model.vehicle;

import de.pgalise.simulation.shared.city.Coordinate;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpOutput;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsClockInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.GpsInterferer;
import java.util.Iterator;
import java.util.Set;
import javax.ejb.EJB;
import de.pgalise.simulation.shared.city.Vector2d;

/**
 *
 * @author richter
 */
public abstract class AbstractVehicleFactory implements VehicleFactory
{
	private static final long serialVersionUID = 1L;
	private TrafficGraphExtensions trafficGraphExtensions;
	@EJB
	private IdGenerator idGenerator;
	@EJB
	private RandomSeedService randomSeedService;
	@EJB
	private TcpIpOutput output;
	
	public AbstractVehicleFactory() {
	}

	public AbstractVehicleFactory(		TrafficGraphExtensions trafficGraphExtensions,
		IdGenerator idGenerator,
		RandomSeedService randomSeedService) {
		this.trafficGraphExtensions = trafficGraphExtensions;
		this.idGenerator = idGenerator;
		this.randomSeedService = randomSeedService;
	}

	public void setTrafficGraphExtensions(
		TrafficGraphExtensions trafficGraphExtensions) {
		this.trafficGraphExtensions = trafficGraphExtensions;
	}

	public TrafficGraphExtensions getTrafficGraphExtensions() {
		return trafficGraphExtensions;
	}

	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}

	@Override
	public IdGenerator getIdGenerator() {
		return idGenerator;
	}
	
	public Coordinate generateRandomPosition(Set<TrafficEdge> edges) {
		int edgeCount = edges.size();
		int chosenIndex = (int)(Math.random()*edgeCount);
		int i=0;
		Iterator<TrafficEdge> it = edges.iterator();
		while(i<chosenIndex) {
			it.next();
		}
		TrafficEdge chosenEdge = it.next();
		double chosenOffset = chosenEdge.getEdgeLength()*Math.random();
		Vector2d offsetVector = new Vector2d(chosenEdge.getVector());
		offsetVector.scale(chosenOffset);
		Vector2d positionVector = new Vector2d(chosenEdge.getSource().getGeoLocation().getX(), chosenEdge.getSource().getGeoLocation().getY());
		positionVector.add(offsetVector);
		return new Coordinate(positionVector.getX(), positionVector.getY());
	}

	public void setRandomSeedService(RandomSeedService randomSeedService) {
		this.randomSeedService = randomSeedService;
	}

	@Override
	public RandomSeedService getRandomSeedService() {
		return randomSeedService;
	}
	
	/**
	 * 
	 * @return 
	 */
	/*
	encapsulating the access to GpsInterferer(s) allows to change the mechanism of retrieval later (e.g. get references from a pool)
	*/
	public GpsInterferer retrieveGpsInterferer(){
		return new GpsClockInterferer(randomSeedService);
	}

	@Override
	public TcpIpOutput getTcpIpOutput() {
		return output;
	}
}
