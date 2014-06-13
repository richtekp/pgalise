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
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.entity.CarData;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.internal.model.vehicle.DefaultCar;
import de.pgalise.simulation.traffic.model.vehicle.Car;
import de.pgalise.simulation.traffic.model.vehicle.CarFactory;
import de.pgalise.simulation.traffic.model.vehicle.xml.CarDataList;
import java.awt.Color;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


public class XMLCarFactory extends AbstractXMLVehicleFactory<CarData> implements
  CarFactory {
  private static final long serialVersionUID = 1L;

  @Override
  public Car createVehicle(
    Output output) {
    return createVehicle(null,
      output);
  }

  @Override
  public Car createVehicle(Set<TrafficEdge> edges,
    Output output) {
    CarData data = getRandomVehicleData();
    BaseCoordinate position = null;
    if (edges != null) {
      position = generateRandomPosition(edges);
    }
    return new DefaultCar(getIdGenerator().getNextId(),
      data,
      getTrafficGraphExtensions(),
      position);
  }

  public XMLCarFactory() {
  }

  /**
   * Constructor
   *
   * @param idGenerator
   * @param randomSeedService Random Seed Service
   * @param stream Input stream of the XML data
   * @param trafficGraphExtensions
   */
  public XMLCarFactory(IdGenerator idGenerator,
    TrafficGraphExtensions trafficGraphExtensions,
    RandomSeedService randomSeedService,
    InputStream stream) {
    super(trafficGraphExtensions,
      idGenerator,
      randomSeedService,
      stream);
  }

  public XMLCarFactory(IdGenerator idGenerator,
    TrafficGraphExtensions trafficGraphExtensions,
    RandomSeedService randomSeedService,
    Set<CarData> randomVehicleDataPool) {
    super(trafficGraphExtensions,
      idGenerator,
      randomSeedService,
      randomVehicleDataPool);
  }

  /**
   * Updates the {@link CarData} with new information
   *
   * @param data loaded {@link CarData}
   * @param color
   * @return updated {@link CarData} object
   */
  private CarData updateCarData(CarData data,
    Color color) {
    data.setColor(color);

    return data;
  }

  /**
   * Create new CarData
   *
   * @return
   */
  @Override
  public CarData getRandomVehicleData() {
    CarData referenceData = super.getRandomVehicleData();
    return new CarData(referenceData);
  }

  @Override
  protected Set<CarData> readVehicleData(InputStream doc) {

    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(CarDataList.class);

      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

      CarDataList carDataList = (CarDataList) jaxbUnmarshaller.
        unmarshal(doc);
      return new HashSet<>(carDataList.getList());

//    Map<String, CarData> vehicles = new HashMap<>();
//
//    // Get strategies node
//    NodeList nList = doc.getElementsByTagName("car");
//
//    // Get all strategies
//    for (int i = 0; i < nList.getLength(); i++) {
//      /* Vehicle item */
//      Node vehicleItem = nList.item(i);
//
//      NodeList vehicleChildrens = vehicleItem.getChildNodes();
//
//      // Init variables
//      Color color = Color.WHITE;
//      String typeid = "" + COUNTER, description = null;
//      int axleCount = 0, wheelbase1 = 0, wheelbase2 = 0, height = 0, maxSpeed = 0, weight = 0, wheelDistanceWidth = 0, width = 0, length = 0;
//      double power = 0;
//
//      // Get properties of the vehicle
//      for (int y = 0; y < vehicleChildrens.getLength(); y++) {
//        /* Property item */
//        Node propertyItem = vehicleChildrens.item(y);
//        String nodeName = propertyItem.getNodeName();
//
//        /* Check */
//        if (nodeName.equals("typeid")) {
//          typeid = propertyItem.getTextContent();
//        } else if (nodeName.equals("wheelDistanceWidth")) {
//          wheelDistanceWidth = Integer.parseInt(propertyItem.getTextContent());
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
//        }
//      }
//
//      // Add new vehicle
//      vehicles.put(typeid,
//        new CarData(color,
//          wheelDistanceWidth,
//          wheelbase1,
//          wheelbase2,
//          length,
//          width,
//          height,
//          weight,
//          power,
//          maxSpeed,
//          axleCount,
//          description,
//          null,
//          VehicleTypeEnum.CAR));
//    }
//
//    // Returns
//    return vehicles;
    } catch (JAXBException ex) {
      throw new RuntimeException(ex);
    }
  }

}
