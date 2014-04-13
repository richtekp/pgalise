/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.testutils.traffic;

import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.BaseBoundary;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.traffic.entity.CityInfrastructureData;
import de.pgalise.simulation.traffic.entity.TrafficCity;
import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;
import de.pgalise.util.cityinfrastructure.DefaultBuildingEnergyProfileStrategy;
import java.io.InputStream;

/**
 *
 * @author richter
 */
public class TrafficTestUtils {

  public static TrafficCity createDefaultTestCityInstance(
    IdGenerator idGenerator) {

    BaseCoordinate referencePoint = new BaseCoordinate(idGenerator.getNextId(), 52.516667,
      13.4);
    Polygon referenceArea = GeoToolsBootstrapping.getGEOMETRY_FACTORY().
      createPolygon(
        new BaseCoordinate[]{
          new BaseCoordinate(idGenerator.getNextId(), referencePoint.getX() - 1,
            referencePoint.getY() - 1),
          new BaseCoordinate(idGenerator.getNextId(), referencePoint.getX() - 1,
            referencePoint.getY()),
          new BaseCoordinate(idGenerator.getNextId(), referencePoint.getX(),
            referencePoint.getY()),
          new BaseCoordinate(idGenerator.getNextId(), referencePoint.getX(),
            referencePoint.getY() - 1),
          new BaseCoordinate(idGenerator.getNextId(), referencePoint.getX() - 1,
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
    CityInfrastructureData trafficInfrastructureData;
    trafficInfrastructureData = new CityInfrastructureData(idGenerator.
      getNextId());
    TrafficCity city = new TrafficCity(idGenerator.getNextId(),
      "Berlin",
      3375222,
      80,
      true,
      true,
      new BaseBoundary(idGenerator.getNextId(),
        referenceArea),
      trafficInfrastructureData);
    return city;
  }

  private TrafficTestUtils() {
  }
}
