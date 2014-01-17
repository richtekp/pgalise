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
package de.pgalise.simulation.traffic;

import de.pgalise.simulation.traffic.entity.TrafficWay;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.entity.OSMBusStop;
import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.simulation.shared.entity.Building;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
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

import com.Ostermiller.util.CSVParse;
import com.Ostermiller.util.CSVParser;
import com.bbn.openmap.geo.Geo;
import com.bbn.openmap.geo.OMGeo.Polygon;
import com.vividsolutions.jts.geom.Envelope;
import de.pgalise.simulation.service.IdGenerator;

import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.shared.tag.LanduseTagEnum;
import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.simulation.shared.entity.BaseGeoInfo;
import de.pgalise.simulation.traffic.entity.CityInfrastructureData;
import de.pgalise.simulation.traffic.service.FileBasedCityInfrastructureDataService;
import de.pgalise.simulation.shared.entity.Way;
import de.pgalise.simulation.shared.tag.WayTagEnum;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;
import de.pgalise.util.cityinfrastructure.DefaultBuildingEnergyProfileStrategy;
import de.pgalise.util.cityinfrastructure.impl.GraphConstructor;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

/**
 * The DefaultFileBasedCityInfrastructureDataService is the used
 * {@link CityInfrastructureDataService} in this prototype. It parsed the needed
 * information from given openstreetmap and busstop files. The methods {@link CityInfrastructureDataService#getNearestJunctionNode(double, double)},
 * {@link CityInfrastructureDataService#getNearestNode(double, double)}, {@link CityInfrastructureDataService#getNearestStreetNode(double, double)
 * and {@link CityInfrastructureDataService#getNodesInBoundary(Boundary) are implements with a PR-Tree.
 * @author Timo
 */
/*
 @TODO: separate logic from data
 */
@Stateful
public class DefaultFileBasedCityInfrastructureDataService implements
  FileBasedCityInfrastructureDataService {

  private static final Logger log = LoggerFactory.getLogger(
    DefaultFileBasedCityInfrastructureDataService.class);
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
  private CityInfrastructureData cityInfrastructureData;
  @EJB
  private IdGenerator idGenerator;
  /**
   * Distance calculator for the pr-tree
   */
  private DistanceCalculator<NavigationNode> prTreeDistanceCalculator;
  /**
   * Node filter for the pr-tree
   */
  private NodeFilter<NavigationNode> prTreeNodeFilter;
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

  public DefaultFileBasedCityInfrastructureDataService() {
  }

  /**
   * parses the passed input streams and initializes graph
   *
   * @param id
   * @param osmIN the opstreetmap file
   * @param busStopIN the gtfs stops.txt file
   * @param buildingEnergyProfileStrategy the strategy to find the best energy
   * profile for a buidling
   * @throws IOException
   */
  public DefaultFileBasedCityInfrastructureDataService(Long id,
    InputStream osmIN,
    InputStream busStopIN,
    BuildingEnergyProfileStrategy buildingEnergyProfileStrategy) throws IOException {
    this.buildingEnergyProfileStrategy = buildingEnergyProfileStrategy;
    this.prTreeDistanceCalculator = new NodeDistance();
    this.prTreeNodeFilter = new PRTreeNodeFilter();
    this.parse(osmIN,
      busStopIN);
  }

  @PostConstruct
  public void init() {
    this.cityInfrastructureData = new CityInfrastructureData(idGenerator.
      getNextId());
  }

  /**
   * Converts the circle into a rectangle.
   *
   * @param centerPoint
   * @param radiusInMeter
   * @return
   */
  private Envelope circleToRectangle(JaxRSCoordinate centerPoint,
    int radiusInMeter) {

    List<JaxRSCoordinate> tmpPoints = new ArrayList<>();
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
      tmpPoints.add(new JaxRSCoordinate(lat,
        lng));
    }

    double northEastLat = Double.MIN_VALUE;
    double northEastLng = Double.MIN_VALUE;
    double southWestLat = Double.MAX_VALUE;
    double southWestLng = Double.MAX_VALUE;

    for (JaxRSCoordinate p : tmpPoints) {
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

    return new Envelope(new JaxRSCoordinate(northEastLat,
      northEastLng),
      new JaxRSCoordinate(southWestLat,
        southWestLng));
  }

  public TrafficGraph getTrafficGraph() {
    return trafficGraph;
  }

  /**
   * Combines motorways with busstops. Some of the nodes in
   *
   * @Way.getNodeList() are instances of
   * @BusStop!
   *
   * @param osmFile
   * @return
   */
  private List<TrafficWay> combineMotorWaysWithBusStops(
    List<TrafficWay> motorWays,
    List<BusStop> busStops) {
    List<TrafficWay> wayList = new ArrayList<>(motorWays);

    /* Eliminate multiple bus stops: */
    Set<String> busStopNameSet = new HashSet<>();
    List<BusStop> tmpBusStop = new ArrayList<>();
    for (BusStop busStop : busStops) {
      if (busStop != null) {
        int before = busStopNameSet.size();
        busStopNameSet.add(busStop.getStopName());
        if (before < busStopNameSet.size()) {
          tmpBusStop.add(busStop);
        }
      }
    }

    /* Build a node way map: */
    Map<NavigationNode, List<TrafficWay>> nodeWayMap = new HashMap<>();
    for (TrafficWay way : wayList) {
      for (NavigationNode node : way.getNodeList()) {
        List<TrafficWay> tmpWayList = nodeWayMap.get(node);
        if (tmpWayList == null) {
          tmpWayList = new ArrayList<>();
          nodeWayMap.put(node,
            tmpWayList);
        }
        tmpWayList.add(way);
      }
    }

    /* Add busstops to ways as new nodes: */
    class NodeDistanceWrapper {

      private final double distance;
      private final NavigationNode node;

      private NodeDistanceWrapper(NavigationNode node,
        double distance) {
        this.node = node;
        this.distance = distance;
      }

      public double getDistance() {
        return this.distance;
      }

      public NavigationNode getNode() {
        return this.node;
      }
    }

    BusStopLoop:
    for (BusStop busStop : busStops) {

      List<NodeDistanceWrapper> nodeDistanceList = new ArrayList<>();

      for (NavigationNode node : nodeWayMap.keySet()) {
        nodeDistanceList.add(new NodeDistanceWrapper(node,
          this.getDistanceInKM(busStop.getGeoLocation().getX(),
            busStop.getGeoLocation().getY(),
            node.getGeoLocation().getX(),
            node.getGeoLocation().getY())));
      }

      Collections.sort(nodeDistanceList,
        new Comparator<NodeDistanceWrapper>() {
          @Override
          public int compare(NodeDistanceWrapper o1,
            NodeDistanceWrapper o2) {
            if (o1.getDistance() < o2.getDistance()) {
              return -1;
            } else if (o1.getDistance() > o2.getDistance()) {
              return 1;
            }

            return 0;
          }
        });

      /* find possible street for the busstop: */
      for (int i = 0; i < nodeDistanceList.size(); i++) {
        NavigationNode node1 = nodeDistanceList.get(i).getNode();

        for (int j = i + 1; j < nodeDistanceList.size(); j++) {
          NavigationNode node2 = nodeDistanceList.get(j).getNode();
          List<TrafficWay> waysNode2 = nodeWayMap.get(node2);

          for (TrafficWay possibleWay : nodeWayMap.get(node1)) {
            /*
             * Node1 and node2 must be on the same street and the distance between node1 and node2 must be
             * larger than the distance between bus stop and node2.
             */
            if (waysNode2.contains(possibleWay)
              && (this.getDistanceInKM(node1.getGeoLocation().getX(),
                node1.getGeoLocation().getY(),
                node2.getGeoLocation().getX(),
                node2.getGeoLocation().getY()) <= this.getDistanceInKM(
                node2.getGeoLocation().getX(),
                node2.getGeoLocation().getY(),
                busStop.getGeoLocation().getX(),
                busStop.getGeoLocation().getY()))) {

              /* Insert busstop as new node: */
              List<TrafficNode> newNodeList = new ArrayList<>();
              boolean nodeFound = false;
              for (TrafficNode node : possibleWay.getNodeList()) {
                if (!nodeFound && (node.equals(node1) || node.equals(node2))) {
                  newNodeList.add(busStop);
                  nodeFound = true;
                }

                newNodeList.add(node);
              }

              possibleWay.setEdgeList(newNodeList,
                trafficGraph);

              continue BusStopLoop;
            }
          }
        }
      }
    }

    return wayList;
  }

  /**
   * Returns a list of buildings.
   *
   * @param wayList
   * @return
   */
  private List<Building> extractBuildings(List<TrafficWay> wayList) {
    List<Building> buildingList = new ArrayList<>();

    List<String> tmpLandUseList = new ArrayList<>();

    /* Build a map with interesting landuse polygons: */
    /**
     * Map<String = landuse tag, List<SimplePolygon2D = polygon of the landuse
     * are.
     */
    Map<String, List<Polygon>> landUseMap = new HashMap<>();
    for (Way<?, ?> landuse : this.cityInfrastructureData.getLandUseWays()) {
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

        if (node.getGeoLocation().getX() > maxLat) {
          maxLat = node.getGeoLocation().getX();
        }
        if (node.getGeoLocation().getX() < minLat) {
          minLat = node.getGeoLocation().getX();
        }
        if (node.getGeoLocation().getY() > maxLng) {
          maxLng = node.getGeoLocation().getY();
        }
        if (node.getGeoLocation().getY() < minLng) {
          minLng = node.getGeoLocation().getY();
        }
      }

      /* find other tags */
      for (NavigationNode node : this.getNodesInBoundary(new Envelope(
        new JaxRSCoordinate(maxLat,
          maxLng),
        new JaxRSCoordinate(
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
          if (landUsePolygon.isPointInside(new Geo(centerLat,
            centerLon))) {
            tmpLandUseList.add(entry.getKey());
            landUseArea = entry.getKey();
            break outerLoop;
          }
        }
      }

      /* calc area in m² */
      double length = this.getDistanceInKM(maxLat,
        maxLng,
        maxLat,
        minLng) * 1000.0;
      double width = this.getDistanceInKM(maxLat,
        maxLng,
        minLat,
        maxLng) * 1000.0;
      double area = length * width;
      area *= area;

      BaseGeoInfo buildingPosition = new BaseGeoInfo(idGenerator.getNextId(),
        GeoToolsBootstrapping.
        getGEOMETRY_FACTORY().createPolygon(
          new JaxRSCoordinate[]{new JaxRSCoordinate(maxLat,
              maxLng), new JaxRSCoordinate(minLat,
              minLng)}));
      buildingList.add(
        new Building(idGenerator.getNextId(),
          buildingPosition.getCenterPoint(),
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
          military,
          buildingPosition));
    }

    return buildingList;
  }

  /**
   * Extracts the cycle ways.
   *
   * @param wayList
   * @return
   */
  private List<TrafficWay> extractCycleWays(List<TrafficWay> wayList) {
    List<TrafficWay> cycleWays0 = new ArrayList<>();

    for (TrafficWay way : wayList) {
      if ((way.getWayTags().contains(WayTagEnum.CYCLEWAY))) {
        cycleWays0.add(way);
      }
    }

    return cycleWays0;
  }

  /**
   * Extracts the landuse ways.
   *
   * @param ways
   * @return
   */
  private List<TrafficWay> extractLanduseWays(List<TrafficWay> ways) {
    List<TrafficWay> wayList = new ArrayList<>();

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
  private List<TrafficWay> extractMotorWays(List<TrafficWay> ways) {
    List<TrafficWay> wayList = new ArrayList<>();

    OuterLoop:
    for (Way<?, ?> way : ways) {
      if (way instanceof TrafficWay) {
        TrafficWay wayCast = (TrafficWay) way;
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
  private List<TrafficNode> extractRoundAbouts(List<TrafficNode> usedNodes) {
    List<TrafficNode> roundAboutList = new ArrayList<>();

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
    JaxRSCoordinate geolocation,
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
  public List<Building> getBuildingsInRadius(JaxRSCoordinate centerPoint,
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
      if (this.getDistanceInMeter(centerPoint,
        building.getPosition().getCenterPoint()) <= radiusInMeter) {
        buildings0.add(building);
      }
    }

    return buildings0;
  }

  /**
   * Computes the distance between the two points in km.
   */
  private double getDistanceInKM(double myLat,
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
  private double getDistanceInMeter(JaxRSCoordinate start,
    JaxRSCoordinate target) {

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
   * @param osmIN osm-file
   * @param busStopIN busstop file
   * @throws IOException
   */
  @Override
  public void parse(InputStream osmIN,
    InputStream busStopIN) throws IOException {
    /* Holds the way data till we collected every node */
    List<TmpWay> tmpWayList = new ArrayList<>();
    List<BusStop> tmpBusStopList = new ArrayList<>();
    /* String = node id / nd ref */
    Map<String, TrafficNode> nodeMap = new HashMap<>();

    NavigationNode northEastBoundary = null;
    NavigationNode southWestBoundary = null;

    XMLStreamReader parser = null;

    try {
      parser = XMLInputFactory.newInstance().createXMLStreamReader(osmIN);

      TmpWay lastFoundWay = null;
      BusStop lastBusstop = null;
      TrafficNode lastNode = null;

      while (parser.hasNext()) {
        switch (parser.getEventType()) {

          case XMLStreamConstants.START_ELEMENT:
            /* Collect all the nodes */

            String id = null;
            Double lat = null;
            Double lon = null;

            if (parser.getLocalName().equalsIgnoreCase("node")) {
              for (int i = 0; i < parser.getAttributeCount(); i++) {

                if (parser.getAttributeLocalName(i).equalsIgnoreCase("id")) {
                  id = parser.getAttributeValue(i);
                } else if (parser.getAttributeLocalName(i).equalsIgnoreCase(
                  "lat")) {
                  lat = Double.valueOf(parser.getAttributeValue(i));
                } else if (parser.getAttributeLocalName(i).equalsIgnoreCase(
                  "lon")) {
                  lon = Double.valueOf(parser.getAttributeValue(i));
                }
              }

              if ((id != null) && (lat != null) && (lon != null)) {
                lastNode = new TrafficNode(idGenerator.getNextId(),
                  new JaxRSCoordinate(lat,
                    lon));
                nodeMap.put(id,
                  lastNode);

                if (northEastBoundary == null
                  || (northEastBoundary.getGeoLocation().getX() < lat && northEastBoundary.
                  getGeoLocation().getY() < lon)) {
                  northEastBoundary = lastNode;
                } else if (southWestBoundary == null
                  || (southWestBoundary.getGeoLocation().getX() > lat && southWestBoundary.
                  getGeoLocation().getY() > lon)) {
                  southWestBoundary = lastNode;
                }
              }

              /* Collect all the ways */
            } else if ((lastNode != null) && parser.getLocalName().
              equalsIgnoreCase("tag")) {

              String kValue = parser.getAttributeValue(0);
              String vValue = parser.getAttributeValue(1);

              if (kValue.equalsIgnoreCase("highway")) {
                if (vValue.equalsIgnoreCase("bus_stop")) {
                  lastBusstop = new BusStop(idGenerator.getNextId(),
                    "",
                    null,
                    new JaxRSCoordinate(lastNode.getGeoLocation().getX(),
                      lastNode.getGeoLocation().getY()));
                } else if (vValue.equalsIgnoreCase("mini_roundabout")
                  || vValue.equalsIgnoreCase("roundabout")) {
                  lastNode.setRoundabout(true);
                }
              } else if (kValue.equalsIgnoreCase("name") && (lastBusstop != null)) {
                lastBusstop.setStopName(vValue);
                tmpBusStopList.add(lastBusstop);
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
            } else if (parser.getLocalName().equalsIgnoreCase("way")) {

              lastFoundWay = new TmpWay();
              lastNode = null;
              lastBusstop = null;

              /* Collect node references <way>..<nd ref="180449581"/>..</way> */
            } else if (lastFoundWay != null) {
              if (parser.getLocalName().equalsIgnoreCase("nd")) {
                for (int i = 0; i < parser.getAttributeCount(); i++) {
                  if (parser.getAttributeLocalName(i).equalsIgnoreCase("ref")) {
                    lastFoundWay.addNodeID(parser.getAttributeValue(i));
                  }
                }
                /* e.g. <tag k="name" v="Heiligengeistwall"/> */
              } else if (parser.getLocalName().equalsIgnoreCase("tag")) {
                String kValue = parser.getAttributeValue(0);
                String vValue = parser.getAttributeValue(1);
                if (kValue.equalsIgnoreCase("name")) {
                  lastFoundWay.setStreetName(vValue);
                } else if (kValue.equalsIgnoreCase("maxspeed")) {
                  try {
                    lastFoundWay.applyMaxSpeed(Integer.valueOf(vValue));
                  } catch (NumberFormatException e) {
                    /* maxspeed with unit */
                    Matcher matcher = MAX_SPEED_PATTERN.matcher(vValue);
                    if (matcher.find()) {
                      int maxSpeed = Integer.valueOf(matcher.group(1));
                      if (matcher.group(3).equalsIgnoreCase("mph")) {
                        maxSpeed = (int) (maxSpeed * MILE_TO_KM);
                      }
                    }
                  }
                } else if (kValue.equalsIgnoreCase("highway")) {
                  for (String nonInterestingHighway : NON_INTERESTING_HIGHWAYS) {
                    if (vValue.equalsIgnoreCase(nonInterestingHighway)) {
                      lastFoundWay = null;
                      break;
                    }
                  }
                  if (lastFoundWay != null) {
                    lastFoundWay.getWayTags().add(vValue);
                  }
                } else if (kValue.equalsIgnoreCase("oneway") && vValue.
                  equalsIgnoreCase("yes")) {
                  lastFoundWay.applyOneWay(true);
                } else if (kValue.equalsIgnoreCase("landuse")) {
                  lastFoundWay.getLanduseTags().add(vValue);
                } else if (kValue.equalsIgnoreCase("railway")) {
                  lastFoundWay.getWayTags().add(WayTagEnum.RAILWAY.
                    getStringValue());
                } else if (kValue.equalsIgnoreCase("building")) {
                  lastFoundWay.getWayTags().add(vValue);
                } else if (kValue.equalsIgnoreCase("cycleway")) {
                  lastFoundWay.getWayTags().add(WayTagEnum.CYCLEWAY.
                    getStringValue());
                }
              }
            }
            break;

          case XMLStreamConstants.END_ELEMENT:

            /* Way should be completed */
            if (parser.getLocalName().equalsIgnoreCase("way") && (lastFoundWay != null)
              && !lastFoundWay.getNodeReferenceList().isEmpty()) {
              tmpWayList.add(lastFoundWay);
            }

            if (!(parser.getLocalName().equalsIgnoreCase("tag") || parser.
              getLocalName().equalsIgnoreCase(
                "nd"))) {
              lastFoundWay = null;
            }

            break;
          default:
            break;
        }

        parser.next();
      }
    } catch (FactoryConfigurationError | XMLStreamException | NumberFormatException e) {
      log.error(e.getMessage(),
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
    Set<TrafficNode> usedNodes0 = new HashSet<>();
    List<TrafficWay> wayList = new ArrayList<>();
    OuterLoop:
    for (TmpWay way : tmpWayList) {
      List<TrafficNode> nodeList = new ArrayList<>();
      for (String nodeReference : way.getNodeReferenceList()) {
        TrafficNode node = nodeMap.get(nodeReference);
        if (node == null) {
          continue OuterLoop;
        }

        nodeList.add(node);
        usedNodes0.add(node);
      }

      if (way instanceof TrafficWay) {
        wayList.add(new TrafficWay(way.getEdgeList(),
          way.getStreetName()));
      } else {
        wayList.add(new TrafficWay(way.getEdgeList(),
          way.getStreetName()));
      }
    }

    cityInfrastructureData.setNodes(new ArrayList<>(usedNodes0));

    cityInfrastructureData.setWays(wayList);
    cityInfrastructureData.setMotorWays(extractMotorWays(wayList));

    /* Parse busstop file: */
    List<BusStop> tmpBusStops = this.parseBusStops(busStopIN);
    cityInfrastructureData.setMotorWaysWithBusStops(this.
      combineMotorWaysWithBusStops(
        this.cityInfrastructureData.getMotorWays(),
        tmpBusStops));
    OuterLoop:
    for (TrafficWay way : this.cityInfrastructureData.getMotorWaysWithBusStops()) {
      for (NavigationNode node : way.getNodeList()) {
        if (node instanceof TrafficNode && ((TrafficNode) node).getBusStop() != null) {
          this.cityInfrastructureData.getBusStops().add(((TrafficNode) node).
            getBusStop());
          this.cityInfrastructureData.getMotorWaysWithBusStops().add(way);
          continue OuterLoop;
        }
      }
    }

    cityInfrastructureData.setLandUseWays(this.extractLanduseWays(wayList));
    cityInfrastructureData.setCycleWays(this.extractCycleWays(wayList));
    cityInfrastructureData.setRoundAbouts(this.extractRoundAbouts(
      this.cityInfrastructureData.getNodes()));

    Set<TrafficWay> cycleAndMotorwaySet = new HashSet<>(
      this.cityInfrastructureData.getMotorWays());
    cycleAndMotorwaySet.addAll(this.cityInfrastructureData.getMotorWays());
    cityInfrastructureData.setCycleAndMotorways(new ArrayList<>(
      cycleAndMotorwaySet));

    /* find used street nodes and junction nodes */
    Set<TrafficNode> usedStreetNodes = new HashSet<>();
    Map<TrafficNode, Set<Way<?, ?>>> junctionWayMap = new HashMap<>();
    for (TrafficWay way : this.cityInfrastructureData.getMotorWaysWithBusStops()) {
      for (TrafficNode node : way.getNodeList()) {
        /* comparison with size is much faster than contains. */
        int sizeBefore = usedStreetNodes.size();
        usedStreetNodes.add(node);
        if (sizeBefore == usedStreetNodes.size()) {
          Set<Way<?, ?>> tmpJunctionWays = junctionWayMap.get(way);
          if (tmpJunctionWays == null) {
            tmpJunctionWays = new HashSet<>();
            junctionWayMap.put(node,
              tmpJunctionWays);
          }
          tmpJunctionWays.add(way);
        }

      }
    }

    cityInfrastructureData.setStreetNodes(new ArrayList<>(usedStreetNodes));

    /* Check the junction nodes. They need at least three edges: */
    cityInfrastructureData.setJunctionNodes(new LinkedList<TrafficNode>());
    for (Entry<TrafficNode, Set<Way<?, ?>>> entry : junctionWayMap.entrySet()) {
      if (entry.getValue().size() >= 3) {
        cityInfrastructureData.getJunctionNodes().add(entry.getKey());
        /* check if three or more edges (junction is not the last node on one of the ways): */
      } else {
        for (Way<?, ?> way : entry.getValue()) {
          if (way.getNodeList().size() > 1) {
            if (!way.getNodeList().get(0).equals(entry.getKey()) && !way.
              getNodeList().get(way.getNodeList().size() - 1).equals(entry.
                getKey())) {
              cityInfrastructureData.getJunctionNodes().add(entry.getKey());
              break;
            }
          }
        }
      }
    }

    /* build the pr-trees for nodes */
    this.allNodesTree = new PRTree<>(new NodeMBRConverter(),
      prTreeBranchFactor);
    this.allNodesTree.load(this.cityInfrastructureData.getNodes());
    this.streetNodesTree = new PRTree<>(new NodeMBRConverter(),
      prTreeBranchFactor);
    this.streetNodesTree.load(this.cityInfrastructureData.getStreetNodes());
    this.junctionNodesTree = new PRTree<>(new NodeMBRConverter(),
      prTreeBranchFactor);
    this.junctionNodesTree.load(this.cityInfrastructureData.getJunctionNodes());

    if (northEastBoundary != null) {
      cityInfrastructureData.setBoundary(new Envelope(new JaxRSCoordinate(
        northEastBoundary.
        getGeoLocation().getX(),
        northEastBoundary.getGeoLocation().getY()),
        new JaxRSCoordinate(southWestBoundary.getGeoLocation().getX(),
          southWestBoundary.getGeoLocation().getY())));
    }

    this.cityInfrastructureData.setBuildings(this.extractBuildings(wayList));
    /* build the pr-tree for buildings */
    this.buildingTree = new PRTree<>(new BuildingMBRConverter(),
      prTreeBranchFactor);
    this.buildingTree.load(this.cityInfrastructureData.getBuildings());
  }

  /**
   * Parses the a bus stop text file.
   *
   * @param busstopGTFSFile busstop gtfs csv file
   * @return
   * @throws IOException
   */
  private List<BusStop> parseBusStops(InputStream busstopGTFSFile) throws IOException {
    List<BusStop> busStopList = new LinkedList<>();

    CSVParse csvParser = new CSVParser(busstopGTFSFile);

    try {
      /* Read header: */
      int nameColumn = 0;
      int idColumn = 0;
      int latColumn = 0;
      int lngColumn = 0;
      String[] header = csvParser.getLine();
      for (int i = 0; i < header.length; i++) {
        switch (header[i]) {
          case "stop_name":
            nameColumn = i;
            break;
          case "stop_id":
            idColumn = i;
            break;
          case "stop_lat":
            latColumn = i;
            break;
          case "stop_lon":
            lngColumn = i;
            break;
          default:
            break;
        }
      }

      /* Parse other: */
      for (String[] text = csvParser.getLine(); text != null; text = csvParser.
        getLine()) {
        busStopList.add(new OSMBusStop(idGenerator.getNextId(),
          text[idColumn],
          text[nameColumn],
          null,
          new JaxRSCoordinate(Double.valueOf(text[latColumn]),
            Double
            .valueOf(text[lngColumn]))));
      }
    } catch (IOException e) {
      throw new IOException(e);
    } finally {
      try {
        csvParser.close();
      } catch (IOException e) {
        log.warn("see nested exception",
          e);
      }
      try {
        busstopGTFSFile.close();
      } catch (IOException e) {
        log.warn("see nested exception",
          e);
      }
    }

    return busStopList;
  }

  /**
   * Uses a way to create a polygon.
   *
   * @param way
   * @return
   */
  private Polygon wayToPolygon2d(Way<?, ?> way) {
    Geo[] geoArray = new Geo[way.getNodeList().size()];

    for (int i = 0; i < way.getNodeList().size(); i++) {
      NavigationNode tmpNode = way.getNodeList().get(i);
      geoArray[i] = new Geo(tmpNode.getGeoLocation().getX(),
        tmpNode.getGeoLocation().getY());
    }

    return new Polygon(geoArray);
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
  public Envelope getBoundary() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public CityInfrastructureData createCityInfrastructureData() {
    return cityInfrastructureData;
  }

  @Override
  public void parse(File osmFile,
    File busStopFile) throws IOException {
    parse(new FileInputStream(osmFile),
      new FileInputStream(busStopFile));
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
      return Math.sqrt(Math.pow((b.getPosition().getCenterPoint().getX() - p.
        getOrd(0)),
        2)
        + Math.pow((b.getPosition().getCenterPoint().getY() - p.getOrd(1)),
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
      return arg0 == 0 ? arg1.getPosition().getCenterPoint().getX() : arg1.
        getPosition().getCenterPoint().getY();
    }

    @Override
    public double getMin(int arg0,
      Building arg1) {
      return arg0 == 0 ? arg1.getPosition().getCenterPoint().getX() : arg1.
        getPosition().getCenterPoint().getY();
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
      return Math.sqrt(Math.pow((n.getGeoLocation().getX() - p.getOrd(0)),
        2)
        + Math.pow((n.getGeoLocation().getY() - p.getOrd(1)),
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
      return arg0 == 0 ? arg1.getGeoLocation().getX() : arg1.getGeoLocation().
        getY();
    }

    @Override
    public double getMin(int arg0,
      NavigationNode arg1) {
      return arg0 == 0 ? arg1.getGeoLocation().getX() : arg1.getGeoLocation().
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

  /**
   * Holds the OSM way data, till everything is collected.
   *
   * @author Timo
   */
  private static class TmpWay extends TrafficWay {

    private static final long serialVersionUID = 2310070836622850662L;
    private List<String> nodeReferenceList;
    private double maxSpeed;

    TmpWay() {
      this.nodeReferenceList = new ArrayList<>();
      super.applyOneWay(false);
//			super.setBuildingTypeMap(new HashMap<String, String>());
    }

    public void addNodeID(String nodeID) {
      this.nodeReferenceList.add(nodeID);
    }

    public List<String> getNodeReferenceList() {
      return this.nodeReferenceList;
    }

    @SuppressWarnings("unused")
    public void setNodeReferenceList(List<String> nodeReferenceList) {
      this.nodeReferenceList = nodeReferenceList;
    }

    public void setMaxSpeed(double maxSpeed) {
      this.maxSpeed = maxSpeed;
    }

    public double getMaxSpeed() {
      return maxSpeed;
    }
  }
}
