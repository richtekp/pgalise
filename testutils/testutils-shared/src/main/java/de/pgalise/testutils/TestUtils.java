
package de.pgalise.testutils;

import de.pgalise.simulation.shared.entity.BaseCoordinate;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.shared.entity.BaseBoundary;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ejb.embeddable.EJBContainer;

/**
 *
 * @author richter
 */
public class TestUtils {

  private final static Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);
  private static EJBContainer container;

  private final static Properties p;

  static {
    p = new Properties();
    p.setProperty("java.naming.factory.initial",
      "org.apache.openejb.client.LocalInitialContextFactory");
    p.setProperty("jdbc/pgalise",
      "new://Resource?type=DataSource");
    p.setProperty("jdbc/pgalise.JdbcDriver",
      "org.postgresql.Driver");
    p.setProperty("jdbc/pgalise.JdbcUrl",
      "jdbc:postgresql://127.0.0.1:5201/pgalise_test");
    p.setProperty("jdbc/pgalise.UserName",
      "pgalise");
    p.setProperty("jdbc/pgalise.Password",
      "somepw");
    p.setProperty("jdbc/pgalise.JtaManaged",
      "true");
//			p.setProperty(
//				"hibernate.dialect",
//				"org.hibernate.spatial.dialect.postgis.PostgisDialect"
//			);
    p.setProperty("hibernate.transaction.jta.platform",
      "org.apache.openejb.hibernate.OpenEJBJtaPlatform2");
    p.setProperty("openejb.classloader.forced-skip",
      "org.xml.sax");
//      p.setProperty("openejb.validation.output.level",
//        "VERBOSE");
//      container = EJBContainer.createEJBContainer(p);
//    }
  }

  public static EJBContainer getContainer() {
    if (container == null) {
      container = EJBContainer.createEJBContainer(p);
    }
    return container;
  }

  public static Context getContext() {
//    if (container == null) {

    try {
      InitialContext initialContext = new InitialContext(p);
      return initialContext;
//    return container.getContext();
    } catch (NamingException ex) {
      throw new RuntimeException(ex);
    }
  }

  public static City createDefaultTestCityInstance(IdGenerator idGenerator) {

    BaseCoordinate referencePoint = new BaseCoordinate(idGenerator.getNextId(), 52.516667,
      13.4);
    Polygon referenceArea = GeoToolsBootstrapping.getGeometryFactory().
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
    City city = new City(idGenerator.getNextId(),
      "Berlin",
      3375222,
      80,
      true,
      true,
      new BaseBoundary(idGenerator.getNextId(),
        referenceArea));
    return city;
  }

  private TestUtils() {
  }
}
