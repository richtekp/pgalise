/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal;

import de.pgalise.simulation.traffic.OSMNavigationNode;
import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.city.DefaultNavigationNode;
import de.pgalise.simulation.traffic.internal.model.vehicle.BaseVehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.rules.TrafficRule;
import de.pgalise.simulation.traffic.server.sensor.StaticTrafficSensor;
import java.util.Set;

/**
 *
 * @author richter
 * @param <D>
 */
public class DefaultOSMNavigationNode<D extends VehicleData> extends DefaultNavigationNode implements OSMNavigationNode<DefaultTrafficNode<D>, DefaultTrafficEdge<D>, D, BaseVehicle<D>> {
	private static final long serialVersionUID = 1L;
	private String oSMID;

	protected DefaultOSMNavigationNode() {
	}

	public DefaultOSMNavigationNode(Coordinate geoLocation) {
		super(geoLocation);
	}

	@Override
	public String getOSMId() {
		return oSMID;
	}

	public void setOSMId(String oSMId) {
		this.oSMID = oSMId;
	}

	@Override
	public boolean isOnStreet() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isOnJunction() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isRoundabout() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setRoundabout(boolean roundabout) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public TrafficRule<D, DefaultTrafficNode<D>, DefaultTrafficEdge<D>, BaseVehicle<D>> getTrafficRule() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setTrafficRule(
		TrafficRule<D, DefaultTrafficNode<D>, DefaultTrafficEdge<D>, BaseVehicle<D>> trafficRule) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Set<BaseVehicle<D>> getVehicles() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setVehicles(Set<BaseVehicle<D>> vehicles) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Set<StaticTrafficSensor<DefaultTrafficNode<D>, DefaultTrafficEdge<D>, D, BaseVehicle<D>>> getSensors() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setSensors(
		Set<StaticTrafficSensor<DefaultTrafficNode<D>, DefaultTrafficEdge<D>, D, BaseVehicle<D>>> sensors) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
