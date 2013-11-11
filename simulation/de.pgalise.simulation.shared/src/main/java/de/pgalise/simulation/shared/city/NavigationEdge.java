/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import de.pgalise.simulation.shared.city.NavigationEdge;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import javax.vecmath.Vector2d;

/**
 *
 * @param <N> 
 * @param <E> 
 * @author richter
 */
public class NavigationEdge<N extends NavigationNode> extends AbstractIdentifiable {
	private static final long serialVersionUID = 1L;

	@Transient
	private LineString edgeLine;
	@Transient
	private Double edgeLineLength;
	@Transient
	private Double lineAzimuth;
	private long updateTimestamp;
	@ManyToOne
	private N source;
	@ManyToOne
	private N target;
	private Vector2d vector = null;
	private boolean oneWay = false;
	
	/**
	 * <tt>source</tt> and <tt>target</tt> are set using {@link Graph#addEdge(java.lang.Object, java.lang.Object) } or {@link Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object) }. You should invoke {@link #validateNavigationNodeDistance() } after a call to {@link Graph#addEdge(java.lang.Object, java.lang.Object) } or {@link Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object) }!
	 * @throws IllegalArgumentException if the distance between <tt>node0</tt> and <tt>node1</tt> is less than or equals {@link NavigationNode#NODE_RADIUS}
	 */
	public NavigationEdge() {
		super();
	}
	
	public NavigationEdge(N source, N target) {
		this();
		this.source = source;
		this.target = target;
	}
	
	/**
	 * generates edge line lazily in order to allow usage of this class in an {@link EdgeFactory}
	 * @return 
	 */
	public LineString getEdgeLine() {
		if(edgeLine == null) {
			this.edgeLine = GeoToolsBootstrapping.getGEOMETRY_FACTORY().createLineString(
				new Coordinate[] {
				getSource().getGeoLocation(), getTarget().getGeoLocation()
				}
			);
		}
		return edgeLine;
	}

	public double getEdgeLength() {
		if(edgeLineLength == null) {
			double distance = GeoToolsBootstrapping.distanceHaversineInM(source.getGeoLocation(), target.getGeoLocation());
			edgeLineLength = distance;
		}
		return edgeLineLength;
	}

	public void invalidateLength() {
		this.edgeLineLength = null;
	}

	public double getLineAzimuth() {
		if(lineAzimuth == null) {
			Vector2d edgeVector = new Vector2d(source.getGeoLocation().x, source.getGeoLocation().y);
			edgeVector.sub(new Vector2d(target.getGeoLocation().x, target.getGeoLocation().y));
			Vector2d northVector = new Vector2d(0,1);
			lineAzimuth = edgeVector.angle(northVector)*180/Math.PI;
		}
		return lineAzimuth;
	}

	public void setTarget(N target) {
		this.target = target;
	}
		public void setSource(N source) {
		this.source = source;
	}

		public N getSource() {
		return source;
	}

	public N getTarget() {
		return target;
	}

	public void setUpdateTimestamp(long updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}
	
	public long getUpdateTimestamp() {
		return updateTimestamp;
	}

	public Vector2d getVector() {
		if(this.vector == null) {
			this.vector = new Vector2d(getSource().getGeoLocation().x,
				getSource().getGeoLocation().y);
			vector.sub(new Vector2d(getTarget().getGeoLocation().x,
				getTarget().getGeoLocation().y));
		}
		return this.vector;
	}

	public void invalidateVector() {
		this.vector = null;
	}

	public N getOpposite(N node) {
		if(node.equals(getSource())) {
			return getTarget();
		}else if(node.equals(getTarget())) {
			return getSource();
		}else {
			throw new IllegalArgumentException(String.format("node %s is not part of this edge", this));
		}
	}

	public Boolean isOneWay() {
		return this.oneWay;
	}

	public void setOneWay(Boolean oneWay) {
		this.oneWay = oneWay;
	}
}
