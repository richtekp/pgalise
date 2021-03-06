/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.entity;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author richter
 */
/*
 implementations of IdGenerator not available (in shared-impl)
 */
public class BaseBoundaryTest {

  private final IdGenerator idGenerator = new IdGenerator() {
    private long nextId = 1L;

    @Override
    public Long getNextId() {
      nextId++;
      return nextId;
    }
  };

  public BaseBoundaryTest() {
  }

  /**
   * Test of retrieveCenterPoint method, of class BaseBoundary.
   */
  @Test
  public void testRetrieveCenterPoint() {
    City testCity = createDefaultTestCityInstance(idGenerator);
    BaseBoundary instance = new BaseBoundary(idGenerator.getNextId(),
            testCity.getBoundary().getReferencePoint(),
            testCity.getBoundary().getBoundary());
    Coordinate expResult
            = GeoToolsBootstrapping.getGeometryFactory().createMultiPoint(
                    testCity.getBoundary().getBoundary().getCoordinates()
            ).getCentroid().getCoordinate();
    Coordinate result = instance.retrieveCenterPoint();
    assertEquals(expResult.x,
            result.x, 0.5);
    assertEquals(expResult.y,
            result.y, 0.5);
  }

  /*
   copied from TestUtils because test classes can't be shared easily
   */
  private static City createDefaultTestCityInstance(IdGenerator idGenerator) {

    BaseCoordinate referencePoint = new BaseCoordinate(52.516667,
            13.4);
    BasePolygon referenceArea = new BasePolygon(idGenerator.getNextId(),
      GeoToolsBootstrapping.getGeometryFactory().createPolygon(
        new LinkedList<Coordinate>(
          Arrays.asList(
            new BaseCoordinate(referencePoint.getX() - 1,
                    referencePoint.getY() - 1),
            new BaseCoordinate(referencePoint.getX() - 1,
                    referencePoint.getY() + 1),
            new BaseCoordinate(referencePoint.getX() + 1,
                    referencePoint.getY() + 1),
            new BaseCoordinate(referencePoint.getX() + 1,
                    referencePoint.getY() - 1),
            new BaseCoordinate(referencePoint.getX() - 1,
                    referencePoint.getY() - 1)
          )
        ).toArray(new Coordinate[5])
      )
    );
    City city = new City(idGenerator.getNextId(),
            "Berlin",
            3375222,
            80,
            true,
            true,
            new BaseBoundary(idGenerator.getNextId(),referencePoint,
            referenceArea));
    return city;
  }
}
