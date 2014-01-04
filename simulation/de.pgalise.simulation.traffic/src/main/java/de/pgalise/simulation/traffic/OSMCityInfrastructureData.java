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

import de.pgalise.simulation.shared.city.Building;
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

import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import de.pgalise.simulation.shared.city.JaxRSCoordinate;
import de.pgalise.simulation.shared.city.AmenityTag;
import de.pgalise.simulation.shared.city.AmenityTagCustom;
import de.pgalise.simulation.shared.city.AttractionTag;
import de.pgalise.simulation.shared.city.AttractionTagCustom;
import de.pgalise.simulation.shared.city.Boundary;
import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.shared.city.CraftTag;
import de.pgalise.simulation.shared.city.CraftTagCustom;
import de.pgalise.simulation.shared.city.EmergencyServiceTag;
import de.pgalise.simulation.shared.city.EmergencyServiceTagCustom;
import de.pgalise.simulation.shared.city.GamblingTag;
import de.pgalise.simulation.shared.city.GamblingTagCustom;
import de.pgalise.simulation.shared.city.LanduseTagCustom;
import de.pgalise.simulation.shared.city.LanduseTagEnum;
import de.pgalise.simulation.shared.city.LeisureTag;
import de.pgalise.simulation.shared.city.LeisureTagCustom;
import de.pgalise.simulation.shared.city.NavigationNode;
import de.pgalise.simulation.shared.city.BaseGeoInfo;
import de.pgalise.simulation.shared.city.PublicTransportTag;
import de.pgalise.simulation.shared.city.PublicTransportTagCustom;
import de.pgalise.simulation.shared.city.RepairTag;
import de.pgalise.simulation.shared.city.RepairTagCustom;
import de.pgalise.simulation.shared.city.SchoolTag;
import de.pgalise.simulation.shared.city.SchoolTagCustom;
import de.pgalise.simulation.shared.city.ServiceTag;
import de.pgalise.simulation.shared.city.ServiceTagCustom;
import de.pgalise.simulation.shared.city.ShopTag;
import de.pgalise.simulation.shared.city.ShopTagCustom;
import de.pgalise.simulation.shared.city.SportTag;
import de.pgalise.simulation.shared.city.SportTagCustom;
import de.pgalise.simulation.shared.city.TourismTag;
import de.pgalise.simulation.shared.city.TourismTagCustom;
import de.pgalise.simulation.shared.city.Way;
import de.pgalise.simulation.shared.city.WayTag;
import de.pgalise.simulation.shared.city.WayTagCustom;
import de.pgalise.simulation.shared.city.WayTagEnum;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;
import java.util.Arrays;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

/**
 * The OSMCityInfrastructureData is the used {@link CityInfrastructureData}
 * in this prototype. It parsed the needed information from given openstreetmap
 * and busstop files. The methods {@link CityInfrastructureData#getNearestJunctionNode(double, double)},
 * {@link CityInfrastructureData#getNearestNode(double, double)}, {@link CityInfrastructureData#getNearestStreetNode(double, double)
 * and {@link CityInfrastructureData#getNodesInBoundary(Boundary) are implements with a PR-Tree.
 * @author Timo
 */
public class OSMCityInfrastructureData extends TrafficInfrastructureData {

	/**
	 * Distance calculator for our buildings.
	 * 
	 * @author Timo
	 */
	@SuppressWarnings("unused")
	private static class BuildingDistance implements DistanceCalculator<Building> {
		private static final long serialVersionUID = 7659553740208196204L;

		@Override
		public double distanceTo(Building b, PointND p) {
			/* euclidean distance */
			return Math.sqrt(Math.pow((b.getPosition().getCenterPoint().getX() - p.getOrd(0)), 2)
					+ Math.pow((b.getPosition().getCenterPoint().getY() - p.getOrd(1)), 2));
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
		public double getMax(int arg0, Building arg1) {
			return arg0 == 0 ? arg1.getPosition().getCenterPoint().getX() : arg1.getPosition().getCenterPoint().getY();
		}

		@Override
		public double getMin(int arg0, Building arg1) {
			return arg0 == 0 ? arg1.getPosition().getCenterPoint().getX() : arg1.getPosition().getCenterPoint().getY();
		}
	}

	/**
	 * Distance calculator for our nodes.
	 * 
	 * @author Timo
	 */
	private static class NodeDistance implements DistanceCalculator<NavigationNode> {
		private static final long serialVersionUID = -1997875613977347848L;

		@Override
		public double distanceTo(NavigationNode n, PointND p) {
			/* euclidean distance */
			return Math.sqrt(Math.pow((n.getGeoLocation().getX() - p.getOrd(0)), 2)
					+ Math.pow((n.getGeoLocation().getY() - p.getOrd(1)), 2));
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
		public double getMax(int arg0, NavigationNode arg1) {
			return arg0 == 0 ? arg1.getGeoLocation().getX() : arg1.getGeoLocation().getY();
		}

		@Override
		public double getMin(int arg0, NavigationNode arg1) {
			return arg0 == 0 ? arg1.getGeoLocation().getX() : arg1.getGeoLocation().getY();
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
			super.setOneWay(false);
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

	private static final Logger log = LoggerFactory.getLogger(OSMCityInfrastructureData.class);
	private static final Pattern MAX_SPEED_PATTERN = Pattern.compile("([0-9]+)([\\s]*)([a-zA-Z]+)");
	private static final double MILE_TO_KM = 1.609344;

	/**
	 * OSM ways with these highway values will be ignored.
	 * 
	 * @see OSMCityInfrastructureData.parseWays
	 */
	private static final Set<WayTag> NON_INTERESTING_HIGHWAYS = new HashSet<WayTag>(Arrays.asList(WayTagEnum.BUS_STOP, WayTagEnum.UNCLASSIFIED ));
	/**
	 * OSM ways with these highway values will be ignored.
	 * 
	 * @see OSMCityInfrastructureData.parseMotorWays
	 */
	private static final Set<WayTag> NON_MOTOR_HIGHWAYS = new HashSet<WayTag>(Arrays.asList(WayTagEnum.pedestrian, WayTagEnum.CYCLEWAY, WayTagEnum.STEPS, WayTagEnum.FOOTPATH, WayTagEnum.PATH, 
			WayTagEnum.service, WayTagEnum.natural, WayTagEnum.track ));
	/**
	 * Branch factor for the pr-tree
	 */
	private static final int prTreeBranchFactor = 30;
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -4969795656547242552L;
	private List<BusStop> busStops;
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

	private List<NavigationNode> usedNodes, streetNodes;
	private List<TrafficNode> roundAbouts, junctionNodes;

	private List<Way<?,?>> ways, landUseWays, cycleWays;
	private List<TrafficWay> motorWays, motorWaysWithBusstops, cycleAndMotorways;
	private Boundary boundary;
	private List<Building> buildings;
	private BuildingEnergyProfileStrategy buildingEnergyProfileStrategy;
	private TrafficGraph graph;

	/**
	 * Constructor
	 * 
	 * @param osmIN
	 * 			the opstreetmap file
	 * @param busStopIN
	 *            the gtfs stops.txt file
	 * @param buildingEnergyProfileStrategy
	 * 			  the strategy to find the best energy profile for a buidling
	 * @throws IOException
	 */
	public OSMCityInfrastructureData(InputStream osmIN, InputStream busStopIN,
			BuildingEnergyProfileStrategy buildingEnergyProfileStrategy, TrafficGraph graph) throws IOException {
		this.prTreeDistanceCalculator = new NodeDistance();
		this.prTreeNodeFilter = new PRTreeNodeFilter();
		this.buildingEnergyProfileStrategy = buildingEnergyProfileStrategy;
		this.parse(osmIN, busStopIN);
		this.graph = graph;
	}

	/**
	 * Converts the circle into a rectangle.
	 * 
	 * @param centerPoint
	 * @param radiusInMeter
	 * @return
	 */
	private Boundary circleToRectangle(JaxRSCoordinate centerPoint, int radiusInMeter) {

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
			tmpPoints.add(new JaxRSCoordinate(lat, lng));
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

		return new Boundary(new JaxRSCoordinate(northEastLat, northEastLng), new JaxRSCoordinate(southWestLat, southWestLng));
	}

	/**
	 * Combines motorways with busstops. Some of the nodes in @Way.getNodeList() are instances of @BusStop!
	 * 
	 * @param osmFile
	 * @return
	 */
	private List<TrafficWay> 
	combineMotorWaysWithBusStops(
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
					nodeWayMap.put(node, tmpWayList);
				}
				tmpWayList.add(way);
			}
		}

		/* Add busstops to ways as new nodes: */
		class NodeDistanceWrapper {
			private double distance;
			private NavigationNode node;

			private NodeDistanceWrapper(NavigationNode node, double distance) {
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

		BusStopLoop: for (BusStop busStop : busStops) {

			List<NodeDistanceWrapper> nodeDistanceList = new ArrayList<>();

			for (NavigationNode node : nodeWayMap.keySet()) {
				nodeDistanceList.add(new NodeDistanceWrapper(node, this.getDistanceInKM(busStop.getGeoLocation().getX(),
						busStop.getGeoLocation().getY(), node.getGeoLocation().getX(), node.getGeoLocation().getY())));
			}

			Collections.sort(nodeDistanceList, new Comparator<NodeDistanceWrapper>() {

				@Override
				public int compare(NodeDistanceWrapper o1, NodeDistanceWrapper o2) {
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
								&& (this.getDistanceInKM(node1.getGeoLocation().getX(), node1.getGeoLocation().getY(),
										node2.getGeoLocation().getX(), node2.getGeoLocation().getY()) <= this.getDistanceInKM(
										node2.getGeoLocation().getX(), node2.getGeoLocation().getY(), busStop.getGeoLocation().getX(),
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

							possibleWay.setEdgeList(newNodeList, graph);

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
	private List<Building> extractBuildings(List<Way<?,?>> wayList) {
		List<Building> buildingList = new ArrayList<>();

		List<String> tmpLandUseList = new ArrayList<>();

		/* Build a map with interesting landuse polygons: */
		/**
		 * Map<String = landuse tag, List<SimplePolygon2D = polygon of the landuse are.
		 */
		Map<String, List<Polygon>> landUseMap = new HashMap<>();
		for (Way<?,?> landuse : this.landUseWays) {
			if (landuse.getLanduseTags().contains(LanduseTagEnum.INDUSTRY)) {
				List<Polygon> polygonList = landUseMap.get("industrial");
				if (polygonList == null) {
					polygonList = new ArrayList<>();
					landUseMap.put("industrial", polygonList);
				}
				polygonList.add(this.wayToPolygon2d(landuse));
			} else if (landuse.getLanduseTags().contains(LanduseTagEnum.RETAIL)) {
				List<Polygon> polygonList = landUseMap.get("retail");
				if (polygonList == null) {
					polygonList = new ArrayList<>();
					landUseMap.put("retail", polygonList);
				}
				polygonList.add(this.wayToPolygon2d(landuse));
			} else if (landuse.getLanduseTags().contains(LanduseTagEnum.FARMYARD)) {
				List<Polygon> polygonList = landUseMap.get("farmyard");
				if (polygonList == null) {
					polygonList = new ArrayList<>();
					landUseMap.put("farmyard", polygonList);
				}
				polygonList.add(this.wayToPolygon2d(landuse));
			} else if (landuse.getLanduseTags().contains(LanduseTagEnum.MILITARY)) {
				List<Polygon> polygonList = landUseMap.get("military");
				if (polygonList == null) {
					polygonList = new ArrayList<>();
					landUseMap.put("military", polygonList);
				}
				polygonList.add(this.wayToPolygon2d(landuse));
			} else if (landuse.getLanduseTags().contains(LanduseTagEnum.RESIDENTIAL)) {
				List<Polygon> polygonList = landUseMap.get("residential");
				if (polygonList == null) {
					polygonList = new ArrayList<>();
					landUseMap.put("residential", polygonList);
				}
				polygonList.add(this.wayToPolygon2d(landuse));
			} else if (landuse.getLanduseTags().contains(LanduseTagEnum.FARMLAND)) {
				List<Polygon> polygonList = landUseMap.get("farmland");
				if (polygonList == null) {
					polygonList = new ArrayList<>();
					landUseMap.put("farmland", polygonList);
				}
				polygonList.add(this.wayToPolygon2d(landuse));
			} else if (landuse.getLanduseTags().contains(LanduseTagEnum.COMMERCIAL)) {
				List<Polygon> polygonList = landUseMap.get("commercial");
				if (polygonList == null) {
					polygonList = new ArrayList<>();
					landUseMap.put("commercial", polygonList);
				}
				polygonList.add(this.wayToPolygon2d(landuse));
			}
		}

		for (Way<?,?>  way : wayList) {
			//if (way.isBuilding()) {
				if (way.getNodeList().isEmpty()) {
					continue;
				}
				/* find bound and tags: */
				Set<TourismTag> tourism = null;
				Set<SportTag> sport = null;
				Set<ServiceTag> service = null;
				Set<SchoolTag> school = null;
				Set<RepairTag> repair = null;
				Set<AmenityTag> amenity = null;
				Set<AttractionTag> attraction = null;
				Set<ShopTag> shop = null;
				Set<EmergencyServiceTag> emergencyService = null;
				boolean office = false;
				Set<CraftTag> craft = null;
				Set<LeisureTag> leisure = null;
				boolean military = false;
				Set<PublicTransportTag> publicTransport = null;
				Set<GamblingTag> gambling = null;

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
					if( node instanceof Building) {
						Building nodeCast = (Building) node;
						if (nodeCast.getAmenityTags()!= null) {
							amenity = nodeCast.getAmenityTags();
						}
						if (nodeCast.isOffice()) {
							office = nodeCast.isOffice();
						}
					}
					if (node.getAttractionTags()!= null) {
						attraction = node.getAttractionTags();
					}
					if (node.getShopTags()!= null) {
						shop = node.getShopTags();
					}
					if (node.getEmergencyServiceTags()!= null) {
						emergencyService = node.getEmergencyServiceTags();
					}
					if (node.getCraftTags()!= null) {
						craft = node.getCraftTags();
					}
					if (node.getLeisureTags()!= null) {
						leisure = node.getLeisureTags();
					}
					if (node.isMilitary()) {
						military = node.isMilitary();
					}
					if (node.getPublicTransportTags()!= null) {
						publicTransport = node.getPublicTransportTags();
					}
					if (node.getGamblingTags()!= null) {
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
				for (NavigationNode node : this.getNodesInBoundary(new Boundary(new JaxRSCoordinate(maxLat, maxLng), new JaxRSCoordinate(
						minLat, minLng)))) {
					if (node.getTourismTags()!= null) {
						tourism = node.getTourismTags();
					}
					if (node.getSportTags()!= null) {
						sport = node.getSportTags();
					}
					if (node.getServiceTags()!= null) {
						service = node.getServiceTags();
					}
					if (node.getSchoolTags()!= null) {
						school = node.getSchoolTags();
					}
					if (node.getRepairTags()!= null) {
						repair = node.getRepairTags();
					}
					if(node instanceof Building) {
						Building nodeCast = (Building) node;
						if (nodeCast.getAmenityTags()!= null) {
							amenity = nodeCast.getAmenityTags();
						}
						if (nodeCast.isOffice()) {
							office = nodeCast.isOffice();
						}
					}
					if (node.getAttractionTags()!= null) {
						attraction = node.getAttractionTags();
					}
					if (node.getShopTags()!= null) {
						shop = node.getShopTags();
					}
					if (node.getEmergencyServiceTags()!= null) {
						emergencyService = node.getEmergencyServiceTags();
					}
					if (node.getCraftTags()!= null) {
						craft = node.getCraftTags();
					}
					if (node.getLeisureTags()!= null) {
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
				outerLoop: for (Entry<String, List<Polygon>> entry : landUseMap.entrySet()) {
					for (Polygon landUsePolygon : entry.getValue()) {
						if (landUsePolygon.isPointInside(new Geo(centerLat, centerLon))) {
							tmpLandUseList.add(entry.getKey());
							landUseArea = entry.getKey();
							break outerLoop;
						}
					}
				}

				/* calc area in m² */
				double length = this.getDistanceInKM(maxLat, maxLng, maxLat, minLng) * 1000.0;
				double width = this.getDistanceInKM(maxLat, maxLng, minLat, maxLng) * 1000.0;
				double area = length * width;
				area *= area;

				BaseGeoInfo buildingPosition = new BaseGeoInfo(GeoToolsBootstrapping.getGEOMETRY_FACTORY().createPolygon(new JaxRSCoordinate[] {new JaxRSCoordinate(maxLat, maxLng),new JaxRSCoordinate(minLat, minLng)}));
				buildingList.add(
					
					new Building(
					buildingPosition.getCenterPoint(),
					tourism, service,sport, 
						school, repair,  attraction, shop, emergencyService, craft, 
						leisure, publicTransport, gambling,amenity,null, office, 
					 military, buildingPosition ));
		}

		return buildingList;
	}

	/**
	 * Extracts the cycle ways.
	 * 
	 * @param wayList
	 * @return
	 */
	private List<Way<?,?>> extractCycleWays(List<Way<?,?>> wayList) {
		List<Way<?,?>> cycleWays0 = new ArrayList<>();

		for (Way<?,?>  way : wayList) {
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
	private List<Way<?,?>> extractLanduseWays(List<Way<?,?>> ways) {
		List<Way<?,?>> wayList = new ArrayList<>();

		for (Way<?,?> way : ways) {
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
	private List<TrafficWay> extractMotorWays(List<Way<?,?>> ways) {
		List<TrafficWay> wayList = new ArrayList<>();

		OuterLoop: for (Way<?,?> way : ways) {
			if(way instanceof TrafficWay) {
				TrafficWay wayCast = (TrafficWay) way;
				if (wayCast.getWayTags() != null) {
					for (WayTag wayTag : NON_MOTOR_HIGHWAYS) {
						if (wayCast.getWayTags().contains(wayTag)) {
							continue OuterLoop;
						}
					}

					if (wayCast.getWayTags().contains(WayTagEnum.RAILWAY)) {
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
	private List<TrafficNode> extractRoundAbouts(List<NavigationNode> usedNodes) {
		List<TrafficNode> roundAboutList = new ArrayList<>();

		for (NavigationNode node : usedNodes) {
			if(node instanceof TrafficNode) {
				TrafficNode nodeCast = (TrafficNode) node;
				if (nodeCast.isRoundabout()) {
					roundAboutList.add(nodeCast);
				}
			}
		}

		return roundAboutList;
	}

	@Override
	public Boundary getBoundary() {
		return boundary;
	}

	@Override
	public Map<EnergyProfileEnum, List<Building>> getBuildings(JaxRSCoordinate geolocation, int radiusInMeter) {

		Map<EnergyProfileEnum, List<Building>> energyProfileCountMap = new HashMap<>();
		List<Building> buildingsInRadius = this.getBuildingsInRadius(geolocation, radiusInMeter);

		for (Building building : buildingsInRadius) {
			try {
				EnergyProfileEnum energyProfileEnum = this.buildingEnergyProfileStrategy.getEnergyProfile(building);
				List<Building> buildingList = energyProfileCountMap.get(energyProfileEnum);
				if (buildingList == null) {
					buildingList = new ArrayList<>();
					energyProfileCountMap.put(energyProfileEnum, buildingList);
				}
				buildingList.add(building);
			} catch (RuntimeException e) {
				log.error(e.getMessage(), e);
			}
		}

		return energyProfileCountMap;
	}

	@Override
	public List<Building> getBuildingsInRadius(JaxRSCoordinate centerPoint, int radiusInMeter) {
		Boundary boundary0 = this.circleToRectangle(centerPoint, radiusInMeter);
		List<Building> tmpBuildings = new ArrayList<>();
		for (Building building : this.buildingTree.find(boundary0.getSouthWest().getX(), boundary0
				.getSouthWest().getY(), boundary0.getNorthEast().getX(), boundary0
				.getNorthEast().getY())) {
			tmpBuildings.add(building);
		}

		/* check the distance for every found building, because we used a rectangle instead of a circle: */
		List<Building> buildings0 = new ArrayList<>();
		for (Building building : tmpBuildings) {
			if (this.getDistanceInMeter(centerPoint, building.getPosition().getCenterPoint()) <= radiusInMeter) {
				buildings0.add(building);
			}
		}

		return buildings0;
	}

	public List<BusStop> getBusStops() {
		return this.busStops;
	}

	public List<Way<?,?>> getCycleWays() {
		return this.cycleWays;
	}

	/**
	 * Computes the distance between the two points in km.
	 */
	private double getDistanceInKM(double myLat, double myLng, double hisLat, double hisLng) {

		if ((myLat == hisLat) && (myLng == hisLng)) {
			return 0.0;
		}

		double f = 1 / 298.257223563;
		double a = 6378.137;
		double F = ((myLat + hisLat) / 2) * (Math.PI / 180);
		double G = ((myLat - hisLat) / 2) * (Math.PI / 180);
		double l = ((myLng - hisLng) / 2) * (Math.PI / 180);
		double S = (Math.pow(Math.sin(G), 2) * Math.pow(Math.cos(l), 2))
				+ (Math.pow(Math.cos(F), 2) * Math.pow(Math.sin(l), 2));
		double C = (Math.pow(Math.cos(G), 2) * Math.pow(Math.cos(l), 2))
				+ (Math.pow(Math.sin(F), 2) * Math.pow(Math.sin(l), 2));
		double w = Math.atan(Math.sqrt(S / C));
		double D = 2 * w * a;
		double R = Math.sqrt(S * C) / w;
		double H1 = ((3 * R) - 1) / (2 * C);
		double H2 = ((3 * R) + 1) / (2 * S);

		double distance = D
				* ((1 + (f * H1 * Math.pow(Math.sin(F), 2) * Math.pow(Math.cos(G), 2))) - (f * H2
						* Math.pow(Math.cos(F), 2) * Math.pow(Math.sin(G), 2)));

		return distance;
	}

	/**
	 * Gives the distance in meter between start and target.
	 * 
	 * @param start
	 * @param target
	 * @return
	 */
	private double getDistanceInMeter(JaxRSCoordinate start, JaxRSCoordinate target) {

		if ((start.getX() == target.getX())
				&& (start.getY() == target.getY())) {
			return 0.0;
		}

		double f = 1 / 298.257223563;
		double a = 6378.137;
		double F = ((start.getX() + target.getX()) / 2) * (Math.PI / 180);
		double G = ((start.getX() - target.getX()) / 2) * (Math.PI / 180);
		double l = ((start.getY() - target.getY()) / 2) * (Math.PI / 180);
		double S = Math.pow(Math.sin(G), 2) * Math.pow(Math.cos(l), 2) + Math.pow(Math.cos(F), 2)
				* Math.pow(Math.sin(l), 2);
		double C = Math.pow(Math.cos(G), 2) * Math.pow(Math.cos(l), 2) + Math.pow(Math.sin(F), 2)
				* Math.pow(Math.sin(l), 2);
		double w = Math.atan(Math.sqrt(S / C));
		double D = 2 * w * a;
		double R = Math.sqrt(S * C) / w;
		double H1 = (3 * R - 1) / (2 * C);
		double H2 = (3 * R + 1) / (2 * S);

		double distance = D
				* (1 + f * H1 * Math.pow(Math.sin(F), 2) * Math.pow(Math.cos(G), 2) - f * H2 * Math.pow(Math.cos(F), 2)
						* Math.pow(Math.sin(G), 2));

		return distance * 1000.0;
	}

	public List<Way<?,?>> getLandUseWays() {
		return this.landUseWays;
	}

	public List<TrafficWay> getMotorWays() {
		return this.motorWays;
	}

	public List<TrafficWay> getMotorWaysWithBusstops() {
		return this.motorWaysWithBusstops;
	}

	@Override
	public NavigationNode getNearestNode(double latitude, double longitude) {
		List<DistanceResult<NavigationNode>> results = this.allNodesTree.nearestNeighbour(this.prTreeDistanceCalculator,
				this.prTreeNodeFilter, 1, new SimplePointND(latitude, longitude));

		if (results.size() > 0) {
			return results.get(0).get();
		}

		throw new RuntimeException("No nearest node found for: latitude: " + latitude + " longitude: " + longitude);
	}

	@Override
	public NavigationNode getNearestStreetNode(double latitude, double longitude) {
		List<DistanceResult<NavigationNode>> results = this.streetNodesTree.nearestNeighbour(this.prTreeDistanceCalculator,
				this.prTreeNodeFilter, 1, new SimplePointND(latitude, longitude));

		if (results.size() > 0) {
			return results.get(0).get();
		}

		throw new RuntimeException("No nearest node found for: latitude: " + latitude + " longitude: " + longitude);
	}

	@Override
	public List<NavigationNode> getNodes() {
		return this.usedNodes;
	}

	@Override
	public List<TrafficNode> getRoundAbouts() {
		return this.roundAbouts;
	}

	@Override
	public List<Way<?,?>> getWays() {
		return this.ways;
	}

	/**
	 * @param osmIN
	 *            osm-file
	 * @param busStopIN
	 *            busstop file
	 * @throws IOException
	 */
	private void parse(InputStream osmIN, InputStream busStopIN) throws IOException {
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
								} else if (parser.getAttributeLocalName(i).equalsIgnoreCase("lat")) {
									lat = Double.valueOf(parser.getAttributeValue(i));
								} else if (parser.getAttributeLocalName(i).equalsIgnoreCase("lon")) {
									lon = Double.valueOf(parser.getAttributeValue(i));
								}
							}

							if ((id != null) && (lat != null) && (lon != null)) {
								lastNode = new TrafficNode(new JaxRSCoordinate(lat, lon));
								nodeMap.put(id, lastNode);

								if (northEastBoundary == null
										|| (northEastBoundary.getGeoLocation().getX() < lat && northEastBoundary.getGeoLocation().getY() < lon)) {
									northEastBoundary = lastNode;
								} else if (southWestBoundary == null
										|| (southWestBoundary.getGeoLocation().getX() > lat && southWestBoundary.getGeoLocation().getY() > lon)) {
									southWestBoundary = lastNode;
								}
							}

							/* Collect all the ways */
						} else if ((lastNode != null) && parser.getLocalName().equalsIgnoreCase("tag")) {

							String kValue = parser.getAttributeValue(0);
							String vValue = parser.getAttributeValue(1);

							if (kValue.equalsIgnoreCase("highway")) {
								if (vValue.equalsIgnoreCase("bus_stop")) {
									lastBusstop = new BusStop("", null, new JaxRSCoordinate(lastNode.getGeoLocation().getX(), lastNode.getGeoLocation().getY()));
								} else if (vValue.equalsIgnoreCase("mini_roundabout")
										|| vValue.equalsIgnoreCase("roundabout")) {
									lastNode.setRoundabout(true);
								}
							} else if (kValue.equalsIgnoreCase("name") && (lastBusstop != null)) {
								lastBusstop.setStopName(vValue);
								tmpBusStopList.add(lastBusstop);
								lastBusstop = null;
							} else if (kValue.equalsIgnoreCase("tourism")) {
								lastNode.getTourismTags().add(new TourismTagCustom(vValue));
							} else if (kValue.equalsIgnoreCase("sport")) {
								lastNode.getSportTags().add(new SportTagCustom(vValue));

							} else if (kValue.equalsIgnoreCase("school")) {
								lastNode.getSchoolTags().add(new SchoolTagCustom(vValue));

							} else if (kValue.equalsIgnoreCase("shop")) {
								lastNode.getShopTags().add(new ShopTagCustom(vValue));

							} else if (kValue.equalsIgnoreCase("service")) {
								lastNode.getServiceTags().add(new ServiceTagCustom(vValue));

							} else if (kValue.equalsIgnoreCase("repair")) {
								lastNode.getRepairTags().add(new RepairTagCustom(vValue));

							} else if (kValue.equalsIgnoreCase("amenity")) {
								lastNode.getAmenityTags().add(new AmenityTagCustom(vValue));

							} else if (kValue.equalsIgnoreCase("attraction")) {
								lastNode.getAttractionTags().add(new AttractionTagCustom(vValue));

							} else if (kValue.equalsIgnoreCase("emergency_service")) {
								lastNode.getEmergencyServiceTags().add(new 
									EmergencyServiceTagCustom(vValue));

							} else if (kValue.equalsIgnoreCase("office")) {
								lastNode.setOffice(true);

							} else if (kValue.equalsIgnoreCase("craft")) {
								lastNode.getCraftTags().add(new CraftTagCustom(vValue));

							} else if (kValue.equalsIgnoreCase("leisure")) {
								lastNode.getLeisureTags().add(new LeisureTagCustom(vValue));

							} else if (kValue.equalsIgnoreCase("military")) {
								lastNode.setMilitary(true);

							} else if (kValue.equalsIgnoreCase("public_transport")) {
								lastNode.getPublicTransportTags().add(
									new PublicTransportTagCustom(vValue));

							} else if (kValue.equalsIgnoreCase("gambling")) {
								lastNode.getGamblingTags().add(new GamblingTagCustom(vValue));
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
										lastFoundWay.setMaxSpeed(Integer.valueOf(vValue));
									} catch (Exception e) {
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
									for (WayTag nonInterestingHighway : NON_INTERESTING_HIGHWAYS) {
										if (vValue.equalsIgnoreCase(nonInterestingHighway.getStringValue())) {
											lastFoundWay = null;
											break;
										}
									}
									if (lastFoundWay != null) {
										lastFoundWay.getWayTags().add(new WayTagCustom(vValue));
									}
								} else if (kValue.equalsIgnoreCase("oneway") && vValue.equalsIgnoreCase("yes")) {
									lastFoundWay.setOneWay(true);
								} else if (kValue.equalsIgnoreCase("landuse")) {
									lastFoundWay.getLanduseTags().add(new LanduseTagCustom(vValue));
								} else if (kValue.equalsIgnoreCase("railway")) {
									lastFoundWay.getWayTags().add(WayTagEnum.RAILWAY);
								} else if (kValue.equalsIgnoreCase("building")) {
									lastFoundWay.getWayTags().add(new WayTagCustom(vValue));
								} else if (kValue.equalsIgnoreCase("cycleway")) {
									lastFoundWay.getWayTags().add(WayTagEnum.CYCLEWAY);
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

						if (!(parser.getLocalName().equalsIgnoreCase("tag") || parser.getLocalName().equalsIgnoreCase(
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
			log.error(e.getMessage(), e);
		} finally {
			if(parser != null) {
				try {
					parser.close();
				} catch (XMLStreamException ex) {
					log.warn("see nested exception",
						ex);
				}
			}
		}

		/* Set real nodes for all ways and save used nodes */
		Set<NavigationNode> usedNodes0 = new HashSet<>();
		List<Way<?,?>> wayList = new ArrayList<>();
		OuterLoop: for (TmpWay way : tmpWayList) {
			List<TrafficNode> nodeList = new ArrayList<>();
			for (String nodeReference : way.getNodeReferenceList()) {
				TrafficNode node = nodeMap.get(nodeReference);
				if (node == null) {
					continue OuterLoop;
				}

				nodeList.add(node);
				usedNodes0.add(node);
			}

			if(way instanceof TrafficWay) {
				wayList.add(new TrafficWay(way.getEdgeList(),
					way.getStreetName()));
			}else {
				wayList.add(new Way<>(way.getEdgeList(), way.getStreetName()));
			}
		}

		this.usedNodes = new ArrayList<>(usedNodes0);

		this.ways = wayList;
		this.motorWays = this.extractMotorWays(wayList);

		/* Parse busstop file: */
		List<BusStop> tmpBusStops = this.parseBusStops(busStopIN);
		this.motorWaysWithBusstops = this.combineMotorWaysWithBusStops(this.motorWays, tmpBusStops);
		this.busStops = new ArrayList<>();
		OuterLoop: for (Way<?,?> way : this.motorWaysWithBusstops) {
			for (NavigationNode node : way.getNodeList()) {
				if (node instanceof TrafficNode && ((TrafficNode)node).getBusStop() != null) {
					this.busStops.add(((TrafficNode) node).getBusStop());
					continue OuterLoop;
				}
			}
		}

		this.landUseWays = this.extractLanduseWays(wayList);
		this.cycleWays = this.extractCycleWays(wayList);
		this.roundAbouts = this.extractRoundAbouts(this.usedNodes);

		Set<TrafficWay> cycleAndMotorwaySet = new HashSet<>(this.motorWays);
		cycleAndMotorwaySet.addAll(this.motorWays);
		this.cycleAndMotorways = new ArrayList<>(cycleAndMotorwaySet);

		/* find used street nodes and junction nodes */
		Set<NavigationNode> usedStreetNodes = new HashSet<>();
		Map<TrafficNode, Set<Way<?,?>>> junctionWayMap = new HashMap<>();
		for (TrafficWay way : this.getMotorWaysWithBusstops()) {
			for (TrafficNode node : way.getNodeList()) {
				/* comparison with size is much faster than contains. */
				int sizeBefore = usedStreetNodes.size();
				usedStreetNodes.add(node);
				if (sizeBefore == usedStreetNodes.size()) {
					Set<Way<?,?>> tmpJunctionWays = junctionWayMap.get(way);
					if(tmpJunctionWays == null) {
						tmpJunctionWays = new HashSet<>();
						junctionWayMap.put(node, tmpJunctionWays);
					}
					tmpJunctionWays.add(way);
				}

			}
		}
		
		this.streetNodes = new ArrayList<>(usedStreetNodes);
		
		/* Check the junction nodes. They need at least three edges: */
		this.junctionNodes = new LinkedList<>();
		for(Entry<TrafficNode, Set<Way<?,?>>> entry : junctionWayMap.entrySet()) {
			if(entry.getValue().size() >= 3) {
				junctionNodes.add(entry.getKey());
				/* check if three or more edges (junction is not the last node on one of the ways): */
			} else {
				for(Way<?,?> way : entry.getValue()) {
					if(way.getNodeList().size() > 1) {
						if(!way.getNodeList().get(0).equals(entry.getKey()) && !way.getNodeList().get(way.getNodeList().size() - 1).equals(entry.getKey())) {
							junctionNodes.add(entry.getKey());
							break;
						}
					}
				}
			}
		}

		/* build the pr-trees for nodes */
		this.allNodesTree = new PRTree<>(new NodeMBRConverter(), prTreeBranchFactor);
		this.allNodesTree.load(this.usedNodes);
		this.streetNodesTree = new PRTree<>(new NodeMBRConverter(), prTreeBranchFactor);
		this.streetNodesTree.load(this.streetNodes);
		this.junctionNodesTree = new PRTree<>(new NodeMBRConverter(), prTreeBranchFactor);
		this.junctionNodesTree.load(this.junctionNodes);

		if (northEastBoundary != null) {
			this.boundary = new Boundary(new JaxRSCoordinate(northEastBoundary.getGeoLocation().getX(),
					northEastBoundary.getGeoLocation().getY()), new JaxRSCoordinate(southWestBoundary.getGeoLocation().getX(),
					southWestBoundary.getGeoLocation().getY()));
		}

		this.buildings = this.extractBuildings(wayList);
		/* build the pr-tree for buildings */
		this.buildingTree = new PRTree<>(new BuildingMBRConverter(), prTreeBranchFactor);
		this.buildingTree.load(this.buildings);
	}

	/**
	 * Parses the a bus stop text file.
	 * 
	 * @param busstopGTFSFile
	 *            busstop gtfs csv file
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
			for (String[] text = csvParser.getLine(); text != null; text = csvParser.getLine()) {
				busStopList.add(new OSMBusStop(text[idColumn], text[nameColumn], null, new JaxRSCoordinate(Double.valueOf(text[latColumn]), Double
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
	private Polygon wayToPolygon2d(Way<?,?> way) {
		Geo[] geoArray = new Geo[way.getNodeList().size()];

		for (int i = 0; i < way.getNodeList().size(); i++) {
			NavigationNode tmpNode = way.getNodeList().get(i);
			geoArray[i] = new Geo(tmpNode.getGeoLocation().getX(), tmpNode.getGeoLocation().getY());
		}

		return new Polygon(geoArray);
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	@Override
	public List<NavigationNode> getNodesInBoundary(Boundary boundary) {

		List<NavigationNode> nodes = new ArrayList<>();
		for (NavigationNode node : this.allNodesTree.find(boundary.getSouthWest().getX(), boundary
				.getSouthWest().getY(), boundary.getNorthEast().getX(), boundary
				.getNorthEast().getY())) {
			nodes.add(node);
		}

		return nodes;
	}

	@Override
	public List<NavigationNode> getStreetNodes() {
		return this.streetNodes;
	}

	@Override
	public NavigationNode getNearestJunctionNode(double latitude, double longitude) {
		List<DistanceResult<NavigationNode>> results = this.junctionNodesTree.nearestNeighbour(this.prTreeDistanceCalculator,
				this.prTreeNodeFilter, 1, new SimplePointND(latitude, longitude));

		if (results.size() > 0) {
			return results.get(0).get();
		}

		throw new RuntimeException("No nearest node found for: latitude: " + latitude + " longitude: " + longitude);
	}

	@Override
	public List<TrafficNode> getJunctionNodes() {
		return this.junctionNodes;
	}

	@Override
	public List<TrafficWay> getCycleAndMotorways() {
		return this.cycleAndMotorways;
	}
}
