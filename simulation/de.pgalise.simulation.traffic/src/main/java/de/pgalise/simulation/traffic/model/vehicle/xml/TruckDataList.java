/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.model.vehicle.xml;

import de.pgalise.simulation.traffic.entity.TruckData;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement(name = "trucks")
@XmlAccessorType(XmlAccessType.FIELD)
public class TruckDataList {

  @XmlElement(name = "truck")
  private List<TruckData> list;

  public TruckDataList() {
  }

  public TruckDataList(List<TruckData> list) {
    this.list = list;
  }

  public void setList(List<TruckData> list) {
    this.list = list;
  }

  public List<TruckData> getList() {
    return list;
  }

}
