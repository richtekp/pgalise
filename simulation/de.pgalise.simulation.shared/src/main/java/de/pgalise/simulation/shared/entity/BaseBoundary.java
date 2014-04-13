/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.entity;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * This classes is used to share the code for boundary and centerPoint property
 * between City and Building (which would be in incompatible and pointless 
 * inheritance hierarchy)
 *
 * @author richter
 */
@Entity
public class BaseBoundary extends BaseCoordinate {

  private static final long serialVersionUID = 1L;

  //@TODO: re-enable hibernate-spatial as soon as hibernate spatial is compatible Java EE 7 and/or integrated in hibernate-orm 5.x
//	@Type(type="org.hibernate.spatial.GeometryType")
//	@Column(name = "geometry", columnDefinition="Geometry", nullable = true) 
  List<BaseCoordinate> boundaryCoordinates;
  @Transient
  private Polygon boundary;
  @Transient
  private JaxRSCoordinate centerPoint;

  protected BaseBoundary() {
  }
  
  public BaseBoundary(Long id) {
    super(id);
  }

  public BaseBoundary(Long id,
    Polygon boundary) {
    this(id);
    this.boundary = boundary;
  }

  /**
   * lazily calculates the geometrical center point based on the boundary
   * @return 
   */
  public JaxRSCoordinate retrieveCenterPoint() {
    if (centerPoint == null) {
      Point centroid = boundary.getCentroid();
      this.centerPoint = new JaxRSCoordinate(centroid.getX(),
        centroid.getY());
    }
    return this.centerPoint;
  }
  
  /**
   * lazily calculates the geometrical boundary based on the bondary 
   * coordinates
   * @return 
   */
  public Polygon retrieveBoundary() {
    if (boundary == null) {
      boundary = GeoToolsBootstrapping.getGEOMETRY_FACTORY().createPolygon(
        boundaryCoordinates.toArray(new Coordinate[boundaryCoordinates.size()]));
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
}
