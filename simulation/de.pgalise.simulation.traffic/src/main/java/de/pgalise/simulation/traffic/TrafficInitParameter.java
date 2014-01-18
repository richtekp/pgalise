/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.service.SimulationInitParameter;
import de.pgalise.simulation.shared.controller.TrafficFuzzyData;
import de.pgalise.simulation.traffic.entity.TrafficCity;
import java.net.URL;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class TrafficInitParameter extends SimulationInitParameter {

  private static final long serialVersionUID = 1L;
  private TrafficCity city;
  private int TrafficServerCount = 2;

  /**
   * Traffic fuzzy data
   */
  private TrafficFuzzyData trafficFuzzyData;

  public TrafficInitParameter() {
    super();
  }

  /**
   * Initializes a TrafficInitParamter with default values of {@link SimulationInitParameter#SimulationInitParameter()
   * }
   *
   * @param city
   * @param trafficFuzzyData
   */
  public TrafficInitParameter(TrafficCity city,
    TrafficFuzzyData trafficFuzzyData) {
    super();
    this.city = city;
    this.trafficFuzzyData = trafficFuzzyData;
  }

  public TrafficInitParameter(
    TrafficCity city,
    long startTimestamp,
    long endTimestamp,
    long interval,
    long clockGeneratorInterval,
    URL operationCenterURL,
    URL controlCenterURL,
    TrafficFuzzyData trafficFuzzyData) {
    super(
      startTimestamp,
      endTimestamp,
      interval,
      clockGeneratorInterval,
      operationCenterURL,
      controlCenterURL,
      city.getPosition().getBoundaries().getEnvelopeInternal());
    this.city = city;
    this.trafficFuzzyData = trafficFuzzyData;
  }

  public TrafficInitParameter(
    TrafficCity city,
    long startTimestamp,
    long endTimestamp,
    long interval,
    long clockGeneratorInterval,
    URL operationCenterURL,
    URL controlCenterURL,
    TrafficFuzzyData trafficFuzzyData,
    int trafficServerCount) {
    this(city,
      startTimestamp,
      endTimestamp,
      interval,
      clockGeneratorInterval,
      operationCenterURL,
      controlCenterURL,
      trafficFuzzyData);
    this.TrafficServerCount = trafficServerCount;
  }

  public void setCity(
    TrafficCity city) {
    this.city = city;
  }

  public TrafficCity getCity() {
    return city;
  }

  public void setTrafficServerCount(int TrafficServerCount) {
    this.TrafficServerCount = TrafficServerCount;
  }

  public int getTrafficServerCount() {
    return TrafficServerCount;
  }

  public void setTrafficFuzzyData(TrafficFuzzyData trafficFuzzyData) {
    this.trafficFuzzyData = trafficFuzzyData;
  }

  public TrafficFuzzyData getTrafficFuzzyData() {
    return trafficFuzzyData;
  }

}
