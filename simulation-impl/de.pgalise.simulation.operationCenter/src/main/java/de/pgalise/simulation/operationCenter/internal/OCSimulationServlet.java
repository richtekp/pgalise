/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package de.pgalise.simulation.operationCenter.internal;

import java.io.IOException;
import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import de.pgalise.simulation.sensorFramework.Sensor;

import de.pgalise.simulation.service.GsonService;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.TrafficStartParameter;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import java.util.Set;

/**
 * The oc simulation servlet receives simulation updates directly from the
 * simulation controler via HTTP by using an implementation of the
 * {@link OperationCenterController}. It provides the information to the
 * prodived {@link OCSimulationController}. The bindings can be changed in the
 * class {@link OCModule}.
 *
 * @author Timo
 */
public class OCSimulationServlet extends HttpServlet {

	private static final long serialVersionUID = -2330660894525825600L;
	private static final Logger log = LoggerFactory.getLogger(
		OCSimulationServlet.class);
	@EJB
	private OCSimulationController ocSimulationController;
	private static final TypeToken<Collection<Sensor>> sensorCollectionTypeToken = new TypeToken<Collection<Sensor>>() {
	};

	@EJB
	private GsonService gsonService;
	private Gson gson;

	/**
	 * Only to handle post construct stuff. Don't call this separately.
	 */
	@PostConstruct
  public void initialize() {
		this.gson = this.gsonService.getGson();

	}

	public OCSimulationController getOCSimulationController() {
		return ocSimulationController;
	}

	@Override
	protected void doGet(HttpServletRequest req,
		HttpServletResponse resp)
		throws ServletException, IOException {
		processRequest(req,
			resp);
	}

	@Override
	protected void doPost(HttpServletRequest req,
		HttpServletResponse resp)
		throws ServletException, IOException {
		processRequest(req,
			resp);
	}

	/**
	 * Processes the request. New items needs to be set here.
	 *
	 * @param req
	 * @param resp
	 * @throws IOException
	 * @throws IllegalStateException
	 * @throws JsonSyntaxException
	 */
	protected void processRequest(HttpServletRequest req,
		HttpServletResponse resp) throws IOException {

		try {

			/* Remove sensor: */
			if (req.getParameter("deletesensor") != null && req.getParameter(
				"deletesensor").equalsIgnoreCase("true")) {

				log.debug("deletesensor");
				ocSimulationController.deleteSensor(gson.fromJson(req.getParameter(
					"json"),
					Sensor.class));


				/* Add sensor: */
			} else if (req.getParameter("createsensor") != null && req.getParameter(
				"createsensor").equalsIgnoreCase("true")) {
				log.debug("create sensor");
				Sensor<?, ?> sensor = gson.fromJson(req.getParameter("json"),
					Sensor.class);
				ocSimulationController.createSensor(sensor);

				/* Add sensors: */
			} else if (req.getParameter("createsensors") != null && req.getParameter(
				"createsensor").equalsIgnoreCase("true")) {
				log.debug("create sensors");
				Set<Sensor<?,?>> sensors = gson.fromJson(req.
					getParameter("json"),
					sensorCollectionTypeToken.getType());
				ocSimulationController.createSensors(sensors);

				/* Remove sensors:: */
			} else if (req.getParameter("deletesensors") != null && req.getParameter(
				"createsensor").equalsIgnoreCase("true")) {
				log.debug("delete sensors");
				Set<Sensor<?,?>> sensors = gson.fromJson(req.
					getParameter("json"),
					sensorCollectionTypeToken.getType());
				ocSimulationController.deleteSensors(sensors);

				/* simulation update: */
			} else if (req.getParameter("update") != null && req.
				getParameter("update").equalsIgnoreCase("true")) {

				EventList simulationEventList = gson.fromJson(req.getParameter("json"),
					EventList.class);
				if (!simulationEventList.getEventList().isEmpty()) {
					log.debug(req.getParameter("json"));
				}
				ocSimulationController.update(gson.fromJson(req.getParameter("json"),
					EventList.class));

				/* simulation paused: */
			} else if (req.getParameter("stop") != null && req.getParameter("stop").
				equalsIgnoreCase("true")) {

				log.debug("Stopped!");
				// Not included, because simulation is much faster than infosphere!
				//ocSimulationController.stop();

			} else if (req.getParameter("start") != null && req.getParameter("start").
				equalsIgnoreCase("true")) {

				log.debug("start");
				ocSimulationController.start(gson.fromJson(req.getParameter("json"),
					TrafficStartParameter.class));

			} else if (req.getParameter("init") != null && req.getParameter("init").
				equalsIgnoreCase("true")) {

				log.debug("init");
				/* If the simulation is already stopped, than stop if, before start. */
				try {
					ocSimulationController.init(gson.fromJson(req.getParameter("json"),
						TrafficInitParameter.class));
				} catch (JsonSyntaxException | IllegalStateException e) {
					ocSimulationController.stop();
					ocSimulationController.reset();
					ocSimulationController.init(gson.fromJson(req.getParameter("json"),
						TrafficInitParameter.class));
				}

			} else if (req.getParameter("reset") != null && req.getParameter("reset").
				equalsIgnoreCase("true")) {

				log.debug("reset");
				ocSimulationController.reset();

			} else if (req.getParameter("exception") != null) {

				String exceptionMessage = req.getParameter("exception");
				ocSimulationController.displayException(new Exception(exceptionMessage));
				log.error(exceptionMessage);
			}

		} catch (JsonSyntaxException | IllegalStateException e) {
			log.error("Exception",
				e);
		}
	}
}
