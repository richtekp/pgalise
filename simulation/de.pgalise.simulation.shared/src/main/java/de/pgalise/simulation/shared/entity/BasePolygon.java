/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.shared.entity;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.CoordinateSequenceComparator;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryComponentFilter;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.GeometryFilter;
import com.vividsolutions.jts.geom.IntersectionMatrix;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.Id;

/**
 *
 * @author richter
 */
/*
internal implementation notes:
- As the constructors of geotools/vividsolutions Polygon doesn't expose 
its coordinate collection or array, the extension has to be done with the 
wrapper pattern -> if Polygons can be persisted with hibernate-spatial, the 
class is obsolete
*/
@Embeddable
public class BasePolygon extends Polygon {
//  @Id
//  private Long id;  
  /*
  @TODO: change to hibernate spatial (announced to be implemented in 
  hibernate 5), incompatible with 4.3.x which is the minimum version for 
  JAVA EE 7
  */
//  @Type(type="org.hibernate.spatial.GeometryType")
//	@Column(name = "geometry", columnDefinition="Geometry", nullable = true) 
  @Basic
  private Polygon polygon;

  protected BasePolygon() {
    super(null, null, GeoToolsBootstrapping.getGeometryFactory());
  }

  public BasePolygon(Long id, Polygon polygon) {
    this();
    this.polygon = polygon;
//    this.id = id;
  }

//  protected void setId(Long id) {
//    this.id = id;
//  }
//
//  public Long getId() {
//    return id;
//  }
  
  public Polygon getPolygon() {
    return polygon;
  }
  
  public void setPolygon(Polygon polygon) {
    this.polygon = polygon;
  }

  @Override
  public Geometry getEnvelope() {
    return polygon.getEnvelope();
  }

  @Override
  public void apply(CoordinateFilter filter) {
    polygon.apply(filter); 
  }

  @Override
  public void apply(CoordinateSequenceFilter filter) {
    polygon.apply(filter); 
  }

  @Override
  public void apply(GeometryComponentFilter filter) {
    polygon.apply(filter); 
  }

  @Override
  public void apply(GeometryFilter filter) {
    polygon.apply(filter); 
  }

  @Override
  public Geometry buffer(double distance) {
    return polygon.buffer(distance); 
  }

  @Override
  public Geometry buffer(double distance, int quadrantSegments) {
    return polygon.buffer(distance, quadrantSegments); 
  }

  @Override
  protected int compare(Collection a, Collection b) {
    throw new UnsupportedOperationException(); // has protected access in 
      // superclass
  }

  @Override
  public int compareTo(Object o, CoordinateSequenceComparator comp) {
    throw new UnsupportedOperationException(); // has protected access in 
      // superclass
  }

  @Override
  protected Envelope computeEnvelopeInternal() {
    throw new UnsupportedOperationException(); // has protected access in 
      // superclass
  }

  @Override
  protected boolean equal(Coordinate a, Coordinate b, double tolerance) {
    throw new UnsupportedOperationException(); // has protected access in 
      // superclass
  }

  @Override
  public Point getCentroid() {
    return polygon.getCentroid(); 
  }

  @Override
  public int getDimension() {
    return polygon.getDimension(); 
  }

  @Override
  public Coordinate[] getCoordinates() {
    return polygon.getCoordinates(); 
  }

  @Override
  public Coordinate getCoordinate() {
    return polygon.getCoordinate(); 
  }

  @Override
  public int getBoundaryDimension() {
    return polygon.getBoundaryDimension(); 
  }

  @Override
  public Geometry getBoundary() {
    return polygon.getBoundary(); 
  }

  @Override
  public double getArea() {
    return polygon.getArea(); 
  }

  @Override
  protected void geometryChangedAction() {    
    throw new UnsupportedOperationException(); // has protected access in 
      // superclass
  }

  @Override
  public void geometryChanged() {
    polygon.geometryChanged(); 
  }

  @Override
  protected void finalize() throws Throwable {
    throw new UnsupportedOperationException(); // has protected access in 
      // superclass
  }

  @Override
  public boolean equalsTopo(Geometry g) {
    return polygon.equalsTopo(g); 
  }

  @Override
  public boolean equalsNorm(Geometry g) {
    return polygon.equalsNorm(g); 
  }

  @Override
  public boolean equalsExact(Geometry other, double tolerance) {
    return polygon.equalsExact(other, tolerance); 
  }

  @Override
  public boolean equalsExact(Geometry other) {
    return polygon.equalsExact(other); 
  }

  @Override
  public boolean equals(Object o) {
    return polygon.equals(o); 
  }

  @Override
  public boolean equals(Geometry g) {
    return polygon.equals(g); 
  }

  @Override
  public double distance(Geometry g) {
    return polygon.distance(g); 
  }

  @Override
  public boolean disjoint(Geometry g) {
    return polygon.disjoint(g); 
  }

  @Override
  public Geometry difference(Geometry other) {
    return polygon.difference(other); 
  }

  @Override
  public boolean crosses(Geometry g) {
    return polygon.crosses(g); 
  }

  @Override
  public boolean covers(Geometry g) {
    return polygon.covers(g); 
  }

  @Override
  public boolean coveredBy(Geometry g) {
    return polygon.coveredBy(g); 
  }

  @Override
  public Geometry convexHull() {
    return polygon.convexHull(); 
  }

  @Override
  public boolean contains(Geometry g) {
    return polygon.contains(g); 
  }

  @Override
  protected int compareToSameClass(Object o, CoordinateSequenceComparator comp) {
    throw new UnsupportedOperationException(); // has protected access in 
      // superclass
  }

  @Override
  protected int compareToSameClass(Object o) {
    throw new UnsupportedOperationException(); // has protected access in 
      // superclass
  }

  @Override
  public int compareTo(Object o) {
    return polygon.compareTo(o); 
  }

  @Override
  public Object clone() {
    return polygon.clone(); 
  }

  @Override
  protected void checkNotGeometryCollection(Geometry g) {
    throw new UnsupportedOperationException(); // has protected access in 
      // superclass
  }

  @Override
  public Geometry buffer(double distance, int quadrantSegments, int endCapStyle) {
    return polygon.buffer(distance, quadrantSegments, endCapStyle); 
  }

  @Override
  public LineString getExteriorRing() {
    return polygon.getExteriorRing(); 
  }

  @Override
  public GeometryFactory getFactory() {
    return GeoToolsBootstrapping.getGeometryFactory(); //constructor 
      //invokes overridable getter for factory in constructor 
  }

  public LinearRing[] getHoles() {
    return holes;
  }

  @Override
  public PrecisionModel getPrecisionModel() {
    return polygon.getPrecisionModel(); 
  }

  @Override
  public IntersectionMatrix relate(Geometry g) {
    return polygon.relate(g); 
  }

  @Override
  public boolean within(Geometry g) {
    return polygon.within(g); 
  }

  @Override
  public Geometry union(Geometry other) {
    return polygon.union(other); 
  }

  @Override
  public Geometry union() {
    return polygon.union(); 
  }

  @Override
  public boolean touches(Geometry g) {
    return polygon.touches(g); 
  }

  @Override
  public String toText() {
    return polygon.toText(); 
  }

  @Override
  public String toString() {
    return polygon.toString(); 
  }

  @Override
  public Geometry symDifference(Geometry other) {
    return polygon.symDifference(other); 
  }

  @Override
  public void setUserData(Object userData) {
    polygon.setUserData(userData); 
  }

  public void setShell(LinearRing shell) {
    this.shell = shell;
  }

  @Override
  public void setSRID(int SRID) {
    polygon.setSRID(SRID); 
  }

  public void setHoles(LinearRing[] holes) {
    this.holes = holes;
  }

  public void setEnvelope(Envelope envelope) {
    this.envelope = envelope;
  }

  @Override
  public Geometry reverse() {
    return polygon.reverse(); 
  }

  @Override
  public boolean relate(Geometry g, String intersectionPattern) {
    return polygon.relate(g, intersectionPattern); 
  }

  @Override
  public boolean overlaps(Geometry g) {
    return polygon.overlaps(g); 
  }

  @Override
  public void normalize() {
    polygon.normalize(); 
  }

  @Override
  public Geometry norm() {
    return polygon.norm(); 
  }

  @Override
  public boolean isWithinDistance(Geometry geom, double distance) {
    return polygon.isWithinDistance(geom, distance); 
  }

  @Override
  public boolean isValid() {
    return polygon.isValid(); 
  }

  @Override
  public boolean isSimple() {
    return polygon.isSimple(); 
  }

  @Override
  public boolean isRectangle() {
    return polygon.isRectangle(); 
  }

  @Override
  protected boolean isGeometryCollection() {
    throw new UnsupportedOperationException(); // has protected access in 
      // superclass
  }

  @Override
  protected boolean isEquivalentClass(Geometry other) {
    throw new UnsupportedOperationException(); // has protected access in 
      // superclass
  }

  @Override
  public boolean isEmpty() {
    return polygon.isEmpty(); 
  }

  @Override
  public boolean intersects(Geometry g) {
    return polygon.intersects(g); 
  }

  @Override
  public Geometry intersection(Geometry other) {
    return polygon.intersection(other); 
  }

  @Override
  public int hashCode() {
    return polygon.hashCode(); 
  }

  @Override
  public Object getUserData() {
    return polygon.getUserData(); 
  }

  public LinearRing getShell() {
    return shell;
  }

  @Override
  public int getSRID() {
    return polygon.getSRID(); 
  }

  @Override
  public int getNumPoints() {
    return polygon.getNumPoints(); 
  }

  @Override
  public int getNumInteriorRing() {
    return polygon.getNumInteriorRing(); 
  }

  @Override
  public int getNumGeometries() {
    return polygon.getNumGeometries(); 
  }

  @Override
  public double getLength() {
    return polygon.getLength(); 
  }

  @Override
  public LineString getInteriorRingN(int n) {
    return polygon.getInteriorRingN(n); 
  }

  @Override
  public Point getInteriorPoint() {
    return polygon.getInteriorPoint(); 
  }

  @Override
  public String getGeometryType() {
    return polygon.getGeometryType(); 
  }

  @Override
  public Geometry getGeometryN(int n) {
    return polygon.getGeometryN(n); 
  }

  @Override
  public Envelope getEnvelopeInternal() {
    return polygon.getEnvelopeInternal(); 
  }
  
  
}
