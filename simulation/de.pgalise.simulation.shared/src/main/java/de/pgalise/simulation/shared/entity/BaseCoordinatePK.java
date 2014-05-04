/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.shared.entity;

import java.io.Serializable;
import javax.persistence.IdClass;

/**
 * the primary key class for {@link IdClass} of {@link BaseCoordinate}
 * @author richter
 */
public class BaseCoordinatePK implements Serializable {
  private static final long serialVersionUID = 1L;
  private double x;
  private double y;

  BaseCoordinatePK() {
  }

  public BaseCoordinatePK(double x,
    double y) {
    this.x = x;
    this.y = y;
  }

  public void setY(double y) {
    this.y = y;
  }

  public double getY() {
    return y;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getX() {
    return x;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
    hash = 97 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final BaseCoordinatePK other = (BaseCoordinatePK) obj;
    if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
      return false;
    }
    if (Double.doubleToLongBits(this.y) != Double.doubleToLongBits(other.y)) {
      return false;
    }
    return true;
  }
  
  public static BaseCoordinatePK create(BaseCoordinate from) {
    return new BaseCoordinatePK(from.getX(), from.getY());
  }
}
