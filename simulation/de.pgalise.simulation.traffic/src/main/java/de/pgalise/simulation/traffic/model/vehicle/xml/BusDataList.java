/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.model.vehicle.xml;

import de.pgalise.simulation.traffic.entity.BusData;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement(name = "buses")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusDataList {

  @XmlElement(name = "bus")
  private List<BusData> list;

  public BusDataList() {
  }

  public BusDataList(List<BusData> list) {
    this.list = list;
  }

  public void setList(List<BusData> list) {
    this.list = list;
  }

  public List<BusData> getList() {
    return list;
  }
}
