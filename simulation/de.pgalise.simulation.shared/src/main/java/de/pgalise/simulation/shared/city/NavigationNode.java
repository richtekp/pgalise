/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.city.CityNodeTag;
import de.pgalise.simulation.shared.city.CityNodeTag;
import de.pgalise.simulation.shared.city.CityNodeTagCategoryEnum;
import de.pgalise.simulation.shared.city.CityNodeTagCategoryEnum;
import de.pgalise.simulation.shared.city.LanduseTagEnum;
import de.pgalise.simulation.shared.persistence.Identifiable;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author richter
 */
public interface NavigationNode extends Identifiable {
	/**
	 * The radius in which locations are considered to be on the node
	 */
	public final static double NODE_RADIUS = 5.0;

	void setGeoLocation(Coordinate geoLocation) ;

	Coordinate getGeoLocation() ;
	
	Map<CityNodeTagCategoryEnum, Set<? extends CityNodeTag>> getTags() ;

	void setTags(Map<CityNodeTagCategoryEnum, Set<? extends CityNodeTag>> tags) ;
}
