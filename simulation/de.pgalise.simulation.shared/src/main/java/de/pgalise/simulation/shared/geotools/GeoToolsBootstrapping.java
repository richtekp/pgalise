/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.geotools;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;
import com.vividsolutions.jts.geom.GeometryFactory;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 *
 * @author richter
 */
/*
 * this class also increases the portability to other OpenGIS implementations 
 * possibly requiring more initialization
 */
public class GeoToolsBootstrapping {

  private final static GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();
  public final static CoordinateReferenceSystem COORDINATE_REFERENCE_SYSTEM = DefaultGeographicCRS.WGS84;

  public static GeometryFactory getGeometryFactory() {
    return GEOMETRY_FACTORY;
  }

  /**
   * uses {@link LatLngTool#distance(com.javadocmd.simplelatlng.LatLng, com.javadocmd.simplelatlng.LatLng, com.javadocmd.simplelatlng.util.LengthUnit)
   * } which obviously uses Haversine distance calculation because results are
   * correct
   *
   * @param c1
   * @param c2
   * @return
   */
  /*
   * Geotools uses calculation that returns immense errors (> 100 %) for values 
   * below 500 m
   */
  public static double distanceHaversineInM(BaseCoordinate c1,
    BaseCoordinate c2) {
    LatLng p1 = new LatLng(c1.getX(),
      c1.getY());
    LatLng p2 = new LatLng(c2.getX(),
      c2.getY());
    return LatLngTool.distance(p1,
      p2,
      LengthUnit.METER);
  }

  private GeoToolsBootstrapping() {
  }

  /**
   * Computes the distance between the two points in km.
   */
  public static double getDistanceInKM(double myLat,
    double myLng,
    double hisLat,
    double hisLng) {

    if ((myLat == hisLat) && (myLng == hisLng)) {
      return 0.0;
    }

    double f = 1 / 298.257223563;
    double a = 6378.137;
    double F = ((myLat + hisLat) / 2) * (Math.PI / 180);
    double G = ((myLat - hisLat) / 2) * (Math.PI / 180);
    double l = ((myLng - hisLng) / 2) * (Math.PI / 180);
    double S = (Math.pow(Math.sin(G),
      2) * Math.pow(Math.cos(l),
        2))
      + (Math.pow(Math.cos(F),
        2) * Math.pow(Math.sin(l),
        2));
    double C = (Math.pow(Math.cos(G),
      2) * Math.pow(Math.cos(l),
        2))
      + (Math.pow(Math.sin(F),
        2) * Math.pow(Math.sin(l),
        2));
    double w = Math.atan(Math.sqrt(S / C));
    double D = 2 * w * a;
    double R = Math.sqrt(S * C) / w;
    double H1 = ((3 * R) - 1) / (2 * C);
    double H2 = ((3 * R) + 1) / (2 * S);

    double distance = D
      * ((1 + (f * H1 * Math.pow(Math.sin(F),
        2) * Math.pow(Math.cos(G),
        2))) - (f * H2
      * Math.pow(Math.cos(F),
        2) * Math.pow(Math.sin(G),
        2)));

    return distance;
  }

  /**
   * Gives the distance in meter between start and target.
   *
   * @param start
   * @param target
   * @return
   */
  public static double getDistanceInMeter(BaseCoordinate start,
    BaseCoordinate target) {

    if ((start.getX() == target.getX())
      && (start.getY() == target.getY())) {
      return 0.0;
    }

    double f = 1 / 298.257223563;
    double a = 6378.137;
    double F = ((start.getX() + target.getX()) / 2) * (Math.PI / 180);
    double G = ((start.getX() - target.getX()) / 2) * (Math.PI / 180);
    double l = ((start.getY() - target.getY()) / 2) * (Math.PI / 180);
    double S = Math.pow(Math.sin(G),
      2) * Math.pow(Math.cos(l),
        2) + Math.pow(Math.cos(F),
        2)
      * Math.pow(Math.sin(l),
        2);
    double C = Math.pow(Math.cos(G),
      2) * Math.pow(Math.cos(l),
        2) + Math.pow(Math.sin(F),
        2)
      * Math.pow(Math.sin(l),
        2);
    double w = Math.atan(Math.sqrt(S / C));
    double D = 2 * w * a;
    double R = Math.sqrt(S * C) / w;
    double H1 = (3 * R - 1) / (2 * C);
    double H2 = (3 * R + 1) / (2 * S);

    double distance = D
      * (1 + f * H1 * Math.pow(Math.sin(F),
        2) * Math.pow(Math.cos(G),
        2) - f * H2 * Math.pow(Math.cos(F),
        2)
      * Math.pow(Math.sin(G),
        2));

    return distance * 1000.0;
  }
}
