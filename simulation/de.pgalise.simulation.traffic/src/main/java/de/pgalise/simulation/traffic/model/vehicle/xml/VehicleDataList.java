/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package de.pgalise.simulation.traffic.model.vehicle.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement(name = "vehicles")
@XmlAccessorType(XmlAccessType.FIELD)
public class VehicleDataList {

  @XmlElement(name = "cars")
  private CarDataList carData;
  @XmlElement(name = "buses")
  private BusDataList busData;
  @XmlElement(name = "bicycles")
  private BicycleDataList bicycleData;
  @XmlElement(name = "motorcycles")
  private MotorcycleDataList motorcycleData;
  @XmlElement(name = "trucks")
  private TruckDataList truckData;

  public VehicleDataList() {
  }

  public VehicleDataList(CarDataList carData,
    BusDataList busData,
    BicycleDataList bicycleData,
    MotorcycleDataList motorcycleData,
    TruckDataList truckData) {
    this.carData = carData;
    this.busData = busData;
    this.bicycleData = bicycleData;
    this.motorcycleData = motorcycleData;
    this.truckData = truckData;
  }

  /**
   * @return the carData
   */
  public CarDataList getCarData() {
    return carData;
  }

  /**
   * @param carData the carData to set
   */
  public void setCarData(
    CarDataList carData) {
    this.carData = carData;
  }

  /**
   * @return the busData
   */
  public BusDataList getBusData() {
    return busData;
  }

  /**
   * @param busData the busData to set
   */
  public void setBusData(
    BusDataList busData) {
    this.busData = busData;
  }

  /**
   * @return the bicycleData
   */
  public BicycleDataList getBicycleData() {
    return bicycleData;
  }

  /**
   * @param bicycleData the bicycleData to set
   */
  public void setBicycleData(
    BicycleDataList bicycleData) {
    this.bicycleData = bicycleData;
  }

  /**
   * @return the motorcycleData
   */
  public MotorcycleDataList getMotorcycleData() {
    return motorcycleData;
  }

  /**
   * @param motorcycleData the motorcycleData to set
   */
  public void setMotorcycleData(
    MotorcycleDataList motorcycleData) {
    this.motorcycleData = motorcycleData;
  }

  /**
   * @return the truckData
   */
  public TruckDataList getTruckData() {
    return truckData;
  }

  /**
   * @param truckData the truckData to set
   */
  public void setTruckData(
    TruckDataList truckData) {
    this.truckData = truckData;
  }

}
