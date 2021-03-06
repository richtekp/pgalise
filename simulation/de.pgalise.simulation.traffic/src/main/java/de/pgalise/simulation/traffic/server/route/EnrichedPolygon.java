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
package de.pgalise.simulation.traffic.server.route;

import java.awt.Polygon;
import java.util.Set;

/**
 * The enriched wraps a polygon with its landuse tag.
 *
 * @author Timo
 */
public class EnrichedPolygon {

	private Set<String> landuse;
	private Polygon polygon;

	public EnrichedPolygon(Polygon p,
		Set<String> l) {
		this.polygon = p;
		this.landuse = l;
	}

	public Set<String> getLanduse() {
		return this.landuse;
	}

	public Polygon getPolygon() {
		return this.polygon;
	}

	public void setLanduse(Set<String> landuse) {
		this.landuse = landuse;
	}

	public void setPolygon(Polygon polygon) {
		this.polygon = polygon;
	}
}
