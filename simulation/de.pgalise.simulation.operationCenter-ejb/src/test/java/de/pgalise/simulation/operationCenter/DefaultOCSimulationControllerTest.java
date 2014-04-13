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

import de.pgalise.simulation.operationCenter.internal.DefaultOCSimulationController;
import de.pgalise.simulation.operationCenter.internal.OCSensorStreamController;
import de.pgalise.simulation.operationCenter.internal.OCSimulationController;
import de.pgalise.simulation.operationCenter.internal.OCWebSocketService;
import de.pgalise.simulation.operationCenter.internal.hqf.OCHQFDataStreamController;
import de.pgalise.simulation.operationCenter.internal.message.NewSensorsMessage;
import de.pgalise.simulation.operationCenter.internal.message.OCWebSocketMessage;
import de.pgalise.simulation.operationCenter.internal.message.RemoveSensorsMessage;
import de.pgalise.simulation.operationCenter.internal.message.SensorDataMessage;
import de.pgalise.simulation.operationCenter.internal.message.SimulationInitMessage;
import de.pgalise.simulation.operationCenter.internal.message.SimulationStartMessage;
import de.pgalise.simulation.operationCenter.internal.message.SimulationStoppedMessage;
import de.pgalise.simulation.operationCenter.internal.strategy.GPSGateStrategy;
import de.pgalise.simulation.operationCenter.internal.strategy.GPSSensorTimeoutStrategy;
import de.pgalise.simulation.operationCenter.internal.strategy.SendSensorDataStrategy;
import de.pgalise.simulation.operationcenter.OCWebSocketUser;
import de.pgalise.simulation.sensorFramework.Sensor;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.exception.InitializationException;
import de.pgalise.simulation.shared.exception.SensorException;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.simulation.traffic.TrafficStartParameter;
import de.pgalise.simulation.traffic.entity.TrafficCity;
import de.pgalise.simulation.traffic.internal.server.sensor.GpsSensor;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.GpsInterferer;
import de.pgalise.testutils.TestUtils;
import de.pgalise.testutils.traffic.TrafficTestUtils;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.naming.NamingException;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * J-Unit tests for {@link DefaultOCSimulationController}
 *
 * @author Timo
 */
@LocalBean
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
    public void sendMessageToAllUsers(OCWebSocketMessage<?> message) throws
      IOException {
      this.lastMessage = message;
    }
  }

  /**
   * Mock for send sensor data strategy. It will directly send every sensor data
   * to the websocket service.
   *
   * @author Timo
   */
  private static class SendSensorDataStrategyMock extends SendSensorDataStrategy {

    /**
     * Constructor
     *
     * @param webSocketService OCWebSocketService
     */
    SendSensorDataStrategyMock(OCWebSocketService webSocketService) {
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
  private OCSimulationController TESTCLASS;

  /**
   * OCSensorStreamController
   */
  private OCSensorStreamController OC_SENSOR_STREAM_CONTROLLER;

  /**
   * GPSSensorTimeoutStrategy
   */
  private GPSSensorTimeoutStrategy GPS_SENSOR_TIMEOUT_STRATEGY;

  /**
   * SendSensorDataStrategy
   */
  private SendSensorDataStrategy SEND_SENSOR_DATA_STRATEGY;

  /**
   * Mock of the OCSimulationController
   */
  private OCSimulationControllerMock OC_WEB_SOCKET_SERVICE;

  /**
   * OCHQFDataStreamController
   */
  private OCHQFDataStreamController OC_HQFDATA_STREAM_CONTROLLER;

  /**
   * GPSGateStrategy
   */
  private GPSGateStrategy GATE_MESSAGE_STRATEGY;

  /**
   * Init parameter
   */
  private TrafficInitParameter INIT_PARAMETER;

  /**
   * Start parameter
   */
  private TrafficStartParameter START_PARAMTER;

  /**
   * List of sensor helper
   */
  private Set<Sensor<?, ?>> SENSOR_HELPER_LIST = new HashSet<>();
  @EJB
  private IdGenerator idGenerator;

  public DefaultOCSimulationControllerTest() {
  }

  @Before
  public void setUpClass() throws NamingException, MalformedURLException {
    TestUtils.getContext().bind("inject",
      this);
    SENSOR_HELPER_LIST = new HashSet<>();
    Output output = EasyMock.createNiceMock(Output.class);
    GpsInterferer gpsInterferer = EasyMock.createNiceMock(GpsInterferer.class);
    GpsSensor sensor = new GpsSensor(idGenerator.getNextId(),
      output,
      null,
      gpsInterferer);
    SENSOR_HELPER_LIST.add(
      sensor
    );
    SENSOR_HELPER_LIST.add(
      sensor);
    SENSOR_HELPER_LIST.add(sensor);

    OC_SENSOR_STREAM_CONTROLLER = EasyMock
      .createNiceMock(OCSensorStreamController.class);
    GPS_SENSOR_TIMEOUT_STRATEGY = EasyMock
      .createNiceMock(GPSSensorTimeoutStrategy.class);
    OC_HQFDATA_STREAM_CONTROLLER = EasyMock
      .createNiceMock(OCHQFDataStreamController.class);
    OC_WEB_SOCKET_SERVICE = new OCSimulationControllerMock();
    SEND_SENSOR_DATA_STRATEGY = new SendSensorDataStrategyMock(
      OC_WEB_SOCKET_SERVICE);
    GATE_MESSAGE_STRATEGY = EasyMock.
      createNiceMock(GPSGateStrategy.class);

    /* Create init paramter: */
    INIT_PARAMETER = new TrafficInitParameter(
      null,
      0,
      0,
      0,
      0,
      new URL(""),
      new URL(""),
      null,
      output);

    /* Create start parameter: */
    TrafficCity city = TrafficTestUtils.createDefaultTestCityInstance(
      idGenerator);
    START_PARAMTER = new TrafficStartParameter(
      city,
      true,
      null);

    TESTCLASS = new DefaultOCSimulationController(
      OC_WEB_SOCKET_SERVICE,
      OC_SENSOR_STREAM_CONTROLLER,
      GPS_SENSOR_TIMEOUT_STRATEGY,
      SEND_SENSOR_DATA_STRATEGY,
      OC_HQFDATA_STREAM_CONTROLLER,
      GATE_MESSAGE_STRATEGY);
  }

  @Test
  public void test() throws SensorException, IllegalStateException,
    InitializationException {
    TESTCLASS.init(
      INIT_PARAMETER);
    Assert.assertTrue(OC_WEB_SOCKET_SERVICE.
      getLastMessage() instanceof SimulationInitMessage);

    TESTCLASS.start(
      START_PARAMTER);
    Assert.assertTrue(OC_WEB_SOCKET_SERVICE.
      getLastMessage() instanceof SimulationStartMessage);

    TESTCLASS.stop();
    Assert.assertTrue(OC_WEB_SOCKET_SERVICE.
      getLastMessage() instanceof SimulationStoppedMessage);

    TESTCLASS.createSensors(
      SENSOR_HELPER_LIST);
    Assert.assertTrue(OC_WEB_SOCKET_SERVICE.
      getLastMessage() instanceof NewSensorsMessage);

    for (Sensor<?, ?> sensorHelper
      : SENSOR_HELPER_LIST) {
      TESTCLASS.update(0,
        sensorHelper);
      Assert.assertTrue(OC_WEB_SOCKET_SERVICE.
        getLastMessage() instanceof SensorDataMessage);
    }

    TESTCLASS.deleteSensors(
      SENSOR_HELPER_LIST);
    Assert.assertTrue(OC_WEB_SOCKET_SERVICE.
      getLastMessage() instanceof RemoveSensorsMessage);
  }
}
