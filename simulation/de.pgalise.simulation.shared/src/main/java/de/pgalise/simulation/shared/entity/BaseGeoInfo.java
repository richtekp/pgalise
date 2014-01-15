/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.entity;

import de.pgalise.simulation.shared.JaxRSCoordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * This classes is used to share the code for boundary and centerPoint property
 * between City and Building (which are in incompatible inheritance hierarchy)
 *
 * @author richter
 */
@Entity
public class BaseGeoInfo extends Identifiable {

  private static final long serialVersionUID = 1L;

  //@TODO: re-enable hibernate-spatial as soon as hibernate spatial is compatible Java EE 7 and/or integrated in hibernate-orm 5.x
//	@Type(type="org.hibernate.spatial.GeometryType")
//	@Column(name = "geometry", columnDefinition="Geometry", nullable = true) 
  private Polygon boundaries;
  @Transient
  private JaxRSCoordinate centerPoint;

  protected BaseGeoInfo() {
  }

  public BaseGeoInfo(Long id,
    Polygon boundaries) {
    super(id);
    this.boundaries = boundaries;
  }

  public JaxRSCoordinate getCenterPoint() {
    if (centerPoint == null) {
      Point centroid = boundaries.getCentroid();
      this.centerPoint = new JaxRSCoordinate(centroid.getX(),
        centroid.getY());
    }
    return this.centerPoint;
  }

  public Polygon getBoundaries() {
    return boundaries;
  }

  public void setBoundaries(Polygon boundaries) {
    this.boundaries = boundaries;
  }
}
