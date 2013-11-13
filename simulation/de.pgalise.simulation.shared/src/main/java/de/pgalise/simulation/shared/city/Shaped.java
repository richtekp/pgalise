/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.persistence.Identifiable;

/**
 *
 * @author richter
 */
public interface Shaped extends Identifiable {
	
	public Coordinate getCenterPoint() ;

	public Polygon getBoundaries() ;

	public void setBoundaries(Polygon boundaries) ;
}
