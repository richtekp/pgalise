/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.model.vehicle.xml;

import java.awt.Color;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author richter
 */
public class ColorAdapter extends XmlAdapter<String, Color> {

  public ColorAdapter() {
  }

  @Override
  public Color unmarshal(String s) {
    return Color.decode(s);
  }

  @Override
  public String marshal(Color c) {
    return "#" + Integer.toHexString(c.getRGB());
  }

}
