/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import de.pgalise.simulation.shared.tag.BaseTag;
import de.pgalise.simulation.shared.tag.RepairTag;
import de.pgalise.simulation.shared.tag.SchoolTag;
import de.pgalise.simulation.shared.tag.LanduseTag;
import de.pgalise.simulation.shared.tag.CraftTag;
import de.pgalise.simulation.shared.tag.LeisureTag;
import de.pgalise.simulation.shared.tag.GamblingTag;
import de.pgalise.simulation.shared.tag.AttractionTag;
import de.pgalise.simulation.shared.tag.ServiceTag;
import de.pgalise.simulation.shared.tag.PublicTransportTag;
import de.pgalise.simulation.shared.tag.AmenityTag;
import de.pgalise.simulation.shared.tag.SportTag;
import de.pgalise.simulation.shared.tag.EmergencyServiceTag;
import de.pgalise.simulation.shared.tag.TourismTag;
import de.pgalise.simulation.shared.tag.ShopTag;
import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
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
public class NavigationNode extends AbstractIdentifiable {

	private static final long serialVersionUID = 1L;
	/**
	 * the radius in which vehicles are considered to be on a NavigationNode
	 */
	public final static int NODE_RADIUS = 5;

	@Embedded
	private JaxRSCoordinate geoLocation;
	@ElementCollection(targetClass = BaseTag.class)
	private Set<TourismTag> tourismTags = new HashSet<>();
	@ElementCollection(targetClass = BaseTag.class)
	private Set<ServiceTag> serviceTags = new HashSet<>();
	@ElementCollection(targetClass = BaseTag.class)
	private Set<SportTag> sportTags = new HashSet<>();
	@ElementCollection(targetClass = BaseTag.class)
	private Set<SchoolTag> schoolTags = new HashSet<>();
	@ElementCollection(targetClass = BaseTag.class)
	@XmlAnyElement
	private Set<RepairTag> repairTags = new HashSet<>();
	@ElementCollection(targetClass = BaseTag.class)
	private Set<AttractionTag> attractionTags = new HashSet<>();
	@ElementCollection(targetClass = BaseTag.class)
	private Set<ShopTag> shopTags = new HashSet<>();
	@ElementCollection(targetClass = BaseTag.class)
	private Set<EmergencyServiceTag> emergencyServiceTags = new HashSet<>();
	@ElementCollection(targetClass = BaseTag.class)
	private Set<CraftTag> craftTags = new HashSet<>();
	@ElementCollection(targetClass = BaseTag.class)
	private Set<LeisureTag> leisureTags = new HashSet<>();
	private Boolean military = false;
	@ElementCollection(targetClass = BaseTag.class)
	private Set<PublicTransportTag> publicTransportTags = new HashSet<>();
	@ElementCollection(targetClass = BaseTag.class)
	private Set<GamblingTag> gamblingTags = new HashSet<>();
	@ElementCollection(targetClass = BaseTag.class)
	private Set<AmenityTag> amenityTags = new HashSet<>();
	@ElementCollection(targetClass = BaseTag.class)
	private Set<LanduseTag> landuseTags = new HashSet<>();
	private boolean office = false; // this should be in Building (research OSM specification what office is supposed to mean)

	protected NavigationNode() {
	}

	public NavigationNode(JaxRSCoordinate geoLocation) {
		this.geoLocation = geoLocation;
	}

	public NavigationNode(JaxRSCoordinate geoLocation,
		Set<TourismTag> tourismTags,
		Set<ServiceTag> serviceTags,
		Set<SportTag> sportTags,
		Set<SchoolTag> schoolTags,
		Set<RepairTag> repairTags,
		Set<AttractionTag> attractionTags,
		Set<ShopTag> shopTags,
		Set<EmergencyServiceTag> emergencyServiceTags,
		Set<CraftTag> craftTags,
		Set<LeisureTag> leisureTags,
		Set<PublicTransportTag> publicTransportTags,
		Set<GamblingTag> gamblingTags,
		Set<AmenityTag> amenityTags,
		Set<LanduseTag> landuseTags,
		boolean office,
		boolean military) {
		this(geoLocation);
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
	public Set<TourismTag> getTourismTags() {
		return tourismTags;
	}

	/**
	 * @return the serviceTags
	 */
	public Set<ServiceTag> getServiceTags() {
		return serviceTags;
	}

	/**
	 * @return the sportTags
	 */
	public Set<SportTag> getSportTags() {
		return sportTags;
	}

	/**
	 * @return the schoolTags
	 */
	public Set<SchoolTag> getSchoolTags() {
		return schoolTags;
	}

	/**
	 * @return the repairTags
	 */
	public Set<RepairTag> getRepairTags() {
		return repairTags;
	}

	/**
	 * @return the attractionTags
	 */
	public Set<AttractionTag> getAttractionTags() {
		return attractionTags;
	}

	/**
	 * @return the shopTags
	 */
	public Set<ShopTag> getShopTags() {
		return shopTags;
	}

	/**
	 * @return the emergencyServiceTags
	 */
	public Set<EmergencyServiceTag> getEmergencyServiceTags() {
		return emergencyServiceTags;
	}

	/**
	 * @return the craftTags
	 */
	public Set<CraftTag> getCraftTags() {
		return craftTags;
	}

	/**
	 * @return the leisureTags
	 */
	public Set<LeisureTag> getLeisureTags() {
		return leisureTags;
	}

	/**
	 * @return the publicTransportTags
	 */
	public Set<PublicTransportTag> getPublicTransportTags() {
		return publicTransportTags;
	}

	/**
	 * @return the gamblingTags
	 */
	public Set<GamblingTag> getGamblingTags() {
		return gamblingTags;
	}

	/**
	 * @param tourismTags the tourismTags to set
	 */
	protected void setTourismTags(
		Set<TourismTag> tourismTags) {
		this.tourismTags = tourismTags;
	}

	/**
	 * @param serviceTags the serviceTags to set
	 */
	protected void setServiceTags(
		Set<ServiceTag> serviceTags) {
		this.serviceTags = serviceTags;
	}

	/**
	 * @param sportTags the sportTags to set
	 */
	protected void setSportTags(
		Set<SportTag> sportTags) {
		this.sportTags = sportTags;
	}

	/**
	 * @param schoolTags the schoolTags to set
	 */
	protected void setSchoolTags(
		Set<SchoolTag> schoolTags) {
		this.schoolTags = schoolTags;
	}

	/**
	 * @param repairTags the repairTags to set
	 */
	protected void setRepairTags(
		Set<RepairTag> repairTags) {
		this.repairTags = repairTags;
	}

	/**
	 * @param attractionTags the attractionTags to set
	 */
	protected void setAttractionTags(
		Set<AttractionTag> attractionTags) {
		this.attractionTags = attractionTags;
	}

	/**
	 * @param shopTags the shopTags to set
	 */
	protected void setShopTags(
		Set<ShopTag> shopTags) {
		this.shopTags = shopTags;
	}

	/**
	 * @param emergencyServiceTags the emergencyServiceTags to set
	 */
	protected void setEmergencyServiceTags(
		Set<EmergencyServiceTag> emergencyServiceTags) {
		this.emergencyServiceTags = emergencyServiceTags;
	}

	/**
	 * @param craftTags the craftTags to set
	 */
	protected void setCraftTags(
		Set<CraftTag> craftTags) {
		this.craftTags = craftTags;
	}

	/**
	 * @param leisureTags the leisureTags to set
	 */
	protected void setLeisureTags(
		Set<LeisureTag> leisureTags) {
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
		Set<PublicTransportTag> publicTransportTags) {
		this.publicTransportTags = publicTransportTags;
	}

	/**
	 * @param gamblingTags the gamblingTags to set
	 */
	protected void setGamblingTags(
		Set<GamblingTag> gamblingTags) {
		this.gamblingTags = gamblingTags;
	}

	public void setAmenityTags(
		Set<AmenityTag> amenityTags) {
		this.amenityTags = amenityTags;
	}

	public Set<AmenityTag> getAmenityTags() {
		return amenityTags;
	}

	public void setLanduseTags(Set<LanduseTag> landuseTags) {
		this.landuseTags = landuseTags;
	}

	public Set<LanduseTag> getLanduseTags() {
		return landuseTags;
	}
}
