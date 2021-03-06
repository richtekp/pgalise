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
import de.pgalise.simulation.traffic.TrafficSensorFactory;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TruckData;
import de.pgalise.simulation.traffic.internal.model.factory.AbstractXMLVehicleFactory;
import de.pgalise.simulation.traffic.internal.model.vehicle.DefaultTruck;
import de.pgalise.simulation.traffic.model.vehicle.Truck;
import de.pgalise.simulation.traffic.model.vehicle.TruckFactory;
import de.pgalise.simulation.traffic.model.vehicle.xml.TruckDataList;
import java.awt.Color;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import javax.ejb.EJB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


public class XMLTruckFactory extends AbstractXMLVehicleFactory<TruckData>
  implements TruckFactory {
  private static final long serialVersionUID = 1L;

  @EJB
  private TrafficSensorFactory sensorFactory;

  public XMLTruckFactory() {
  }

  /**
   * Constructor
   *
   * @param idGenerator
   * @param randomSeedService Random Seed Service
   * @param trafficGraphExtensions
   * @param stream
   * @param sensorFactory
   */
  public XMLTruckFactory(IdGenerator idGenerator,
    TrafficGraphExtensions trafficGraphExtensions,
    RandomSeedService randomSeedService,
    InputStream stream,
    TrafficSensorFactory sensorFactory) {
    super(trafficGraphExtensions,
      idGenerator,
      randomSeedService,
      stream);
    this.sensorFactory = sensorFactory;
  }

  public XMLTruckFactory(IdGenerator idGenerator,
    TrafficGraphExtensions trafficGraphExtensions,
    RandomSeedService randomSeedService,
    Set<TruckData> randomVehicleDataPool,
    TrafficSensorFactory sensorFactory) {
    super(trafficGraphExtensions,
      idGenerator,
      randomSeedService,
      randomVehicleDataPool);
    this.sensorFactory = sensorFactory;
  }
  
  public Truck createVehicle(Output output) {
    return createVehicle(null, output);
  }

  @Override
  public Truck createVehicle(Set<TrafficEdge> edges, Output output) {
    TruckData data = getRandomVehicleData();
    return new DefaultTruck(getIdGenerator().getNextId(),
      data,
      getTrafficGraphExtensions());
  }

  @Override
  public Truck createTruck(Color color,
    int trailercount,
    Output output
  ) {
    Truck retValue = createVehicle(output);
    retValue.getData().setColor(color);
    retValue.setGpsSensor(sensorFactory.createGpsSensor(false,
      output));
    return retValue;
  }

  /**
   * Create new TruckData
   */
  @Override
  public TruckData getRandomVehicleData() {
    TruckData referenceData = super.getRandomVehicleData();

    return new TruckData(referenceData);
  }

  @Override
  protected Set<TruckData> readVehicleData(InputStream doc) {

    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(TruckDataList.class);

      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

      TruckDataList retValue = (TruckDataList) jaxbUnmarshaller.
        unmarshal(doc);
      return new HashSet<>(retValue.getList());

//		Map<String, TruckData> vehicles = new HashMap<>();
//
//		// Get strategies node
//		NodeList nList = doc.getElementsByTagName("truck");
//
//		// Get all strategies
//		for (int i = 0; i < nList.getLength(); i++) {
//			/* Vehicle item */
//			Node vehicleItem = nList.item(i);
//
//			NodeList vehicleChildrens = vehicleItem.getChildNodes();
//
//			// Init variables
//			Color color = Color.WHITE;
//			String typeid = "" + COUNTER, description = null;
//			int length = 0, axleCount = 0, wheelbase1 = 0, wheelbase2 = 0, height = 0, maxSpeed = 0, weight = 0, wheelDistanceWidth = 0, width = 0, trailerCount = 0, trailerDistance = 0, trailerLength = 0;
//			double power = 0;
//
//			// Get properties of the vehicle
//			for (int y = 0; y < vehicleChildrens.getLength(); y++) {
//				/* Property item */
//				Node propertyItem = vehicleChildrens.item(y);
//				String nodeName = propertyItem.getNodeName();
//
//				/* Check */
//				if (nodeName.equals("typeid")) {
//					typeid = propertyItem.getTextContent();
//				} else if (nodeName.equals("wheelDistanceWidth")) {
//					wheelDistanceWidth = Integer.parseInt(propertyItem.getTextContent());
//				} else if (nodeName.equals("wheelbase1")) {
//					wheelbase1 = Integer.parseInt(propertyItem.getTextContent());
//				} else if (nodeName.equals("wheelbase2")) {
//					wheelbase2 = Integer.parseInt(propertyItem.getTextContent());
//				} else if (nodeName.equals("length")) {
//					length = Integer.parseInt(propertyItem.getTextContent());
//				} else if (nodeName.equals("width")) {
//					width = Integer.parseInt(propertyItem.getTextContent());
//				} else if (nodeName.equals("height")) {
//					height = Integer.parseInt(propertyItem.getTextContent());
//				} else if (nodeName.equals("weight")) {
//					weight = Integer.parseInt(propertyItem.getTextContent());
//				} else if (nodeName.equals("power")) {
//					power = Double.parseDouble(propertyItem.getTextContent());
//				} else if (nodeName.equals("maxSpeed")) {
//					maxSpeed = Integer.parseInt(propertyItem.getTextContent());
//				} else if (nodeName.equals("axleCount")) {
//					axleCount = Integer.parseInt(propertyItem.getTextContent());
//				} else if (nodeName.equals("description")) {
//					description = propertyItem.getTextContent();
//				} else if (nodeName.equals("trailerCount")) {
//					trailerCount = Integer.parseInt(propertyItem.getTextContent());
//				} else if (nodeName.equals("trailerDistance")) {
//					trailerDistance = Integer.parseInt(propertyItem.getTextContent());
//				} else if (nodeName.equals("trailerLength")) {
//					trailerLength = Integer.parseInt(propertyItem.getTextContent());
//				}
//			}
//
//			// Add new vehicle
//			vehicles.put(typeid,
//				new TruckData(color,
//					wheelDistanceWidth,
//					wheelbase1,
//					wheelbase2,
//					length,
//					width,
//					height,
//					weight,
//					power,
//					maxSpeed,
//					axleCount,
//					description,
//					trailerCount,
//					trailerDistance,
//					trailerLength,
//					null));
//		}
//
//		// Returns
//		return vehicles;
    } catch (JAXBException ex) {
      throw new RuntimeException(ex);
    }
  }
}
