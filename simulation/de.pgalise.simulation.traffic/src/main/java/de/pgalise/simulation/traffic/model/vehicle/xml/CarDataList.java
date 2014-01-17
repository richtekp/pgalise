/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.model.vehicle.xml;

import de.pgalise.simulation.traffic.entity.CarData;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement(name = "cars")
@XmlAccessorType(XmlAccessType.FIELD)
public class CarDataList {

  @XmlElement(name = "car")
  private List<CarData> list;

  public CarDataList() {
  }

  public CarDataList(List<CarData> list) {
    this.list = list;
  }

  public void setList(List<CarData> list) {
    this.list = list;
  }

  public List<CarData> getList() {
    return list;
  }

}
