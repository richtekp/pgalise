/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.shared.entity;

import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class OSMArea extends Area {
  private static final long serialVersionUID = 1L;
  private String osmId;

  public OSMArea() {
  }
  public OSMArea(Long id, String osmId) {
    super(id);
    this.osmId = osmId;
  }

  public void setOsmId(String osmId) {
    this.osmId = osmId;
  }

  public String getOsmId() {
    return osmId;
  }
  
}
