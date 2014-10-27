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
public class Building extends Identifiable {

  private static final long serialVersionUID = 5874106158590082263L;

  @Transient
  private Double squareMeter;
  @ElementCollection
  private Set<String> tourismTags = new HashSet<>();
  @ElementCollection
  private Set<String> serviceTags = new HashSet<>();
  @ElementCollection
  private Set<String> sportTags = new HashSet<>();
  @ElementCollection
  private Set<String> schoolTags = new HashSet<>();
  @ElementCollection
  @XmlAnyElement
  private Set<String> repairTags = new HashSet<>();
  @ElementCollection
  private Set<String> attractionTags = new HashSet<>();
  @ElementCollection
  private Set<String> shopTags = new HashSet<>();
  @ElementCollection
  private Set<String> emergencyServiceTags = new HashSet<>();
  @ElementCollection
  private Set<String> craftTags = new HashSet<>();
  @ElementCollection
  private Set<String> leisureTags = new HashSet<>();
  private Boolean military = false;
  @ElementCollection
  private Set<String> publicTransportTags = new HashSet<>();
  @ElementCollection
  private Set<String> gamblingTags = new HashSet<>();
  @ElementCollection
  private Set<String> amenityTags = new HashSet<>();
  @ElementCollection
  private Set<String> landuseTags = new HashSet<>();
  private boolean office = false; // this should be in Building (research OSM specification what office is supposed to mean)
  private BaseBoundary boundary;
  
  protected Building() {
  }
  
  public Building(Long id) {
    super(id);
  }

  /**
   *
   * @param id
   * @param referencePoint
   * @param boundaryCoordinates
   */
  public Building(Long id, BaseBoundary boundaryCoordinates) {
    super(id);
  }

  public Building(Long id,
    BaseBoundary referencePoint,
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
    this(id,referencePoint);
    this.tourismTags = tourismTags;
    this.serviceTags = serviceTags;
    this.sportTags = sportTags;
    this.schoolTags = schoolTags;
    this.repairTags = repairTags;
    this.attractionTags = attractionTags;
    this.shopTags = shopTags;
    this.emergencyServiceTags = emergencyServiceTags;
    this.craftTags = craftTags;
    this.leisureTags = leisureTags;
    this.publicTransportTags = publicTransportTags;
    this.gamblingTags = gamblingTags;
    this.amenityTags = amenityTags;
    this.office = office;
    this.military = military;
  }

  public void setBoundary(BaseBoundary boundary) {
    this.boundary = boundary;
  }

  public BaseBoundary getBoundary() {
    return boundary;
  }

  public boolean isMilitary() {
    return military;
  }

  public void setMilitary(boolean military) {
    this.military = military;
  }

  public void setOffice(boolean office) {
    this.office = office;
  }

  public boolean isOffice() {
    return office;
  }

  /**
   * @return the tourismTags
   */
  public Set<String> getTourismTags() {
    return tourismTags;
  }

  /**
   * @return the serviceTags
   */
  public Set<String> getServiceTags() {
    return serviceTags;
  }

  /**
   * @return the sportTags
   */
  public Set<String> getSportTags() {
    return sportTags;
  }

  /**
   * @return the schoolTags
   */
  public Set<String> getSchoolTags() {
    return schoolTags;
  }

  /**
   * @return the repairTags
   */
  public Set<String> getRepairTags() {
    return repairTags;
  }

  /**
   * @return the attractionTags
   */
  public Set<String> getAttractionTags() {
    return attractionTags;
  }

  /**
   * @return the shopTags
   */
  public Set<String> getShopTags() {
    return shopTags;
  }

  /**
   * @return the emergencyServiceTags
   */
  public Set<String> getEmergencyServiceTags() {
    return emergencyServiceTags;
  }

  /**
   * @return the craftTags
   */
  public Set<String> getCraftTags() {
    return craftTags;
  }

  /**
   * @return the leisureTags
   */
  public Set<String> getLeisureTags() {
    return leisureTags;
  }

  /**
   * @return the publicTransportTags
   */
  public Set<String> getPublicTransportTags() {
    return publicTransportTags;
  }

  /**
   * @return the gamblingTags
   */
  public Set<String> getGamblingTags() {
    return gamblingTags;
  }

  /**
   * @param tourismTags the tourismTags to set
   */
  protected void setTourismTags(
    Set<String> tourismTags) {
    this.tourismTags = tourismTags;
  }

  /**
   * @param serviceTags the serviceTags to set
   */
  protected void setServiceTags(
    Set<String> serviceTags) {
    this.serviceTags = serviceTags;
  }

  /**
   * @param sportTags the sportTags to set
   */
  protected void setSportTags(
    Set<String> sportTags) {
    this.sportTags = sportTags;
  }

  /**
   * @param schoolTags the schoolTags to set
   */
  protected void setSchoolTags(
    Set<String> schoolTags) {
    this.schoolTags = schoolTags;
  }

  /**
   * @param repairTags the repairTags to set
   */
  protected void setRepairTags(
    Set<String> repairTags) {
    this.repairTags = repairTags;
  }

  /**
   * @param attractionTags the attractionTags to set
   */
  protected void setAttractionTags(
    Set<String> attractionTags) {
    this.attractionTags = attractionTags;
  }

  /**
   * @param shopTags the shopTags to set
   */
  protected void setShopTags(
    Set<String> shopTags) {
    this.shopTags = shopTags;
  }

  /**
   * @param emergencyServiceTags the emergencyServiceTags to set
   */
  protected void setEmergencyServiceTags(
    Set<String> emergencyServiceTags) {
    this.emergencyServiceTags = emergencyServiceTags;
  }

  /**
   * @param craftTags the craftTags to set
   */
  protected void setCraftTags(
    Set<String> craftTags) {
    this.craftTags = craftTags;
  }

  /**
   * @param leisureTags the leisureTags to set
   */
  protected void setLeisureTags(
    Set<String> leisureTags) {
    this.leisureTags = leisureTags;
  }

  /**
   * @param military the military to set
   */
  protected void setMilitary(Boolean military) {
    this.military = military;
  }

  /**
   * @param publicTransportTags the publicTransportTags to set
   */
  protected void setPublicTransportTags(
    Set<String> publicTransportTags) {
    this.publicTransportTags = publicTransportTags;
  }

  /**
   * @param gamblingTags the gamblingTags to set
   */
  protected void setGamblingTags(
    Set<String> gamblingTags) {
    this.gamblingTags = gamblingTags;
  }

  public void setAmenityTags(
    Set<String> amenityTags) {
    this.amenityTags = amenityTags;
  }

  public Set<String> getAmenityTags() {
    return amenityTags;
  }

  public void setLanduseTags(Set<String> landuseTags) {
    this.landuseTags = landuseTags;
  }

  public Set<String> getLanduseTags() {
    return landuseTags;
  }
}
