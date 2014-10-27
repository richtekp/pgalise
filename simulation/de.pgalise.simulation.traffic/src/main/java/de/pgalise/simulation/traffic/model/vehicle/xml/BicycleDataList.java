/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.model.vehicle.xml;

import de.pgalise.simulation.traffic.entity.BicycleData;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement(name = "bicycles")
@XmlAccessorType(XmlAccessType.FIELD)
public class BicycleDataList {

  @XmlElement(name = "bicycle")
  private List<BicycleData> list;

  public BicycleDataList() {
  }

  public BicycleDataList(List<BicycleData> list) {
    this.list = list;
  }

  public List<BicycleData> getList() {
    return list;
  }

  public void setList(List<BicycleData> list) {
    this.list = list;
  }
}
