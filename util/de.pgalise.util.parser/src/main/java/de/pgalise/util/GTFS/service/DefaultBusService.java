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
 
package de.pgalise.util.GTFS.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import de.pgalise.simulation.shared.traffic.BusRoute;
import de.pgalise.simulation.traffic.BusStopTime;
import de.pgalise.simulation.traffic.BusTrip;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lena
 */
@Singleton
public class DefaultBusService implements BusService {
	private final static Logger LOGGER = LoggerFactory.getLogger(DefaultBusService.class);
	private Connection con;
	@PersistenceContext
	private EntityManager entityManager;

	// public static void main(String[] args) {
	// DefaultBusService bs = new DefaultBusService();
	// try {
	// bs.getAllBusRoutes();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	@Override
	public List<BusTrip> getBusLineData(String busRoute, long timeInMs) throws ClassNotFoundException, SQLException {
		List<BusTrip> trips = new ArrayList<>();
		TypedQuery<BusTrip> query = entityManager.createQuery("SELECT x FROM BusTrip x",
			BusTrip.class);
		List<BusTrip> retValue = query.getResultList();
		return retValue;
	}

	@Override
	public int getTotalNumberOfBusTrips(List<BusRoute> busRoutes, long timeInMs) {
		Query query = entityManager.createQuery("SELECT COUNT(x) FROM BusTrip x");
		int retValue = (int) query.getSingleResult();
		return retValue;
	}

	// @Override
	// public List<BusRoute> getAllBusRoutes(InputStream routesStream) {
	// try {
	// // FileInputStream input = new FileInputStream(new File(
	// // "VWG-GTFS-Files/routes.txt"));
	// // CsvListReader csvReader = new CsvListReader(new InputStreamReader(
	// // input), CsvPreference.STANDARD_PREFERENCE);
	//
	// CsvListReader csvReader = new CsvListReader(new InputStreamReader(routesStream),
	// CsvPreference.STANDARD_PREFERENCE);
	//
	// List<BusRoute> routes = new ArrayList<>();
	// List<String> rowAsTokensAllStops;
	// csvReader.read();//skip first line
	// while ((rowAsTokensAllStops = csvReader.read()) != null) {
	// BusRoute route = new BusRoute(rowAsTokensAllStops.get(0).trim(), rowAsTokensAllStops.get(1).trim(),
	// rowAsTokensAllStops.get(2).trim(), Integer.valueOf(rowAsTokensAllStops.get(3).trim()));
	// routes.add(route);
	// System.out.println(route.getRouteId() + " " + route.getRouteShortName() + " " + route.getRouteLongName() + " " +
	// route.getRouteType());
	// }
	// csvReader.close();
	//
	// return null;
	// } catch (IOException e) {
	// throw new RuntimeException(e);
	// }
	// }

	@Override
	public List<BusRoute> getAllBusRoutes() throws ClassNotFoundException, SQLException {
		List<BusRoute> routes = new ArrayList<>();
		
		

		Class.forName("org.apache.derby.jdbc.ClientDriver");
		con = DriverManager.getConnection("jdbc:derby://127.0.0.1:5201/database", "pgalise", "somepw");

		String query = "select route_id, route_short_name, route_long_name, route_type from pgalise.bus_routes";
		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(query);

		while (rs.next()) {
			BusRoute r = new BusRoute(rs.getString("route_id"), rs.getString("route_short_name"),
					rs.getString("route_long_name"), rs.getInt("route_type"));
			// System.out.println(rs.getString("route_id") + ", " + rs.getString("route_short_name") + ", " +
			// rs.getString("route_long_name") + ", " + rs.getInt("route_type"));
			routes.add(r);
		}

		return routes;
	}

	// verschoben, siehe de.pgalise.simulation.traffic.route.BusStopParser
	// public List<BusStop> parseBusStops(Graph graph, CityInfrastructureData
	// cityInfo) {}
}
