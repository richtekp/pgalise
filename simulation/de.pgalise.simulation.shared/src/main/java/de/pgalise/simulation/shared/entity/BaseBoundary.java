/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.entity;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
BaseBoundary has a business id because two different BaseBoundaries with the 
same reference point (denoted by x and y) might have differet boundary
*/
@Entity
public class BaseBoundary extends Identifiable {

  private static final long serialVersionUID = 1L;
  @ManyToOne
  private BaseCoordinate referencePoint;

  //@TODO: re-enable hibernate-spatial as soon as hibernate spatial is compatible Java EE 7 and/or integrated in hibernate-orm 5.x
//	@Type(type="org.hibernate.spatial.GeometryType")
//	@Column(name = "geometry", columnDefinition="Geometry", nullable = true) 
  @OneToMany
  private List<BaseCoordinate> boundaryCoordinates = new LinkedList<>();
  @Transient
  private Polygon boundary;
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
    List<BaseCoordinate> boundaryCoordinates ) {
    super(id);
    this.referencePoint = referencePoint;
    this.boundaryCoordinates = boundaryCoordinates;
    if(!boundaryCoordinates.get(0).equals(boundaryCoordinates.get(boundaryCoordinates.size()-1))) {
      throw new IllegalArgumentException("boundaryCoordinates don't form a closed polygon");
    }
  }

  /**
   * lazily calculates the geometrical center point based on the boundary
   * @return 
   */
  public BaseCoordinate retrieveCenterPoint() {
    if (centerPoint == null) {
      Point centroid = retrieveBoundary().getCentroid();
      this.centerPoint = new BaseCoordinate(centroid.getX(),
        centroid.getY());
    }
    return this.centerPoint;
  }
  
  /**
   * lazily calculates the geometrical boundary based on the bondary 
   * coordinates. 
   * @return 
   */
  public Polygon retrieveBoundary() {
    if (boundary == null) {
      boundary = GeoToolsBootstrapping.getGeometryFactory().createPolygon(
        //boundaryCoordinatesArray
        boundaryCoordinates.toArray(new Coordinate[boundaryCoordinates.size()])
      );
    }
    return this.boundary;
  }

  public void setBoundaryCoordinates(List<BaseCoordinate> boundaryCoordinates) {
    this.boundaryCoordinates = boundaryCoordinates;
  }

  public List<BaseCoordinate> getBoundaryCoordinates() {
    return boundaryCoordinates;
  }

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 17 * hash + Objects.hashCode(boundaryCoordinates);
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
		if (!Objects.equals(this.boundaryCoordinates,
			other.getBoundaryCoordinates())) {
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
