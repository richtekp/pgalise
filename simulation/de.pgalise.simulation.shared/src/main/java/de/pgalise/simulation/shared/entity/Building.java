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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAnyElement;

/**
 * A building is a {@link NavigationNode} which referes to the reference point 
 * of the building (e.g. main enterance, central court) with surrounding 
 * {@link BaseCoordinate}s forming the boundary of the building (they can be 
 * used for navigation if they are {@link NavigationNode}s). NavigationNodes 
 * inside the building are not related to building instances. Navigation node 
 * inside buildings are considered to be accessible if there's a {@link NavigationEdge} starting at the boundary of the building.
 * 
 * <h3>Tags</h3>
 * A lot of tags can be used on both {@link NavigationNode} and 
 * <tt>Building</tt>. Buildings are NavigationNode. Their geoLocation property 
 * refers to a refernce point of the building (e.g. the main enterance), 
 * geographical information can be retrieved from geoInfo property. Further 
 * point which are interesting for navigation e.g. other enterances, parking 
 * in the court can be stored in navigationNodes (geoLocation is passed 
 * separately to distungish the refernce point from other NavigationNode). 
 * Don't rely to navigationNodes for the boundary, it is in geoInfo.<br/>
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
  private BaseBoundary geoInfo;

  protected Building() {
  }
  
  public Building(Long id) {
    super(id);
  }

  /**
   *
   * @param id
   * @param navigationNodes positions at the border of the building or 
   * accessible in side the building's boundary (e.g. in a court) which can be 
   * used for navigation (The most important point, e.g. pointing to the main 
   * enterance, can be accessed through the geoInfo.centerPoint property)
   * @param position
   */
  public Building(Long id,BaseCoordinate geoLocation,
    BaseBoundary position) {
    this(id);
    this.geoInfo = position;
  }

  public Building(Long id,
    BaseCoordinate geoLocation,
    BaseBoundary geoInfo,
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
    boolean military) {
    super(id,
      geoLocation.x,
      geoLocation.y,
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
  }

  public double getSquareMeter() {
    if (squareMeter == null) {
      squareMeter = geoInfo.retrieveBoundary().getArea();
    }
    return squareMeter;
  }

  public void setGeoInfo(BaseBoundary geoInfo) {
    this.geoInfo = geoInfo;
  }

  public BaseBoundary getGeoInfo() {
    return geoInfo;
  }
}
