/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.geotools;

import com.vividsolutions.jts.geom.GeometryFactory;

/**
 * 
 * @author richter
 */
/*
 * this class also increases the portability to other OpenGIS implementations 
 * possibly requiring more initialization
 */
public class GeotoolsBootstrapping {
	private final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

	public static GeometryFactory getGEOMETRY_FACTORY() {
		return GEOMETRY_FACTORY;
	}

	private GeotoolsBootstrapping() {
	}
	
	
}
