/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal;

import de.pgalise.simulation.traffic.internal.server.rules.AbstractTrafficRule;
import de.pgalise.simulation.shared.city.DefaultNavigationNode;
import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.city.CityNodeTag;
import de.pgalise.simulation.shared.city.CityNodeTagCategoryEnum;
import de.pgalise.simulation.shared.city.LanduseTagEnum;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.rules.TrafficRule;
import de.pgalise.simulation.traffic.server.sensor.StaticTrafficSensor;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

/**
 * A <tt>TrafficNode</tt> is a position (represented by a 
 * {@link DirectPosition2D}) which are passed by {@link Vehicle}s when they 
 * navigate from one {@link NavigationNode} to another. 
 * <tt>NavigationNode</tt>s are supposed to be of no interest as a navigation target.<br/>
 * <tt>NavigationNode</tt> uses a String {@link Id} which will be initialized with a the return of {@link UUID#randomUUID() } in order to avoid having two properties with the same semantic and still be able to implement {@link Node#getId() }.<br/>
 * A node is considered to be reached if the difference of its location and another point is less or equals than {@link NavigationNode#NODE_RADIUS}. Any <tt>NavigationNode</tt> is obliged to have a distance of {@link #NODE_RADIUS} (exclusive) to any other different node. This constraint will be not checked at creation of <tt>NavigationNode</tt>s, but in {@link NavigationEdge} and possibly other data structures.
 * @param <D> 
 * @author richter
 */
/*
 * implementation notes:
 * - default constructor is necessary and needs to be public in order to be 
 * usage for Graph.addEdge (no idea why)
 */
@Entity
public class DefaultTrafficNode<D extends VehicleData> extends DefaultNavigationNode implements TrafficNode<DefaultTrafficNode<D>, DefaultTrafficEdge<D>,D, BaseVehicle<D>> {
	private static final long serialVersionUID = 1L;
	
	private boolean onJunction;
	
	private boolean onStreet;
	private Set<StaticTrafficSensor<DefaultTrafficNode<D>,DefaultTrafficEdge<D>>> sensors;
	private Set<BaseVehicle<D>> vehicles;
	private AbstractTrafficRule<D> trafficRule;
	
	protected DefaultTrafficNode() {}

	public DefaultTrafficNode(Coordinate geoLocation) {
		super(geoLocation);
	}

	@Override
	public boolean isOnJunction() {
		return onJunction;
	}

	@Override
	public boolean isOnStreet() {
		return onStreet;
	}

	@Override
	public void setSensors(
		Set<StaticTrafficSensor<DefaultTrafficNode<D>,DefaultTrafficEdge<D>>> sensors) {
		this.sensors = sensors;
	}

	@Override
	public Set<StaticTrafficSensor<DefaultTrafficNode<D>,DefaultTrafficEdge<D>>> getSensors() {
		return sensors;
	}

	@Override
	public void setVehicles(
		Set<BaseVehicle<D>> vehicles) {
		this.vehicles = vehicles;
	}

	@Override
	public Set<BaseVehicle<D>> getVehicles() {
		return vehicles;
	}

	@Override
	public void setTrafficRule(AbstractTrafficRule<D> trafficRule) {
		this.trafficRule = trafficRule;
	}

	@Override
	public AbstractTrafficRule<D> getTrafficRule() {
		return trafficRule;
	}
}
