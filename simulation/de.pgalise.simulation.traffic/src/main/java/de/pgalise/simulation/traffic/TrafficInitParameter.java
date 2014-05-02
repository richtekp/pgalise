/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.SimulationInitParameter;
import de.pgalise.simulation.shared.controller.TrafficFuzzyData;
import de.pgalise.simulation.traffic.entity.TrafficCity;
import de.pgalise.simulation.weather.service.WeatherInitParameter;
import java.net.URL;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class TrafficInitParameter extends WeatherInitParameter<TrafficCity> {

  private static final long serialVersionUID = 1L;
  private int TrafficServerCount = 2;

  /**
   * Traffic fuzzy data
   */
  //@ManagedProperty(value = "#{trafficFuzzyData}")
  private TrafficFuzzyData trafficFuzzyData = new TrafficFuzzyData();

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
    super(city);
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
    Output output) {
    super(city,
      startTimestamp,
      endTimestamp,
      interval,
      clockGeneratorInterval,
      operationCenterURL,
      controlCenterURL,
      city.getBoundary().retrieveBoundary().getEnvelopeInternal(),
      output);
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
    int trafficServerCount,
    Output output) {
    this(city,
      startTimestamp,
      endTimestamp,
      interval,
      clockGeneratorInterval,
      operationCenterURL,
      controlCenterURL,
      trafficFuzzyData,
      output);
    this.TrafficServerCount = trafficServerCount;
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
