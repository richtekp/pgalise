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
 
package de.pgalise.staticsensor.internal;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorFactory;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.exception.NoValidControllerForSensorException;
import de.pgalise.simulation.sensorFramework.SensorHelper;
import de.pgalise.simulation.energy.SensorHelperPhotovoltaik;
import de.pgalise.simulation.sensorFramework.SensorHelperSmartMeter;
import de.pgalise.simulation.energy.SensorHelperWindPower;
import de.pgalise.simulation.energy.sensor.EnergySensorTypeEnum;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.shared.sensor.SensorInterferer;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.staticsensor.sensor.energy.EnergyInterferer;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InductionLoopSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.InfraredSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.TopoRadarSensor;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.CompositeGpsInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsAtmosphereInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsClockInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsNoInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsReceiverInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.gps.GpsWhiteNoiseInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.inductionloop.CompositeInductionLoopInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.inductionloop.InductionLoopNoInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.inductionloop.InductionLoopWhiteNoiseInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.infrared.CompositeInfraredInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.infrared.InfraredNoInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.infrared.InfraredWhiteNoiseInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.toporadar.CompositeTopoRadarInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.toporadar.TopoRadarNoInterferer;
import de.pgalise.simulation.traffic.internal.server.sensor.interferer.toporadar.TopoRadarWhiteNoiseInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.GpsInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.InductionLoopInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.InfraredInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.TopoRadarInterferer;
import de.pgalise.simulation.weather.service.WeatherController;
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

/**
 * Default implementation of the SensorFactory.
 * 
 * @author Marcus
 * @version 1.0 (Apr 5, 2013)
 */
public class DefaultSensorFactory implements SensorFactory {

	/**
	 * Sensor output
	 */
	private Output sensorOutput;

	/**
	 * Random seed service
	 */
	private RandomSeedService rss;

	/**
	 * Weather controller
	 */
	private WeatherController weatherCtrl;

	/**
	 * Energy controller
	 */
	private EnergyController energyCtrl;

	/**
	 * Constructor
	 * 
	 * @param rss
	 *            Random seed service
	 * @param wctrl
	 *            Weather controller
	 * @param ectrl
	 *            Energy controller
	 * @param sensorOutput
	 *            Sensor output
	 */
	public DefaultSensorFactory(RandomSeedService rss, WeatherController wctrl, EnergyController ectrl,
			Output sensorOutput) {
		this.rss = rss;
		this.weatherCtrl = wctrl;
		this.energyCtrl = ectrl;
		this.sensorOutput = sensorOutput;
	}

	@Override
	public Sensor createSensor(SensorHelper sensorHelper, EnumSet<SensorType> allowedTypes)
			throws InterruptedException, ExecutionException {
		if (!allowedTypes.contains(sensorHelper.getSensorType())) {
			// log.warn("Sensor not created, because the caller is not responsible for this type of sensors");
			return null;
		}
		Coordinate position = new Coordinate(0, 0);
		if (sensorHelper.getPosition() != null) {
			position = sensorHelper.getPosition();
		}
		switch (sensorHelper.getSensorType()) {
			case PHOTOVOLTAIK:
				if (sensorHelper instanceof SensorHelperPhotovoltaik) {
					return new PhotovoltaikSensor(this.sensorOutput, sensorHelper.getSensorID(), position,
							this.weatherCtrl, this.energyCtrl, this.rss,
							((SensorHelperPhotovoltaik) sensorHelper).getArea(), sensorHelper.getUpdateSteps(),
							this.createEnergyInterferer(sensorHelper.getSensorInterfererType()));
				} else {
					throw new RuntimeException("Photovoltaik sensorhelper is not of type SensorHelperPhotovoltaik");
				}

			case WINDPOWERSENSOR:
				if (sensorHelper instanceof SensorHelperWindPower) {
					SensorHelperWindPower sensorHelperWindPower = (SensorHelperWindPower) sensorHelper;
					return new WindPowerSensor(this.sensorOutput, sensorHelper.getSensorID(), position,
							this.weatherCtrl, this.energyCtrl, this.rss, sensorHelperWindPower.getRotorLength(),
							sensorHelperWindPower.getActivityValue(), sensorHelper.getUpdateSteps(),
							this.createEnergyInterferer(sensorHelper.getSensorInterfererType()));
				} else {
					throw new RuntimeException("Windpower sensorhelper is not of type SensorHelperWindPower.");
				}

			case THERMOMETER:
				return new Thermometer(this.sensorOutput, sensorHelper.getSensorID(), position, this.weatherCtrl,
						sensorHelper.getUpdateSteps(), this.createWeatherInterferer(sensorHelper
								.getSensorInterfererType()));
			case WINDFLAG:
				return new WindFlagSensor(this.sensorOutput, sensorHelper.getSensorID(), position, this.weatherCtrl,
						sensorHelper.getUpdateSteps(), this.createWeatherInterferer(sensorHelper
								.getSensorInterfererType()));
			case BAROMETER:
				return new Barometer(this.sensorOutput, sensorHelper.getSensorID(), position, this.weatherCtrl,
						sensorHelper.getUpdateSteps(), this.createWeatherInterferer(sensorHelper
								.getSensorInterfererType()));
			case HYGROMETER:
				return new Hygrometer(this.sensorOutput, sensorHelper.getSensorID(), position, this.weatherCtrl,
						sensorHelper.getUpdateSteps(), this.createWeatherInterferer(sensorHelper
								.getSensorInterfererType()));
			case PYRANOMETER:
				return new Pyranometer(this.sensorOutput, sensorHelper.getSensorID(), position, this.weatherCtrl,
						sensorHelper.getUpdateSteps(), this.createWeatherInterferer(sensorHelper
								.getSensorInterfererType()));
			case RAIN:
				return new RainSensor(this.sensorOutput, sensorHelper.getSensorID(), position, this.weatherCtrl,
						sensorHelper.getUpdateSteps(), this.createWeatherInterferer(sensorHelper
								.getSensorInterfererType()));
			case ANEMOMETER:
				return new Anemometer(this.sensorOutput, sensorHelper.getSensorID(), position, this.weatherCtrl,
						sensorHelper.getUpdateSteps(), this.createWeatherInterferer(sensorHelper
								.getSensorInterfererType()));
			case LUXMETER:
				return new Luxmeter(this.sensorOutput, sensorHelper.getSensorID(), position, this.weatherCtrl,
						sensorHelper.getUpdateSteps(), this.createWeatherInterferer(sensorHelper
								.getSensorInterfererType()));
			case SMARTMETER:
				if (sensorHelper instanceof SensorHelperSmartMeter) {
					return new SmartMeterSensor(this.sensorOutput, sensorHelper.getSensorID(), position,
							this.weatherCtrl, this.energyCtrl, this.rss,
							((SensorHelperSmartMeter) sensorHelper).getMeasureRadiusInMeter(),
							sensorHelper.getUpdateSteps(), this.createEnergyInterferer(sensorHelper
									.getSensorInterfererType()));
				} else {
					throw new RuntimeException("SmartMeter sensorhelper is not of type SmartMeterSensorHelper");
				}
			case TRAFFIC_LIGHT_INTERSECTION:
				// Can't be returned here, because of missing dependencies
				return null;
			case INFRARED: // Infrared
				InfraredInterferer infraredInterferer;
				if (sensorHelper.getSensorInterfererType() != null && !sensorHelper.getSensorInterfererType().isEmpty()) {
					List<InfraredInterferer> infraredInterferers = new ArrayList<>();
					infraredInterferer = new CompositeInfraredInterferer(infraredInterferers);

					for (SensorInterfererType sensorInterfererType : sensorHelper.getSensorInterfererType()) {
						switch (sensorInterfererType) {
							case INFRARED_WHITE_NOISE_INTERFERER:
								infraredInterferers.add(new InfraredWhiteNoiseInterferer(this.rss));
								break;
							default:
								break;
						}
					}
				} else {
					infraredInterferer = new InfraredNoInterferer();
				}

				return new InfraredSensor(this.sensorOutput, sensorHelper.getSensorID(), null,
						sensorHelper.getPosition(), sensorHelper.getUpdateSteps(),
						infraredInterferer);

			case INDUCTIONLOOP: // Inductionloop

				InductionLoopInterferer inductionLoopInterferer;
				if (sensorHelper.getSensorInterfererType() != null && !sensorHelper.getSensorInterfererType().isEmpty()) {
					List<InductionLoopInterferer> inductionLoopInterfers = new ArrayList<>();
					inductionLoopInterferer = new CompositeInductionLoopInterferer(inductionLoopInterfers);
					for (SensorInterfererType sensorInterfererType : sensorHelper.getSensorInterfererType()) {
						switch (sensorInterfererType) {
							case INDUCTION_LOOP_WHITE_NOISE_INTERFERER:
								inductionLoopInterfers.add(new InductionLoopWhiteNoiseInterferer(this.rss));
								break;
							default:
								break;
						}
					}
				} else {
					inductionLoopInterferer = new InductionLoopNoInterferer();
				}

				return new InductionLoopSensor(this.sensorOutput, sensorHelper.getSensorID(),
						sensorHelper.getPosition(), sensorHelper.getUpdateSteps(),
						inductionLoopInterferer);

			case TOPORADAR: // Toporadar

				TopoRadarInterferer toporadarInterferer;
				if (sensorHelper.getSensorInterfererType() != null && !sensorHelper.getSensorInterfererType().isEmpty()) {
					List<TopoRadarInterferer> toporadarInterfers = new ArrayList<>();
					toporadarInterferer = new CompositeTopoRadarInterferer(toporadarInterfers);

					for (SensorInterfererType sensorInterfererType : sensorHelper.getSensorInterfererType()) {
						switch (sensorInterfererType) {
							case TOPO_RADAR_WHITE_NOISE_INTERFERER:
								toporadarInterfers.add(new TopoRadarWhiteNoiseInterferer(this.rss));
								break;
							default:
								break;
						}
					}
				} else {
					toporadarInterferer = new TopoRadarNoInterferer();
				}

				return new TopoRadarSensor(this.sensorOutput, sensorHelper.getSensorID(),
						sensorHelper.getPosition(), sensorHelper.getUpdateSteps(),
						toporadarInterferer);

			case GPS_BIKE: // GPS
			case GPS_BUS:
			case GPS_CAR:
			case GPS_TRUCK:
			case GPS_MOTORCYCLE:

				GpsInterferer gpsInterferer;
				if (sensorHelper.getSensorInterfererType() != null && !sensorHelper.getSensorInterfererType().isEmpty()) {

					List<GpsInterferer> gpsInterferes = new ArrayList<>();
					gpsInterferer = new CompositeGpsInterferer(gpsInterferes);

					for (SensorInterfererType sensorInterfererType : sensorHelper.getSensorInterfererType()) {
						switch (sensorInterfererType) {
							case GPS_ATMOSPHERE_INTERFERER:
								gpsInterferes.add(new GpsAtmosphereInterferer(this.rss, this.weatherCtrl));
								break;
							case GPS_CLOCK_INTERFERER:
								gpsInterferes.add(new GpsClockInterferer(this.rss));
								break;
							case GPS_RECEIVER_INTERFERER:
								gpsInterferes.add(new GpsReceiverInterferer(this.rss));
								break;
							case GPS_WHITE_NOISE_INTERFERER:
								gpsInterferes.add(new GpsWhiteNoiseInterferer(this.rss));
								break;
							default:
								break;
						}
					}

				} else {
					gpsInterferer = new GpsNoInterferer();
				}

				return new GpsSensor(this.sensorOutput, sensorHelper.getSensorID(), null,
						sensorHelper.getUpdateSteps(), sensorHelper.getSensorType(), new Coordinate(0, 0), gpsInterferer);
			default:
				throw new NoValidControllerForSensorException(String.format("%s is not a known SensorType!",
						sensorHelper.getSensorType().toString()));
		}
	}

	private EnergyInterferer createEnergyInterferer(List<SensorInterfererType> sensorInterfererTypes) {
		if (sensorInterfererTypes.isEmpty()) {
			return new EnergyNoInterferer();
		}
		if (sensorInterfererTypes.size() == 1) {
			return (EnergyInterferer) this.createSensorInterferer(sensorInterfererTypes.get(0));
		}
		CompositeEnergyInterferer compositeEnergyInterferer = new CompositeEnergyInterferer();
		for (SensorInterfererType sensorInterfererType : sensorInterfererTypes) {
			compositeEnergyInterferer.attach((EnergyInterferer) this.createSensorInterferer(sensorInterfererType));
		}
		return compositeEnergyInterferer;
	}

	private WeatherInterferer createWeatherInterferer(List<SensorInterfererType> sensorInterfererTypes) {
		if (sensorInterfererTypes.isEmpty()) {
			return new WeatherNoInterferer();
		}
		if (sensorInterfererTypes.size() == 1) {
			return (WeatherInterferer) this.createSensorInterferer(sensorInterfererTypes.get(0));
		}
		CompositeWeatherInterferer compositeEnergyInterferer = new CompositeWeatherInterferer();
		for (SensorInterfererType sensorInterfererType : sensorInterfererTypes) {
			compositeEnergyInterferer.attach((WeatherInterferer) this.createSensorInterferer(sensorInterfererType));
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
	private SensorInterferer createSensorInterferer(SensorInterfererType sensorInterfererType) {
		switch (sensorInterfererType) {
			case ANEMOMETER_WHITE_NOISE_INTERFERER:
				return new AnemometerWhiteNoiseInterferer(this.rss);
			case BAROMETER_WHITE_NOISE_INTERFERER:
				return new BarometerWhiteNoiseInterferer(this.rss);
			case HYGROMETER_WHITE_NOISE_INTERFERER:
				return new HygrometerWhiteNoiseInterferer(this.rss);
				// case INDUCTION_LOOP_WHITE_NOISE_INTERFERER:
				// return new InductionLoopWhiteNoiseInterferer(this.rss);
				// case INFRARED_WHITE_NOISE_INTERFERER:
				// return new InfraredWhiteNoiseInterferer(this.rss);
			case LUXMETER_WHITE_NOISE_INTERFERER:
				return new LuxmeterWhiteNoiseInterferer(this.rss);
			case PHOTOVOLTAIK_WHITE_NOISE_INTERFERER:
				return new PhotovoltaikWhiteNoiseInterferer(this.rss);
			case PYRANOMETER_WHITE_NOISE_INTERFERER:
				return new PyranometerWhiteNoiseInterferer(this.rss);
			case RAINSENSOR_WHITE_NOISE_INTERFERER:
				return new RainsensorWhiteNoiseInterferer(this.rss);
			case SMART_METER_WHITE_NOISE_INTERFERER:
				return new SmartMeterWhiteNoiseInterferer(this.rss);
			case THERMOMETER_WHITE_NOISE_INTERFERER:
				return new ThermometerWhiteNoiseInterferer(this.rss);
			case WIND_FLAG_WHITE_NOISE_INTERFERER:
				return new WindFlagWhiteNoiseInterferer(this.rss);
			case WIND_POWER_WHITE_NOISE_INTERFERER:
				return new WindPowerWhiteNoiseInterferer(this.rss);
			default:
				break;
		}
		throw new RuntimeException();
	}

	@Override
	public Output getOutput() {
		return this.sensorOutput;
	}
}
