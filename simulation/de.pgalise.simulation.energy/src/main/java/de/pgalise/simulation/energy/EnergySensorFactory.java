/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.energy;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.staticsensor.SensorFactory;
import de.pgalise.staticsensor.internal.sensor.energy.PhotovoltaikSensor;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author richter
 */
public interface EnergySensorFactory extends SensorFactory {

  public PhotovoltaikSensor createPhotovoltaikSensor(BaseCoordinate position,
    List<SensorInterfererType> sensorInterfererTypes,
    int area,
    Output output) throws InterruptedException, ExecutionException;

  public PhotovoltaikSensor createPhotovoltaikSensor(
    List<SensorInterfererType> sensorInterfererTypes,
    int area,
    Output output) throws InterruptedException, ExecutionException;
}
