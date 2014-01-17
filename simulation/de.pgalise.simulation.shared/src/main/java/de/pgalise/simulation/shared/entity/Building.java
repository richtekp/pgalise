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
package de.pgalise.simulation.shared.entity;

import de.pgalise.simulation.shared.JaxRSCoordinate;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * A building is a {@link RectangularBoundary} with several openstreetmap
 * tags.<br/>
 * A lot of useful information can be retrieved using {@link AreaFunction} and
 * other geotools classes and the <tt>boundary</tt> property. Surface size and
 * center point will be calculated lazily.
 *
 * @author Timo
 */
@Entity
public class Building extends NavigationNode {

  private static final long serialVersionUID = 5874106158590082263L;

  @Transient
  private Double squareMeter;
  private BaseGeoInfo position;

  protected Building() {
  }

  /**
   *
   * @param geoLocation the center position of the building which will be used
   * for navigation
   * @param tags
   * @param boundary an optional boundary which is not used in the simulation
   */
  public Building(Long id,
    JaxRSCoordinate geoLocation,
    BaseGeoInfo position) {
    super(id,
      geoLocation);
    this.position = position;
  }

  public Building(Long id,
    JaxRSCoordinate geoLocation,
    Set<String> tourismTags,
    Set<String> serviceTags,
    Set<String> sportTags,
    Set<String> schoolTags,
    Set<String> repairTags,
    Set<String> attractionTags,
    Set<String> shopTags,
    Set<String> emergencyServiceTags,
    Set<String> craftTags,
    Set<String> leisureTags,
    Set<String> publicTransportTags,
    Set<String> gamblingTags,
    Set<String> amenityTags,
    Set<String> landuseTags,
    boolean office,
    boolean military,
    BaseGeoInfo position) {
    super(id,
      geoLocation,
      tourismTags,
      serviceTags,
      sportTags,
      schoolTags,
      repairTags,
      attractionTags,
      shopTags,
      emergencyServiceTags,
      craftTags,
      leisureTags,
      publicTransportTags,
      gamblingTags,
      amenityTags,
      landuseTags,
      office,
      military);
    this.position = position;
  }

  public double getSquareMeter() {
    if (squareMeter == null) {
      squareMeter = position.getBoundaries().getArea();
    }
    return squareMeter;
  }

  public void setPosition(BaseGeoInfo position) {
    this.position = position;
  }

  public BaseGeoInfo getPosition() {
    return position;
  }

}
