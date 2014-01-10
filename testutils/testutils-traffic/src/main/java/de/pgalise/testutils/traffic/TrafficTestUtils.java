/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.testutils.traffic;

import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.city.BaseGeoInfo;
import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.traffic.OSMCityInfrastructureData;
import de.pgalise.simulation.traffic.TrafficCity;
import de.pgalise.simulation.traffic.TrafficInfrastructureData;
import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;
import de.pgalise.util.cityinfrastructure.DefaultBuildingEnergyProfileStrategy;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author richter
 */
public class TrafficTestUtils {

	public static TrafficCity createDefaultTestCityInstance(Long id) {

		JaxRSCoordinate referencePoint = new JaxRSCoordinate(52.516667,
			13.4);
		Polygon referenceArea = GeoToolsBootstrapping.getGEOMETRY_FACTORY().
			createPolygon(
				new JaxRSCoordinate[]{
					new JaxRSCoordinate(referencePoint.getX() - 1,
						referencePoint.getY() - 1),
					new JaxRSCoordinate(referencePoint.getX() - 1,
						referencePoint.getY()),
					new JaxRSCoordinate(referencePoint.getX(),
						referencePoint.getY()),
					new JaxRSCoordinate(referencePoint.getX(),
						referencePoint.getY() - 1),
					new JaxRSCoordinate(referencePoint.getX() - 1,
						referencePoint.getY() - 1)
				}
			);
		InputStream osmFileInputStream = Thread.currentThread().
			getContextClassLoader().getResourceAsStream("oldenburg_pg.osm");
		if (osmFileInputStream == null) {
			throw new RuntimeException("could not load osm file");
		}
		InputStream busStopFileInputStream = Thread.currentThread().
			getContextClassLoader().getResourceAsStream("stops.gtfs");
		if (busStopFileInputStream == null) {
			throw new RuntimeException("could not load bus stop file");
		}
		BuildingEnergyProfileStrategy buildingEnergyProfileStrategy = new DefaultBuildingEnergyProfileStrategy();
		TrafficInfrastructureData trafficInfrastructureData;
		try {
			trafficInfrastructureData = new OSMCityInfrastructureData(id,
				osmFileInputStream,
				busStopFileInputStream,
				buildingEnergyProfileStrategy
			);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		TrafficCity city = new TrafficCity(
			"Berlin",
			3375222,
			80,
			true,
			true,
			new BaseGeoInfo(referenceArea),
			trafficInfrastructureData);
		return city;
	}
}
