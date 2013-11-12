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
 
package de.pgalise.simulation.operationCenter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.pgalise.simulation.operationCenter.internal.DefaultOCSimulationController;
import de.pgalise.simulation.operationCenter.internal.OCSensorStreamController;
import de.pgalise.simulation.operationCenter.internal.OCSimulationController;
import de.pgalise.simulation.operationCenter.internal.OCWebSocketService;
import de.pgalise.simulation.operationCenter.internal.OCWebSocketUser;
import de.pgalise.simulation.operationCenter.internal.hqf.OCHQFDataStreamController;
import de.pgalise.simulation.operationCenter.internal.message.NewSensorsMessage;
import de.pgalise.simulation.operationCenter.internal.message.OCWebSocketMessage;
import de.pgalise.simulation.operationCenter.internal.message.RemoveSensorsMessage;
import de.pgalise.simulation.operationCenter.internal.message.SensorDataMessage;
import de.pgalise.simulation.operationCenter.internal.message.SimulationInitMessage;
import de.pgalise.simulation.operationCenter.internal.message.SimulationStartMessage;
import de.pgalise.simulation.operationCenter.internal.message.SimulationStoppedMessage;
import de.pgalise.simulation.operationCenter.internal.model.sensordata.SensorData;
import de.pgalise.simulation.operationCenter.internal.strategy.GPSGateStrategy;
import de.pgalise.simulation.operationCenter.internal.strategy.GPSSensorTimeoutStrategy;
import de.pgalise.simulation.operationCenter.internal.strategy.SendSensorDataStrategy;
import de.pgalise.simulation.service.InitParameter;
import de.pgalise.simulation.shared.controller.StartParameter;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.it.TestUtils;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.sensorFramework.SensorHelper;
import de.pgalise.simulation.shared.sensor.SensorInterfererType;
import de.pgalise.simulation.sensorFramework.SensorTypeEnum;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.traffic.BusRoute;
import de.pgalise.simulation.shared.city.City;
import de.pgalise.simulation.traffic.InfrastructureInitParameter;
import de.pgalise.simulation.traffic.InfrastructureStartParameter;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEvent;
import de.pgalise.simulation.traffic.server.sensor.interferer.GpsInterferer;

/**
 * J-Unit tests for {@link DefaultOCSimulationController}
 * 
 * @author Timo
 */
public class DefaultOCSimulationControllerTest {
	/**
	 * OCSimulationController mock for this test.
	 * 
	 * @author Timo
	 */
	private static class OCSimulationControllerMock implements OCWebSocketService {

		private OCWebSocketMessage<?> lastMessage;

		public OCWebSocketMessage<?> getLastMessage() {
			return this.lastMessage;
		}

		@Override
		public Set<OCWebSocketUser> getOCWebSocketUsers() {
			return null;
		}

		@Override
		public void handleNewOpenedWebSocket(OCWebSocketUser user) {

		}

		@Override
		public void registerNewUserEventListener(NewUserEventListener listener) {
		}

		@Override
		public void removeNewUserEventListener(NewUserEventListener listener) {
		}

		@Override
		public void sendMessageToAllUsers(OCWebSocketMessage<?> message) throws IOException {
			this.lastMessage = message;
		}
	}

	/**
	 * Mock for send sensor data strategy. It will directly send every sensor data to the websocket service.
	 * 
	 * @author Timo
	 */
	private static class SendSensorDataStrategyMock extends SendSensorDataStrategy {

		/**
		 * Constructor
		 * 
		 * @param webSocketService
		 *            OCWebSocketService
		 */
		public SendSensorDataStrategyMock(OCWebSocketService webSocketService) {
			super(webSocketService);
		}

		@Override
		protected boolean isItTimeToSend(long timestamp) {
			return true;
		}
	}

	/**
	 * Test class
	 */
	private static OCSimulationController TESTCLASS;

	/**
	 * OCSensorStreamController
	 */
	private static OCSensorStreamController OC_SENSOR_STREAM_CONTROLLER;

	/**
	 * GPSSensorTimeoutStrategy
	 */
	private static GPSSensorTimeoutStrategy GPS_SENSOR_TIMEOUT_STRATEGY;

	/**
	 * SendSensorDataStrategy
	 */
	private static SendSensorDataStrategy SEND_SENSOR_DATA_STRATEGY;

	/**
	 * Mock of the OCSimulationController
	 */
	private static OCSimulationControllerMock OC_WEB_SOCKET_SERVICE;

	/**
	 * OCHQFDataStreamController
	 */
	private static OCHQFDataStreamController OC_HQFDATA_STREAM_CONTROLLER;

	/**
	 * GPSGateStrategy
	 */
	private static GPSGateStrategy GATE_MESSAGE_STRATEGY;

	/**
	 * Init parameter
	 */
	private static InfrastructureInitParameter INIT_PARAMETER;

	/**
	 * Start parameter
	 */
	private static InfrastructureStartParameter START_PARAMTER;

	/**
	 * List of sensor helper
	 */
	private static List<SensorHelper<?>> SENSOR_HELPER_LIST;

	@BeforeClass
	public static void setUp() {
		DefaultOCSimulationControllerTest.SENSOR_HELPER_LIST = new LinkedList<>();
		Output output = EasyMock.createNiceMock(Output.class);
		GpsInterferer gpsInterferer = EasyMock.createNiceMock(GpsInterferer.class);
		Sensor sensor = new GpsSensor(output,
			null,
			SensorTypeEnum.RAIN,
			null,
			gpsInterferer);
		DefaultOCSimulationControllerTest.SENSOR_HELPER_LIST.add(
			new SensorHelper<>(
				sensor, 
				new Coordinate(),
				SensorTypeEnum.ANEMOMETER, 
				new LinkedList<SensorInterfererType>()
			)
		);
		DefaultOCSimulationControllerTest.SENSOR_HELPER_LIST.add(
			new SensorHelper(
				sensor, 
				new Coordinate(),
				SensorTypeEnum.BAROMETER, 
				new LinkedList<SensorInterfererType>()));
		DefaultOCSimulationControllerTest.SENSOR_HELPER_LIST.add(new SensorHelper(sensor, new Coordinate(),
				SensorTypeEnum.INDUCTIONLOOP, new LinkedList<SensorInterfererType>()));

		DefaultOCSimulationControllerTest.OC_SENSOR_STREAM_CONTROLLER = EasyMock
				.createNiceMock(OCSensorStreamController.class);
		DefaultOCSimulationControllerTest.GPS_SENSOR_TIMEOUT_STRATEGY = EasyMock
				.createNiceMock(GPSSensorTimeoutStrategy.class);
		DefaultOCSimulationControllerTest.OC_HQFDATA_STREAM_CONTROLLER = EasyMock
				.createNiceMock(OCHQFDataStreamController.class);
		DefaultOCSimulationControllerTest.OC_WEB_SOCKET_SERVICE = new OCSimulationControllerMock();
		DefaultOCSimulationControllerTest.SEND_SENSOR_DATA_STRATEGY = new SendSensorDataStrategyMock(
				DefaultOCSimulationControllerTest.OC_WEB_SOCKET_SERVICE);
		DefaultOCSimulationControllerTest.GATE_MESSAGE_STRATEGY = EasyMock.createNiceMock(GPSGateStrategy.class);

		/* Create init paramter: */
		DefaultOCSimulationControllerTest.INIT_PARAMETER = new InfrastructureInitParameter(null, null, 0, 0, 0, 0, "", "", null, null);

		/* Create start parameter: */
		City city = TestUtils.createDefaultTestCityInstance();
		DefaultOCSimulationControllerTest.START_PARAMTER = new InfrastructureStartParameter(city, true, null);

		DefaultOCSimulationControllerTest.TESTCLASS = new DefaultOCSimulationController(
				DefaultOCSimulationControllerTest.OC_WEB_SOCKET_SERVICE,
				DefaultOCSimulationControllerTest.OC_SENSOR_STREAM_CONTROLLER,
				DefaultOCSimulationControllerTest.GPS_SENSOR_TIMEOUT_STRATEGY,
				DefaultOCSimulationControllerTest.SEND_SENSOR_DATA_STRATEGY,
				DefaultOCSimulationControllerTest.OC_HQFDATA_STREAM_CONTROLLER,
				DefaultOCSimulationControllerTest.GATE_MESSAGE_STRATEGY);
	}

	@Test
	public void test() throws SensorException, IllegalStateException, InitializationException {
		DefaultOCSimulationControllerTest.TESTCLASS.init(DefaultOCSimulationControllerTest.INIT_PARAMETER);
		Assert.assertTrue(DefaultOCSimulationControllerTest.OC_WEB_SOCKET_SERVICE.getLastMessage() instanceof SimulationInitMessage);

		DefaultOCSimulationControllerTest.TESTCLASS.start(DefaultOCSimulationControllerTest.START_PARAMTER);
		Assert.assertTrue(DefaultOCSimulationControllerTest.OC_WEB_SOCKET_SERVICE.getLastMessage() instanceof SimulationStartMessage);

		DefaultOCSimulationControllerTest.TESTCLASS.stop();
		Assert.assertTrue(DefaultOCSimulationControllerTest.OC_WEB_SOCKET_SERVICE.getLastMessage() instanceof SimulationStoppedMessage);

		DefaultOCSimulationControllerTest.TESTCLASS.createSensors(
			DefaultOCSimulationControllerTest.SENSOR_HELPER_LIST);
		Assert.assertTrue(DefaultOCSimulationControllerTest.OC_WEB_SOCKET_SERVICE.getLastMessage() instanceof NewSensorsMessage);

		for (SensorHelper sensorHelper : DefaultOCSimulationControllerTest.SENSOR_HELPER_LIST) {
			DefaultOCSimulationControllerTest.TESTCLASS.update(0, new SensorData(sensorHelper.getSensorType()
					.getSensorTypeId(), sensorHelper.getSensorID()));
			Assert.assertTrue(DefaultOCSimulationControllerTest.OC_WEB_SOCKET_SERVICE.getLastMessage() instanceof SensorDataMessage);
		}

		DefaultOCSimulationControllerTest.TESTCLASS.deleteSensors(DefaultOCSimulationControllerTest.SENSOR_HELPER_LIST);
		Assert.assertTrue(DefaultOCSimulationControllerTest.OC_WEB_SOCKET_SERVICE.getLastMessage() instanceof RemoveSensorsMessage);
	}
}
