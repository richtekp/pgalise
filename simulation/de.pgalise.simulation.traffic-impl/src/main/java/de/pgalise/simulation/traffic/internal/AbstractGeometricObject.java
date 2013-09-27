/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;

/**
 *
 * @author richter
 */
@MappedSuperclass
public abstract class AbstractGeometricObject extends AbstractIdentifiable {
	@Type(type="org.hibernate.spatial.GeometryType")
	@Column(name = "geometry", columnDefinition="Geometry", nullable = true) 
	private Polygon boundaries;
	@Transient
	private Coordinate centerPoint;
	/**
	 * a point which is considered the most important in the geometry which is not forcibly always the geographical center of the referenced area
	 */
	private Coordinate referencePoint;

	protected AbstractGeometricObject() {
	}

	public AbstractGeometricObject(Polygon boundaries) {
		super();
		this.boundaries = boundaries;
	}

	protected AbstractGeometricObject(Polygon boundaries,
		Long id) {
		super(id);
		this.boundaries = boundaries;
	}

	public AbstractGeometricObject(Polygon boundaries,
		Coordinate referencePoint) {
		super();
		this.boundaries = boundaries;
		this.referencePoint = referencePoint;
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

	public void setReferencePoint(Coordinate referencePoint) {
		this.referencePoint = referencePoint;
	}

	public Coordinate getReferencePoint() {
		return referencePoint;
	}
}
