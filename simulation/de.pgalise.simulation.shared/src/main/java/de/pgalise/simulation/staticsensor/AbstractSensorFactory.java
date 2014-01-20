/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.staticsensor;

import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import javax.ejb.EJB;

/**
 *
 * @param <S>
 * @author richter
 */
public abstract class AbstractSensorFactory<S extends Sensor<?, ?>> implements
  SensorFactory {

  /**
   * Random seed service
   */
  @EJB
  private RandomSeedService randomSeedService;
  @EJB
  private IdGenerator idGenerator;

  private int updateLimit = 1;

  public AbstractSensorFactory() {
  }

  public AbstractSensorFactory(int updateLimit) {
    this.updateLimit = updateLimit;
  }

  public void setIdGenerator(IdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  public IdGenerator getIdGenerator() {
    return idGenerator;
  }

  public void setUpdateLimit(int updateLimit) {
    this.updateLimit = updateLimit;
  }

  public int getUpdateLimit() {
    return updateLimit;
  }

  public void setRandomSeedService(RandomSeedService randomSeedService) {
    this.randomSeedService = randomSeedService;
  }

  public RandomSeedService getRandomSeedService() {
    return randomSeedService;
  }

  public JaxRSCoordinate createRandomPositionEnergySensor() {
    throw new UnsupportedOperationException();
  }

  public JaxRSCoordinate createRandomPositionWeatherSensor() {
    throw new UnsupportedOperationException();
  }

  public JaxRSCoordinate createRandomPositionInfraredSensor() {
    throw new UnsupportedOperationException();
  }

  public JaxRSCoordinate createRandomPositionInductionLoopSensor() {
    throw new UnsupportedOperationException();
  }

  public JaxRSCoordinate createRandomPositionTopoRadarSensor() {
    throw new UnsupportedOperationException();
  }
}
