///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package de.pgalise.simulation.traffic.internal;
//
//import de.pgalise.simulation.traffic.NavigationEdge;
//import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
///**
// * An implementation of {@link NavigationEdge} which is suitable for the Nagel Schreckenberg traffic (jam) model (see {@linkplain http://de.wikipedia.org/wiki/Nagel-Schreckenberg-Modell}) ignoring lane count
// * @author richter
// */
//public class NagelSchreckenbergNavigationEdge extends DefaultAbstractNavigationEdge {
//	private static final long serialVersionUID = 1L;
//	/**
//	 * size of the sections list is the information about 
//	 */
//	private List<Vehicle<?>> sections;
//	
//	/**
//	 * creates a <tt>NagelSchreckenbergNavigationEdge</tt> which a section for every km
//	 */
//	public NagelSchreckenbergNavigationEdge() {
//		super();
//		this.sections = new ArrayList<>((int) (getEdgeLength().to(SI.METER).getValue()/0.001));
//	}
//
//	public NagelSchreckenbergNavigationEdge(int sectionsCount) {
//		this.sections = new ArrayList<>(sectionsCount);
//	}
//
//	/**
//	 * checks whether a vehicle can be taken according to the Nagel Schreckenberg model
//	 * @param vehicle
//	 * @return 
//	 */
//	@Override
//	public boolean takeVehicle(Vehicle<?> vehicle, long timestamp) {
//		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//	}
//
//	@Override
//	public Set<Vehicle<?>> getLeavingVehicles() {
//		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//	}
//
//	@Override
//	public Map<Vehicle<?>, List<NavigationEdge>> updateVehicles(long timestamp) {
//		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//	}
//	
//}
