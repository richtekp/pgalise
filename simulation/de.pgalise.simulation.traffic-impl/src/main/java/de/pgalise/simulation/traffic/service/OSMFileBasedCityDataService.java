/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package de.pgalise.simulation.traffic.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.entity.BaseBoundary;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.entity.Building;
import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.simulation.shared.entity.Way;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.shared.tag.LanduseTagEnum;
import de.pgalise.simulation.shared.tag.WayTagEnum;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.entity.BusRoute;
import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.simulation.traffic.entity.CityInfrastructureData;
import de.pgalise.simulation.traffic.entity.CycleWay;
import de.pgalise.simulation.traffic.entity.TrafficCity;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.entity.MotorWay;
import de.pgalise.simulation.traffic.entity.TrafficWay;
import de.pgalise.simulation.traffic.entity.osm.OSMBuilding;
import de.pgalise.simulation.traffic.entity.osm.OSMBusRoute;
import de.pgalise.simulation.traffic.entity.osm.OSMBusStop;
import de.pgalise.simulation.traffic.entity.osm.OSMCycleWay;
import de.pgalise.simulation.traffic.entity.osm.OSMTrafficNode;
import de.pgalise.simulation.traffic.entity.osm.OSMTrafficWay;
import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;
import de.pgalise.util.cityinfrastructure.DefaultBuildingEnergyProfileStrategy;
import de.pgalise.util.cityinfrastructure.impl.GraphConstructor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateful;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.khelekore.prtree.DistanceCalculator;
import org.khelekore.prtree.DistanceResult;
import org.khelekore.prtree.MBRConverter;
import org.khelekore.prtree.NodeFilter;
import org.khelekore.prtree.PRTree;
import org.khelekore.prtree.PointND;
import org.khelekore.prtree.SimplePointND;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The OSMFileBasedCityDataService is the used
 * {@link CityInfrastructureDataService} in this prototype. It parsed the needed
 * information from given openstreetmap and busstop files. The methods null {@link CityInfrastructureDataService#getNearestJunctionNode(double, double)},
 * {@link CityInfrastructureDataService#getNearestNode(double, double)},
 * {@link CityInfrastructureDataService#getNearestStreetNode(double, double)}
 * and {@link CityInfrastructureDataService#getNodesInBoundary(Boundary)} are
 * implements with a PR-Tree.
 *
 * Parsing of bus system data takes place seperately in
 * {@link PublicTransportDataService}. Because {@link CityInfrastructureData} is
 * kept as a unit together, the bus system information has to be inserted
 * separately using {@link BusStopDataService#insertBusStops(de.pgalise.simulation.traffic.entity.CityInfrastructureData, java.util.Set)
 * }.
 *
 * Properties of this class, e.g. boundary, refer to all files passed to it with
 * {@link #parseFile(java.io.File)} or
 * {@link #parseStream(java.io.InputStream)}.
 *
 * @author Timo
 */
/*
 @TODO: separate logic from data
 */
@Stateful
@Local(FileBasedCityDataService.class)
public class OSMFileBasedCityDataService implements
  FileBasedCityDataService {

  private static final Logger log = LoggerFactory.getLogger(
    OSMFileBasedCityDataService.class);
  private static final Pattern MAX_SPEED_PATTERN = Pattern.compile(
    "([0-9]+)([\\s]*)([a-zA-Z]+)");
  private static final double MILE_TO_KM = 1.609344;
  /**
   * OSM ways with these highway values will be ignored.
   *
   * @see DefaultFileBasedCityInfrastructureDataService.parseWays
   */
  private static final Set<String> NON_INTERESTING_HIGHWAYS = new HashSet<>(
    Arrays.asList(WayTagEnum.BUS_STOP.getStringValue(),
      WayTagEnum.UNCLASSIFIED.getStringValue()));
  /**
   * OSM ways with these highway values will be ignored.
   *
   * @see DefaultFileBasedCityInfrastructureDataService.parseMotorWays
   */
  private static final Set<String> NON_MOTOR_HIGHWAYS = new HashSet<>(
    Arrays.asList(WayTagEnum.pedestrian.getStringValue(),
      WayTagEnum.CYCLEWAY.getStringValue(),
      WayTagEnum.STEPS.getStringValue(),
      WayTagEnum.FOOTPATH.getStringValue(),
      WayTagEnum.PATH.getStringValue(),
      WayTagEnum.service.getStringValue(),
      WayTagEnum.natural.getStringValue(),
      WayTagEnum.track.getStringValue()));
  /**
   * Branch factor for the pr-tree
   */
  private static final int prTreeBranchFactor = 30;
  /**
   * Serial
   */
  private static final long serialVersionUID = -4969795656547242552L;
  private TrafficCity city;
  @EJB
  private IdGenerator idGenerator;
  /**
   * Distance calculator for the pr-tree
   */
  private DistanceCalculator<NavigationNode> prTreeDistanceCalculator
    = new NodeDistance();
  /**
   * Node filter for the pr-tree
   */
  private NodeFilter<NavigationNode> prTreeNodeFilter = new PRTreeNodeFilter();
  /**
   * PR-Tree to answer spatial queries
   */
  private PRTree<NavigationNode> allNodesTree;
  /**
   * PR-Tree to answer spatial queries
   */
  private PRTree<NavigationNode> streetNodesTree;
  private PRTree<NavigationNode> junctionNodesTree;
  /**
   * PR-Tree to answer spatial queries for buildings
   */
  private PRTree<Building> buildingTree;
  private BuildingEnergyProfileStrategy buildingEnergyProfileStrategy = new DefaultBuildingEnergyProfileStrategy();
  private TrafficGraph trafficGraph;
  @EJB
  private GraphConstructor graphConstructor;
  /**
   * as relations between OSM entities are determined by their ID we need a map
   * to access it without trouble
   */
  private final Map<String, MotorWay> osmIdWayMap = new HashMap<>();
  /**
   * store a list of node references for every busRoute
   */
  private final Map<String, List<TrafficNode>> busRouteNodeListMap = new HashMap<>();
  private final Map<String, List<BusStop>> osmIdBusStopsMap = new HashMap<>();
  /* String = node id / nd ref */
  private Map<String, TrafficNode> osmIdNodeMap = new HashMap<>();
  /**
   * used to inidicate whether the OSM files relates to a city or city part or
   * just landscape.
   */
  private Polygon administrativeBoundary = null;

  public OSMFileBasedCityDataService() {
  }

  /**
   *
   * @param idGenerator
   * @param trafficGraph
   * @param graphConstructor
   * @param city
   */
  /*
   should have an argument for CityInfrastructureData because this constructor 
   ought to be used in tests (cityInfrastructureData is initialized in 
   @PostConstruct method)
   */
  public OSMFileBasedCityDataService(
    IdGenerator idGenerator,
    TrafficGraph trafficGraph,
    GraphConstructor graphConstructor,
    TrafficCity city
  ) {
    this.idGenerator = idGenerator;
    this.trafficGraph = trafficGraph;
    this.graphConstructor = graphConstructor;
    this.city = city;
  }

  /**
   * parsing needs to be done with {@link #parseStream(java.io.InputStream) }
   *
   * @param buildingEnergyProfileStrategy the strategy to find the best energy
   * profile for a buidling
   * @throws IOException
   */
  public OSMFileBasedCityDataService(
    BuildingEnergyProfileStrategy buildingEnergyProfileStrategy) throws IOException {
    this.buildingEnergyProfileStrategy = buildingEnergyProfileStrategy;
  }

  @PostConstruct
  public void initialize() {
    this.city = new TrafficCity(idGenerator.
      getNextId(),
      new CityInfrastructureData(idGenerator.getNextId()));
  }

  /**
   * Converts the circle into a rectangle.
   *
   * @param centerPoint
   * @param radiusInMeter
   * @return
   */
  private Envelope circleToRectangle(BaseCoordinate centerPoint,
    int radiusInMeter) {

    List<BaseCoordinate> tmpPoints = new ArrayList<>();
    int pointNumber = 4;
    double radiusInKm = radiusInMeter / 1000.0;
    double earthRadius = 6378.137; // in km
    double r2d = 180.0 / Math.PI; // degrees to radians
    double d2r = Math.PI / 180.0; // radians to degrees
    double rlat = (radiusInKm / earthRadius) * r2d;
    double rlng = rlat / Math.cos(centerPoint.getX() * d2r);

    for (int i = 0; i < pointNumber; i++) {
      double t = Math.PI * (i / (pointNumber / 2.0));
      double lat = centerPoint.getX() + (rlat * Math.cos(t));
      double lng = centerPoint.getY() + (rlng * Math.sin(t));
      tmpPoints.add(new BaseCoordinate(idGenerator.getNextId(),
        lat,
        lng));
    }

    double northEastLat = Double.MIN_VALUE;
    double northEastLng = Double.MIN_VALUE;
    double southWestLat = Double.MAX_VALUE;
    double southWestLng = Double.MAX_VALUE;

    for (BaseCoordinate p : tmpPoints) {
      if (p.getX() > northEastLat) {
        northEastLat = p.getX();
      }
      if (p.getX() < southWestLat) {
        southWestLat = p.getX();
      }
      if (p.getY() > northEastLng) {
        northEastLng = p.getY();
      }
      if (p.getY() < southWestLng) {
        southWestLng = p.getY();
      }
    }

    return new Envelope(new BaseCoordinate(idGenerator.getNextId(),
      northEastLat,
      northEastLng),
      new BaseCoordinate(idGenerator.getNextId(),
        southWestLat,
        southWestLng));
  }

  public TrafficGraph getTrafficGraph() {
    return trafficGraph;
  }

  /**
   * Returns a list of buildings.
   *
   * @param wayList
   * @return
   */
  private Set<Building> extractBuildings(Set<TrafficWay> wayList) {
    Set<Building> buildingList = new HashSet<>();

    List<String> tmpLandUseList = new ArrayList<>();

    /* Build a map with interesting landuse polygons: */
    /**
     * Map<String = landuse tag, List<SimplePolygon2D = polygon of the landuse
     * are.
     */
    Map<String, List<Polygon>> landUseMap = new HashMap<>();
    for (Way<?, ?> landuse : this.city.getCityInfrastructureData().
      getLandUseWays()) {
      if (landuse.getLanduseTags().contains(LanduseTagEnum.INDUSTRY)) {
        List<Polygon> polygonList = landUseMap.get("industrial");
        if (polygonList == null) {
          polygonList = new ArrayList<>();
          landUseMap.put("industrial",
            polygonList);
        }
        polygonList.add(this.wayToPolygon2d(landuse));
      } else if (landuse.getLanduseTags().contains(LanduseTagEnum.RETAIL)) {
        List<Polygon> polygonList = landUseMap.get("retail");
        if (polygonList == null) {
          polygonList = new ArrayList<>();
          landUseMap.put("retail",
            polygonList);
        }
        polygonList.add(this.wayToPolygon2d(landuse));
      } else if (landuse.getLanduseTags().contains(LanduseTagEnum.FARMYARD)) {
        List<Polygon> polygonList = landUseMap.get("farmyard");
        if (polygonList == null) {
          polygonList = new ArrayList<>();
          landUseMap.put("farmyard",
            polygonList);
        }
        polygonList.add(this.wayToPolygon2d(landuse));
      } else if (landuse.getLanduseTags().contains(LanduseTagEnum.MILITARY)) {
        List<Polygon> polygonList = landUseMap.get("military");
        if (polygonList == null) {
          polygonList = new ArrayList<>();
          landUseMap.put("military",
            polygonList);
        }
        polygonList.add(this.wayToPolygon2d(landuse));
      } else if (landuse.getLanduseTags().contains(LanduseTagEnum.RESIDENTIAL)) {
        List<Polygon> polygonList = landUseMap.get("residential");
        if (polygonList == null) {
          polygonList = new ArrayList<>();
          landUseMap.put("residential",
            polygonList);
        }
        polygonList.add(this.wayToPolygon2d(landuse));
      } else if (landuse.getLanduseTags().contains(LanduseTagEnum.FARMLAND)) {
        List<Polygon> polygonList = landUseMap.get("farmland");
        if (polygonList == null) {
          polygonList = new ArrayList<>();
          landUseMap.put("farmland",
            polygonList);
        }
        polygonList.add(this.wayToPolygon2d(landuse));
      } else if (landuse.getLanduseTags().contains(LanduseTagEnum.COMMERCIAL)) {
        List<Polygon> polygonList = landUseMap.get("commercial");
        if (polygonList == null) {
          polygonList = new ArrayList<>();
          landUseMap.put("commercial",
            polygonList);
        }
        polygonList.add(this.wayToPolygon2d(landuse));
      }
    }

    for (Way<?, ?> way : wayList) {
      //if (way.isBuilding()) {
      if (way.getNodeList().isEmpty()) {
        continue;
      }
      /* find bound and tags: */
      Set<String> tourism = null;
      Set<String> sport = null;
      Set<String> service = null;
      Set<String> school = null;
      Set<String> repair = null;
      Set<String> amenity = null;
      Set<String> attraction = null;
      Set<String> shop = null;
      Set<String> emergencyService = null;
      boolean office = false;
      Set<String> craft = null;
      Set<String> leisure = null;
      boolean military = false;
      Set<String> publicTransport = null;
      Set<String> gambling = null;

      double minLat = Double.MAX_VALUE;
      double minLng = Double.MAX_VALUE;
      double maxLat = Double.MIN_VALUE;
      double maxLng = Double.MIN_VALUE;

      for (NavigationNode node : way.getNodeList()) {

        if (node.getTourismTags() != null) {
          tourism = node.getTourismTags();
        }
        if (node.getSportTags() != null) {
          sport = node.getSportTags();
        }
        if (node.getServiceTags() != null) {
          service = node.getServiceTags();
        }
        if (node.getSchoolTags() != null) {
          school = node.getSchoolTags();
        }
        if (node.getRepairTags() != null) {
          repair = node.getRepairTags();
        }
        if (node instanceof Building) {
          Building nodeCast = (Building) node;
          if (nodeCast.getAmenityTags() != null) {
            amenity = nodeCast.getAmenityTags();
          }
          if (nodeCast.isOffice()) {
            office = nodeCast.isOffice();
          }
        }
        if (node.getAttractionTags() != null) {
          attraction = node.getAttractionTags();
        }
        if (node.getShopTags() != null) {
          shop = node.getShopTags();
        }
        if (node.getEmergencyServiceTags() != null) {
          emergencyService = node.getEmergencyServiceTags();
        }
        if (node.getCraftTags() != null) {
          craft = node.getCraftTags();
        }
        if (node.getLeisureTags() != null) {
          leisure = node.getLeisureTags();
        }
        if (node.isMilitary()) {
          military = node.isMilitary();
        }
        if (node.getPublicTransportTags() != null) {
          publicTransport = node.getPublicTransportTags();
        }
        if (node.getGamblingTags() != null) {
          gambling = node.getGamblingTags();
        }

        if (node.getX() > maxLat) {
          maxLat = node.getX();
        }
        if (node.getX() < minLat) {
          minLat = node.getX();
        }
        if (node.getY() > maxLng) {
          maxLng = node.getY();
        }
        if (node.getY() < minLng) {
          minLng = node.getY();
        }
      }

      /* find other tags */
      for (NavigationNode node : this.getNodesInBoundary(new Envelope(
        new BaseCoordinate(idGenerator.getNextId(),
          maxLat,
          maxLng),
        new BaseCoordinate(idGenerator.getNextId(),
          minLat,
          minLng)))) {
        if (node.getTourismTags() != null) {
          tourism = node.getTourismTags();
        }
        if (node.getSportTags() != null) {
          sport = node.getSportTags();
        }
        if (node.getServiceTags() != null) {
          service = node.getServiceTags();
        }
        if (node.getSchoolTags() != null) {
          school = node.getSchoolTags();
        }
        if (node.getRepairTags() != null) {
          repair = node.getRepairTags();
        }
        if (node instanceof Building) {
          Building nodeCast = (Building) node;
          if (nodeCast.getAmenityTags() != null) {
            amenity = nodeCast.getAmenityTags();
          }
          if (nodeCast.isOffice()) {
            office = nodeCast.isOffice();
          }
        }
        if (node.getAttractionTags() != null) {
          attraction = node.getAttractionTags();
        }
        if (node.getShopTags() != null) {
          shop = node.getShopTags();
        }
        if (node.getEmergencyServiceTags() != null) {
          emergencyService = node.getEmergencyServiceTags();
        }
        if (node.getCraftTags() != null) {
          craft = node.getCraftTags();
        }
        if (node.getLeisureTags() != null) {
          leisure = node.getLeisureTags();
        }
        if (node.isMilitary()) {
          military = node.isMilitary();
        }
        if (node.getPublicTransportTags() != null) {
          publicTransport = node.getPublicTransportTags();
        }
        if (node.getGamblingTags() != null) {
          gambling = node.getGamblingTags();
        }
      }

      double centerLon = minLng + ((maxLng - minLng) / 2.0);
      double centerLat = minLat + ((maxLat - minLat) / 2.0);

      /* find landuse area: */
      String landUseArea = null;
      outerLoop:
      for (Entry<String, List<Polygon>> entry : landUseMap.entrySet()) {
        for (Polygon landUsePolygon : entry.getValue()) {
          if (landUsePolygon.covers(GeoToolsBootstrapping.getGeometryFactory().
            createPoint(new Coordinate(centerLat,
                centerLon)))) {
            tmpLandUseList.add(entry.getKey());
            landUseArea = entry.getKey();
            break outerLoop;
          }
        }
      }

      /* calc area in m² */
      double length = GeoToolsBootstrapping.getDistanceInKM(maxLat,
        maxLng,
        maxLat,
        minLng) * 1000.0;
      double width = GeoToolsBootstrapping.getDistanceInKM(maxLat,
        maxLng,
        minLat,
        maxLng) * 1000.0;
      double area = length * width;
      area *= area;

      BaseBoundary buildingPosition = new BaseBoundary(idGenerator.getNextId(),
        GeoToolsBootstrapping.getGeometryFactory().createPolygon(
          new BaseCoordinate[]{new BaseCoordinate(idGenerator.getNextId(),
              maxLat,
              maxLng), new BaseCoordinate(idGenerator.getNextId(),
              minLat,
              minLng)}));
      buildingList.add(
        new Building(idGenerator.getNextId(),
          new BaseCoordinate(idGenerator.getNextId(),
            buildingPosition.retrieveCenterPoint()),
          new BaseBoundary(idGenerator.getNextId()),
          tourism,
          service,
          sport,
          school,
          repair,
          attraction,
          shop,
          emergencyService,
          craft,
          leisure,
          publicTransport,
          gambling,
          amenity,
          null,
          office,
          military));
    }

    return buildingList;
  }

  /**
   * Extracts the cycle ways.
   *
   * @param wayList
   * @return
   */
  private Set<CycleWay> extractCycleWays(Set<TrafficWay> wayList) {
    Set<CycleWay> retValue = new HashSet<>();

    for (TrafficWay way : wayList) {
      if (way instanceof CycleWay) {
        retValue.add((CycleWay) way);
      }
    }

    return retValue;
  }

  /**
   * Extracts the landuse ways.
   *
   * @param ways
   * @return
   */
  private Set<TrafficWay> extractLanduseWays(Set<TrafficWay> ways) {
    Set<TrafficWay> wayList = new HashSet<>();

    for (TrafficWay way : ways) {
      if ((way.getLanduseTags() != null) && !way.getLanduseTags().isEmpty()) {
        wayList.add(way);
      }
    }

    return wayList;
  }

  /**
   * Parses all motorways.
   *
   * @param osmFile
   * @return
   */
  private Set<MotorWay> extractMotorWays(Set<TrafficWay> ways) {
    Set<MotorWay> wayList = new HashSet<>();

    OuterLoop:
    for (Way<?, ?> way : ways) {
      if (way instanceof MotorWay) {
        MotorWay wayCast = (MotorWay) way;
        if (wayCast.getWayTags() != null) {
          for (String wayTag : NON_MOTOR_HIGHWAYS) {
            if (wayCast.getWayTags().contains(wayTag)) {
              continue OuterLoop;
            }
          }

          if (wayCast.getWayTags().contains(WayTagEnum.RAILWAY.getStringValue())) {
            log.debug(wayCast.getWayTags().toString());
          }
          wayList.add(wayCast);
        }
      }
    }

    return wayList;
  }

  /**
   * Extracts the roundabouts.
   *
   * @param usedNodes
   * @return
   */
  private Set<TrafficNode> extractRoundAbouts(Set<TrafficNode> usedNodes) {
    Set<TrafficNode> roundAboutList = new HashSet<>();

    for (NavigationNode node : usedNodes) {
      if (node instanceof TrafficNode) {
        TrafficNode nodeCast = (TrafficNode) node;
        if (nodeCast.isRoundabout()) {
          roundAboutList.add(nodeCast);
        }
      }
    }

    return roundAboutList;
  }

  @Override
  public Map<EnergyProfileEnum, List<Building>> getBuildings(
    BaseCoordinate geolocation,
    int radiusInMeter) {

    Map<EnergyProfileEnum, List<Building>> energyProfileCountMap = new HashMap<>();
    List<Building> buildingsInRadius = this.getBuildingsInRadius(geolocation,
      radiusInMeter);

    for (Building building : buildingsInRadius) {
      try {
        EnergyProfileEnum energyProfileEnum = this.buildingEnergyProfileStrategy.
          getEnergyProfile(building);
        List<Building> buildingList = energyProfileCountMap.get(
          energyProfileEnum);
        if (buildingList == null) {
          buildingList = new ArrayList<>();
          energyProfileCountMap.put(energyProfileEnum,
            buildingList);
        }
        buildingList.add(building);
      } catch (RuntimeException e) {
        log.error(e.getMessage(),
          e);
      }
    }

    return energyProfileCountMap;
  }

  @Override
  public List<Building> getBuildingsInRadius(BaseCoordinate centerPoint,
    int radiusInMeter) {
    Envelope boundary0 = this.circleToRectangle(centerPoint,
      radiusInMeter);
    List<Building> tmpBuildings = new ArrayList<>();
    for (Building building : this.buildingTree.find(boundary0.getMinX(),
      boundary0
      .getMinY(),
      boundary0.getMaxX(),
      boundary0
      .getMaxY())) {
      tmpBuildings.add(building);
    }

    /* check the distance for every found building, because we used a rectangle instead of a circle: */
    List<Building> buildings0 = new ArrayList<>();
    for (Building building : tmpBuildings) {
      if (GeoToolsBootstrapping.getDistanceInMeter(centerPoint,
        building.getGeoInfo().retrieveCenterPoint()) <= radiusInMeter) {
        buildings0.add(building);
      }
    }

    return buildings0;
  }

  @Override
  public NavigationNode getNearestNode(double latitude,
    double longitude) {
    List<DistanceResult<NavigationNode>> results = this.allNodesTree.
      nearestNeighbour(this.prTreeDistanceCalculator,
        this.prTreeNodeFilter,
        1,
        new SimplePointND(latitude,
          longitude));

    if (results.size() > 0) {
      return results.get(0).get();
    }

    throw new RuntimeException(
      "No nearest node found for: latitude: " + latitude + " longitude: " + longitude);
  }

  @Override
  public NavigationNode getNearestStreetNode(double latitude,
    double longitude) {
    List<DistanceResult<NavigationNode>> results = this.streetNodesTree.
      nearestNeighbour(this.prTreeDistanceCalculator,
        this.prTreeNodeFilter,
        1,
        new SimplePointND(latitude,
          longitude));

    if (results.size() > 0) {
      return results.get(0).get();
    }

    throw new RuntimeException(
      "No nearest node found for: latitude: " + latitude + " longitude: " + longitude);
  }

  /**
   * OSM files (and changesets) consist of nodes with name
   * <ul>
   * <li>node (with tag nodes as children)</li>
   * <li>bounds (only one makes sense)</li>
   * <li>way: not necessarily a way in the sense of traffic, polyline (closed or
   * opened) (can be a building, area, etc. determined by tags) (nd and tag
   * children)</li>
   * <li>relations: logical conjuntion of nodes and ways (have member and tag
   * children)</li>
   * </ul>
   *
   * This methods doesn't retrieve bounds from file, but calculates it from
   * parsed geometries.
   *
   * Nodes which are not buildings, bus stops, bus routes, motorways, cycleways,
   * etc. will be ignored
   *
   * @TODO: current implementation ignores relations (important for bus routes
   * (tests looks like retrieval of bus routes from OSM files is intended))
   *
   * Nodes which are referenced in ways or relations, but not listed as nodes
   * are considered to be outside the envelope of the OSM file and kept in
   * internal storage, but are not exposed.
   *
   * @param osmIN osm-file
   * @throws IOException
   */
  @Override
  public void parseStream(InputStream osmIN) throws IOException {
    /* Holds the way data till we collected every node */
    Set<TrafficNode> nodes = new HashSet<>();
    Set<BusStop> busStops = new HashSet<>();
    Set<BusRoute> busRoutes = new HashSet<>();
    Set<Building> buildings = new HashSet<>();
    Set<TrafficWay> ways = new HashSet<>();

    XMLStreamReader parser = null;
    try {
      parser = XMLInputFactory.newInstance().createXMLStreamReader(osmIN);

      //context (in the sense of context sensitivity of the parsing problem) is 
      //determined by the variables lastFoundWay, lastBusStop, lastNode and 
      //lastBusRoute. Only one of them is != null. If no check for current 
      //context succeeds, a new context is created (this means the parser 
      //enters a relevant child of the osm root element (currently all used 
      //childs node, way and relation are used). If a context is created 
      //(currently a context will be created every time as all element matter), 
      //one of lastFoundWay, lastBustop, lastNode and lastBusRoute will be 
      //initialized with the information contained in attribute of the start 
      //element (as this information will be gone in iteration of streaming 
      //based parsing when entering the child nodes)
      BusStop lastBusstop = null;
      TrafficNode lastNode = null;
      BusRoute lastBusRoute = null;

      String osmWayId = null;
      List<String> osmNodeRefs = new LinkedList<>();
      String routeLongName = null;

      Map<Integer, String> eventTypeMap = new HashMap<Integer, String>();
      eventTypeMap.put(XMLStreamConstants.START_ELEMENT,
        "start element");
      eventTypeMap.put(XMLStreamConstants.END_ELEMENT,
        "end element");
      eventTypeMap.put(XMLStreamConstants.ATTRIBUTE,
        "attribute");
      eventTypeMap.put(XMLStreamConstants.CHARACTERS,
        "characters");
      eventTypeMap.put(XMLStreamConstants.END_DOCUMENT,
        "end document");
      eventTypeMap.put(XMLStreamConstants.START_DOCUMENT,
        "start document");
      eventTypeMap.put(XMLStreamConstants.START_DOCUMENT,
        "start document");

      while (parser.hasNext()) {
        if (parser.getEventType() == XMLStreamConstants.START_ELEMENT || parser.
          getEventType() == XMLStreamConstants.END_ELEMENT) {
          log.info(String.format("%s %s",
            eventTypeMap.get(parser.getEventType()),
            parser.getLocalName()));
        } else {
          log.info(String.format("%s",
            eventTypeMap.get(parser.getEventType())));
        }
        switch (parser.getEventType()) {
          case XMLStreamConstants.START_ELEMENT:
            //First check wether there's a context. In every context parse all 
            //possibly relevant information (tags, refernces, etc.), then check 
            //whether condition(s) are fulfilled for certain objects or whether 
            //the parsed information is irrelevant and can be thrown away. At 
            //this point confliciting information is treated as well.
            if (lastNode != null) {
              //current context is node
              // inside a node element, parse all tag elements (only interesing 
              // children so far
              // all nodes are interesting regardless of tags 
              while (!(parser.getEventType() == XMLStreamConstants.END_ELEMENT && parser.
                getLocalName().equals("node"))) {
                if (parser.getEventType() == XMLStreamConstants.START_ELEMENT) {
                  if (parser.getLocalName().equalsIgnoreCase("tag")) {
                    //in child of (valid) node tag ...

                    String kValue = parser.getAttributeValue(0);
                    String vValue = parser.getAttributeValue(1);

                    if (kValue.equalsIgnoreCase("highway")) {
                      if (vValue.equalsIgnoreCase("bus_stop")) {
                        lastBusstop = new BusStop(idGenerator.getNextId(),
                          "",
                          null,
                          new BaseCoordinate(idGenerator.getNextId(),
                            lastNode.getX(),
                            lastNode.getY()));
                        city.getCityInfrastructureData().getBusStops().add(
                          lastBusstop);
                      } else if (vValue.equalsIgnoreCase("mini_roundabout")
                        || vValue.equalsIgnoreCase("roundabout")) {
                        lastNode.setRoundabout(true);
                      }
                    } else if (kValue.equalsIgnoreCase("name") && (lastBusstop != null)) {
                      lastBusstop.setStopName(vValue);
                      busStops.add(lastBusstop);
                      lastBusstop = null;
                    } else if (kValue.equalsIgnoreCase("tourism")) {
                      lastNode.getTourismTags().add(
                        vValue);
                    } else if (kValue.equalsIgnoreCase("sport")) {
                      lastNode.getSportTags().add(vValue);

                    } else if (kValue.equalsIgnoreCase("school")) {
                      lastNode.getSchoolTags().add(vValue);

                    } else if (kValue.equalsIgnoreCase("shop")) {
                      lastNode.getShopTags().add(vValue);

                    } else if (kValue.equalsIgnoreCase("service")) {
                      lastNode.getServiceTags().add(
                        vValue);

                    } else if (kValue.equalsIgnoreCase("repair")) {
                      lastNode.getRepairTags().add(vValue);

                    } else if (kValue.equalsIgnoreCase("amenity")) {
                      lastNode.getAmenityTags().add(vValue);

                    } else if (kValue.equalsIgnoreCase("attraction")) {
                      lastNode.getAttractionTags().add(vValue);

                    } else if (kValue.equalsIgnoreCase("emergency_service")) {
                      lastNode.getEmergencyServiceTags().add(vValue);

                    } else if (kValue.equalsIgnoreCase("office")) {
                      lastNode.setOffice(true);

                    } else if (kValue.equalsIgnoreCase("craft")) {
                      lastNode.getCraftTags().add(vValue);

                    } else if (kValue.equalsIgnoreCase("leisure")) {
                      lastNode.getLeisureTags().add(vValue);

                    } else if (kValue.equalsIgnoreCase("military")) {
                      lastNode.setMilitary(true);

                    } else if (kValue.equalsIgnoreCase("public_transport")) {
                      lastNode.getPublicTransportTags().add(vValue);
                    } else if (kValue.equalsIgnoreCase("gambling")) {
                      lastNode.getGamblingTags().add(vValue);
                    }
                  }
                }
                parser.next();
              }
              nodes.add(lastNode);
              lastNode = null;
            } else if (osmWayId != null) {
              //current context is way
              //inside a way element, parse all nd elements and tag elements
              // ways can be buildings, (highways, streets and cycleways) 
              //(tagged with highway), and areas (not yet mapped)
              List<String> osmWayNodeRefs = new LinkedList<>();
              String building = null;
              Set<String> motorWayTags = new HashSet<>();
              Integer maxSpeed = null;
              String streetName = null;
              Boolean oneWay = null;
              Set<String> landuseTags = new HashSet<>();
              Set<String> railwayTags = new HashSet<>();
              Set<String> buildingTags = new HashSet<>();
              Set<String> cycleWayTags = new HashSet<>();
              wayChildrenParsing:
              while (!(parser.getEventType() == XMLStreamConstants.END_ELEMENT && parser.
                getLocalName().equals("way"))) {
                if (parser.getEventType() == XMLStreamConstants.START_ELEMENT) {
                  if (parser.getLocalName().equalsIgnoreCase("nd")) {
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                      if (parser.getAttributeLocalName(i).
                        equalsIgnoreCase("ref")) {
                        String refId = parser.getAttributeValue(null,
                          "ref");
                        osmWayNodeRefs.add(refId);
                      }
                    }
                    /* e.g. <tag k="name" v="Heiligengeistwall"/> */
                  } else if (parser.getLocalName().equalsIgnoreCase("tag")) {
                    String kValue = parser.getAttributeValue(0);
                    String vValue = parser.getAttributeValue(1);
                    if (kValue.equalsIgnoreCase("name")) {
                      if (streetName != null) {
                        log.info(String.format(
                          "multiple name tags for way with OSM id %s, skipping",
                          osmWayId));
                      }
                      streetName = vValue;
                    } else if (kValue.equalsIgnoreCase("maxspeed")) {
                      try {
                        maxSpeed = Integer.valueOf(vValue);
                      } catch (NumberFormatException e) {
                        /* maxspeed with unit */
                        Matcher matcher = MAX_SPEED_PATTERN.matcher(vValue);
                        if (matcher.find()) {
                          maxSpeed = Integer.valueOf(matcher.group(1));
                          if (matcher.group(3).equalsIgnoreCase("mph")) {
                            maxSpeed = (int) (maxSpeed * MILE_TO_KM);
                          }
                        }
                      }
                    } else if (kValue.equalsIgnoreCase("highway")) {
                      for (String nonInterestingHighway : NON_INTERESTING_HIGHWAYS) {
                        if (vValue.equalsIgnoreCase(nonInterestingHighway)) {
                          osmWayId = null;
                          break wayChildrenParsing;
                        }
                      }
                      motorWayTags.add(vValue);
                    } else if (kValue.equalsIgnoreCase("oneway") && vValue.
                      equalsIgnoreCase("yes")) {
                      oneWay = true;
                    } else if (kValue.equalsIgnoreCase("landuse")) {
                      landuseTags.add(vValue);
                    } else if (kValue.equalsIgnoreCase("railway")) {
                      railwayTags.add(vValue);
                    } else if (kValue.equalsIgnoreCase("building")) {
                      buildingTags.add(vValue);
                    } else if (kValue.equalsIgnoreCase("cycleway")) {
                      cycleWayTags.add(vValue);
                    }
                  }
                }
                parser.next();
              } //end while parsing child nodes
              //evalutate parsed information (create way object or throw away);
              //treat conflicts (currently no treatment)
              if (!buildingTags.isEmpty()) {
                Building lastBuilding = new OSMBuilding(idGenerator.getNextId(),
                  osmWayId);
                for (String osmWayNodeRef : osmWayNodeRefs) {
                  TrafficNode osmWayNode = osmIdNodeMap.get(osmWayNodeRef);
                  if (osmWayNode == null) {
                    osmWayNode = new OSMTrafficNode(idGenerator.getNextId(),
                      osmWayNodeRef,
                      -1, //passing null as geoLocation causes 
                      //NullPointerException
                      -1
                    );
                    osmIdNodeMap.put(osmWayNodeRef,
                      osmWayNode);
                  }
                  if (lastBuilding.getGeoInfo() == null) {
                    lastBuilding.setGeoInfo(new BaseBoundary(idGenerator.
                      getNextId()));
                  }
                  lastBuilding.getGeoInfo().getBoundaryCoordinates().add(
                    osmWayNode);
                }
                buildings.add(lastBuilding);
              } else {
                if (osmWayNodeRefs.size() < 2) {
                  log.
                    info("way with less than 2 nodes with OSM id %s, skipping",
                      osmWayId);
                } else {
                  TrafficWay lastWay = null;
                  if (!motorWayTags.isEmpty()) {
                    lastWay = new OSMTrafficWay(
                      idGenerator.getNextId(),
                      osmWayId
                    );
                  } else if (!cycleWayTags.isEmpty()) {
                    lastWay = new OSMCycleWay(
                      idGenerator.getNextId(),
                      osmWayId
                    );
                  } else {
                    //unclassified way
                    lastWay = new OSMTrafficWay(
                      idGenerator.getNextId(),
                      osmWayId
                    );
                  }
                  List<TrafficNode> wayNodes = new LinkedList<>();
                  for (String osmWayNodeRef : osmWayNodeRefs) {
                    TrafficNode trafficNode = osmIdNodeMap.get(osmWayNodeRef);
                    if (trafficNode == null) {
                      trafficNode = new OSMTrafficNode(idGenerator.getNextId(),
                        osmWayNodeRef,
                        -1, //passing null as geoLocation causes 
                        //NullPointerException
                        -1
                      );
                      osmIdNodeMap.put(osmWayNodeRef,
                        trafficNode);
                    }
                    wayNodes.add(trafficNode);
                  }
                  Iterator<TrafficNode> wayNodesItr = wayNodes.iterator();
                  TrafficNode lastWayNode = wayNodesItr.next();
                  while (wayNodesItr.hasNext()) {
                    TrafficNode wayNode = wayNodesItr.next();
                    TrafficEdge wayEdge = new TrafficEdge(idGenerator.
                      getNextId(),
                      lastWayNode,
                      wayNode);
                    lastWayNode = wayNode;
                    wayEdge.setOneWay(oneWay);
                    lastWay.getEdgeList().add(wayEdge);
                  }
                  ways.add(lastWay);
                }
              }
              osmWayId = null; //reset to null in order to leave context
            } else if (lastBusRoute != null) {
              //current context is relation/bus route
              for (String osmNodeRef : osmNodeRefs) {
                TrafficNode trafficNode = osmIdNodeMap.get(osmNodeRef);
                BusStop busStop = null;
                if (trafficNode != null) {
                  //make the trafficNode a busStop
                  if (!(trafficNode instanceof BusStop)) {
                    busStop = new OSMBusStop(idGenerator.getNextId(),
                      osmNodeRef,
                      null, //@TODO busStopName
                      null, //busStopInformation 
                      trafficNode);
                    osmIdNodeMap.put(osmNodeRef,
                      busStop);
                  }
                } else {
                  busStop = new OSMBusStop(idGenerator.getNextId(),
                    osmNodeRef,
                    null,
                    null,
                    -1, //passing null as geoLocation causes 
                    //NullPointerException
                    -1);
                  osmIdNodeMap.put(osmNodeRef,
                    busStop);
                }
                lastBusRoute.getBusStops().add(busStop);
              }
              lastBusRoute.setRouteLongName(routeLongName);
              busRoutes.add(lastBusRoute);
              lastBusRoute = null;
            } else {
              //... if there's no current context, create new context 
              if (parser.getLocalName().equalsIgnoreCase("node")) {
                String osmId = null;
                Double nodeLat = null;
                Double nodeLon = null;
                for (int i = 0; i < parser.getAttributeCount(); i++) {

                  if (parser.getAttributeLocalName(i).equalsIgnoreCase("id")) {
                    osmId = parser.getAttributeValue(i);
                  } else if (parser.getAttributeLocalName(i).equalsIgnoreCase(
                    "lat")) {
                    nodeLat = Double.valueOf(parser.getAttributeValue(i));
                  } else if (parser.getAttributeLocalName(i).equalsIgnoreCase(
                    "lon")) {
                    nodeLon = Double.valueOf(parser.getAttributeValue(i));
                  }
                }

                if ((osmId != null) && (nodeLat != null) && (nodeLon != null)) {
                  lastNode = new OSMTrafficNode(idGenerator.getNextId(),
                    osmId,
                    new BaseCoordinate(idGenerator.getNextId(),
                      nodeLat,
                      nodeLon));
                  osmIdNodeMap.put(osmId,
                    lastNode);
                } else {
                  log.info("missing id, lat or lng on node, skipped");
                }
                /* Collect all the ways */
              } else if (parser.getLocalName().equalsIgnoreCase("way")) {
                osmWayId = parser.getAttributeValue(null,
                  "id");
                if (osmWayId == null) {
                  log.info("node element without id attribute");
                }
                //no further interesting attributes in way element
                /* Collect node references <way>..<nd ref="180449581"/>..</way>  in first if statements */
              } else if (parser.getLocalName().equalsIgnoreCase("relation")) {
                //parse bus routes
                //save all content from stream and treat it in the next step
                osmNodeRefs = new LinkedList<>();
                routeLongName = null;
                parser.next();
                while (!(parser.getEventType() == XMLStreamConstants.END_ELEMENT && parser.
                  getLocalName().equals("relation"))) {
                  if (parser.getEventType() == XMLStreamConstants.START_ELEMENT) {
                    if (parser.getLocalName().equals("member")) {
                      String type = parser.getAttributeValue(null,
                        "type");
                      if (type != null && type.equals("node")) {
                        String role = parser.getAttributeValue(null,
                          "role");
                        if (role != null && role.equals("stop")) {
                          String ref = parser.getAttributeValue(null,
                            "ref");
                          if (ref == null) {
                            log.info("node without ref attribute, skipping");
                          } else {
                            osmNodeRefs.add(ref);
                          }
                        }
                      }
                    } else if (parser.getLocalName().equals("tag")) {
                      String k = parser.getAttributeValue(null,
                        "k");
                      if (k != null) {
                        if (k.equals("route")) {
                          String v = parser.getAttributeValue(null,
                            "v");
                          if (v != null && v.equals("bus")) {
                            lastBusRoute = new OSMBusRoute(idGenerator.
                              getNextId());
                          }
                        } else if (k.equals("name")) {
                          String v = parser.getAttributeValue(null,
                            "v");
                          routeLongName = v;
                        }
                      }
                    }
                  }
                  parser.next();
                }
              }
            }
            break;
          //end case XMLStreamConstants.START_ELEMENT
          case XMLStreamConstants.END_ELEMENT:
            break;
          default:
            break;
        } //end switch
        parser.next();
      }
    } catch (FactoryConfigurationError | XMLStreamException | NumberFormatException e) {
      log.error(
        "exception in OSM file parsing (see nested exception for details)",
        e.getMessage(),
        e);
    } finally {
      if (parser != null) {
        try {
          parser.close();
        } catch (XMLStreamException ex) {
          log.warn("see nested exception",
            ex);
        }
      }
    }

    /* Set real nodes for all ways and save used nodes */
    city.getCityInfrastructureData().setNodes(nodes);
    city.getCityInfrastructureData().setBusStops(busStops);
    city.getCityInfrastructureData().setWays(ways);
    city.getCityInfrastructureData().setMotorWays(extractMotorWays(ways));

    OuterLoop:
    for (MotorWay way : this.city.getCityInfrastructureData().
      getWaysWithBusStops()) {
      for (NavigationNode node : way.getNodeList()) {
        if (node instanceof TrafficNode && ((TrafficNode) node).getBusStop() != null) {
          this.city.getCityInfrastructureData().getBusStops().add(
            ((TrafficNode) node).
            getBusStop());
          this.city.getCityInfrastructureData().getWaysWithBusStops().add(way);
          continue OuterLoop;
        }
      }
    }

    city.getCityInfrastructureData().setLandUseWays(this.extractLanduseWays(
      ways));
    city.getCityInfrastructureData().
      setCycleWays(this.extractCycleWays(ways));
    city.getCityInfrastructureData().setRoundAbouts(this.extractRoundAbouts(
      this.city.getCityInfrastructureData().getNodes()));

    Set<TrafficWay> cycleAndMotorwaySet = new HashSet<>();
    for (TrafficWay trafficWay : this.city.getCityInfrastructureData().
      getMotorWays()) {
      cycleAndMotorwaySet.add(trafficWay);
    }
    cycleAndMotorwaySet.addAll(this.city.getCityInfrastructureData().
      getMotorWays());
    city.getCityInfrastructureData().setCycleAndMotorways(new HashSet<>(
      cycleAndMotorwaySet));

    /* find used street nodes and junction nodes */
    Set<TrafficNode> usedStreetNodes = new HashSet<>();
    Map<TrafficNode, Set<Way<?, ?>>> junctionWayMap = new HashMap<>();
    for (MotorWay way : this.city.getCityInfrastructureData().
      getWaysWithBusStops()) {
      for (TrafficNode node : way.getNodeList()) {
        if (usedStreetNodes.contains(node)) {
          Set<Way<?, ?>> tmpJunctionWays = junctionWayMap.get(node);
          if (tmpJunctionWays == null) {
            tmpJunctionWays = new HashSet<>();
            junctionWayMap.put(node,
              tmpJunctionWays);
          }
          tmpJunctionWays.add(way);
        }
      }
    }

    city.getCityInfrastructureData().setStreetNodes(new HashSet<>(
      usedStreetNodes));

    /* Check the junction nodes. They need at least three edges: */
    city.getCityInfrastructureData().setJunctionNodes(
      new HashSet<TrafficNode>());
    for (Entry<TrafficNode, Set<Way<?, ?>>> entry : junctionWayMap.entrySet()) {
      if (entry.getValue().size() >= 3) {
        city.getCityInfrastructureData().getJunctionNodes().add(entry.getKey());
        /* check if three or more edges (junction is not the last node on one of the ways): */
      } else {
        for (Way<?, ?> way : entry.getValue()) {
          if (way.getNodeList().size() > 1) {
            if (!way.getNodeList().get(0).equals(entry.getKey()) && !way.
              getNodeList().get(way.getNodeList().size() - 1).equals(entry.
                getKey())) {
              city.getCityInfrastructureData().getJunctionNodes().add(entry.
                getKey());
              break;
            }
          }
        }
      }
    }

    /* build the pr-trees for nodes */
    this.allNodesTree = new PRTree<>(new NodeMBRConverter(),
      prTreeBranchFactor);
    this.allNodesTree.load(this.city.getCityInfrastructureData().getNodes());
    this.streetNodesTree = new PRTree<>(new NodeMBRConverter(),
      prTreeBranchFactor);
    this.streetNodesTree.load(this.city.getCityInfrastructureData().
      getStreetNodes());
    this.junctionNodesTree = new PRTree<>(new NodeMBRConverter(),
      prTreeBranchFactor);
    this.junctionNodesTree.load(this.city.getCityInfrastructureData().
      getJunctionNodes());

    if (this.city.getGeoInfo() == null) {
      this.city.setGeoInfo(new BaseBoundary(idGenerator.getNextId()));
    }
    List<BaseCoordinate> boundaryCoordinates = new LinkedList<>();
    for (Coordinate boundaryCoordinate : GeoToolsBootstrapping.
      getGeometryFactory().createMultiPoint(
        nodes.toArray(
          new Coordinate[nodes.size()]
        )
      ).getEnvelope().getCoordinates()) {
      boundaryCoordinates.add(new BaseCoordinate(idGenerator.getNextId(),
        boundaryCoordinate));
    }
    this.city.getGeoInfo().setBoundaryCoordinates(boundaryCoordinates);
    this.city.getCityInfrastructureData().setBuildings(this.extractBuildings(
      ways));
    /* build the pr-tree for buildings */
    this.buildingTree = new PRTree<>(new BuildingMBRConverter(),
      prTreeBranchFactor);
    this.buildingTree.load(this.city.getCityInfrastructureData().getBuildings());
  }

  /**
   * Uses a way to create a polygon.
   *
   * @param way
   * @return
   */
  private Polygon wayToPolygon2d(Way<?, ?> way) {
    List<Coordinate> wayCoords = new LinkedList<>();
    for (NavigationNode navigationNode : way.getNodeList()) {
      wayCoords.add(navigationNode);
    }

    return GeoToolsBootstrapping.getGeometryFactory().createPolygon(wayCoords.
      toArray(new Coordinate[wayCoords.size()]));
  }

  @Override
  public List<NavigationNode> getNodesInBoundary(Envelope boundary) {

    List<NavigationNode> nodes = new ArrayList<>();
    for (NavigationNode node : this.allNodesTree.find(boundary.getMinX(),
      boundary
      .getMinY(),
      boundary.getMaxX(),
      boundary
      .getMaxY())) {
      nodes.add(node);
    }

    return nodes;
  }

  @Override
  public NavigationNode getNearestJunctionNode(double latitude,
    double longitude) {
    List<DistanceResult<NavigationNode>> results = this.junctionNodesTree.
      nearestNeighbour(this.prTreeDistanceCalculator,
        this.prTreeNodeFilter,
        1,
        new SimplePointND(latitude,
          longitude));

    if (results.size() > 0) {
      return results.get(0).get();
    }

    throw new RuntimeException(
      "No nearest node found for: latitude: " + latitude + " longitude: " + longitude);
  }

  @Override
  public TrafficCity createCity() {
    return city;
  }

  @Override
  public void parseFile(File osmFile) throws IOException {
    parseStream(new FileInputStream(osmFile));
  }

  /**
   * Distance calculator for our buildings.
   *
   * @author Timo
   */
  @SuppressWarnings("unused")
  private static class BuildingDistance implements DistanceCalculator<Building> {

    private static final long serialVersionUID = 7659553740208196204L;

    @Override
    public double distanceTo(Building b,
      PointND p) {
      /* euclidean distance */
      return Math.sqrt(Math.pow(
        (b.getGeoInfo().retrieveCenterPoint().getX() - p.
        getOrd(0)),
        2)
        + Math.pow((b.getGeoInfo().retrieveCenterPoint().getY() - p.getOrd(1)),
          2));
    }
  }

  /**
   * MBRConverter for our buildings.
   *
   * @author Timo
   */
  private static class BuildingMBRConverter implements MBRConverter<Building> {

    private static final long serialVersionUID = -4200086366401051943L;

    @Override
    public int getDimensions() {
      return 2;
    }

    @Override
    public double getMax(int arg0,
      Building arg1) {
      return arg0 == 0 ? arg1.getGeoInfo().retrieveCenterPoint().getX() : arg1.
        getGeoInfo().retrieveCenterPoint().getY();
    }

    @Override
    public double getMin(int arg0,
      Building arg1) {
      return arg0 == 0 ? arg1.getGeoInfo().retrieveCenterPoint().getX() : arg1.
        getGeoInfo().retrieveCenterPoint().getY();
    }
  }

  /**
   * Distance calculator for our nodes.
   *
   * @author Timo
   */
  private static class NodeDistance implements
    DistanceCalculator<NavigationNode> {

    private static final long serialVersionUID = -1997875613977347848L;

    @Override
    public double distanceTo(NavigationNode n,
      PointND p) {
      /* euclidean distance */
      return Math.sqrt(Math.pow((n.getX() - p.getOrd(0)),
        2)
        + Math.pow((n.getY() - p.getOrd(1)),
          2));
    }
  }

  /**
   * MBRConverter for our nodes.
   *
   * @author Timo
   */
  private static class NodeMBRConverter implements MBRConverter<NavigationNode> {

    private static final long serialVersionUID = 4175774127701744257L;

    @Override
    public int getDimensions() {
      return 2;
    }

    @Override
    public double getMax(int arg0,
      NavigationNode arg1) {
      return arg0 == 0 ? arg1.getX() : arg1.
        getY();
    }

    @Override
    public double getMin(int arg0,
      NavigationNode arg1) {
      return arg0 == 0 ? arg1.getX() : arg1.
        getY();
    }
  }

  /**
   * Node filter for our buildings.
   *
   * @author Timo
   */
  @SuppressWarnings("unused")
  private static class PRTreeBuildingFilter implements NodeFilter<Building> {

    private static final long serialVersionUID = -5316364560210465675L;

    @Override
    public boolean accept(Building arg0) {
      return true;
    }
  }

  /**
   * Node filter for our nodes.
   *
   * @author Timo
   */
  private static class PRTreeNodeFilter implements NodeFilter<NavigationNode> {

    private static final long serialVersionUID = -7994547301033310792L;

    @Override
    public boolean accept(NavigationNode arg0) {
      return true;
    }
  }
}
