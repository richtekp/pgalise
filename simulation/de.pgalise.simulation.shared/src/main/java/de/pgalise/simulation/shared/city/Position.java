/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import com.vividsolutions.jts.geom.Coordinate;
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
public class Position extends AbstractIdentifiable {
	@Type(type="org.hibernate.spatial.GeometryType")
	@Column(name = "geometry", columnDefinition="Geometry", nullable = true) 
	private Polygon boundaries;
	@Transient
	private Coordinate centerPoint;

	protected Position() {
	}

	public Position(Polygon boundaries) {
		super();
		this.boundaries = boundaries;
	}

	protected Position(Polygon boundaries,
		Long id) {
		super(id);
		this.boundaries = boundaries;
	}

	public Coordinate getCenterPoint() {
		if(centerPoint == null) {
			Point centroid = boundaries.getCentroid();
			this.centerPoint = new Coordinate(centroid.getX(),
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
