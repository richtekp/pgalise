/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.testutils;

import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.shared.city.BaseGeoInfo;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import java.util.Properties;
import javax.ejb.embeddable.EJBContainer;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
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
		if (container == null) {
			Properties p;
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

			p.setProperty("movieDatabaseUnmanaged",
				"new://Resource?type=DataSource");
			p.setProperty("movieDatabaseUnmanaged.JdbcDriver",
				"org.postgresql.Driver");
			p.setProperty("movieDatabaseUnmanaged.JdbcUrl",
				"jdbc:postgresql://127.0.0.1:5201/pgalise_test");
			p.setProperty("movieDatabaseUnmanaged.UserName",
				"pgalise");
			p.setProperty("movieDatabaseUnmanaged.Password",
				"somepw");
			p.setProperty("movieDatabaseUnmanaged.JtaManaged",
				"false");
//			p.setProperty(
//				"hibernate.dialect",
//				"org.hibernate.spatial.dialect.postgis.PostgisDialect"
//			);
			p.setProperty("openejb.classloader.forced-skip",
				"org.xml.sax");
			p.setProperty("openejb.validation.output.level",
				"VERBOSE");
			container = EJBContainer.createEJBContainer(p);
		}
		return container;
	}

	public static EntityManagerFactory createEntityManagerFactory(String unitName) {
		return createEntityManagerFactory(unitName,
			null);
	}

	public static EntityManagerFactory createEntityManagerFactory(String unitName,
		Properties initialProperties) {
		Properties p = initialProperties;
		if (p == null) {
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
		EntityManagerFactory retValue = Persistence.createEntityManagerFactory(
			unitName,
			p);
		return retValue;
	}

	public static City createDefaultTestCityInstance() {

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
		City city = new City(
			"Berlin",
			3375222,
			80,
			true,
			true,
			new BaseGeoInfo(referenceArea));
		return city;
	}

	private TestUtils() {
	}
}
