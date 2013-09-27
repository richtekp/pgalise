/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import de.pgalise.simulation.shared.city.Shaped;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.city.AmenityEnum;
import java.util.Set;

/**
 *
 * @author richter
 */
public interface Building extends Shaped {

	public void setAmenities(Set<AmenityEnum> amenities) ;

	public Set<AmenityEnum> getAmenities() ;

	public void setBoundary(Polygon boundary) ;

	public Polygon getBoundary() ;

	public double getSquareMeter() ;
}
