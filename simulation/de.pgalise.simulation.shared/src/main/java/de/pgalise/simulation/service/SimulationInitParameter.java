/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.service;

import com.vividsolutions.jts.geom.Envelope;
import de.pgalise.simulation.sensorFramework.output.Output;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.validation.constraints.NotNull;

/**
 *
 * @author richter
 */
public class SimulationInitParameter extends InitParameter {

  private static final long serialVersionUID = 1L;

  /**
   * Timestamp of the simulation start
   */
  /*
   Has to be a java.util.Date in order to work out-of-the box with Primefaces p:calendar
   */
  @NotNull
  private Date startTimestamp;

  /**
   * Timestamp of the simulation end
   */
  /*
   Has to be a java.util.Date in order to work out-of-the box with Primefaces p:calendar
   */
  @NotNull
  private Date endTimestamp;

  /**
   * Simulation interval
   */
  private long interval = 10;

  /**
   * Clock generator interval
   */
  private long clockGeneratorInterval = 10;

  /**
   * URL to the operation center
   */
  private URL operationCenterURL;

  /**
   * URL to the control center
   */
  private URL controlCenterURL;

  /**
   * City boundary
   */
  private Envelope cityBoundary;

  /**
   * Inializes a SimulationInitParameter with default operationCenterURL,
   * controlCenterURL, current time as startTimestamp and current time + 3600 s
   * as endTimestamp
   */
  public SimulationInitParameter() {
    try {
      this.operationCenterURL = new URL("http://localhost:8080/operationCenter");
      this.controlCenterURL = new URL("http://localhost:8080/controlCenter");
    } catch (MalformedURLException ex) {
      Logger.getLogger(InitParameter.class.getName()).log(Level.SEVERE,
        null,
        ex);
    }
    startTimestamp = new Date();
    endTimestamp = new Date(startTimestamp.getTime() + 1000 * 60 * 60);
  }

  /**
   * Constructor
   *
   * @param startTimestamp Timestamp of the simulation start
   * @param endTimestamp Timestamp of the simulation end
   * @param interval Simulation interval
   * @param clockGeneratorInterval Clock generator interval
   * @param operationCenterURL URL to the operation center
   * @param controlCenterURL URL to the control center
   * @param trafficFuzzyData Traffic fuzzy data
   * @param cityBoundary City boundary
   */
  public SimulationInitParameter(
    long startTimestamp,
    long endTimestamp,
    long interval,
    long clockGeneratorInterval,
    URL operationCenterURL,
    URL controlCenterURL,
    Envelope cityBoundary,
    Output output) {
    super(output);
    this.startTimestamp = new Date(startTimestamp);
    this.endTimestamp = new Date(endTimestamp);
    if (this.startTimestamp.after(this.endTimestamp)) {
      throw new IllegalArgumentException(String.format(
        "endTimestamp %s lies before startTimestamp %s",
        this.endTimestamp.toString(),
        this.startTimestamp.toString()));
    }
    this.interval = interval;
    this.clockGeneratorInterval = clockGeneratorInterval;
    this.operationCenterURL = operationCenterURL;
    this.cityBoundary = cityBoundary;
    this.operationCenterURL = operationCenterURL;
    this.controlCenterURL = controlCenterURL;
  }

  public long getClockGeneratorInterval() {
    return this.clockGeneratorInterval;
  }

  public Date getEndTimestamp() {
    return this.endTimestamp;
  }

  public long getInterval() {
    return this.interval;
  }

  public URL getOperationCenterURL() {
    return this.operationCenterURL;
  }

  public Date getStartTimestamp() {
    return this.startTimestamp;
  }

  public void setClockGeneratorInterval(long clockGeneratorInterval) {
    this.clockGeneratorInterval = clockGeneratorInterval;
  }

  public void setEndTimestamp(Date endTimestamp) {
    this.endTimestamp = endTimestamp;
  }

  public void setInterval(long interval) {
    this.interval = interval;
  }

  public void setOperationCenterURL(URL operationCenterURL) {
    this.operationCenterURL = operationCenterURL;
  }

  public void setStartTimestamp(Date startTimestamp) {
    this.startTimestamp = startTimestamp;
  }

  public Envelope getCityBoundary() {
    return cityBoundary;
  }

  public void setCityBoundary(Envelope cityBoundary) {
    this.cityBoundary = cityBoundary;
  }

  public URL getControlCenterURL() {
    return controlCenterURL;
  }

  public void setControlCenterURL(URL controlCenterURL) {
    this.controlCenterURL = controlCenterURL;
  }
}
