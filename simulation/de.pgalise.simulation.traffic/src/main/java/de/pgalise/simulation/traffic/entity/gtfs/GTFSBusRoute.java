/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.entity.gtfs;

import de.pgalise.simulation.traffic.entity.BusRoute;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * An extension of {@link BusRoute} which can be used to preserve GTFS related 
 * information which are not relevant for the application.
 * 
 * Some properties are already available in <tt>BusRoute</tt>.
 * @author richter
 */
@Entity
public class GTFSBusRoute extends BusRoute {
  private static final long serialVersionUID = 1L;
  
  private String routeId;
	@ManyToOne
	private GTFSBusAgency agency;

	@Column(name = "ROUTE_URL")
	private String routeUrl;

	@Column(name = "ROUTE_COLOR")
	private String routeColor;

	@Column(name = "ROUTE_TEXT_COLOR")
	private String routeTextColor;

  public GTFSBusRoute() {
  }
  
  public GTFSBusRoute(Long id) {
    super(id);
  }

  public GTFSBusRoute(
    Long id,
    String routeId,
    GTFSBusAgency agency,
    String routeUrl,
    String routeColor,
    String routeTextColor,
    String routeShortName,
    String routeLongName,
    boolean used) {
    super(id,
      routeShortName,
      routeLongName,
      used);
    this.routeId = routeId;
    this.agency = agency;
    this.routeUrl = routeUrl;
    this.routeColor = routeColor;
    this.routeTextColor = routeTextColor;
  }

  /**
   * @return the agency
   */
  public GTFSBusAgency getAgency() {
    return agency;
  }

  /**
   * @param agency the agency to set
   */
  public void setAgency(GTFSBusAgency agency) {
    this.agency = agency;
  }

  /**
   * @return the routeUrl
   */
  public String getRouteUrl() {
    return routeUrl;
  }

  /**
   * @param routeUrl the routeUrl to set
   */
  public void setRouteUrl(String routeUrl) {
    this.routeUrl = routeUrl;
  }

  /**
   * @return the routeColor
   */
  public String getRouteColor() {
    return routeColor;
  }

  /**
   * @param routeColor the routeColor to set
   */
  public void setRouteColor(String routeColor) {
    this.routeColor = routeColor;
  }

  /**
   * @return the routeTextColor
   */
  public String getRouteTextColor() {
    return routeTextColor;
  }

  /**
   * @param routeTextColor the routeTextColor to set
   */
  public void setRouteTextColor(String routeTextColor) {
    this.routeTextColor = routeTextColor;
  }

  public void setRouteId(String routeId) {
    this.routeId = routeId;
  }

  public String getRouteId() {
    return routeId;
  }
}
