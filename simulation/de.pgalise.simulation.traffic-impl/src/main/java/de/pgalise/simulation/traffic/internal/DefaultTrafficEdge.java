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
import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
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
 * @param <D>
 * @author richter
 */
@Entity
public class DefaultTrafficEdge<D extends VehicleData> extends DefaultNavigationEdge<DefaultTrafficNode<D>,DefaultTrafficEdge<D>> implements TrafficEdge<DefaultTrafficNode<D>, DefaultTrafficEdge<D>,D, BaseVehicle<D>> {
	private static final long serialVersionUID = 1L;
//	protected final static Set<Field> TO_STRING_FIELDS_EXCLUDES_ABSTRACT_NAVIGATION_EDGE;
//	static {
//		Set<Field> toStringFieldsExcludes = new HashSet<>(AbstractIdentifiable.TO_STRING_FIELDS_EXCLUDES);
//		TO_STRING_FIELDS_EXCLUDES_ABSTRACT_NAVIGATION_EDGE = toStringFieldsExcludes;
//	}
	private Set<BaseVehicle<D>> vehicles = new HashSet<>(16);
	
	private DefaultTrafficEdge<D> oncomingTrafficEdge;
	
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
		return distance > NavigationNode.NODE_RADIUS;
	}

	@Override
	public Set<BaseVehicle<D>> getVehicles() {
		return vehicles;
	}

	@Override
	public void setVehicles(Set<BaseVehicle<D>> vehicles) {
		this.vehicles = vehicles;
	}

	@Override
	public DefaultTrafficEdge<D> getOncomingTrafficEdge() {
		return oncomingTrafficEdge;
	}

	public void setOncomingTrafficEdge(DefaultTrafficEdge<D> oncomingTrafficEdge) {
		this.oncomingTrafficEdge = oncomingTrafficEdge;
	}

	@Override
	public boolean isOncomingTrafficEdgeReachable() {
		return oncomingTrafficEdgeReachable;
	}

	public void setOncomingTrafficEdgeReachable(boolean oncomingTrafficEdgeReachable) {
		this.oncomingTrafficEdgeReachable = oncomingTrafficEdgeReachable;
	}

	@Override
	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	@Override
	public double getMaxSpeed() {
		return maxSpeed;
	}

	@Override
	public Way<?, ?> getWay() {
		return way;
	}

	public void setWay(
		Way<?, ?> way) {
		this.way = way;
	}

	@Override
	public boolean isCarsAllowed() {
		return carsAllowed;
	}

	@Override
	public void setCarsAllowed(boolean carsAllowed) {
		this.carsAllowed = carsAllowed;
	}

	@Override
	public boolean isBicyclesAllowed() {
		return bicyclesAllowed;
	}

	@Override
	public void setBicyclesAllowed(boolean bicyclesAllowed) {
		this.bicyclesAllowed = bicyclesAllowed;
	}

	@Override
	public int getGrossVehicleWeight() {
		return grossVehicleWeight;
	}

	@Override
	public void setGrossVehicleWeight(int grossVehicleWeight) {
		this.grossVehicleWeight = grossVehicleWeight;
	}

	@Override
	public void setPriorityRoad(boolean priorityRoad) {
		this.priorityRoad = priorityRoad;
	}

	@Override
	public boolean isPriorityRoad() {
		return priorityRoad;
	}
}
