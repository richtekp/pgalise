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
package de.pgalise.simulation.traffic.internal.model.factory;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.entity.BusData;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.internal.model.factory.AbstractXMLVehicleFactory;
import de.pgalise.simulation.traffic.internal.model.vehicle.DefaultBus;
import de.pgalise.simulation.traffic.model.vehicle.Bus;
import de.pgalise.simulation.traffic.model.vehicle.BusFactory;
import de.pgalise.simulation.traffic.model.vehicle.xml.BusDataList;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Implements a factory for {@link Bus}. The vehicles are loaded by a XML file.
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Dec 24, 2012)
 */
/*
 use XML serialization framework (currently the usage of typeid as Identifiable.id is possibly not correct
 */
public class XMLBusFactory extends AbstractXMLVehicleFactory<BusData>
  implements BusFactory {

  public XMLBusFactory() {
  }

  /**
   * Constructor
   *
   * @param idGenerator
   * @param trafficGraphExtensions
   * @param stream Input stream of the XML data
   * @param randomSeedService Random Seed Service
   */
  public XMLBusFactory(IdGenerator idGenerator,
    TrafficGraphExtensions trafficGraphExtensions,
    RandomSeedService randomSeedService,
    InputStream stream) {
    super(trafficGraphExtensions,
      idGenerator,
      randomSeedService,
      stream);
  }

  public XMLBusFactory(IdGenerator idGenerator,
    TrafficGraphExtensions trafficGraphExtensions,
    RandomSeedService randomSeedService,
    Set<BusData> randomVehicleDataPool) {
    super(trafficGraphExtensions,
      idGenerator,
      randomSeedService,
      randomVehicleDataPool);
  }

  /**
   * Create new BicycleData
   * @return 
   */
  @Override
  public BusData getRandomVehicleData() {
    BusData referenceData = super.getRandomVehicleData();

    return new BusData(referenceData);
  }

  @Override
  public Bus createVehicle(Set<TrafficEdge> edges, Output output) {
    BusData data = getRandomVehicleData();
    return new DefaultBus(getIdGenerator().getNextId(),
      data,
      this.getTrafficGraphExtensions());
  }

  @Override
  public Bus createVehicle(Output output) {
    return createVehicle(null, output);
  }

  @Override
  protected Set<BusData> readVehicleData(InputStream doc) {

    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(BusDataList.class);

      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

      BusDataList retValue = (BusDataList) jaxbUnmarshaller.
        unmarshal(doc);
      return new HashSet<>(retValue.getList());

//    Map<VehicleType, BusData> vehicles = new HashMap<>();
//
//    // Get strategies node
//    NodeList nList = doc.getElementsByTagName("bus");
//
//    // Get all strategies
//    for (int i = 0; i < nList.getLength(); i++) {
//      /* Vehicle item */
//      Node vehicleItem = nList.item(i);
//
//      NodeList vehicleChildrens = vehicleItem.getChildNodes();
//
//      // Init variables
//      String typeidValue = "" + COUNTER, description = null;
//      int length = 0, axleCount = 0, wheelbase1 = 0, wheelbase2 = 0, height = 0, maxSpeed = 0, weight = 0, width = 0, maxPassengerCount = 0;
//      double power = 0;
//      VehicleType vehicleType = null;
//
//      // Get properties of the vehicle
//      for (int y = 0; y < vehicleChildrens.getLength(); y++) {
//        /* Property item */
//        Node propertyItem = vehicleChildrens.item(y);
//        String nodeName = propertyItem.getNodeName();
//
//        /* Check */
//        if (nodeName.equals("typeid")) {
//          typeidValue = propertyItem.getTextContent();
//          vehicleType = VehicleTypeEnum.getForVehicleTypeId(Integer.parseInt(
//            typeidValue));
//        } else if (nodeName.equals("wheelbase1")) {
//          wheelbase1 = Integer.parseInt(propertyItem.getTextContent());
//        } else if (nodeName.equals("wheelbase2")) {
//          wheelbase2 = Integer.parseInt(propertyItem.getTextContent());
//        } else if (nodeName.equals("length")) {
//          length = Integer.parseInt(propertyItem.getTextContent());
//        } else if (nodeName.equals("width")) {
//          width = Integer.parseInt(propertyItem.getTextContent());
//        } else if (nodeName.equals("height")) {
//          height = Integer.parseInt(propertyItem.getTextContent());
//        } else if (nodeName.equals("weight")) {
//          weight = Integer.parseInt(propertyItem.getTextContent());
//        } else if (nodeName.equals("power")) {
//          power = Double.parseDouble(propertyItem.getTextContent());
//        } else if (nodeName.equals("maxSpeed")) {
//          maxSpeed = Integer.parseInt(propertyItem.getTextContent());
//        } else if (nodeName.equals("axleCount")) {
//          axleCount = Integer.parseInt(propertyItem.getTextContent());
//        } else if (nodeName.equals("description")) {
//          description = propertyItem.getTextContent();
//        } else if (nodeName.equals("maxPassengerCount")) {
//          maxPassengerCount = Integer.parseInt(propertyItem.getTextContent());
//        }
//      }
//      if (vehicleType == null) {
//        throw new IllegalArgumentException(String.format(
//          "document contains illegal vehilce type id %s",
//          typeidValue));
//      }
//
//      // Add new vehicle
//      vehicles.put(vehicleType,
//        new BusData(wheelbase1,
//          wheelbase2,
//          length,
//          width,
//          height,
//          weight,
//          power,
//          maxSpeed,
//          description,
//          maxPassengerCount,
//          0,
//          axleCount,
//          null,
//          null));
//    }
//
//    // Returns
//    return vehicles;
    } catch (JAXBException ex) {
      throw new RuntimeException(ex);
    }
  }

}
