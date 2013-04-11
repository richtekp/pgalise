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
 
package de.pgalise.util.GTFS.dataParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

/**
 * @author Lena
 */
public class GTFSParser {

	private Connection con;

	private GTFSParser() {
		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver");
			con = DriverManager
					.getConnection("jdbc:derby://127.0.0.1:5201/database ", "pgalise", "somepw");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private List<File> txtFiles;

	public static void main(String[] args) {
		Date d = new Date(System.currentTimeMillis());
		System.out.println(d.toString());
		GTFSParser p = new GTFSParser();
		try {
			p.saveGTFSToDB("VWG-GTFS-Files");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveGTFSToDB(String pathToGTFSFiles) throws IOException {
		txtFiles = new ArrayList<>();
		File folder = new File(pathToGTFSFiles);
		listFilesForFolder(folder);
		// sort files for correct insert order
		// order: agency, calendar, routes, stops, trips, stop_times
		HashMap<Integer, File> sortedFilesMap = new HashMap<>();
		for (File f : txtFiles) {
			switch (f.getName()) {
				case "agency.txt":
					sortedFilesMap.put(0, f);
					break;
				case "calendar.txt":
					sortedFilesMap.put(1, f);
					break;
				case "routes.txt":
					sortedFilesMap.put(2, f);
					break;
				case "stops.txt":
					sortedFilesMap.put(3, f);
					break;
				case "trips.txt":
					sortedFilesMap.put(4, f);
					break;
				case "stop_times.txt":
					sortedFilesMap.put(5, f);
					break;
				default:
					break;
			}
		}

		// List<File> sortedFiles = new ArrayList<>();
		// for (int i = 0; i < 6; i++) {
		// sortedFiles.add(sortedFilesMap.get(i));
		// }

		for (int j = 0; j < 6; j++) {
			File file = sortedFilesMap.get(j);
			FileInputStream input = new FileInputStream(file);
			CsvListReader txtReader = new CsvListReader(new InputStreamReader(input, "UTF-8"),
					CsvPreference.STANDARD_PREFERENCE);

			List<String> firstRow;
			List<String> rowAsTokens = new ArrayList<>();
			if (file.getName().equalsIgnoreCase("agency.txt")) {
				// agency_id,agency_name,agency_url,agency_timezone
				int indexAgencyId = -1;
				int indexAgencyName = -1;
				int indexAgencyUrl = -1;
				int indexAgencyTimezone = -1;
				rowAsTokens.clear();
				firstRow = txtReader.read();
				for (int i = 0; i < firstRow.size(); i++) {
					if (firstRow.get(i).trim().equalsIgnoreCase("agency_id"))
						indexAgencyId = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("agency_name"))
						indexAgencyName = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("agency_url"))
						indexAgencyUrl = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("agency_timezone"))
						indexAgencyTimezone = i;
				}

				while ((rowAsTokens = txtReader.read()) != null) {
					String query = "insert into pgalise.bus_agency(agency_id,agency_name,agency_url,agency_timezone) values('";

					query += rowAsTokens.get(indexAgencyId) + "','" + rowAsTokens.get(indexAgencyName) + "','"
							+ rowAsTokens.get(indexAgencyUrl) + "','" + rowAsTokens.get(indexAgencyTimezone) + "')";

					try {
						Statement st = con.createStatement();
						st.executeUpdate(query);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				try {
					con.commit();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (file.getName().equalsIgnoreCase("calendar.txt")) {
				// service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date
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
					if (firstRow.get(i).trim().equalsIgnoreCase("service_id"))
						indexServiceId = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("monday"))
						indexMon = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("tuesday"))
						indexTue = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("wednesday"))
						indexWed = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("thursday"))
						indexThu = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("friday"))
						indexFri = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("saturday"))
						indexSat = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("sunday"))
						indexSun = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("start_date"))
						indexStartDate = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("end_date"))
						indexEndDate = i;
				}

				while ((rowAsTokens = txtReader.read()) != null) {
					String query = "insert into pgalise.bus_calendar(service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date) values('";

					String startDate = rowAsTokens.get(indexStartDate);
					startDate = startDate.substring(0, 4) + "-" + startDate.substring(4, 6) + "-"
							+ startDate.substring(6);
					String endDate = rowAsTokens.get(indexEndDate);
					endDate = endDate.substring(0, 4) + "-" + endDate.substring(4, 6) + "-" + endDate.substring(6);

					query += rowAsTokens.get(indexServiceId) + "','" + rowAsTokens.get(indexMon) + "','"
							+ rowAsTokens.get(indexTue) + "','" + rowAsTokens.get(indexWed) + "','"
							+ rowAsTokens.get(indexThu) + "','" + rowAsTokens.get(indexFri) + "','"
							+ rowAsTokens.get(indexSat) + "','" + rowAsTokens.get(indexSun) + "','" + startDate + "','"
							+ endDate + "')";

					try {
						Statement st = con.createStatement();
						st.executeUpdate(query);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				try {
					con.commit();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (file.getName().equalsIgnoreCase("routes.txt")) {
				// route_id,route_short_name,route_long_name,route_type
				int indexRouteId = -1;
				int indexShort = -1;
				int indexLong = -1;
				int indexType = -1;
				rowAsTokens.clear();
				firstRow = txtReader.read();
				for (int i = 0; i < firstRow.size(); i++) {
					if (firstRow.get(i).trim().equalsIgnoreCase("route_id"))
						indexRouteId = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("route_short_name"))
						indexShort = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("route_long_name"))
						indexLong = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("route_type"))
						indexType = i;
				}

				while ((rowAsTokens = txtReader.read()) != null) {
					String query = "insert into pgalise.bus_routes(route_id,route_short_name,route_long_name,route_type) values('";

					query += rowAsTokens.get(indexRouteId) + "','" + rowAsTokens.get(indexShort) + "','"
							+ rowAsTokens.get(indexLong) + "'," + rowAsTokens.get(indexType) + ")";

					try {
						Statement st = con.createStatement();
						st.executeUpdate(query);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				try {
					con.commit();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (file.getName().equalsIgnoreCase("stops.txt")) {
				// stop_name,stop_id,stop_lat,stop_lon
				int indexStopName = -1;
				int indexStopId = -1;
				int indexLat = -1;
				int indexLong = -1;
				rowAsTokens.clear();
				firstRow = txtReader.read();
				for (int i = 0; i < firstRow.size(); i++) {
					if (firstRow.get(i).trim().equalsIgnoreCase("stop_name"))
						indexStopName = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("stop_id"))
						indexStopId = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("stop_lat"))
						indexLat = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("stop_lon"))
						indexLong = i;
				}

				while ((rowAsTokens = txtReader.read()) != null) {
					String query = "insert into pgalise.bus_stops(stop_name,stop_id,stop_lat,stop_lon) values('";

					query += rowAsTokens.get(indexStopName) + "','" + rowAsTokens.get(indexStopId) + "',"
							+ rowAsTokens.get(indexLat) + "," + rowAsTokens.get(indexLong) + ")";

					try {
						Statement st = con.createStatement();
						st.executeUpdate(query);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				try {
					con.commit();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (file.getName().equalsIgnoreCase("trips.txt")) {
				// service_id,route_id,trip_id
				int indexServiceId = -1;
				int indexRouteId = -1;
				int indexTripId = -1;
				rowAsTokens.clear();
				firstRow = txtReader.read();
				for (int i = 0; i < firstRow.size(); i++) {
					if (firstRow.get(i).trim().equalsIgnoreCase("service_id"))
						indexServiceId = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("route_id"))
						indexRouteId = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("trip_id"))
						indexTripId = i;
				}

				while ((rowAsTokens = txtReader.read()) != null) {
					String query = "insert into pgalise.bus_trips(service_id,route_id,trip_id) values('";

					query += rowAsTokens.get(indexServiceId) + "','" + rowAsTokens.get(indexRouteId) + "','"
							+ rowAsTokens.get(indexTripId) + "')";

					try {
						Statement st = con.createStatement();
						st.executeUpdate(query);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				try {
					con.commit();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else if (file.getName().equalsIgnoreCase("stop_times.txt")) {
				// trip_id,stop_id,arrival_time,departure_time,stop_sequence
				int indexTripId = -1;
				int indexStopId = -1;
				int indexArrival = -1;
				int indexDeparture = -1;
				int indexSeq = -1;
				rowAsTokens.clear();
				firstRow = txtReader.read();
				for (int i = 0; i < firstRow.size(); i++) {
					if (firstRow.get(i).trim().equalsIgnoreCase("trip_id"))
						indexTripId = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("stop_id"))
						indexStopId = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("arrival_time"))
						indexArrival = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("departure_time"))
						indexDeparture = i;
					else if (firstRow.get(i).trim().equalsIgnoreCase("stop_sequence"))
						indexSeq = i;
				}

				while ((rowAsTokens = txtReader.read()) != null) {
					String query = "insert into pgalise.bus_stop_times(trip_id,stop_id,arrival_time,departure_time,stop_sequence) values('";

					query += rowAsTokens.get(indexTripId) + "','" + rowAsTokens.get(indexStopId) + "','"
							+ rowAsTokens.get(indexArrival) + "','" + rowAsTokens.get(indexDeparture) + "',"
							+ rowAsTokens.get(indexSeq) + ")";

					try {
						Statement st = con.createStatement();
						st.executeUpdate(query);
					} catch (SQLException e) {
						// System.out.println(query);
						e.printStackTrace();
					}
				}
				try {
					con.commit();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			txtReader.close();
		}
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Date d = new Date(System.currentTimeMillis());
		System.out.println(d.toString());
	}

	public void listFilesForFolder(File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				txtFiles.add(fileEntry);
			}
		}
	}
}
