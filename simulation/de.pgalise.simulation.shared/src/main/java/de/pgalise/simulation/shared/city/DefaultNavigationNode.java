/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.city.CityNodeTag;
import de.pgalise.simulation.shared.city.CityNodeTagCategoryEnum;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import java.util.Map;
import java.util.Set;
import javax.persistence.Embedded;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

/**
 *
 * @author richter
 */
public class DefaultNavigationNode extends AbstractIdentifiable implements NavigationNode {
	private static final long serialVersionUID = 1L;
	@Embedded
	private Coordinate geoLocation;
	@MapKey
	@OneToMany
	private Map<CityNodeTagCategoryEnum, Set<? extends CityNodeTag>> tags;

	protected DefaultNavigationNode() {
	}

	public DefaultNavigationNode(Coordinate geoLocation) {
		this.geoLocation = geoLocation;
	}

	public Coordinate getGeoLocation() {
		return geoLocation;
	}

	public void setGeoLocation(Coordinate geoLocation) {
		this.geoLocation = geoLocation;
	}

	public Map<CityNodeTagCategoryEnum, Set<? extends CityNodeTag>> getTags() {
		return tags;
	}

	public void setTags(Map<CityNodeTagCategoryEnum, Set<? extends CityNodeTag>> tags) {
		this.tags = tags;
	}
}
