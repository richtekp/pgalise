/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.service;

import com.vividsolutions.jts.geom.Envelope;
import de.pgalise.simulation.service.SimulationInitParameter;
import de.pgalise.simulation.shared.entity.City;
import java.net.URL;

/**
 *
 * @author richter
 * @param <C>
 */
public class WeatherInitParameter<C extends City> extends SimulationInitParameter {

  private static final long serialVersionUID = 1L;

  private C city;

  public WeatherInitParameter() {
  }

  public WeatherInitParameter(C city) {
    this.city = city;
  }

  public WeatherInitParameter(C city,
    long startTimestamp,
    long endTimestamp,
    long interval,
    long clockGeneratorInterval,
    URL operationCenterURL,
    URL controlCenterURL,
    Envelope cityBoundary) {
    super(startTimestamp,
      endTimestamp,
      interval,
      clockGeneratorInterval,
      operationCenterURL,
      controlCenterURL,
      cityBoundary);
    this.city = city;
  }

  public void setCity(C city) {
    this.city = city;
  }

  public C getCity() {
    return city;
  }
}
