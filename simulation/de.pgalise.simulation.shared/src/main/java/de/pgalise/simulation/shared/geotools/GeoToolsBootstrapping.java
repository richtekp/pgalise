/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.geotools;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import de.pgalise.simulation.shared.city.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * 
 * @author richter
 */
/*
 * this class also increases the portability to other OpenGIS implementations 
 * possibly requiring more initialization
 */
public class GeoToolsBootstrapping {
	private final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

	public static GeometryFactory getGEOMETRY_FACTORY() {
		return GEOMETRY_FACTORY;
	}
	
	/**
	 * uses {@link LatLngTool#distance(com.javadocmd.simplelatlng.LatLng, com.javadocmd.simplelatlng.LatLng, com.javadocmd.simplelatlng.util.LengthUnit) } which obviously uses Haversine distance calculation because results are correct
	 * @param c1
	 * @param c2
	 * @return 
	 */
	/*
	 * Geotools uses calculation that returns immense errors (> 100 % for values 
	 * below 500 m)
	 */
	public static double distanceHaversineInM(Coordinate c1, Coordinate c2) {
		LatLng p1 = new LatLng(c1.getX(), c1.getY());
		LatLng p2 = new LatLng(c2.getX(), c2.getY());
		return LatLngTool.distance(p1, p2, LengthUnit.METER);
	}

	private GeoToolsBootstrapping() {
	}
	
	
}
