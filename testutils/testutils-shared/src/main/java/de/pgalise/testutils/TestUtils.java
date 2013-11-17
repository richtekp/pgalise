/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.testutils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.city.Position;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import java.sql.Date;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.ejb.embeddable.EJBContainer;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author richter
 */
public class TestUtils {
	private final static Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);
	private static EJBContainer container;
	
	public static EJBContainer getContainer() {
		if(container == null) {
			Properties p;
			p = new Properties();
			p.setProperty("pgalise", "new://Resource?type=DataSource");
			p.setProperty("pgalise.JdbcDriver", "org.postgresql.Driver");
			p.setProperty("pgalise.JdbcUrl", "jdbc:postgresql://127.0.0.1:5201/pgalise_test");
			p.setProperty("pgalise.UserName", "pgalise");
			p.setProperty("pgalise.Password", "somepw");
			p.setProperty("pgalise.JtaManaged",	"true");
			
//			p.setProperty("pgaliseTest", "new://Resource?type=DataSource");
//			p.setProperty("pgaliseTest.JdbcDriver", "org.postgresql.Driver");
//			p.setProperty("pgaliseTest.JdbcUrl", "jdbc:postgresql://127.0.0.1:5201/pgalise_test");
//			p.setProperty("pgaliseTest.UserName", "postgis");
//			p.setProperty("pgaliseTest.Password", "postgis");
//			p.setProperty("pgaliseTest.JtaManaged",	"true");

			p.setProperty(
				"hibernate.dialect",
				"org.hibernate.spatial.dialect.postgis.PostgisDialect"
			);
			p.setProperty("openejb.classloader.forced-skip", "org.xml.sax");		
			p.setProperty("openejb.validation.output.level", "VERBOSE");
			container= EJBContainer.createEJBContainer(p);
		}
		return container;
	}
	
	public static EntityManagerFactory createEntityManagerFactory(String unitName) {
		return createEntityManagerFactory(unitName,
			null);
	}
	
	public static EntityManagerFactory createEntityManagerFactory(String unitName, Properties initialProperties) {
		Properties p = initialProperties;		
		if(p ==  null) {
			p = new Properties();
		}
			
//		p.setProperty(
//			"hibernate.dialect",
//			"org.hibernate.spatial.dialect.postgis.PostgisDialect"
//		);
//		p.setProperty("javax.persistence.jdbc.driver", "org.postgresql.Driver");
//		p.setProperty("javax.persistence.jdbc.url", "jdbc:postgresql://127.0.0.1:5201/weather_data");
//		p.setProperty("javax.persistence.jdbc.user", "postgis");
//		p.setProperty("javax.persistence.jdbc.password", "postgis");		
//		p.setProperty("hibernate.hbm2ddl.auto", "create-drop");
//		p.setProperty("hibernate.show_sql", "false");
		EntityManagerFactory retValue = Persistence.createEntityManagerFactory(unitName, p);
		return retValue;
	}
	
	public static City createDefaultTestCityInstance() {
		
		Coordinate referencePoint = new Coordinate(52.516667, 13.4);
		Polygon referenceArea = GeoToolsBootstrapping.getGEOMETRY_FACTORY().createPolygon(
			new Coordinate[] {
				new Coordinate(referencePoint.x-1, referencePoint.y-1), 
				new Coordinate(referencePoint.x-1, referencePoint.y), 
				new Coordinate(referencePoint.x, referencePoint.y), 
				new Coordinate(referencePoint.x, referencePoint.y-1),
				new Coordinate(referencePoint.x-1, referencePoint.y-1)
			}
		);
		City city = new City("Berlin",
			3375222,
			80,
			true,
			true,
			new Position(referenceArea));
		return city;
	}

	private TestUtils() {
	}
}