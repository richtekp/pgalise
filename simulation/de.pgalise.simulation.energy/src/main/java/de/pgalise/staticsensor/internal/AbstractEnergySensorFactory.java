package de.pgalise.staticsensor.internal;

/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */


import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.energy.EnergyControllerLocal;
import de.pgalise.simulation.energy.EnergySensorFactory;
import de.pgalise.simulation.energy.sensor.EnergyInterferer;
import de.pgalise.simulation.energy.sensor.EnergySensorTypeEnum;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.exception.NoValidControllerForSensorException;
import de.pgalise.simulation.shared.sensor.SensorInterferer;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.staticsensor.AbstractSensorFactory;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherInterferer;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherSensorTypeEnum;
import de.pgalise.simulation.traffic.TrafficSensorTypeEnum;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InductionLoopSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.TopoRadarSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.inductionloop.CompositeInductionLoopInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.inductionloop.InductionLoopNoInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.inductionloop.InductionLoopWhiteNoiseInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.infrared.CompositeInfraredInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.infrared.InfraredNoInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.infrared.InfraredWhiteNoiseInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.toporadar.CompositeTopoRadarInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.toporadar.TopoRadarNoInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.toporadar.TopoRadarWhiteNoiseInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.CompositeGpsInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.GpsAtmosphereInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.GpsClockInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.GpsInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.GpsNoInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.GpsReceiverInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.GpsWhiteNoiseInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.InductionLoopInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.InfraredInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.TopoRadarInterferer;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.simulation.weather.service.WeatherControllerLocal;
import de.pgalise.staticsensor.internal.sensor.energy.PhotovoltaikSensor;
import de.pgalise.staticsensor.internal.sensor.energy.SmartMeterSensor;
import de.pgalise.staticsensor.internal.sensor.energy.WindPowerSensor;
import de.pgalise.staticsensor.internal.sensor.energy.interferer.CompositeEnergyInterferer;
import de.pgalise.staticsensor.internal.sensor.energy.interferer.EnergyNoInterferer;
import de.pgalise.staticsensor.internal.sensor.energy.interferer.PhotovoltaikWhiteNoiseInterferer;
import de.pgalise.staticsensor.internal.sensor.energy.interferer.SmartMeterWhiteNoiseInterferer;
import de.pgalise.staticsensor.internal.sensor.energy.interferer.WindPowerWhiteNoiseInterferer;
import de.pgalise.staticsensor.internal.sensor.weather.Anemometer;
import de.pgalise.staticsensor.internal.sensor.weather.Barometer;
import de.pgalise.staticsensor.internal.sensor.weather.Hygrometer;
import de.pgalise.staticsensor.internal.sensor.weather.Luxmeter;
import de.pgalise.staticsensor.internal.sensor.weather.Pyranometer;
import de.pgalise.staticsensor.internal.sensor.weather.RainSensor;
import de.pgalise.staticsensor.internal.sensor.weather.Thermometer;
import de.pgalise.staticsensor.internal.sensor.weather.WindFlagSensor;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.AnemometerWhiteNoiseInterferer;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.BarometerWhiteNoiseInterferer;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.CompositeWeatherInterferer;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.HygrometerWhiteNoiseInterferer;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.LuxmeterWhiteNoiseInterferer;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.PyranometerWhiteNoiseInterferer;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.RainsensorWhiteNoiseInterferer;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.ThermometerWhiteNoiseInterferer;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.WeatherNoInterferer;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.WindFlagWhiteNoiseInterferer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.ejb.EJB;

/**
 * Default implementation of the SensorFactory.
 *
 * @author Marcus
 * @version 1.0 (Apr 5, 2013)
 */
public class AbstractEnergySensorFactory extends AbstractSensorFactory<Sensor<?, ?>>
  implements EnergySensorFactory {

  @EJB
  private WeatherControllerLocal weatherController;
  @EJB
  private EnergyControllerLocal energyController;
  @EJB
  private GpsClockInterferer gpsClockInterferer;
  @EJB
  private GpsAtmosphereInterferer gpsAtmosphereInterferer;
  @EJB
  private GpsReceiverInterferer gpsReceiverInterferer;
  @EJB
  private GpsWhiteNoiseInterferer gpsWhiteNoiseInterferer;
  @EJB
  private GpsNoInterferer gpsNoInterferer;

  public AbstractEnergySensorFactory() {
    super();
  }

  /**
   * Constructor
   *
   * @param rss Random seed service
   * @param idGenerator
   * @param wctrl Weather controller
   * @param ectrl Energy controller
   * @param sensorOutput Sensor output
   * @param updateLimit
   */
  public AbstractEnergySensorFactory(RandomSeedService rss,
    IdGenerator idGenerator,
    WeatherControllerLocal wctrl,
    EnergyControllerLocal ectrl,
    int updateLimit) {
    super(
      updateLimit);
    this.energyController = ectrl;
    this.weatherController = wctrl;
  }

  public void setWeatherController(WeatherControllerLocal weatherController) {
    this.weatherController = weatherController;
  }

  public void setEnergyController(EnergyControllerLocal energyController) {
    this.energyController = energyController;
  }

  public EnergyController getEnergyController() {
    return energyController;
  }

  public WeatherController getWeatherController() {
    return weatherController;
  }

  public final static int PHOTOVOLTAIK_AREA_MIN = 5;
  public final static int PHOTOVOLTAIK_AREA_MAX = 500;
  public final static int WIND_POWER_ROTOR_LENGTH_MIN = 15;
  public final static int WIND_POWER_ROTOR_LENGTH_MAX = 40;
  public final static int ACTIVITY_VALUE_MIN = 15;
  public final static int ACTIVITY_VALUE_MAX = 40;
  public final static int SMART_METER_MEASURE_RADIUS_MIN = 1;
  public final static int SMART_METER_MEASURE_RADIUS_MAX = 10;

  private int middle(int a,
    int b) {
    return (int) (a + (b - a) * Math.random());
  }

  @Override
  public PhotovoltaikSensor createPhotovoltaikSensor(BaseCoordinate position,
    List<SensorInterfererType> sensorInterfererTypes,
    int area,
    Output output) throws InterruptedException, ExecutionException {
    return new PhotovoltaikSensor(this.getIdGenerator().getNextId(),
      output,
      position,
      this.getWeatherController(),
      this.getEnergyController(),
      this.getRandomSeedService(),
      area,
      getUpdateLimit(),
      this.createEnergyInterferer(sensorInterfererTypes));
  }

  @Override
  public PhotovoltaikSensor createPhotovoltaikSensor(
    List<SensorInterfererType> sensorInterfererTypes,
    int area,
    Output output) throws InterruptedException, ExecutionException {
    BaseCoordinate randomPosition = null;
    return createPhotovoltaikSensor(randomPosition,
      sensorInterfererTypes,
      area,
      output);
  }

  @Override
  public Sensor<?, ?> createSensor(SensorType sensorType,
    List<SensorInterfererType> sensorInterfererTypes,
    Output output)
    throws InterruptedException, ExecutionException {
    if (sensorType.equals(EnergySensorTypeEnum.PHOTOVOLTAIK)) {
      BaseCoordinate position = createRandomPositionEnergySensor();
      return new PhotovoltaikSensor(this.getIdGenerator().getNextId(),
        output,
        position,
        this.getWeatherController(),
        this.getEnergyController(),
        this.getRandomSeedService(),
        middle(PHOTOVOLTAIK_AREA_MIN,
          PHOTOVOLTAIK_AREA_MAX),
        getUpdateLimit(),
        this.createEnergyInterferer(sensorInterfererTypes));

    } else if (sensorType.equals(
      EnergySensorTypeEnum.WINDPOWERSENSOR)) {
      BaseCoordinate position = createRandomPositionEnergySensor();
      return new WindPowerSensor(this.getIdGenerator().getNextId(),
        output,
        position,
        this.getWeatherController(),
        this.getEnergyController(),
        this.getRandomSeedService(),
        middle(WIND_POWER_ROTOR_LENGTH_MIN,
          WIND_POWER_ROTOR_LENGTH_MAX),
        middle(ACTIVITY_VALUE_MIN,
          ACTIVITY_VALUE_MAX),
        getUpdateLimit(),
        this.createEnergyInterferer(sensorInterfererTypes));

    } else if (sensorType.equals(WeatherSensorTypeEnum.THERMOMETER)) {
      BaseCoordinate position = createRandomPositionWeatherSensor();
      return new Thermometer(getIdGenerator().getNextId(),
        output,
        position,
        this.getWeatherController(),
        getUpdateLimit(),
        this.createWeatherInterferer(sensorInterfererTypes));
    } else if (sensorType.equals(WeatherSensorTypeEnum.WINDFLAG)) {
      BaseCoordinate position = createRandomPositionWeatherSensor();
      return new WindFlagSensor(getIdGenerator().getNextId(),
        output,
        position,
        this.getWeatherController(),
        getUpdateLimit(),
        this.createWeatherInterferer(sensorInterfererTypes));
    } else if (sensorType.equals(WeatherSensorTypeEnum.BAROMETER)) {
      BaseCoordinate position = createRandomPositionWeatherSensor();
      return new Barometer(getIdGenerator().getNextId(),
        output,
        position,
        this.getWeatherController(),
        getUpdateLimit(),
        this.createWeatherInterferer(sensorInterfererTypes));
    } else if (sensorType.equals(WeatherSensorTypeEnum.HYGROMETER)) {
      BaseCoordinate position = createRandomPositionWeatherSensor();
      return new Hygrometer(getIdGenerator().getNextId(),
        output,
        position,
        this.getWeatherController(),
        getUpdateLimit(),
        this.createWeatherInterferer(sensorInterfererTypes));
    } else if (sensorType.equals(WeatherSensorTypeEnum.PYRANOMETER)) {
      BaseCoordinate position = createRandomPositionWeatherSensor();
      return new Pyranometer(getIdGenerator().getNextId(),
        output,
        position,
        this.getWeatherController(),
        getUpdateLimit(),
        this.createWeatherInterferer(sensorInterfererTypes));
    } else if (sensorType.equals(WeatherSensorTypeEnum.RAIN)) {
      BaseCoordinate position = createRandomPositionWeatherSensor();
      return new RainSensor(getIdGenerator().getNextId(),
        output,
        position,
        this.getWeatherController(),
        getUpdateLimit(),
        this.createWeatherInterferer(sensorInterfererTypes));
    } else if (sensorType.equals(WeatherSensorTypeEnum.ANEMOMETER)) {
      BaseCoordinate position = createRandomPositionWeatherSensor();
      return new Anemometer(getIdGenerator().getNextId(),
        output,
        position,
        this.getWeatherController(),
        getUpdateLimit(),
        this.createWeatherInterferer(sensorInterfererTypes));
    } else if (sensorType.equals(WeatherSensorTypeEnum.LUXMETER)) {
      BaseCoordinate position = createRandomPositionWeatherSensor();
      return new Luxmeter(getIdGenerator().getNextId(),
        output,
        position,
        this.getWeatherController(),
        getUpdateLimit(),
        this.createWeatherInterferer(sensorInterfererTypes));
    } else if (sensorType.equals(EnergySensorTypeEnum.SMARTMETER)) {
      BaseCoordinate position = createRandomPositionWeatherSensor();
      return new SmartMeterSensor(this.getIdGenerator().getNextId(),
        output,
        position,
        this.getWeatherController(),
        this.getEnergyController(),
        this.getRandomSeedService(),
        middle(SMART_METER_MEASURE_RADIUS_MIN,
          SMART_METER_MEASURE_RADIUS_MAX),
        getUpdateLimit(),
        this.createEnergyInterferer(sensorInterfererTypes));

    } else if (sensorType.equals(
      TrafficSensorTypeEnum.TRAFFIC_LIGHT_INTERSECTION)) {
      // Can't be returned here, because of missing dependencies
      return null;
    } else if (sensorType.equals(TrafficSensorTypeEnum.INFRARED)) { // Infrared
      InfraredInterferer infraredInterferer;
      if (sensorInterfererTypes != null && !sensorInterfererTypes.isEmpty()) {
        List<InfraredInterferer> infraredInterferers = new ArrayList<>();
        infraredInterferer = new CompositeInfraredInterferer(infraredInterferers);

        for (SensorInterfererType sensorInterfererType : sensorInterfererTypes) {
          switch (sensorInterfererType) {
            case INFRARED_WHITE_NOISE_INTERFERER:
              infraredInterferers.add(new InfraredWhiteNoiseInterferer(this.
                getRandomSeedService()));
              break;
            default:
              break;
          }
        }
      } else {
        infraredInterferer = new InfraredNoInterferer();
      }

      BaseCoordinate position = createRandomPositionInfraredSensor();
      return new InfraredSensor(getIdGenerator().getNextId(),
        output,
        null,
        position,
        getUpdateLimit(),
        infraredInterferer);

    } else if (sensorType.equals(TrafficSensorTypeEnum.INDUCTIONLOOP)) { // Inductionloop

      InductionLoopInterferer inductionLoopInterferer;
      if (sensorInterfererTypes != null && !sensorInterfererTypes.isEmpty()) {
        List<InductionLoopInterferer> inductionLoopInterfers = new ArrayList<>();
        inductionLoopInterferer = new CompositeInductionLoopInterferer(
          inductionLoopInterfers);
        for (SensorInterfererType sensorInterfererType : sensorInterfererTypes) {
          switch (sensorInterfererType) {
            case INDUCTION_LOOP_WHITE_NOISE_INTERFERER:
              inductionLoopInterfers.add(new InductionLoopWhiteNoiseInterferer(
                this.getRandomSeedService()));
              break;
            default:
              break;
          }
        }
      } else {
        inductionLoopInterferer = new InductionLoopNoInterferer();
      }

      BaseCoordinate position = createRandomPositionInductionLoopSensor();
      return new InductionLoopSensor(getIdGenerator().getNextId(),
        output,
        null,
        getUpdateLimit(),
        inductionLoopInterferer);

    } else if (sensorType.equals(TrafficSensorTypeEnum.TOPORADAR)) { // Toporadar

      TopoRadarInterferer toporadarInterferer;
      if (sensorInterfererTypes != null && !sensorInterfererTypes.isEmpty()) {
        List<TopoRadarInterferer> toporadarInterfers = new ArrayList<>();
        toporadarInterferer = new CompositeTopoRadarInterferer(
          toporadarInterfers);

        for (SensorInterfererType sensorInterfererType : sensorInterfererTypes) {
          switch (sensorInterfererType) {
            case TOPO_RADAR_WHITE_NOISE_INTERFERER:
              toporadarInterfers.add(new TopoRadarWhiteNoiseInterferer(this.
                getRandomSeedService()));
              break;
            default:
              break;
          }
        }
      } else {
        toporadarInterferer = new TopoRadarNoInterferer();
      }

      BaseCoordinate position = createRandomPositionTopoRadarSensor();
      return new TopoRadarSensor(getIdGenerator().getNextId(),
        output,
        null,
        getUpdateLimit(),
        toporadarInterferer);

    } else if (sensorType.equals(TrafficSensorTypeEnum.GPS)) {

      GpsInterferer gpsInterferer;
      if (sensorInterfererTypes != null && !sensorInterfererTypes.isEmpty()) {

        List<GpsInterferer> gpsInterferes = new ArrayList<>();
        gpsInterferer = new CompositeGpsInterferer(gpsInterferes);

        for (SensorInterfererType sensorInterfererType : sensorInterfererTypes) {
          switch (sensorInterfererType) {
            case GPS_ATMOSPHERE_INTERFERER:
              gpsInterferes.add(gpsAtmosphereInterferer);
              break;
            case GPS_CLOCK_INTERFERER:
              gpsInterferes.add(gpsClockInterferer);
              break;
            case GPS_RECEIVER_INTERFERER:
              gpsInterferes.add(gpsReceiverInterferer);
              break;
            case GPS_WHITE_NOISE_INTERFERER:
              gpsInterferes.add(gpsWhiteNoiseInterferer);
              break;
            default:
              break;
          }
        }

      } else {
        gpsInterferer = gpsNoInterferer;
      }

      return new GpsSensor(getIdGenerator().getNextId(),
        output,
        null,
        getUpdateLimit(),
        gpsInterferer);
    } else {
      throw new NoValidControllerForSensorException(String.format(
        "%s is not a known SensorType!",
        sensorType.toString()));
    }
  }

  private EnergyInterferer createEnergyInterferer(
    List<SensorInterfererType> sensorInterfererTypes) {
    if (sensorInterfererTypes.isEmpty()) {
      return new EnergyNoInterferer();
    }
    if (sensorInterfererTypes.size() == 1) {
      return (EnergyInterferer) this.createSensorInterferer(
        sensorInterfererTypes.get(0));
    }
    CompositeEnergyInterferer compositeEnergyInterferer = new CompositeEnergyInterferer();
    for (SensorInterfererType sensorInterfererType : sensorInterfererTypes) {
      compositeEnergyInterferer.attach((EnergyInterferer) this.
        createSensorInterferer(sensorInterfererType));
    }
    return compositeEnergyInterferer;
  }

  private WeatherInterferer createWeatherInterferer(
    List<SensorInterfererType> sensorInterfererTypes) {
    if (sensorInterfererTypes.isEmpty()) {
      return new WeatherNoInterferer();
    }
    if (sensorInterfererTypes.size() == 1) {
      return (WeatherInterferer) this.createSensorInterferer(
        sensorInterfererTypes.get(0));
    }
    CompositeWeatherInterferer compositeEnergyInterferer = new CompositeWeatherInterferer();
    for (SensorInterfererType sensorInterfererType : sensorInterfererTypes) {
      compositeEnergyInterferer.attach((WeatherInterferer) this.
        createSensorInterferer(sensorInterfererType));
    }
    return compositeEnergyInterferer;
  }

  /**
   * @param sensorInterfererType
   * @return
   */
  /*
   * TODO INDUCTION_LOOP_WHITE_NOISE_INTERFERER und INFRARED_WHITE_NOISE_INTERFERER wieder einabuen -> Interferer sind
   * im Traffic.internal package. Muss Marcus ordentlicher machen
   */
  private SensorInterferer createSensorInterferer(
    SensorInterfererType sensorInterfererType) {
    if (sensorInterfererType.equals(
      SensorInterfererType.ANEMOMETER_WHITE_NOISE_INTERFERER)) {
      return new AnemometerWhiteNoiseInterferer(this.getRandomSeedService());
    } else if (sensorInterfererType.equals(
      SensorInterfererType.BAROMETER_WHITE_NOISE_INTERFERER)) {
      return new BarometerWhiteNoiseInterferer(this.getRandomSeedService());
    } else if (sensorInterfererType.equals(
      SensorInterfererType.HYGROMETER_WHITE_NOISE_INTERFERER)) {
      return new HygrometerWhiteNoiseInterferer(this.getRandomSeedService());
      // case INDUCTION_LOOP_WHITE_NOISE_INTERFERER:
      // return new InductionLoopWhiteNoiseInterferer(this.getRandomSeedService());
      // case INFRARED_WHITE_NOISE_INTERFERER:
      // return new InfraredWhiteNoiseInterferer(this.getRandomSeedService());
    } else if (sensorInterfererType.equals(
      SensorInterfererType.LUXMETER_WHITE_NOISE_INTERFERER)) {
      return new LuxmeterWhiteNoiseInterferer(this.getRandomSeedService());
    } else if (sensorInterfererType.equals(
      SensorInterfererType.PHOTOVOLTAIK_WHITE_NOISE_INTERFERER)) {
      return new PhotovoltaikWhiteNoiseInterferer(this.getRandomSeedService());
    } else if (sensorInterfererType.equals(
      SensorInterfererType.PYRANOMETER_WHITE_NOISE_INTERFERER)) {
      return new PyranometerWhiteNoiseInterferer(this.getRandomSeedService());
    } else if (sensorInterfererType.equals(
      SensorInterfererType.RAINSENSOR_WHITE_NOISE_INTERFERER)) {
      return new RainsensorWhiteNoiseInterferer(this.getRandomSeedService());
    } else if (sensorInterfererType.equals(
      SensorInterfererType.SMART_METER_WHITE_NOISE_INTERFERER)) {
      return new SmartMeterWhiteNoiseInterferer(this.getRandomSeedService());
    } else if (sensorInterfererType.equals(
      SensorInterfererType.THERMOMETER_WHITE_NOISE_INTERFERER)) {
      return new ThermometerWhiteNoiseInterferer(this.getRandomSeedService());
    } else if (sensorInterfererType.equals(
      SensorInterfererType.WIND_FLAG_WHITE_NOISE_INTERFERER)) {
      return new WindFlagWhiteNoiseInterferer(this.getRandomSeedService());
    } else if (sensorInterfererType.equals(
      SensorInterfererType.WIND_POWER_WHITE_NOISE_INTERFERER)) {
      return new WindPowerWhiteNoiseInterferer(this.getRandomSeedService());
    } else {
      throw new RuntimeException();
    }

  }

  @Override
  public BaseCoordinate createEnergySensor(BaseCoordinate position,
    List<SensorInterfererType> sensorInterfererTypes,
    Output output) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
