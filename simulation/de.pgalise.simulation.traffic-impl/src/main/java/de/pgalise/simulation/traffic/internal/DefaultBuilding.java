/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
 
package de.pgalise.simulation.traffic.internal;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.city.AmenityEnum;
import de.pgalise.simulation.shared.city.CityNodeTag;
import de.pgalise.simulation.shared.city.CityNodeTagCategoryEnum;
import java.awt.Point;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import javax.persistence.Transient;

/**
 * A building is a {@link RectangularBoundary} with several openstreetmap tags.<br/>
 * A lot of useful information can be retrieved using {@link AreaFunction} and other geotools classes and the <tt>boundary</tt> property. Surface size and center point will be calculated lazily.
 * @author Timo
 */
public class DefaultBuilding extends DefaultTrafficNode  {
	private static final long serialVersionUID = 5874106158590082263L;
	
	@Transient
	private Double squareMeter;
	@Transient
	private Point centerPoint;
	private Polygon boundary;
	private Set<AmenityEnum> amenities;

	/**
	 *
	 * @param geoLocation the center position of the building which will be used 
	 * for navigation
	 * @param tags 
	 * @param boundary an optional boundary which is not used in the simulation
	 */
	public DefaultBuilding(Coordinate geoLocation, Map<CityNodeTagCategoryEnum, Collection<? extends CityNodeTag>> tags, Polygon boundary) {
		super(geoLocation);
		this.boundary = boundary;
	}

	public void setAmenities(Set<AmenityEnum> amenities) {
		this.amenities = amenities;
	}

	public Set<AmenityEnum> getAmenities() {
		return amenities;
	}

	public void setBoundary(Polygon boundary) {
		this.boundary = boundary;
	}

	public Polygon getBoundary() {
		return boundary;
	}

	public double getSquareMeter() {
		if(squareMeter == null) {
			squareMeter = boundary.getArea();
		}
		return squareMeter;
	}
}
