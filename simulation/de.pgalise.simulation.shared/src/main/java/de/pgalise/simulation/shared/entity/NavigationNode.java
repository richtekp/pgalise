/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.entity;

import de.pgalise.simulation.shared.JaxRSCoordinate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class NavigationNode extends Identifiable {

  private static final long serialVersionUID = 1L;
  /**
   * the radius in which vehicles are considered to be on a NavigationNode
   */
  public final static int NODE_RADIUS = 5;

  @Embedded
  private JaxRSCoordinate geoLocation;
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

  protected NavigationNode() {
  }

  public NavigationNode(Long id,
    JaxRSCoordinate geoLocation) {
    super(id);
    this.geoLocation = geoLocation;
  }

  public NavigationNode(Long id,
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
    boolean military) {
    this(id,
      geoLocation);
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

  public JaxRSCoordinate getGeoLocation() {
    return geoLocation;
  }

  public void setGeoLocation(JaxRSCoordinate geoLocation) {
    this.geoLocation = geoLocation;
  }

  public void setMilitary(boolean military) {
    this.setMilitary((Boolean) military);
  }

  public boolean isMilitary() {
    return military;
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
