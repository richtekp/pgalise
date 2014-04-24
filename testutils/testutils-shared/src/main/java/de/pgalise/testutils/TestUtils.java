
package de.pgalise.testutils;

import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.BaseBoundary;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    BaseCoordinate referencePoint = new BaseCoordinate( 52.516667,
      13.4);
    List<BaseCoordinate> referenceArea = new LinkedList<>(Arrays.asList(
          new BaseCoordinate( referencePoint.getX() - 1,
            referencePoint.getY() - 1),
          new BaseCoordinate( referencePoint.getX() - 1,
            referencePoint.getY()+1),
          new BaseCoordinate( referencePoint.getX()+1,
            referencePoint.getY()+1),
          new BaseCoordinate( referencePoint.getX()+1,
            referencePoint.getY() - 1),
          new BaseCoordinate( referencePoint.getX() - 1,
            referencePoint.getY() - 1)        
      ));
    City city = new City(idGenerator.getNextId(),
      "Berlin",
      3375222,
      80,
      true,
      true,
      referencePoint,
        referenceArea);
    return city;
  }

  private TestUtils() {
  }
}
