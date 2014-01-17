/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.entity;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author richter
 */
public class TruckTrailerData {

  /**
   * Distance between preceeding tailer or truck in mm
   */
  private int trailerDistance;

  /**
   * Length of each trailer. Assumption every trailer has the same length.
   */
  private int trailerLength;

  /**
   * wheelbases between the preceeding trailer or the truck and wheels of the
   * trailer
   */
  private List<Integer> wheelbases = new LinkedList<>();

  public TruckTrailerData() {
  }

  public TruckTrailerData(int trailerDistance,
    int trailerLength) {
    this.trailerDistance = trailerDistance;
    this.trailerLength = trailerLength;
  }
}
