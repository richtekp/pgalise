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
 
package de.pgalise.util.cityinfrastructure.impl;

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

import de.pgalise.simulation.shared.city.Boundary;
import de.pgalise.simulation.shared.city.Building;
import de.pgalise.simulation.shared.city.BusStop;
import de.pgalise.simulation.shared.city.CityInfrastructureData;
import de.pgalise.simulation.shared.city.Node;
import de.pgalise.simulation.shared.city.Way;
import de.pgalise.simulation.shared.energy.EnergyProfileEnum;
import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;
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
public class OSMCityInfrastructureData implements CityInfrastructureData {

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
			return Math.sqrt(Math.pow((b.getCenterPoint().x - p.getOrd(0)), 2)
					+ Math.pow((b.getCenterPoint().y - p.getOrd(1)), 2));
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
			return arg0 == 0 ? arg1.getCenterPoint().x : arg1.getCenterPoint().y;
		}

		@Override
		public double getMin(int arg0, Building arg1) {
			return arg0 == 0 ? arg1.getCenterPoint().x : arg1.getCenterPoint().y;
		}
	}

	/**
	 * Distance calculator for our nodes.
	 * 
	 * @author Timo
	 */
	private static class NodeDistance implements DistanceCalculator<Node> {
		private static final long serialVersionUID = -1997875613977347848L;

		@Override
		public double distanceTo(Node n, PointND p) {
			/* euclidean distance */
			return Math.sqrt(Math.pow((n.getLatitude() - p.getOrd(0)), 2)
					+ Math.pow((n.getLongitude() - p.getOrd(1)), 2));
		}
	}

	/**
	 * MBRConverter for our nodes.
	 * 
	 * @author Timo
	 */
	private static class NodeMBRConverter implements MBRConverter<Node> {
		private static final long serialVersionUID = 4175774127701744257L;

		@Override
		public int getDimensions() {
			return 2;
		}

		@Override
		public double getMax(int arg0, Node arg1) {
			return arg0 == 0 ? arg1.getLatitude() : arg1.getLongitude();
		}

		@Override
		public double getMin(int arg0, Node arg1) {
			return arg0 == 0 ? arg1.getLatitude() : arg1.getLongitude();
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
	private static class PRTreeNodeFilter implements NodeFilter<Node> {
		private static final long serialVersionUID = -7994547301033310792L;

		@Override
		public boolean accept(Node arg0) {
			return true;
		}
	}

	/**
	 * Holds the OSM way data, till everything is collected.
	 * 
	 * @author Timo
	 */
	private static class TmpWay extends Way {
		private static final long serialVersionUID = 2310070836622850662L;
		private List<String> nodeReferenceList;

		TmpWay() {
			this.nodeReferenceList = new ArrayList<>();
			super.setOneWay(false);
			super.setBuildingTypeMap(new HashMap<String, String>());
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
	}

	private static final Logger log = LoggerFactory.getLogger(OSMCityInfrastructureData.class);
	private static final Pattern MAX_SPEED_PATTERN = Pattern.compile("([0-9]+)([\\s]*)([a-zA-Z]+)");
	private static final double MILE_TO_KM = 1.609344;

	/**
	 * OSM ways with these highway values will be ignored.
	 * 
	 * @see OSMCityInfrastructureData.parseWays
	 */
	private static final String[] NON_INTERESTING_HIGHWAYS = { "bus_stop", "unclassified" };
	/**
	 * OSM ways with these highway values will be ignored.
	 * 
	 * @see OSMCityInfrastructureData.parseMotorWays
	 */
	private static final String[] NON_MOTOR_HIGHWAYS = { "pedestrian", "cycleway", "steps", "footway", "path",
			"service", "natural", "track" };
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
	private DistanceCalculator<Node> prTreeDistanceCalculator;
	/**
	 * Node filter for the pr-tree
	 */
	private NodeFilter<Node> prTreeNodeFilter;

	/**
	 * PR-Tree to answer spatial queries
	 */
	private PRTree<Node> allNodesTree;

	/**
	 * PR-Tree to answer spatial queries
	 */
	private PRTree<Node> streetNodesTree;

	private PRTree<Node> junctionNodesTree;

	/**
	 * PR-Tree to answer spatial queries for buildings
	 */
	private PRTree<Building> buildingTree;

	private List<Node> usedNodes, roundAbouts, streetNodes, junctionNodes;

	private List<Way> ways, motorWays, motorWaysWithBusstops, landUseWays, cycleWays, cycleAndMotorways;
	private Boundary boundary;
	private List<Building> buildings;
	private BuildingEnergyProfileStrategy buildingEnergyProfileStrategy;

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
			BuildingEnergyProfileStrategy buildingEnergyProfileStrategy) throws IOException {
		this.prTreeDistanceCalculator = new NodeDistance();
		this.prTreeNodeFilter = new PRTreeNodeFilter();
		this.buildingEnergyProfileStrategy = buildingEnergyProfileStrategy;
		this.parse(osmIN, busStopIN);
	}

	/**
	 * Converts the circle into a rectangle.
	 * 
	 * @param centerPoint
	 * @param radiusInMeter
	 * @return
	 */
	private Boundary circleToRectangle(Coordinate centerPoint, int radiusInMeter) {

		List<Coordinate> tmpPoints = new ArrayList<>();
		int pointNumber = 4;
		double radiusInKm = radiusInMeter / 1000.0;
		double earthRadius = 6378.137; // in km
		double r2d = 180.0 / Math.PI; // degrees to radians
		double d2r = Math.PI / 180.0; // radians to degrees
		double rlat = (radiusInKm / earthRadius) * r2d;
		double rlng = rlat / Math.cos(centerPoint.x * d2r);

		for (int i = 0; i < pointNumber; i++) {
			double t = Math.PI * (i / (pointNumber / 2.0));
			double lat = centerPoint.x + (rlat * Math.cos(t));
			double lng = centerPoint.y + (rlng * Math.sin(t));
			tmpPoints.add(new Coordinate(lat, lng));
		}

		double northEastLat = Double.MIN_VALUE;
		double northEastLng = Double.MIN_VALUE;
		double southWestLat = Double.MAX_VALUE;
		double southWestLng = Double.MAX_VALUE;

		for (Coordinate p : tmpPoints) {
			if (p.x > northEastLat) {
				northEastLat = p.x;
			}
			if (p.x < southWestLat) {
				southWestLat = p.x;
			}
			if (p.y > northEastLng) {
				northEastLng = p.y;
			}
			if (p.y < southWestLng) {
				southWestLng = p.y;
			}
		}

		return new Boundary(new Coordinate(northEastLat, northEastLng), new Coordinate(southWestLat, southWestLng));
	}

	/**
	 * Combines motorways with busstops. Some of the nodes in @Way.getNodeList() are instances of @BusStop!
	 * 
	 * @param osmFile
	 * @return
	 */
	private List<Way> combineMotorWaysWithBusStops(List<Way> motorWays, List<BusStop> busStops) {
		List<Way> wayList = new ArrayList<>(motorWays);

		/* Eliminate multiple bus stops: */
		Set<String> busStopNameSet = new HashSet<>();
		List<BusStop> tmpBusStop = new ArrayList<>();
		for (BusStop busStop : busStops) {
			if (busStop != null) {
				int before = busStopNameSet.size();
				busStopNameSet.add(busStop.getName());
				if (before < busStopNameSet.size()) {
					tmpBusStop.add(busStop);
				}
			}
		}

		/* Build a node way map: */
		Map<Node, List<Way>> nodeWayMap = new HashMap<>();
		for (Way way : wayList) {
			for (Node node : way.getNodeList()) {
				List<Way> tmpWayList = nodeWayMap.get(node);
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
			private Node node;

			private NodeDistanceWrapper(Node node, double distance) {
				this.node = node;
				this.distance = distance;
			}

			public double getDistance() {
				return this.distance;
			}

			public Node getNode() {
				return this.node;
			}
		}

		BusStopLoop: for (BusStop busStop : busStops) {

			List<NodeDistanceWrapper> nodeDistanceList = new ArrayList<>();

			for (Node node : nodeWayMap.keySet()) {
				nodeDistanceList.add(new NodeDistanceWrapper(node, this.getDistanceInKM(busStop.getLatitude(),
						busStop.getLongitude(), node.getLatitude(), node.getLongitude())));
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
				Node node1 = nodeDistanceList.get(i).getNode();

				for (int j = i + 1; j < nodeDistanceList.size(); j++) {
					Node node2 = nodeDistanceList.get(j).getNode();
					List<Way> waysNode2 = nodeWayMap.get(node2);

					for (Way possibleWay : nodeWayMap.get(node1)) {
						/*
						 * Node1 and node2 must be on the same street and the distance between node1 and node2 must be
						 * larger than the distance between bus stop and node2.
						 */
						if (waysNode2.contains(possibleWay)
								&& (this.getDistanceInKM(node1.getLatitude(), node1.getLongitude(),
										node2.getLatitude(), node2.getLongitude()) <= this.getDistanceInKM(
										node2.getLatitude(), node2.getLongitude(), busStop.getLatitude(),
										busStop.getLongitude()))) {

							/* Insert busstop as new node: */
							List<Node> newNodeList = new ArrayList<>();
							boolean nodeFound = false;
							for (Node node : possibleWay.getNodeList()) {
								if (!nodeFound && (node.equals(node1) || node.equals(node2))) {
									newNodeList.add(busStop);
									nodeFound = true;
								}

								newNodeList.add(node);
							}

							possibleWay.setNodeList(newNodeList);

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
	private List<Building> extractBuildings(List<Way> wayList) {
		List<Building> buildingList = new ArrayList<>();

		List<String> tmpLandUseList = new ArrayList<>();

		/* Build a map with interesting landuse polygons: */
		/**
		 * Map<String = landuse tag, List<SimplePolygon2D = polygon of the landuse are.
		 */
		Map<String, List<Polygon>> landUseMap = new HashMap<>();
		for (Way landuse : this.landUseWays) {
			if (landuse.getLanduse().equalsIgnoreCase("industrial")) {
				List<Polygon> polygonList = landUseMap.get("industrial");
				if (polygonList == null) {
					polygonList = new ArrayList<>();
					landUseMap.put("industrial", polygonList);
				}
				polygonList.add(this.wayToPolygon2d(landuse));
			} else if (landuse.getLanduse().equalsIgnoreCase("retail")) {
				List<Polygon> polygonList = landUseMap.get("retail");
				if (polygonList == null) {
					polygonList = new ArrayList<>();
					landUseMap.put("retail", polygonList);
				}
				polygonList.add(this.wayToPolygon2d(landuse));
			} else if (landuse.getLanduse().equalsIgnoreCase("farmyard")) {
				List<Polygon> polygonList = landUseMap.get("farmyard");
				if (polygonList == null) {
					polygonList = new ArrayList<>();
					landUseMap.put("farmyard", polygonList);
				}
				polygonList.add(this.wayToPolygon2d(landuse));
			} else if (landuse.getLanduse().equalsIgnoreCase("military")) {
				List<Polygon> polygonList = landUseMap.get("military");
				if (polygonList == null) {
					polygonList = new ArrayList<>();
					landUseMap.put("military", polygonList);
				}
				polygonList.add(this.wayToPolygon2d(landuse));
			} else if (landuse.getLanduse().equalsIgnoreCase("residential")) {
				List<Polygon> polygonList = landUseMap.get("residential");
				if (polygonList == null) {
					polygonList = new ArrayList<>();
					landUseMap.put("residential", polygonList);
				}
				polygonList.add(this.wayToPolygon2d(landuse));
			} else if (landuse.getLanduse().equalsIgnoreCase("farmland")) {
				List<Polygon> polygonList = landUseMap.get("farmland");
				if (polygonList == null) {
					polygonList = new ArrayList<>();
					landUseMap.put("farmland", polygonList);
				}
				polygonList.add(this.wayToPolygon2d(landuse));
			} else if (landuse.getLanduse().equalsIgnoreCase("commercial")) {
				List<Polygon> polygonList = landUseMap.get("commercial");
				if (polygonList == null) {
					polygonList = new ArrayList<>();
					landUseMap.put("commercial", polygonList);
				}
				polygonList.add(this.wayToPolygon2d(landuse));
			}
		}

		for (Way way : wayList) {
			if (way.isBuilding()) {
				if (way.getNodeList().isEmpty()) {
					continue;
				}
				/* find bound and tags: */
				String tourism = null;
				String sport = null;
				String service = null;
				String school = null;
				String repair = null;
				String amenity = null;
				String attraction = null;
				String shop = null;
				String emergencyService = null;
				String office = null;
				String craft = null;
				String leisure = null;
				String military = null;
				String publicTransport = null;
				String gambling = null;

				double minLat = Double.MAX_VALUE;
				double minLng = Double.MAX_VALUE;
				double maxLat = Double.MIN_VALUE;
				double maxLng = Double.MIN_VALUE;

				for (Node node : way.getNodeList()) {

					if (node.getTourism() != null) {
						tourism = node.getTourism();
					}
					if (node.getSport() != null) {
						sport = node.getSport();
					}
					if (node.getService() != null) {
						service = node.getService();
					}
					if (node.getSchool() != null) {
						school = node.getSchool();
					}
					if (node.getRepair() != null) {
						repair = node.getRepair();
					}
					if (node.getAmenity() != null) {
						amenity = node.getAmenity();
					}
					if (node.getAttraction() != null) {
						attraction = node.getAttraction();
					}
					if (node.getShop() != null) {
						shop = node.getShop();
					}
					if (node.getEmergencyService() != null) {
						emergencyService = node.getEmergencyService();
					}
					if (node.getOffice() != null) {
						office = node.getOffice();
					}
					if (node.getCraft() != null) {
						craft = node.getCraft();
					}
					if (node.getLeisure() != null) {
						leisure = node.getLeisure();
					}
					if (node.getMilitary() != null) {
						military = node.getMilitary();
					}
					if (node.getPublicTransport() != null) {
						publicTransport = node.getPublicTransport();
					}
					if (node.getGambling() != null) {
						gambling = node.getGambling();
					}

					if (node.getLatitude() > maxLat) {
						maxLat = node.getLatitude();
					}
					if (node.getLatitude() < minLat) {
						minLat = node.getLatitude();
					}
					if (node.getLongitude() > maxLng) {
						maxLng = node.getLongitude();
					}
					if (node.getLongitude() < minLng) {
						minLng = node.getLongitude();
					}
				}

				/* find other tags */
				for (Node node : this.getNodesInBoundary(new Boundary(new Coordinate(maxLat, maxLng), new Coordinate(
						minLat, minLng)))) {
					if (node.getTourism() != null) {
						tourism = node.getTourism();
					}
					if (node.getSport() != null) {
						sport = node.getSport();
					}
					if (node.getService() != null) {
						service = node.getService();
					}
					if (node.getSchool() != null) {
						school = node.getSchool();
					}
					if (node.getRepair() != null) {
						repair = node.getRepair();
					}
					if (node.getAmenity() != null) {
						amenity = node.getAmenity();
					}
					if (node.getAttraction() != null) {
						attraction = node.getAttraction();
					}
					if (node.getShop() != null) {
						shop = node.getShop();
					}
					if (node.getEmergencyService() != null) {
						emergencyService = node.getEmergencyService();
					}
					if (node.getOffice() != null) {
						office = node.getOffice();
					}
					if (node.getCraft() != null) {
						craft = node.getCraft();
					}
					if (node.getLeisure() != null) {
						leisure = node.getLeisure();
					}
					if (node.getMilitary() != null) {
						military = node.getMilitary();
					}
					if (node.getPublicTransport() != null) {
						publicTransport = node.getPublicTransport();
					}
					if (node.getGambling() != null) {
						gambling = node.getGambling();
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

				buildingList.add(new Building(new Coordinate(maxLat, maxLng), new Coordinate(minLat, minLng),
						new Coordinate(centerLat, centerLon), (int) area, landUseArea, tourism, sport, service,
						school, repair, amenity, attraction, shop, emergencyService, office, craft, leisure, military,
						publicTransport, gambling));
			}
		}

		return buildingList;
	}

	/**
	 * Extracts the cycle ways.
	 * 
	 * @param wayList
	 * @return
	 */
	private List<Way> extractCycleWays(List<Way> wayList) {
		List<Way> cycleWays0 = new ArrayList<>();

		for (Way way : wayList) {
			if ((way.getHighway() != null) && way.getHighway().trim().equalsIgnoreCase("cycleway")) {
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
	private List<Way> extractLanduseWays(List<Way> ways) {
		List<Way> wayList = new ArrayList<>();

		for (Way way : ways) {
			if ((way.getLanduse() != null) && !way.getLanduse().isEmpty()) {
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
	private List<Way> extractMotorWays(List<Way> ways) {
		List<Way> wayList = new ArrayList<>();

		OuterLoop: for (Way way : ways) {
			if (way.getHighway() != null) {
				for (int i = 0; i < NON_MOTOR_HIGHWAYS.length; i++) {
					if (way.getHighway().equalsIgnoreCase(NON_MOTOR_HIGHWAYS[i])) {
						continue OuterLoop;
					}
				}

				if (way.getRailway() != null) {
					log.debug(way.getRailway());
				}
				wayList.add(way);
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
	private List<Node> extractRoundAbouts(List<Node> usedNodes) {
		List<Node> roundAboutList = new ArrayList<>();

		for (Node node : usedNodes) {
			if (node.isRoundabout()) {
				roundAboutList.add(node);
			}
		}

		return roundAboutList;
	}

	@Override
	public Boundary getBoundary() {
		return boundary;
	}

	@Override
	public Map<EnergyProfileEnum, List<Building>> getBuildings(Coordinate geolocation, int radiusInMeter) {

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
	public List<Building> getBuildingsInRadius(Coordinate centerPoint, int radiusInMeter) {
		Boundary boundary0 = this.circleToRectangle(centerPoint, radiusInMeter);
		List<Building> tmpBuildings = new ArrayList<>();
		for (Building building : this.buildingTree.find(boundary0.getSouthWest().x, boundary0
				.getSouthWest().y, boundary0.getNorthEast().x, boundary0
				.getNorthEast().y)) {
			tmpBuildings.add(building);
		}

		/* check the distance for every found building, because we used a rectangle instead of a circle: */
		List<Building> buildings0 = new ArrayList<>();
		for (Building building : tmpBuildings) {
			if (this.getDistanceInMeter(centerPoint, building.getCenterPoint()) <= radiusInMeter) {
				buildings0.add(building);
			}
		}

		return buildings0;
	}

	@Override
	public List<BusStop> getBusStops() {
		return this.busStops;
	}

	@Override
	public List<Way> getCycleWays() {
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
	private double getDistanceInMeter(Coordinate start, Coordinate target) {

		if ((start.x == target.x)
				&& (start.y == target.y)) {
			return 0.0;
		}

		double f = 1 / 298.257223563;
		double a = 6378.137;
		double F = ((start.x + target.x) / 2) * (Math.PI / 180);
		double G = ((start.x - target.x) / 2) * (Math.PI / 180);
		double l = ((start.y - target.y) / 2) * (Math.PI / 180);
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

	@Override
	public List<Way> getLandUseWays() {
		return this.landUseWays;
	}

	@Override
	public List<Way> getMotorWays() {
		return this.motorWays;
	}

	@Override
	public List<Way> getMotorWaysWithBusstops() {
		return this.motorWaysWithBusstops;
	}

	@Override
	public Node getNearestNode(double latitude, double longitude) {
		List<DistanceResult<Node>> results = this.allNodesTree.nearestNeighbour(this.prTreeDistanceCalculator,
				this.prTreeNodeFilter, 1, new SimplePointND(latitude, longitude));

		if (results.size() > 0) {
			return results.get(0).get();
		}

		throw new RuntimeException("No nearest node found for: latitude: " + latitude + " longitude: " + longitude);
	}

	@Override
	public Node getNearestStreetNode(double latitude, double longitude) {
		List<DistanceResult<Node>> results = this.streetNodesTree.nearestNeighbour(this.prTreeDistanceCalculator,
				this.prTreeNodeFilter, 1, new SimplePointND(latitude, longitude));

		if (results.size() > 0) {
			return results.get(0).get();
		}

		throw new RuntimeException("No nearest node found for: latitude: " + latitude + " longitude: " + longitude);
	}

	@Override
	public List<Node> getNodes() {
		return this.usedNodes;
	}

	@Override
	public List<Node> getRoundAbouts() {
		return this.roundAbouts;
	}

	@Override
	public List<Way> getWays() {
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
		Map<String, Node> nodeMap = new HashMap<>();

		Node northEastBoundary = null;
		Node southWestBoundary = null;

		XMLStreamReader parser = null;

		try {
			parser = XMLInputFactory.newInstance().createXMLStreamReader(osmIN);

			TmpWay lastFoundWay = null;
			BusStop lastBusstop = null;
			Node lastNode = null;

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
								lastNode = new Node(id, lat, lon);
								nodeMap.put(id, lastNode);

								if (northEastBoundary == null
										|| (northEastBoundary.getLatitude() < lat && northEastBoundary.getLongitude() < lon)) {
									northEastBoundary = lastNode;
								} else if (southWestBoundary == null
										|| (southWestBoundary.getLatitude() > lat && southWestBoundary.getLongitude() > lon)) {
									southWestBoundary = lastNode;
								}
							}

							/* Collect all the ways */
						} else if ((lastNode != null) && parser.getLocalName().equalsIgnoreCase("tag")) {

							String kValue = parser.getAttributeValue(0);
							String vValue = parser.getAttributeValue(1);

							if (kValue.equalsIgnoreCase("highway")) {
								if (vValue.equalsIgnoreCase("bus_stop")) {
									lastBusstop = new BusStop(lastNode.getId(), "", lastNode.getLatitude(),
											lastNode.getLongitude());
								} else if (vValue.equalsIgnoreCase("mini_roundabout")
										|| vValue.equalsIgnoreCase("roundabout")) {
									lastNode.setRoundabout(true);
								}
							} else if (kValue.equalsIgnoreCase("name") && (lastBusstop != null)) {
								lastBusstop.setName(vValue);
								tmpBusStopList.add(lastBusstop);
								lastBusstop = null;
							} else if (kValue.equalsIgnoreCase("tourism")) {
								lastNode.setTourism(vValue);

							} else if (kValue.equalsIgnoreCase("sport")) {
								lastNode.setSport(vValue);

							} else if (kValue.equalsIgnoreCase("school")) {
								lastNode.setSchool(vValue);

							} else if (kValue.equalsIgnoreCase("shop")) {
								lastNode.setShop(vValue);

							} else if (kValue.equalsIgnoreCase("service")) {
								lastNode.setService(vValue);

							} else if (kValue.equalsIgnoreCase("repair")) {
								lastNode.setRepair(vValue);

							} else if (kValue.equalsIgnoreCase("amenity")) {
								lastNode.setAmenity(vValue);

							} else if (kValue.equalsIgnoreCase("attraction")) {
								lastNode.setAttraction(vValue);

							} else if (kValue.equalsIgnoreCase("emergency_service")) {
								lastNode.setEmergencyService(vValue);

							} else if (kValue.equalsIgnoreCase("office")) {
								lastNode.setOffice(vValue);

							} else if (kValue.equalsIgnoreCase("craft")) {
								lastNode.setCraft(vValue);

							} else if (kValue.equalsIgnoreCase("leisure")) {
								lastNode.setLeisure(vValue);

							} else if (kValue.equalsIgnoreCase("military")) {
								lastNode.setMilitary(vValue);

							} else if (kValue.equalsIgnoreCase("public_transport")) {
								lastNode.setPublicTransport(vValue);

							} else if (kValue.equalsIgnoreCase("gambling")) {
								lastNode.setGambling(vValue);
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

									lastFoundWay.setStreetname(vValue);
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

									for (int i = 0; i < NON_INTERESTING_HIGHWAYS.length; i++) {
										if (vValue.equalsIgnoreCase(NON_INTERESTING_HIGHWAYS[i])) {
											lastFoundWay = null;
											break;
										}
									}

									if (lastFoundWay != null) {
										lastFoundWay.setHighway(vValue);
									}

								} else if (kValue.equalsIgnoreCase("oneway") && vValue.equalsIgnoreCase("yes")) {
									lastFoundWay.setOneWay(true);

								} else if (kValue.equalsIgnoreCase("landuse")) {
									lastFoundWay.setLanduse(vValue);

								} else if (kValue.equalsIgnoreCase("railway")) {
									lastFoundWay.setRailway(vValue);

								} else if (kValue.equalsIgnoreCase("building")) {

									lastFoundWay.setBuilding(true);

								} else if (kValue.equalsIgnoreCase("cycleway")) {

									lastFoundWay.setCycleway(vValue);

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
		Set<Node> usedNodes0 = new HashSet<>();
		List<Way> wayList = new ArrayList<>();
		OuterLoop: for (TmpWay way : tmpWayList) {
			List<Node> nodeList = new ArrayList<>();
			for (String nodeReference : way.getNodeReferenceList()) {
				Node node = nodeMap.get(nodeReference);
				if (node == null) {
					continue OuterLoop;
				}

				nodeList.add(node);
				usedNodes0.add(node);
			}

			wayList.add(new Way(way.getMaxSpeed(), nodeList, way.isOneWay(), way.isBuilding(), way.getStreetname(), way
					.getHighway(), way.getLanduse(), way.getRailway(), way.getBuildingTypeMap(), way.getCycleway()));
		}

		this.usedNodes = new ArrayList<>(usedNodes0);

		this.ways = wayList;
		this.motorWays = this.extractMotorWays(wayList);

		/* Parse busstop file: */
		List<BusStop> tmpBusStops = this.parseBusStops(busStopIN);
		this.motorWaysWithBusstops = this.combineMotorWaysWithBusStops(this.motorWays, tmpBusStops);
		this.busStops = new ArrayList<>();
		OuterLoop: for (Way way : this.motorWaysWithBusstops) {
			for (Node node : way.getNodeList()) {
				if (node instanceof BusStop) {
					this.busStops.add((BusStop) node);
					continue OuterLoop;
				}
			}
		}

		this.landUseWays = this.extractLanduseWays(wayList);
		this.cycleWays = this.extractCycleWays(wayList);
		this.roundAbouts = this.extractRoundAbouts(this.usedNodes);

		Set<Way> cycleAndMotorwaySet = new HashSet<>(this.motorWays);
		cycleAndMotorwaySet.addAll(this.motorWays);
		this.cycleAndMotorways = new ArrayList<>(cycleAndMotorwaySet);

		/* find used street nodes and junction nodes */
		Set<Node> usedStreetNodes = new HashSet<>();
		Map<Node, Set<Way>> junctionWayMap = new HashMap<>();
		for (Way way : this.getMotorWaysWithBusstops()) {
			for (Node node : way.getNodeList()) {
				/* comparison with size is much faster than contains. */
				int sizeBefore = usedStreetNodes.size();
				usedStreetNodes.add(node);
				if (sizeBefore == usedStreetNodes.size()) {
					Set<Way> tmpJunctionWays = junctionWayMap.get(way);
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
		for(Entry<Node, Set<Way>> entry : junctionWayMap.entrySet()) {
			if(entry.getValue().size() >= 3) {
				junctionNodes.add(entry.getKey());
				/* check if three or more edges (junction is not the last node on one of the ways): */
			} else {
				for(Way way : entry.getValue()) {
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
			this.boundary = new Boundary(new Coordinate(northEastBoundary.getLatitude(),
					northEastBoundary.getLongitude()), new Coordinate(southWestBoundary.getLatitude(),
					southWestBoundary.getLongitude()));
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
				busStopList.add(new BusStop(text[idColumn], text[nameColumn], Double.valueOf(text[latColumn]), Double
						.valueOf(text[lngColumn])));
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
	private Polygon wayToPolygon2d(Way way) {
		Geo[] geoArray = new Geo[way.getNodeList().size()];

		for (int i = 0; i < way.getNodeList().size(); i++) {
			Node tmpNode = way.getNodeList().get(i);
			geoArray[i] = new Geo(tmpNode.getLatitude(), tmpNode.getLongitude());
		}

		return new Polygon(geoArray);
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	@Override
	public List<Node> getNodesInBoundary(Boundary boundary) {

		List<Node> nodes = new ArrayList<>();
		for (Node node : this.allNodesTree.find(boundary.getSouthWest().x, boundary
				.getSouthWest().y, boundary.getNorthEast().x, boundary
				.getNorthEast().y)) {
			nodes.add(node);
		}

		return nodes;
	}

	@Override
	public List<Node> getStreetNodes() {
		return this.streetNodes;
	}

	@Override
	public Node getNearestJunctionNode(double latitude, double longitude) {
		List<DistanceResult<Node>> results = this.junctionNodesTree.nearestNeighbour(this.prTreeDistanceCalculator,
				this.prTreeNodeFilter, 1, new SimplePointND(latitude, longitude));

		if (results.size() > 0) {
			return results.get(0).get();
		}

		throw new RuntimeException("No nearest node found for: latitude: " + latitude + " longitude: " + longitude);
	}

	@Override
	public List<Node> getJunctionNodes() {
		return this.junctionNodes;
	}

	@Override
	public List<Way> getCycleAndMotorways() {
		return this.cycleAndMotorways;
	}
}
