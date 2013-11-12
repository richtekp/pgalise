/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.shared.city.Way;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

/**
 * represents an immutable edge in the {@link TrafficGraph}. <tt>NavigationNode</tt>s of the edge are obliged to have {@link NavigationNode#NODE_RADIUS} distance.
 * 
 * @param <D>
 * @author richter
 */
@Entity
public class TrafficEdge extends NavigationEdge<TrafficNode> {
	private static final long serialVersionUID = 1L;
//	protected final static Set<Field> TO_STRING_FIELDS_EXCLUDES_ABSTRACT_NAVIGATION_EDGE;
//	static {
//		Set<Field> toStringFieldsExcludes = new HashSet<>(AbstractIdentifiable.TO_STRING_FIELDS_EXCLUDES);
//		TO_STRING_FIELDS_EXCLUDES_ABSTRACT_NAVIGATION_EDGE = toStringFieldsExcludes;
//	}
	@Transient
	private Set<Vehicle<?>> vehicles = new HashSet<>(16);
	
	@OneToOne
	private TrafficEdge oncomingTrafficEdge;
	
	private boolean oncomingTrafficEdgeReachable = true;
	
	private double maxSpeed;
	@ManyToOne()
	private Way<?,?> way;
	private boolean priorityRoad;
	private boolean carsAllowed;
	private boolean bicyclesAllowed;
	private int grossVehicleWeight;
	
//	@Override
//	protected Set<Field> getToStringFieldExcludes() {
//		return Collections.unmodifiableSet(TO_STRING_FIELDS_EXCLUDES_ABSTRACT_NAVIGATION_EDGE);
//	}

	protected TrafficEdge() {
	}

	protected TrafficEdge(TrafficNode source, TrafficNode target) {
		super(source,
			target);
	}

	public TrafficEdge(TrafficEdge oncomingTrafficEdge, double maxSpeed, Way<?, ?> way, boolean priorityRoad, boolean carsAllowed, boolean bicyclesAllowed, int grossVehicleWeight) {
		this.oncomingTrafficEdge = oncomingTrafficEdge;
		this.maxSpeed = maxSpeed;
		this.way = way;
		this.priorityRoad = priorityRoad;
		this.carsAllowed = carsAllowed;
		this.bicyclesAllowed = bicyclesAllowed;
		this.grossVehicleWeight = grossVehicleWeight;
	}

	public boolean validateNavigationNodeDistance() {
		double distance = 
			GeoToolsBootstrapping.distanceHaversineInM(getSource().getGeoLocation(), getTarget().getGeoLocation());
		return distance > NavigationNode.NODE_RADIUS;
	}

	public Set<Vehicle<?>> getVehicles() {
		return vehicles;
	}

	public void setVehicles(Set<Vehicle<?>> vehicles) {
		this.vehicles = vehicles;
	}

	public TrafficEdge getOncomingTrafficEdge() {
		return oncomingTrafficEdge;
	}

	public void setOncomingTrafficEdge(TrafficEdge oncomingTrafficEdge) {
		this.oncomingTrafficEdge = oncomingTrafficEdge;
	}

	public boolean isOncomingTrafficEdgeReachable() {
		return oncomingTrafficEdgeReachable;
	}

	public void setOncomingTrafficEdgeReachable(boolean oncomingTrafficEdgeReachable) {
		this.oncomingTrafficEdgeReachable = oncomingTrafficEdgeReachable;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	/**
	 * 
	 * @return the maximal speed on this edge or <code>null</code> is the value is not set
	 */
	public Double getMaxSpeed() {
		return maxSpeed;
	}

	public Way<?, ?> getWay() {
		return way;
	}

	public void setWay(
		Way<?, ?> way) {
		this.way = way;
	}

	public boolean isCarsAllowed() {
		return carsAllowed;
	}

	public void setCarsAllowed(boolean carsAllowed) {
		this.carsAllowed = carsAllowed;
	}

	public boolean isBicyclesAllowed() {
		return bicyclesAllowed;
	}

	public void setBicyclesAllowed(boolean bicyclesAllowed) {
		this.bicyclesAllowed = bicyclesAllowed;
	}

	public int getGrossVehicleWeight() {
		return grossVehicleWeight;
	}

	public void setGrossVehicleWeight(int grossVehicleWeight) {
		this.grossVehicleWeight = grossVehicleWeight;
	}

	public void setPriorityRoad(boolean priorityRoad) {
		this.priorityRoad = priorityRoad;
	}

	public boolean isPriorityRoad() {
		return priorityRoad;
	}

	@Override
	public TrafficNode getSource() {
		return super.getSource();
	}

	@Override
	public TrafficNode getTarget() {
		return super.getTarget(); 
	}
	
	
}
