/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;

/**
 * This classes is used to share the code for boundary and centerPoint property between City and Building (which are in incompatible inheritance hierarchy)
 * @author richter
 */
@Entity
public class BaseGeoInfo extends AbstractIdentifiable {
	private static final long serialVersionUID = 1L;
	@Type(type="org.hibernate.spatial.GeometryType")
	@Column(name = "geometry", columnDefinition="Geometry", nullable = true) 
	private Polygon boundaries;
	@Transient
	private JaxRSCoordinate centerPoint;

	protected BaseGeoInfo() {
	}

	public BaseGeoInfo(Polygon boundaries) {
		super();
		this.boundaries = boundaries;
	}

	protected BaseGeoInfo(Polygon boundaries,
		Long id) {
		super(id);
		this.boundaries = boundaries;
	}

	public JaxRSCoordinate getCenterPoint() {
		if(centerPoint == null) {
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
