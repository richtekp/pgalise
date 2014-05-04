/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.shared.entity;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.List;
import javax.persistence.Entity;

/**
 * 
 * @author richter
 */
@Entity
public class Area extends BaseBoundary {
  private static final long serialVersionUID = 1L;

  public Area() {
  }
  
  public Area(Long id) {
    super(id);
  }
  
  public Area(Long id, BaseCoordinate referencePoint, BasePolygon boundaryCoordinates) {
    super(id, referencePoint,
      boundaryCoordinates);
  }
  
}
