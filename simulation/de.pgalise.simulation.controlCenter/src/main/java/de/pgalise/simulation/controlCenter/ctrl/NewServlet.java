/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.controlCenter.internal.message.SimulationStartParameterMessage;
import de.pgalise.simulation.controlCenter.model.AttractionData;
import de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter;
import de.pgalise.simulation.controlCenter.model.RandomVehicleBundle;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.sensorFramework.output.tcpip.AbstractTcpIpOutput;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpKeepOpenStrategy;
import de.pgalise.simulation.service.GsonService;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.controller.TrafficFuzzyData;
import de.pgalise.simulation.shared.event.EventList;
import de.pgalise.simulation.shared.event.weather.ChangeWeatherEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEvent;
import de.pgalise.simulation.shared.event.weather.WeatherEventTypeEnum;
import de.pgalise.simulation.staticsensor.sensor.weather.WeatherInterferer;
import de.pgalise.simulation.traffic.BusRoute;
import de.pgalise.simulation.traffic.TrafficCity;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.weather.service.WeatherController;
import de.pgalise.staticsensor.internal.sensor.weather.RainSensor;
import de.pgalise.staticsensor.internal.sensor.weather.interferer.RainsensorWhiteNoiseInterferer;
import de.pgalise.testutils.traffic.TrafficTestUtils;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author richter
 */
@WebServlet(name = "NewServlet",
	urlPatterns = {"/NewServlet"})
public class NewServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@EJB
	private TrafficGraph trafficGraph;
	@EJB
	private WeatherController weatherController;
	@EJB
	private RandomSeedService randomSeedService;
	@EJB
	private GsonService gsonService;
	@EJB
	private IdGenerator idGenerator;

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	protected void processRequest(HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		Long someId = 3245429L;
		long nowTimestamp = System.currentTimeMillis();
		long startTimestamp = nowTimestamp + 10;
		long endTimestamp = startTimestamp + 1000;
		long interval = 100;
		long clockGeneratorInterval = 100;
		String ipSimulationController = "127.0.0.1", ipTrafficController = "127.0.0.1", ipWeatherController = "127.0.0.1",
			ipStaticSensorController = "127.0.0.1", ipEnergyController = "127.0.0.1";
//		Position berlinPosition = new Position(GeoToolsBootstrapping.getGEOMETRY_FACTORY().createPolygon(new Coordinates))
//		City city = new TrafficCity(graph, "Berlin", 3400000, 100, true,
//						true, new po);
		TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
			idGenerator.getNextId());
		Output output = new AbstractTcpIpOutput("127.0.0.1",
			22000,
			new TcpIpKeepOpenStrategy());
		WeatherInterferer weatherInterferer = new RainsensorWhiteNoiseInterferer(
			randomSeedService);
		Sensor<?, ?> windSensor = new RainSensor(idGenerator.getNextId(),
			output,
			city.getReferencePoint(),
			weatherController,
			weatherInterferer);
		List<Sensor<?, ?>> sensorHelpers = new ArrayList<Sensor<?, ?>>(
			Arrays.asList(windSensor));
		ChangeWeatherEvent weatherEvent = new ChangeWeatherEvent(10L,
			WeatherEventTypeEnum.HOTDAY,
			1.0f,
			startTimestamp
			+ 20,
			30);
		EventList<?> eventList = new EventList<>(idGenerator.getNextId(),
			new ArrayList<>(Arrays.asList(
					weatherEvent)),
			startTimestamp + 20);
		List<EventList<?>> simulationEventLists = new ArrayList<EventList<?>>(
			Arrays.asList(
				eventList));
		List<WeatherEvent> weatherEventHelpers = new ArrayList<WeatherEvent>(Arrays.
			asList(new ChangeWeatherEvent(11L,
					weatherEvent.getType(),
					weatherEvent.getValue(),
					weatherEvent.
					getTimestamp(),
					weatherEvent.getDuration())));
//		MapAndBusstopFileData oSMAndBusstopFileData = new MapAndBusstopFileData();
		String osmFilePath = "/oldenburg_pg.osm";
		String busStopFilePath = "/stops.gtfs";
		RandomVehicleBundle randomVehicleBundle = new RandomVehicleBundle(
			100,
			1.0,
			100,
			1.0,
			100,
			1.0,
			100,
			1.0);
		TrafficFuzzyData trafficFuzzyData = new TrafficFuzzyData(10,
			2.0,
			2);
		ControlCenterStartParameter simulationStartParameter = new ControlCenterStartParameter(
			startTimestamp,
			endTimestamp,
			interval,
			clockGeneratorInterval,
			ipSimulationController,
			ipTrafficController,
			ipWeatherController,
			ipStaticSensorController,
			ipEnergyController,
			2,
			city,
			sensorHelpers,
			simulationEventLists,
			weatherEventHelpers,
			randomVehicleBundle,
			true,
			10,
			true,
			new ArrayList<BusRoute>(0),
			"127.0.0.1:8080/operationCenter",
			trafficFuzzyData,
			new ArrayList<AttractionData>(0),
			"127.0.0.1:8080/controlCenter"
		);
		SimulationStartParameterMessage msg = new SimulationStartParameterMessage(
			someId,
			simulationStartParameter);
		String message = msg.toJson(gsonService.getGson());
		URL url = new URL("http://localhost:8080/operationCenter/");
		URLConnection uRLConnection = url.openConnection();
		uRLConnection.addRequestProperty("start",
			"true");
		uRLConnection.addRequestProperty("json",
			message);
		uRLConnection.connect();
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {
		processRequest(request,
			response);
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request,
		HttpServletResponse response)
		throws ServletException, IOException {
		processRequest(request,
			response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

}
