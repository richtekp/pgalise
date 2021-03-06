/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.entity;

import de.pgalise.simulation.shared.JaxbVector2d;
import com.vividsolutions.jts.geom.LineString;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 *
 * @param <N>
 * @author richter
 */
@Entity
public class NavigationEdge<N extends NavigationNode> extends Identifiable {

  private static final long serialVersionUID = 1L;

  @Transient
  private LineString edgeLine;
  @Transient
  private Double edgeLineLength;
  @Transient
  private Double lineAzimuth;
  private long updateTimestamp;
  @ManyToOne(targetEntity = NavigationNode.class)
  private N source;
  @ManyToOne(targetEntity = NavigationNode.class)
  private N target;
  @Transient
  private JaxbVector2d vector = null;
  /*
   Wrapper in order to indicate not set status with null
   */
  private Boolean oneWay = false;

  /**
   * <tt>source</tt> and <tt>target</tt> are set using {@link Graph#addEdge(java.lang.Object, java.lang.Object)
   * } or {@link Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object)
   * }. You should invoke {@link #validateNavigationNodeDistance() } after a
   * call to {@link Graph#addEdge(java.lang.Object, java.lang.Object) } or {@link Graph#addEdge(java.lang.Object, java.lang.Object, java.lang.Object)
   * }!
   *
   * @throws IllegalArgumentException if the distance between <tt>node0</tt> and
   * <tt>node1</tt> is less than or equals {@link NavigationNode#NODE_RADIUS}
   */
  public NavigationEdge() {
    super();
  }

  public NavigationEdge(Long id,
    N source,
    N target) {
    super(id);
    this.source = source;
    this.target = target;
  }

  /**
   * generates edge line lazily in order to allow usage of this class in an
   * {@link EdgeFactory}
   *
   * @return
   */
  public LineString getEdgeLine() {
    if (edgeLine == null) {
      this.edgeLine = GeoToolsBootstrapping.getGeometryFactory().
        createLineString(
          new BaseCoordinate[]{
            getSource(), getTarget()
          }
        );
    }
    return edgeLine;
  }

  public double getEdgeLength() {
    if (edgeLineLength == null) {
      double distance = GeoToolsBootstrapping.distanceHaversineInM(source
        ,
        target);
      edgeLineLength = distance;
    }
    return edgeLineLength;
  }

  public void invalidateLength() {
    this.edgeLineLength = null;
  }

  public double getLineAzimuth() {
    if (lineAzimuth == null) {
      JaxbVector2d edgeVector = new JaxbVector2d(source.getX(),
        source.getY());
      edgeVector.sub(new JaxbVector2d(target.getX(),
        target.getY()));
      JaxbVector2d northVector = new JaxbVector2d(0,
        1);
      lineAzimuth = edgeVector.angle(northVector) * 180 / Math.PI;
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

  public JaxbVector2d getVector() {
    if (this.vector == null) {
      this.vector = new JaxbVector2d(getSource().getX(),
        getSource().getY());
      vector.sub(new JaxbVector2d(getTarget().getX(),
        getTarget().getY()));
    }
    return this.vector;
  }

  public void invalidateVector() {
    this.vector = null;
  }

  public N getOpposite(N node) {
    if (node.equals(getSource())) {
      return getTarget();
    } else if (node.equals(getTarget())) {
      return getSource();
    } else {
      throw new IllegalArgumentException(String.format(
        "node %s is not part of this edge",
        this));
    }
  }

  public Boolean isOneWay() {
    return this.oneWay;
  }

  public void setOneWay(Boolean oneWay) {
    this.oneWay = oneWay;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 13 * hash + Objects.hashCode(this.source);
    hash = 13 * hash + Objects.hashCode(this.target);
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
    final NavigationEdge<?> other = (NavigationEdge<?>) obj;
    if (!Objects.equals(this.source,
      other.source)) {
      return false;
    }
    if (!Objects.equals(this.target,
      other.target)) {
      return false;
    }
    return true;
  }
}
