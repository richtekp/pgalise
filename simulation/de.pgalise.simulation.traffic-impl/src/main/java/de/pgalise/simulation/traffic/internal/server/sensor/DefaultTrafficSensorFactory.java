/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.server.sensor;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.energy.EnergyController;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.staticsensor.AbstractSensorFactory;
import de.pgalise.simulation.traffic.TrafficSensorFactory;
import de.pgalise.simulation.traffic.TrafficSensorTypeEnum;
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
import de.pgalise.staticsensor.internal.AbstractEnergySensorFactory;
import de.pgalise.staticsensor.internal.sensor.energy.PhotovoltaikSensor;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author richter
 */
public class DefaultTrafficSensorFactory extends AbstractEnergySensorFactory
	implements TrafficSensorFactory {

	public DefaultTrafficSensorFactory() {
		super();
	}
	
	public DefaultTrafficSensorFactory(RandomSeedService rss,
		WeatherController wctrl,
		EnergyController ectrl,
		Output sensorOutput,
		int updateLimit) {
		super(rss,
			wctrl,
			ectrl,
			sensorOutput,
			updateLimit);
	}

	@Override
	public InductionLoopSensor createInductionLoopSensor(
		List<SensorInterfererType> sensorInterfererTypes) {
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

		Coordinate position = createRandomPositionInductionLoopSensor();
		return new InductionLoopSensor(
			getSensorOutput(),
			null,
			getUpdateLimit(),
			inductionLoopInterferer
		);
	}

	@Override
	public TopoRadarSensor createTopoRadarSensor(
		List<SensorInterfererType> sensorInterfererTypes) {
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

		Coordinate position = createRandomPositionTopoRadarSensor();
		return new TopoRadarSensor(
			getSensorOutput(),
			null,
			getUpdateLimit(),
			toporadarInterferer);
	}

	@Override
	public InfraredSensor createInfraredSensor(
		List<SensorInterfererType> sensorInterfererTypes) {
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

		Coordinate position = createRandomPositionInfraredSensor();
		return new InfraredSensor(getSensorOutput(),
			null,
			position,
			getUpdateLimit(),
			infraredInterferer);
	}
	
	public final static List<SensorInterfererType> DEFAULT_SENSOR_INTERFERER = new LinkedList<>();
	
	@Override
	public GpsSensor createGpsSensor(boolean withSensorInterferer) {
		return createGpsSensor(withSensorInterferer ? DEFAULT_SENSOR_INTERFERER : new ArrayList<SensorInterfererType>(0));
	}

	@Override
	public GpsSensor createGpsSensor(
		List<SensorInterfererType> sensorInterfererTypes) {
		GpsInterferer gpsInterferer;
		if (sensorInterfererTypes != null && !sensorInterfererTypes.isEmpty()) {

			List<GpsInterferer> gpsInterferes = new ArrayList<>();
			gpsInterferer = new CompositeGpsInterferer(gpsInterferes);

			for (SensorInterfererType sensorInterfererType : sensorInterfererTypes) {
				switch (sensorInterfererType) {
					case GPS_ATMOSPHERE_INTERFERER:
						gpsInterferes.add(new GpsAtmosphereInterferer(this.
							getRandomSeedService(),
							this.getWeatherController()));
						break;
					case GPS_CLOCK_INTERFERER:
						gpsInterferes.add(new GpsClockInterferer(this.
							getRandomSeedService()));
						break;
					case GPS_RECEIVER_INTERFERER:
						gpsInterferes.add(new GpsReceiverInterferer(this.
							getRandomSeedService()));
						break;
					case GPS_WHITE_NOISE_INTERFERER:
						gpsInterferes.add(new GpsWhiteNoiseInterferer(this.
							getRandomSeedService()));
						break;
					default:
						break;
				}
			}

		} else {
			gpsInterferer = new GpsNoInterferer();
		}

		return new GpsSensor(getSensorOutput(),
			null,
			getUpdateLimit(),
			gpsInterferer);
	}

	@Override
	public Sensor<?,?> createSensor(SensorType sensorType,
		List<SensorInterfererType> sensorInterfererTypes)
		throws InterruptedException, ExecutionException {
		if (sensorType.equals(TrafficSensorTypeEnum.TRAFFIC_LIGHT_INTERSECTION)) {
			throw new UnsupportedOperationException(
				"there's no traffic light intersection sensor yet");
		} else if (sensorType.equals(TrafficSensorTypeEnum.INFRARED)) { // Infrared

		} else if (sensorType.equals(TrafficSensorTypeEnum.INDUCTIONLOOP)) { // Inductionloop

		} else if (sensorType.equals(TrafficSensorTypeEnum.TOPORADAR)) { // Toporadar

		} else if (sensorType.
			equals(TrafficSensorTypeEnum.GPS)) {

		}
		throw new IllegalArgumentException();
	}

	@Override
	public InfraredSensor createInfraredSensor(Coordinate position,
		List<SensorInterfererType> sensorInterfererTypes) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public InductionLoopSensor createInductionLoopSensor(Coordinate position,
		List<SensorInterfererType> sensorInterfererTypes) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public TopoRadarSensor createTopoRadarSensor(Coordinate position,
		List<SensorInterfererType> sensorInterfererTypes) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
