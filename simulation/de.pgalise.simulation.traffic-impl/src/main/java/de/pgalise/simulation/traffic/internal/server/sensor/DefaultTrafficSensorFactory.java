/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.internal.server.sensor;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.SensorHelper;
import de.pgalise.simulation.sensorFramework.SensorType;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
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
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.sensor.interferer.GpsInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.InductionLoopInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.InfraredInterferer;
import de.pgalise.simulation.traffic.server.sensor.interferer.TopoRadarInterferer;
import de.pgalise.staticsensor.internal.DefaultSensorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author richter
 */
public class DefaultTrafficSensorFactory extends DefaultSensorFactory {

	public DefaultTrafficSensorFactory() {
		super(null,
			null,
			null,
			null);
	}

	@Override
	public Sensor<?> createSensor(
		SensorHelper<?> sensorHelper,
		Set<SensorType> allowedTypes
	) {
		if(sensorHelper.getSensorType().equals(SensorTypeEnum.TRAFFIC_LIGHT_INTERSECTION)) {
				// Can't be returned here, because of missing dependencies
				return null;
			 } else if(sensorHelper.getSensorType().equals(SensorTypeEnum.INFRARED)) { // Infrared
				InfraredInterferer infraredInterferer;
				if (sensorHelper.getSensorInterfererType() != null && !sensorHelper.getSensorInterfererType().isEmpty()) {
					List<InfraredInterferer> infraredInterferers = new ArrayList<>();
					infraredInterferer = new CompositeInfraredInterferer(infraredInterferers);

					for (SensorInterfererType sensorInterfererType : sensorHelper.getSensorInterfererType()) {
						switch (sensorInterfererType) {
							case INFRARED_WHITE_NOISE_INTERFERER:
								infraredInterferers.add(new InfraredWhiteNoiseInterferer(this.getRss()));
								break;
							default:
								break;
						}
					}
				} else {
					infraredInterferer = new InfraredNoInterferer();
				}

				return new InfraredSensor(getOutput(), null,
						sensorHelper.getPosition(), sensorHelper.getUpdateSteps(),
						infraredInterferer);

			 } else if(sensorHelper.getSensorType().equals(SensorTypeEnum.INDUCTIONLOOP)) { // Inductionloop

				InductionLoopInterferer inductionLoopInterferer;
				if (sensorHelper.getSensorInterfererType() != null && !sensorHelper.getSensorInterfererType().isEmpty()) {
					List<InductionLoopInterferer> inductionLoopInterfers = new ArrayList<>();
					inductionLoopInterferer = new CompositeInductionLoopInterferer(inductionLoopInterfers);
					for (SensorInterfererType sensorInterfererType : sensorHelper.getSensorInterfererType()) {
						switch (sensorInterfererType) {
							case INDUCTION_LOOP_WHITE_NOISE_INTERFERER:
								inductionLoopInterfers.add(new InductionLoopWhiteNoiseInterferer(this.getRss()));
								break;
							default:
								break;
						}
					}
				} else {
					inductionLoopInterferer = new InductionLoopNoInterferer();
				}

				return new InductionLoopSensor(
					null,
					getOutput(), 
					sensorHelper.getPosition(), 
					sensorHelper.getUpdateSteps(),
					inductionLoopInterferer
				);
			 } else if(sensorHelper.getSensorType().equals(SensorTypeEnum.TOPORADAR)) { // Toporadar

				TopoRadarInterferer toporadarInterferer;
				if (sensorHelper.getSensorInterfererType() != null && !sensorHelper.getSensorInterfererType().isEmpty()) {
					List<TopoRadarInterferer> toporadarInterfers = new ArrayList<>();
					toporadarInterferer = new CompositeTopoRadarInterferer(toporadarInterfers);

					for (SensorInterfererType sensorInterfererType : sensorHelper.getSensorInterfererType()) {
						switch (sensorInterfererType) {
							case TOPO_RADAR_WHITE_NOISE_INTERFERER:
								toporadarInterfers.add(new TopoRadarWhiteNoiseInterferer(this.getRss()));
								break;
							default:
								break;
						}
					}
				} else {
					toporadarInterferer = new TopoRadarNoInterferer();
				}

				return new TopoRadarSensor(null,getOutput(), 
						sensorHelper.getPosition(), sensorHelper.getUpdateSteps(),
						toporadarInterferer);

			 } else if(sensorHelper.getSensorType().equals(SensorTypeEnum.GPS_BIKE)) {
				 throw new UnsupportedOperationException();// GPS
			 } else if(sensorHelper.getSensorType().equals(SensorTypeEnum.GPS_BUS)) {
				 throw new UnsupportedOperationException();
			 } else if(sensorHelper.getSensorType().equals(SensorTypeEnum.GPS_CAR)) {
				 throw new UnsupportedOperationException();
			 } else if(sensorHelper.getSensorType().equals(SensorTypeEnum.GPS_TRUCK)) {
				 throw new UnsupportedOperationException();
			 } else if(sensorHelper.getSensorType().equals(SensorTypeEnum.GPS_MOTORCYCLE)) {

				GpsInterferer gpsInterferer;
				if (sensorHelper.getSensorInterfererType() != null && !sensorHelper.getSensorInterfererType().isEmpty()) {

					List<GpsInterferer> gpsInterferes = new ArrayList<>();
					gpsInterferer = new CompositeGpsInterferer(gpsInterferes);

					for (SensorInterfererType sensorInterfererType : sensorHelper.getSensorInterfererType()) {
						switch (sensorInterfererType) {
							case GPS_ATMOSPHERE_INTERFERER:
								gpsInterferes.add(new GpsAtmosphereInterferer(this.getRss(), this.getWeatherCtrl()));
								break;
							case GPS_CLOCK_INTERFERER:
								gpsInterferes.add(new GpsClockInterferer(this.getRss()));
								break;
							case GPS_RECEIVER_INTERFERER:
								gpsInterferes.add(new GpsReceiverInterferer(this.getRss()));
								break;
							case GPS_WHITE_NOISE_INTERFERER:
								gpsInterferes.add(new GpsWhiteNoiseInterferer(this.getRss()));
								break;
							default:
								break;
						}
					}

				} else {
					gpsInterferer = new GpsNoInterferer();
				}

				return new GpsSensor(getOutput(), null,
						sensorHelper.getUpdateSteps(), sensorHelper.getSensorType(), new Coordinate(0, 0), gpsInterferer);
		}
		throw new IllegalArgumentException();
	}
}