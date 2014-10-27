/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.entity.osm;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.entity.BasePolygon;
import de.pgalise.simulation.shared.entity.BaseBoundary;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.entity.Building;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class OSMBuilding extends Building {
  private static final long serialVersionUID = 1L;
  private String osmId;

  public OSMBuilding() {
    super();
  }

  public OSMBuilding(Long id,String osmId) {
    super(id);
    this.osmId = osmId;
  }

  public OSMBuilding(String osmId,
    Long id,
    BaseBoundary geoLocation) {
    super(id,
      geoLocation);
    this.osmId = osmId;
  }

  public OSMBuilding(String osmId,
    Long id,
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
      geoInfo,
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
    this.osmId = osmId;
  }

  public void setOsmId(String osmId) {
    this.osmId = osmId;
  }

  public String getOsmId() {
    return osmId;
  }
}
