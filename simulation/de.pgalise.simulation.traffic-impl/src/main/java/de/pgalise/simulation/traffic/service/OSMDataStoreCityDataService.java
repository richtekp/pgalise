/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.service;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.entity.Building;
import de.pgalise.simulation.shared.entity.City;
import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.entity.CityInfrastructureData;
import de.pgalise.simulation.traffic.entity.TrafficCity;
import de.pgalise.simulation.traffic.service.DataStoreCityDataService;
import de.pgalise.util.cityinfrastructure.impl.GraphConstructor;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateful;
import org.geotools.data.DataStore;
import org.geotools.data.Query;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;

/**
 * An implementation of {@link TrafficInfrastructureData} which (similar to
 * {@link OSMCityInfrastructureData} manages its creation and data retrieval
 * autonomously.
 *
 * @author richter
 */
@Stateful
@Local(DataStoreCityDataService.class)
public class OSMDataStoreCityDataService implements
  DataStoreCityDataService {

  private DataStore dataStore;
  private TrafficGraph trafficGraph;
  @EJB
  private GraphConstructor graphConstructor;
  private CityInfrastructureData cityInfrastructureData;
  @EJB
  private IdGenerator idGenerator;

  public OSMDataStoreCityDataService() {
  }

  @PostConstruct
  public void initialize() {
    this.cityInfrastructureData = new CityInfrastructureData(idGenerator.
      getNextId());
  }

  /**
   * creates a <tt>OSMDataStoreCityInfrastructureManager</tt> based on the OSM
   * id of a postgis polygon referencing an OSM city (boundary = administrative)
   *
   * @param cityPolygonOsmId
   */
  public OSMDataStoreCityDataService(
    DataStore dataStore,
    String cityBoundaryOsmId) throws IOException {
    try {
      String typeName = "planet_osm_polygon";
      SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);
      FeatureType schema = featureSource.getSchema();
      String geometryPropertyName = schema.getGeometryDescriptor().
        getLocalName();
      FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
      Filter cityFilter
        = CQL.toFilter("");
      Filter filter
        = cityFilter;
      Query query = new Query(typeName,
        filter);
      query.setMaxFeatures(20);
      SimpleFeatureCollection features = featureSource.getFeatures(query);
      List<City> tags = new LinkedList<>();
      List<Building> buildings = new LinkedList<>();
      SimpleFeatureIterator simpleFeatureIterator = features.features();
      while (simpleFeatureIterator.hasNext()) {
        SimpleFeature nextFeature = simpleFeatureIterator.next();
        String name = (String) nextFeature.getAttribute("tags");
        if (name.contains("bla")) {
          buildings.add(null);
        }
      }
      simpleFeatureIterator.close();
    } catch (CQLException ex) {
      throw new RuntimeException(ex);
    }
    this.trafficGraph = graphConstructor.createGraph(cityInfrastructureData.
      getWays());
    throw new UnsupportedOperationException("not yet implemented");
  }

  public TrafficGraph getTrafficGraph() {
    return trafficGraph;
  }

  @Override
  public Map getBuildingEnergyProfileMap(BaseCoordinate geolocation,
    int radiusInMeter) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List getBuildingsInRadius(BaseCoordinate centerPoint,
    int radiusInMeter) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List getNodesInBoundary(Envelope boundary) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public NavigationNode getNearestNode(double latitude,
    double longitude) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public NavigationNode getNearestStreetNode(double latitude,
    double longitude) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public NavigationNode getNearestJunctionNode(double latitude,
    double longitude) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public TrafficCity createCity() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
