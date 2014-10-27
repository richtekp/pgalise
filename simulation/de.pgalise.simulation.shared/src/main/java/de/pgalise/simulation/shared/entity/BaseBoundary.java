/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.entity;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * A BaseBoundary is a List of coordinates determinating the boundary of a 
 * certain area (might be a building or even a city) and a refernce point 
 * which might be different from the geometrical center point of the boundary 
 * geometry.
 * 
 * The class encapsulates methods to lazily calculate a {@link Polygon} 
 * of the <tt>boundaryCoordinates</tt> property and to lazily calculate the 
 * geometrical center point of this boundary.
 * 
 * This classes is used to share the code for boundary and centerPoint property
 * between City and Building (which would be in incompatible and pointless 
 * inheritance hierarchy)
 *
 * @author richter
 */
/*
internal implementation notes:
- BaseBoundary has a business id because two different BaseBoundaries with the 
same reference point (denoted by x and y) might have differet boundary
- saving boundary coordinates as a list doesn't make sense because functionality 
of polygon is required to validate the form of the coordinates (overlapping 
of coordinates and connections between coordinates)
*/
@Entity
public class BaseBoundary extends Identifiable {

  private static final long serialVersionUID = 1L;
  @ManyToOne
  private BaseCoordinate referencePoint;
  @Embedded
  private BasePolygon boundary;
  /*
  internal implementation notes:
  - see retrieveCenterPoint
  */
  @Transient
  private BaseCoordinate centerPoint;

  protected BaseBoundary() {
  }
  
  public BaseBoundary(Long id) {
    super(id);
  }

  /**
   * 
   * @param id
   * @param referencePoint
   * @param boundaryCoordinates expects the last coordinate and the first to be 
   * identical in order to be conform with {@link Polygon} specification (makes a lot of things easier)
   */
  public BaseBoundary(Long id,
    BaseCoordinate referencePoint, 
    BasePolygon boundaryCoordinates ) {
    super(id);
    this.referencePoint = referencePoint;
    this.boundary = boundaryCoordinates;
  }

  /**
   * lazily calculates the geometrical center point based on the boundary
   * @return 
   */
  /*
  internal implementation notes:
  - return a BaseCoordinate to avoid some code changes, but it is not 
  necessary (can be a Coordinate as well)
  */
  public BaseCoordinate retrieveCenterPoint() {
    if (centerPoint == null) {
      Point centroid = boundary.getCentroid();
      this.centerPoint = new BaseCoordinate(centroid.getX(),
        centroid.getY());
    }
    return this.centerPoint;
  }

  public void setBoundary(BasePolygon boundary) {
    this.boundary = boundary;
  }

  public BasePolygon getBoundary() {
    return boundary;
  }

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 17 * hash + Objects.hashCode(boundary);
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
		final BaseBoundary other = (BaseBoundary) obj;
		if (!Objects.equals(this.boundary,
			other.getBoundary())) {
			return false;
		}
		return true;
	}

  public void setReferencePoint(BaseCoordinate referencePoint) {
    this.referencePoint = referencePoint;
  }

  public BaseCoordinate getReferencePoint() {
    return referencePoint;
  }
}
