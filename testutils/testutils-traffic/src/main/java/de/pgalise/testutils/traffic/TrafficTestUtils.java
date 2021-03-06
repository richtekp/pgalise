/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.testutils.traffic;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.BasePolygon;
import de.pgalise.simulation.shared.entity.BaseBoundary;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.traffic.entity.CityInfrastructureData;
import de.pgalise.simulation.traffic.entity.TrafficCity;
import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;
import de.pgalise.util.cityinfrastructure.DefaultBuildingEnergyProfileStrategy;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author richter
 */
public class TrafficTestUtils {

  public static TrafficCity createDefaultTestCityInstance(
    IdGenerator idGenerator) {

    BaseCoordinate referencePoint = new BaseCoordinate( 52.516667,
      13.4);
    List<BaseCoordinate> referenceArea = new LinkedList<>(Arrays.asList(
          new BaseCoordinate( referencePoint.getX() - 1,
            referencePoint.getY() - 1),
          new BaseCoordinate( referencePoint.getX() - 1,
            referencePoint.getY()),
          new BaseCoordinate( referencePoint.getX(),
            referencePoint.getY()),
          new BaseCoordinate( referencePoint.getX(),
            referencePoint.getY() - 1),
          new BaseCoordinate( referencePoint.getX() - 1,
            referencePoint.getY() - 1)
    )
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
      new BaseBoundary(idGenerator.getNextId(),referencePoint,
        new BasePolygon(idGenerator.getNextId(),
          GeoToolsBootstrapping.getGeometryFactory().createPolygon(
            referenceArea.toArray(new BaseCoordinate[referenceArea.size()])
          )
        )
      ),
      trafficInfrastructureData);
    return city;
  }

  private TrafficTestUtils() {
  }
}
