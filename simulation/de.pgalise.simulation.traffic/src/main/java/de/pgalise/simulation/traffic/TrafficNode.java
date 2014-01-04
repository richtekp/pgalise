/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.server.rules.TrafficRule;
import de.pgalise.simulation.traffic.server.sensor.StaticTrafficSensor;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

/**
 * A <tt>TrafficNode</tt> is a position (represented by a 
 * {@link DirectPosition2D}) which are passed by {@link Vehicle}s when they 
 * navigate from one {@link NavigationNode} to another. 
 * <tt>NavigationNode</tt>s are supposed to be of no interest as a navigation target.<br/>
 * <tt>NavigationNode</tt> uses a String {@link Id} which will be initialized with a the return of {@link UUID#randomUUID() } in order to avoid having two properties with the same semantic and still be able to implement {@link Node#getId() }.<br/>
 * A node is considered to be reached if the difference of its location and another point is less or equals than {@link NavigationNode#NODE_RADIUS}. Any <tt>NavigationNode</tt> is obliged to have a distance of {@link #NODE_RADIUS} (exclusive) to any other different node. This constraint will be not checked at creation of <tt>NavigationNode</tt>s, but in {@link NavigationEdge} and possibly other data structures. 
 * @author richter
 */
/*
 * implementation notes:
 * - default constructor is necessary and needs to be public in order to be 
 * usage for Graph.addEdge (no idea why)
 */
@Entity
public class TrafficNode extends NavigationNode  {
	private static final long serialVersionUID = 1L;
	
	private boolean onJunction;
	
	private boolean onStreet;
	private boolean roundabout;
	@Transient
	@XmlTransient
	private Set<StaticTrafficSensor<?>> sensors;
	@Transient
	@XmlTransient
	private Set<Vehicle<?>> vehicles;
	@Transient
	@XmlTransient
	private TrafficRule trafficRule;
	@OneToOne
	private BusStop busStop;

	protected TrafficNode() {
		super();
	}
	
	public TrafficNode(JaxRSCoordinate geoLocation) {
		super(geoLocation);
	}

	public boolean isOnJunction() {
		return onJunction;
	}

	public boolean isOnStreet() {
		return onStreet;
	}

	public void setSensors(
		Set<StaticTrafficSensor<?>> sensors) {
		this.sensors = sensors;
	}

	public Set<StaticTrafficSensor<?>> getSensors() {
		return sensors;
	}

	public void setVehicles(
		Set<Vehicle<?>> vehicles) {
		this.vehicles = vehicles;
	}

	public Set<Vehicle<?>> getVehicles() {
		return vehicles;
	}

	public void setTrafficRule(TrafficRule trafficRule) {
		this.trafficRule = trafficRule;
	}

	public TrafficRule getTrafficRule() {
		return trafficRule;
	}

	public void setRoundabout(boolean roundabout) {
		this.roundabout = roundabout;
	}

	public boolean isRoundabout() {
		return roundabout;
	}

	public void setBusStop(BusStop busStop) {
		this.busStop = busStop;
	}

	public BusStop getBusStop() {
		return busStop;
	}
}
