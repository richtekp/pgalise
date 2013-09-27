/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal;

import de.pgalise.simulation.shared.city.DefaultNavigationEdge;
import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.shared.city.Way;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.vecmath.Vector2d;

/**
 * represents an immutable edge in the {@link TrafficGraph}. <tt>NavigationNode</tt>s of the edge are obliged to have {@link NavigationNode#NODE_RADIUS} distance.
 * 
 * @param <N> 
 * @param <E> 
 * @author richter
 */
@Entity
public class DefaultTrafficEdge<N extends DefaultTrafficNode, E extends DefaultTrafficEdge<N,E>> extends DefaultNavigationEdge<N,E> implements TrafficEdge<N,E> {
	private static final long serialVersionUID = 1L;
//	protected final static Set<Field> TO_STRING_FIELDS_EXCLUDES_ABSTRACT_NAVIGATION_EDGE;
//	static {
//		Set<Field> toStringFieldsExcludes = new HashSet<>(AbstractIdentifiable.TO_STRING_FIELDS_EXCLUDES);
//		TO_STRING_FIELDS_EXCLUDES_ABSTRACT_NAVIGATION_EDGE = toStringFieldsExcludes;
//	}
	private Set<Vehicle<?>> vehicles = new HashSet<>(16);
	
	private E oncomingTrafficEdge;
	
	private boolean oncomingTrafficEdgeReachable = true;
	
	private double maxSpeed;
	private Way<?,?> way;
	private boolean priorityRoad;
	private boolean carsAllowed;
	private boolean bicyclesAllowed;
	private int grossVehicleWeight;
	
//	@Override
//	protected Set<Field> getToStringFieldExcludes() {
//		return Collections.unmodifiableSet(TO_STRING_FIELDS_EXCLUDES_ABSTRACT_NAVIGATION_EDGE);
//	}

	public boolean validateNavigationNodeDistance() {
		double distance = 
			GeoToolsBootstrapping.distanceHaversineInM(getSource().getGeoLocation(), getTarget().getGeoLocation());
		if(distance <= NavigationNode.NODE_RADIUS) {
			return false;
		}
		return true;
	}

	@Override
	public Set<Vehicle<?>> getVehicles() {
		return vehicles;
	}

	@Override
	public void setVehicles(Set<Vehicle<?>> vehicles) {
		this.vehicles = vehicles;
	}

	@Override
	public E getOncomingTrafficEdge() {
		return oncomingTrafficEdge;
	}

	public void setOncomingTrafficEdge(E oncomingTrafficEdge) {
		this.oncomingTrafficEdge = oncomingTrafficEdge;
	}

	@Override
	public boolean isOncomingTrafficEdgeReachable() {
		return oncomingTrafficEdgeReachable;
	}

	public void setOncomingTrafficEdgeReachable(boolean oncomingTrafficEdgeReachable) {
		this.oncomingTrafficEdgeReachable = oncomingTrafficEdgeReachable;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public double getMaxSpeed() {
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
}
