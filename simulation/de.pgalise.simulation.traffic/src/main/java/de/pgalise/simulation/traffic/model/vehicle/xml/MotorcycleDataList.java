/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.model.vehicle.xml;

import de.pgalise.simulation.traffic.entity.MotorcycleData;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement(name = "motorcycles")
@XmlAccessorType(XmlAccessType.FIELD)
public class MotorcycleDataList {

  @XmlElement(name = "motorcycle")
  private List<MotorcycleData> list;

  public MotorcycleDataList() {
  }

  public MotorcycleDataList(List<MotorcycleData> list) {
    this.list = list;
  }

  public void setList(List<MotorcycleData> list) {
    this.list = list;
  }

  public List<MotorcycleData> getList() {
    return list;
  }

}
