/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.operationCenter.internal;

import java.io.IOException;
import java.net.UnknownHostException;


public class StormStreamController implements OCSensorStreamController {

  @Override
  public void listenStream(OCSimulationController ocSimulationController, String socketStaticSensorIP, int socketStaticSensorPort, String socketDynamicSensorIP, int socketDynamicSensorPort, String socketTopoRadarIP, int socketTopoRadarPort, String socketTrafficLightIP, int socketTrafficLightPort) throws UnknownHostException, IOException {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void unlistenStream() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

//  TopologyBuilder builder = new TopologyBuilder();
//
//  builder.setSpout (
//
//  "words", new TestWordSpout(), 10);        
//  builder.setBolt (
//
//  "exclaim1", new ExclamationBolt(), 3)
//          .shuffleGrouping("words");
//  builder.setBolt (
//
//"exclaim2", new ExclamationBolt(), 2)
//          .shuffleGrouping("exclaim1");
}
