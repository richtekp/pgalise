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
import de.pgalise.simulation.traffic.internal.model.factory.AbstractXMLVehicleFactory;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.RandomSeedService;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.model.vehicle.Bicycle;
import de.pgalise.simulation.traffic.entity.BicycleData;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.internal.model.vehicle.DefaultBicycle;
import de.pgalise.simulation.traffic.model.vehicle.BicycleFactory;
import de.pgalise.simulation.traffic.model.vehicle.xml.BicycleDataList;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


public class XMLBicycleFactory extends AbstractXMLVehicleFactory<BicycleData>
  implements BicycleFactory {
  private static final long serialVersionUID = 1L;

  public XMLBicycleFactory() {
  }

  /**
   * Constructor
   *
   * @param idGenerator
   * @param randomSeedService Random Seed Service
   * @param trafficGraphExtensions
   * @param stream
   */
  public XMLBicycleFactory(IdGenerator idGenerator,
    TrafficGraphExtensions trafficGraphExtensions,
    RandomSeedService randomSeedService,
    InputStream stream) {
    super(trafficGraphExtensions,
      idGenerator,
      randomSeedService,
      stream);
  }

  public XMLBicycleFactory(IdGenerator idGenerator,
    TrafficGraphExtensions trafficGraphExtensions,
    RandomSeedService randomSeedService,
    Set<BicycleData> randomVehicleDataPool) {
    super(trafficGraphExtensions,
      idGenerator,
      randomSeedService,
      randomVehicleDataPool);
  }

  @Override
  public Bicycle createVehicle(Output output) {
    BicycleData data = getRandomVehicleData();
    return new DefaultBicycle(getIdGenerator().getNextId(),
      data,
      this.getTrafficGraphExtensions());
  }

  /**
   * Create new BicycleData
   *
   * @return
   */
  @Override
  public BicycleData getRandomVehicleData() {
    BicycleData referenceData = super.getRandomVehicleData();

    return new BicycleData(referenceData);
  }

  @Override
  protected Set<BicycleData> readVehicleData(InputStream inputStream) {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(BicycleDataList.class);

      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

      BicycleDataList retValue = (BicycleDataList) jaxbUnmarshaller.
        unmarshal(inputStream);
      return new HashSet<>(retValue.getList());

//    Map<VehicleType, BicycleData> vehicles = new HashMap<>();
//
//    // Get strategies node
//    NodeList nList = doc.getElementsByTagName("bicycle");
//
//    // Get all strategies
//    for (int i = 0; i < nList.getLength(); i++) {
//      /* Vehicle item */
//      Node vehicleItem = nList.item(i);
//
//      NodeList vehicleChildrens = vehicleItem.getChildNodes();
//
//      // Init variables
//      VehicleType typeid = null;
//      String material = null, description = null;
//      int size = 0, wheelbase = 0, maxSpeed = 0;
//      double weight = 0;
//      String typeidValue = null;
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
//          typeid = VehicleTypeEnum.getForVehicleTypeId(Integer.valueOf(
//            typeidValue));
//        } else if (nodeName.equals("description")) {
//          description = propertyItem.getTextContent();
//        } else if (nodeName.equals("wheelbase")) {
//          wheelbase = Integer.parseInt(propertyItem.getTextContent());
//        } else if (nodeName.equals("size")) {
//          size = Integer.parseInt(propertyItem.getTextContent());
//        } else if (nodeName.equals("weight")) {
//          weight = Double.parseDouble(propertyItem.getTextContent());
//        } else if (nodeName.equals("maxSpeed")) {
//          maxSpeed = Integer.parseInt(propertyItem.getTextContent());
//        } else if (nodeName.equals("material")) {
//          material = propertyItem.getTextContent();
//        }
//      }
//
//      // Add new vehicle
//      vehicles.put(typeid,
//        new BicycleData(weight,
//          maxSpeed,
//          material,
//          size,
//          wheelbase,
//          description,
//          null));
//    }
//
//    // Returns
//    return vehicles;
    } catch (JAXBException ex) {
      throw new RuntimeException(ex);
    }
  }

  @Override
  public Bicycle createVehicle(Set<TrafficEdge> edges,
    Output output) {
    Bicycle retValue = createVehicle(output);
    if (edges != null) {
      BaseCoordinate position = generateRandomPosition(edges);
      retValue.setPosition(position);
    }
    return retValue;
  }

}
