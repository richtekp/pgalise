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

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.entity.BusRoute;
import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.simulation.traffic.entity.BusTrip;
import de.pgalise.simulation.traffic.entity.CityInfrastructureData;
import de.pgalise.simulation.traffic.entity.MotorWay;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.entity.TrafficWay;
import de.pgalise.simulation.traffic.entity.gtfs.GTFSBusAgency;
import de.pgalise.simulation.traffic.entity.gtfs.GTFSBusCalendar;
import de.pgalise.simulation.traffic.entity.gtfs.GTFSBusRoute;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.ejb.EJB;
import javax.ejb.Local;
import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

/**
 * 
 * @author Lena
 */
@Local
public class GTFSPublicTransportDataService implements PublicTransportDataService<ZipInputStream> {
  private static final long serialVersionUID = 1L;
  private final Set<BusStop> busStops = new HashSet<>();
  private final Set<BusRoute> busRoutes = new HashSet<>();
  @EJB
  private IdGenerator idGenerator;
  private final Set<GTFSBusAgency> busAgencys = new HashSet<>();
  private final Set<GTFSBusCalendar> busCalendars = new HashSet<>();

	public GTFSPublicTransportDataService() {
	}

  public GTFSPublicTransportDataService(IdGenerator idGenerator) {
    this.idGenerator = idGenerator;
  }

  @Override
  @SuppressWarnings("NestedAssignment")
  public void parse(ZipInputStream zis) throws IOException {
    ZipEntry entry;
    Map<String, byte[]> gtfsContent = new HashMap<>();
    while ((entry = zis.getNextEntry()) != null)        {
      ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
      // not sure how to check how much can be read
      while (zis.available() != 0) {
        int read = zis.read();
        byteOutputStream.write(read);
      }
      byteOutputStream.flush();
      byteOutputStream.close();
      byte[] entryBytes = byteOutputStream.toByteArray();
      gtfsContent.put(entry.getName(),
        entryBytes);
    }
    parseFiles(new ByteArrayInputStream(gtfsContent.get("agency.txt")),
      new ByteArrayInputStream(gtfsContent.get("calendar.txt")),
      new ByteArrayInputStream(gtfsContent.get("routes.txt")),
      new ByteArrayInputStream(gtfsContent.get("stops.txt")),
      new ByteArrayInputStream(gtfsContent.get("trips.txt")),
      new ByteArrayInputStream(gtfsContent.get("stop_times.txt")));
  }
  
  @SuppressWarnings("NestedAssignment")
  public void parseFiles(InputStream agency_txt, InputStream calendar_txt, InputStream routes_txt, InputStream stops_txt, InputStream trips_txt, InputStream stop_times_txt) throws UnsupportedEncodingException, IOException {
			List<String> firstRow;
			List<String> rowAsTokens = new ArrayList<>();
      // parse agency.txt
    CsvListReader txtReader = new CsvListReader(new InputStreamReader(agency_txt, "UTF-8"),
					CsvPreference.STANDARD_PREFERENCE);
    // agency_id,agency_name,agency_url,agency_timezone
    int indexAgencyId = -1;
    int indexAgencyName = -1;
    int indexAgencyUrl = -1;
    int indexAgencyTimezone = -1;
    rowAsTokens.clear();
    firstRow = txtReader.read();
    for (int i = 0; i < firstRow.size(); i++) {
      if (firstRow.get(i).trim().equalsIgnoreCase("agency_id")) {
        indexAgencyId = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("agency_name")) {
        indexAgencyName = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("agency_url")) {
        indexAgencyUrl = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("agency_timezone")) {
        indexAgencyTimezone = i;
      }
    }

    while ((rowAsTokens = txtReader.read()) != null) {
      GTFSBusAgency busAgency = new GTFSBusAgency(rowAsTokens.get(indexAgencyName),
        rowAsTokens.get(indexAgencyUrl),
        rowAsTokens.get(indexAgencyTimezone),
        null, //agencyLang
        null, //agencyPhone
        null //agencyFareUrl
      ); 
    }
    //parse calendar.txt
    // service_id,monday,tuesday,wednesday,thursday,friday,
    //saturday,sunday,start_date,end_date
    int indexServiceId = -1;
    int indexMon = -1;
    int indexTue = -1;
    int indexWed = -1;
    int indexThu = -1;
    int indexFri = -1;
    int indexSat = -1;
    int indexSun = -1;
    int indexStartDate = -1;
    int indexEndDate = -1;
    rowAsTokens.clear();
    firstRow = txtReader.read();
    for (int i = 0; i < firstRow.size(); i++) {
      if (firstRow.get(i).trim().equalsIgnoreCase("service_id")) {
        indexServiceId = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("monday")) {
        indexMon = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("tuesday")) {
        indexTue = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("wednesday")) {
        indexWed = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("thursday")) {
        indexThu = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("friday")) {
        indexFri = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("saturday")) {
        indexSat = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("sunday")) {
        indexSun = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("start_date")) {
        indexStartDate = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("end_date")) {
        indexEndDate = i;
      }
    }

    while ((rowAsTokens = txtReader.read()) != null) {
      String startDate = rowAsTokens.get(indexStartDate);
      startDate = startDate.substring(0, 4) + "-" + startDate.substring(4, 6) + "-"
          + startDate.substring(6);
      String endDate = rowAsTokens.get(indexEndDate);
      endDate = endDate.substring(0, 4) + "-" + endDate.substring(4, 6) + "-" + endDate.substring(6);

      char monday = rowAsTokens.get(indexMon).charAt(0);
      char thuesday = rowAsTokens.get(indexTue).charAt(0);
      char wednesday = rowAsTokens.get(indexWed).charAt(0);
      char thursday = rowAsTokens.get(indexThu).charAt(0);
      char friday = rowAsTokens.get(indexFri).charAt(0);
      char saturday = rowAsTokens.get(indexSat).charAt(0);
      char sunday = rowAsTokens.get(indexSun).charAt(0);
        try {
          GTFSBusCalendar busCalendar = new GTFSBusCalendar(monday,
            thuesday,
            wednesday,
            thursday,
            friday,
            saturday,
            sunday,
            SimpleDateFormat.getTimeInstance().parse(startDate),
            SimpleDateFormat.getTimeInstance().parse(endDate)
          );
        } catch (ParseException ex) {
          Logger.getLogger(GTFSPublicTransportDataService.class.getName()).
            log(Level.SEVERE,
            null,
            ex);
        }
    }
    //routes.txt
    // route_id,route_short_name,route_long_name,route_type
    txtReader = new CsvListReader(new InputStreamReader(routes_txt, "UTF-8"),
					CsvPreference.STANDARD_PREFERENCE);
    int indexRouteId = -1;
    int indexShort = -1;
    int indexLong = -1;
    int indexType = -1;
    rowAsTokens.clear();
    firstRow = txtReader.read();
    for (int i = 0; i < firstRow.size(); i++) {
      if (firstRow.get(i).trim().equalsIgnoreCase("route_id")) {
        indexRouteId = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("route_short_name")) {
        indexShort = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("route_long_name")) {
        indexLong = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("route_type")) {
        indexType = i;
      }
    }

    while ((rowAsTokens = txtReader.read()) != null) {
      String routeId = rowAsTokens.get(indexRouteId);
      String routeNameShort = rowAsTokens.get(indexShort);
      String routeNameLong = rowAsTokens.get(indexLong);
      String routeType = rowAsTokens.get(indexType);
      GTFSBusRoute busRoute = new GTFSBusRoute(idGenerator.getNextId(),routeId,
        null, //agency
        null, //routeURL
        null, //routeColor
        null, //routeTextColor
        routeNameShort,
        routeNameLong,
        true);
    }
    
    //stops.txt
    txtReader = new CsvListReader(new InputStreamReader(stops_txt, "UTF-8"),
					CsvPreference.STANDARD_PREFERENCE);
    // stop_name,stop_id,stop_lat,stop_lon
    int indexStopName = -1;
    int indexStopId = -1;
    int indexLat = -1;
    int indexLng = -1;
    rowAsTokens.clear();
    firstRow = txtReader.read();
    for (int i = 0; i < firstRow.size(); i++) {
      if (firstRow.get(i).trim().equalsIgnoreCase("stop_name")) {
        indexStopName = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("stop_id")) {
        indexStopId = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("stop_lat")) {
        indexLat = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("stop_lon")) {
        indexLong = i;
      }
    }

    while ((rowAsTokens = txtReader.read()) != null) {
      String stopName = rowAsTokens.get(indexStopName);
      String lat = rowAsTokens.get(indexLat);
      String lng = rowAsTokens.get(indexLong);
      BusStop busStop = new BusStop(
        stopName,
        null,
        new BaseCoordinate(Double.parseDouble(lat),
          Double.parseDouble(lng)));
    }
    
    //trips.txt
    txtReader = new CsvListReader(new InputStreamReader(trips_txt, "UTF-8"),
					CsvPreference.STANDARD_PREFERENCE);
    // service_id,route_id,trip_id
    int indexTripId = -1;
    rowAsTokens.clear();
    firstRow = txtReader.read();
    for (int i = 0; i < firstRow.size(); i++) {
      if (firstRow.get(i).trim().equalsIgnoreCase("service_id")) {
        indexServiceId = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("route_id")) {
        indexRouteId = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("trip_id")) {
        indexTripId = i;
      }
    }

    while ((rowAsTokens = txtReader.read()) != null) {
      String serviceId = rowAsTokens.get(indexServiceId);
      String tripId = rowAsTokens.get(indexTripId);
      String routeId = rowAsTokens.get(indexRouteId);
      BusTrip busTrip = new BusTrip(null,
        tripId,
        tripId,
        null,
        null,
        null);
    }
    
    //stop_times.txt
    txtReader = new CsvListReader(new InputStreamReader(stops_txt, "UTF-8"),
					CsvPreference.STANDARD_PREFERENCE);
    // trip_id,stop_id,arrival_time,departure_time,
    //stop_sequence
    int indexArrival = -1;
    int indexDeparture = -1;
    int indexSeq = -1;
    rowAsTokens.clear();
    firstRow = txtReader.read();
    for (int i = 0; i < firstRow.size(); i++) {
      if (firstRow.get(i).trim().equalsIgnoreCase("trip_id")) {
        indexTripId = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("stop_id")) {
        indexStopId = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("arrival_time")) {
        indexArrival = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("departure_time")) {
        indexDeparture = i;
      }
      else if (firstRow.get(i).trim().equalsIgnoreCase("stop_sequence")) {
        indexSeq = i;
      }
    }

    while ((rowAsTokens = txtReader.read()) != null) {
      String tripId = rowAsTokens.get(indexTripId);
      String stopId = rowAsTokens.get(indexStopId);
      String arrival_time = rowAsTokens.get(indexArrival);
      String departure_time = rowAsTokens.get(indexDeparture);
      String stopSequence = rowAsTokens.get(indexSeq);
      
    }
    
    txtReader.close();
  }

  /**
   * Combines motorways with busstops. Some of the nodes in
   *
   * @param trafficGraph
   * @Way.getNodeList() are instances of
   * @BusStop!
   */
  @Override
  public void insertBusStops(
    CityInfrastructureData cityInfrastructureData,
    Set<BusStop> busStops,
    TrafficGraph trafficGraph) {
    List<TrafficWay> wayList = new ArrayList<>(cityInfrastructureData.getWays());

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
          GeoToolsBootstrapping.getDistanceInKM(busStop.getX(),
            busStop.getY(),
            node.getX(),
            node.getY())));
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
              && (GeoToolsBootstrapping.getDistanceInKM(node1.
                getX(),
                node1.getY(),
                node2.getX(),
                node2.getY()) <= GeoToolsBootstrapping.
              getDistanceInKM(
                node2.getX(),
                node2.getY(),
                busStop.getX(),
                busStop.getY()))) {

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
  }
  
  //////////////////////////////
  // getter and setter
  //////////////////////////////

  @Override
  public Set<BusStop> getBusStops() {
    return busStops;
  }

  @Override
  public Set<BusRoute> getBusRoutes() {
    return busRoutes;
  }

  public Set<GTFSBusCalendar> getBusCalendars() {
    return busCalendars;
  }

  public Set<GTFSBusAgency> getBusAgencys() {
    return busAgencys;
  }
}
