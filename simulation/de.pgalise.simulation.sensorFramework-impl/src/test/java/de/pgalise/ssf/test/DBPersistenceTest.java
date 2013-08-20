package de.pgalise.ssf.test;

import java.io.IOException;
import java.util.Properties;

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.naming.NamingException;

import org.junit.Test;

import de.pgalise.simulation.sensorFramework.persistence.SensorPersistenceService;
import de.pgalise.simulation.shared.controller.Controller;
import javax.vecmath.Vector2d;

public class DBPersistenceTest {

	@Test
	public void test() throws IOException, NamingException {
		System.setProperty("simulation.configuration.filepath", "src/test/resources/simulation.conf");
		Properties prop = new Properties();
		prop.load(Controller.class.getResourceAsStream("/jndi.properties"));
		EJBContainer container = EJBContainer.createEJBContainer(prop);
		Context ctx = container.getContext();
		SensorPersistenceService s = (SensorPersistenceService) ctx
				.lookup("java:global/de.pgalise.simulation.sensorFramework-impl/de.pgalise.sensorFramework.persistence.SensorPersistenceService");
		System.out.println(s.getClass().getName());
		s.clear();
		for (int i = 0; i < 100; i++) {
			s.saveSensor(new TestSensor(i + 1, new Vector2d(i, i)));
		}
		for (int i = 0; i < 100; i++) {
			s.deleteSensor(i + 1);
		}
	}
}
