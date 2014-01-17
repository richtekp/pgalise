/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.model.vehicle;

import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.entity.VehicleData;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MotorizedVehicleData extends VehicleData {

  private static final long serialVersionUID = 1L;

  public MotorizedVehicleData(Long id,
    int length,
    int wheelbase1,
    int wheelbase2,
    int axleCount,
    VehicleTypeEnum type,
    GpsSensor gpsSensor) {
    super(id,
      length,
      wheelbase1,
      wheelbase2,
      axleCount,
      type,
      gpsSensor);
  }

}
