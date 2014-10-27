/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.entity;

import de.pgalise.simulation.shared.entity.Identifiable;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
//@TODO: make this an @Embeddable as soon as hibernate bug HHH-8860
//https://hibernate.atlassian.net/browse/HHH-8860 is fixed
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TruckTrailerData
  extends Identifiable //implements Serializable 
{

  private static final long serialVersionUID = 1L;

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
  @ElementCollection
  private List<Integer> wheelbases = new LinkedList<>();

  public TruckTrailerData() {
  }

//  public TruckTrailerData(int trailerDistance,
//    int trailerLength,
//    List<Integer> wheelbases) {
//    this.trailerDistance = trailerDistance;
//    this.trailerLength = trailerLength;
//    this.wheelbases = wheelbases;
//  }
  public TruckTrailerData(int trailerDistance,
    int trailerLength,
    List<Integer> wheelbases,
    Long id) {
    super(id);
    this.trailerDistance = trailerDistance;
    this.trailerLength = trailerLength;
    this.wheelbases = wheelbases;
  }

  /**
   * @return the trailerDistance
   */
  public int getTrailerDistance() {
    return trailerDistance;
  }

  /**
   * @param trailerDistance the trailerDistance to set
   */
  public void setTrailerDistance(int trailerDistance) {
    this.trailerDistance = trailerDistance;
  }

  /**
   * @return the trailerLength
   */
  public int getTrailerLength() {
    return trailerLength;
  }

  /**
   * @param trailerLength the trailerLength to set
   */
  public void setTrailerLength(int trailerLength) {
    this.trailerLength = trailerLength;
  }

  /**
   * @return the wheelbases
   */
  public List<Integer> getWheelbases() {
    return wheelbases;
  }

  /**
   * @param wheelbases the wheelbases to set
   */
  public void setWheelbases(List<Integer> wheelbases) {
    this.wheelbases = wheelbases;
  }
}
