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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;

import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import de.pgalise.simulation.shared.traffic.BusLineInformation;
import de.pgalise.simulation.shared.traffic.BusStopInformation;
import de.pgalise.simulation.shared.traffic.BusTrip;

/**
 * @author Lena
 */
public class VWGGTFSCreator {

	private List<String> csvFiles;
	private List<BusStopInformation> busstops;
	private List<BusTrip> weekdayTripWayThere;
	private List<BusTrip> weekdayTripWayBack;
	private List<BusTrip> weekendTripWayThere;
	private List<BusTrip> weekendTripWayBack;

	public static void main(String[] args) {
		VWGGTFSCreator p = new VWGGTFSCreator();
		try {
			// p.parseVWGFilesToTrips();
			p.parseVWGCSVFilesToStopTimes("301");
			p.parseVWGCSVFilesToStopTimes("302");
			p.parseVWGCSVFilesToStopTimes("303");
			p.parseVWGCSVFilesToStopTimes("304");
			p.parseVWGCSVFilesToStopTimes("306");
			p.parseVWGCSVFilesToStopTimes("307");
			p.parseVWGCSVFilesToStopTimes("308");
			p.parseVWGCSVFilesToStopTimes("309");
			p.parseVWGCSVFilesToStopTimes("310");
			p.parseVWGCSVFilesToStopTimes("312");
			p.parseVWGCSVFilesToStopTimes("314");
			p.parseVWGCSVFilesToStopTimes("315");
			p.parseVWGCSVFilesToStopTimes("316");
			p.parseVWGCSVFilesToStopTimes("317");
			p.parseVWGCSVFilesToStopTimes("322");
			p.parseVWGCSVFilesToStopTimes("324");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void parseVWGFilesToTrips() throws IOException {
		FileInputStream input = new FileInputStream(new File("VWG-GTFS-Files/stop_times.txt"));
		CsvListReader csvReader = new CsvListReader(new InputStreamReader(input, "UTF-8"),
				CsvPreference.STANDARD_PREFERENCE);

		String trip_id;
		String route_id;
		String service_id;
		HashSet<String> set = new HashSet<>();
		List<String> rowAsTokensAllStops;
		csvReader.read();// skip first line
		while ((rowAsTokensAllStops = csvReader.read()) != null) {
			set.add(rowAsTokensAllStops.get(0).trim());
		}
		csvReader.close();

		for (String entry : set) {
			trip_id = entry;
			route_id = entry.substring(0, 4);
			service_id = entry.substring(4, 6);
			System.out.println(service_id + "," + route_id + "," + trip_id);
		}
	}

	/**
	 * @throws IOException
	 */
	public void parseVWGCSVFilesToStopTimes(String busLine) throws IOException {
		String base_trip_id = "";
		String trip_id;
		String stop_id;
		String arrival_time;
		String departure_time;
		String stop_sequence;
		int numberOfTrips = 0;
		String busStopName;
		String stopTime = "06:00";
		busstops = new ArrayList<>();

		csvFiles = new ArrayList<>();
		final File folder = new File("VWG-CSV-Files");
		listRelevantFilesForFolder(folder, busLine);

		for (int i = 0; i < csvFiles.size(); i++) {
			String busLineToken = csvFiles.get(i).split("/")[1].substring(0, 5);
			String line = busLineToken.substring(0, 3);
			busLineToken = busLineToken.substring(3, 5);

			// System.out.println(line+" "+busLineToken);
			if (busLineToken.equals("HW")) {
				base_trip_id = line + "aWD";
				numberOfTrips = 72;
			} else if (busLineToken.equals("HE")) {
				base_trip_id = line + "aWE";
				numberOfTrips = 36;
			} else if (busLineToken.equals("RW")) {
				base_trip_id = line + "bWD";
				numberOfTrips = 72;
			} else if (busLineToken.equals("RE")) {
				base_trip_id = line + "bWE";
				numberOfTrips = 36;
			}

			FileInputStream inputStops = new FileInputStream(new File("VWG-GTFS-Files/stops.txt"));
			CsvListReader csvReaderStops = new CsvListReader(new InputStreamReader(inputStops, "UTF-8"),
					CsvPreference.STANDARD_PREFERENCE);
			// Map<String,String> stopsMap = new HashMap<String,String>();
			List<BusStop> stopsList = new ArrayList<>();
			String stopsStopName;
			String stopsStopId;
			List<String> rowAsTokensStops;
			while ((rowAsTokensStops = csvReaderStops.read()) != null) {
				stopsStopName = rowAsTokensStops.get(0).trim();
				stopsStopId = rowAsTokensStops.get(1).trim();
				// stopsMap.put(stopsStopId, stopsStopName);//keine Map, da die
				// Ids nicht unique sind!!
				stopsList.add(new BusStop(stopsStopId, stopsStopName));
				// System.out.println(stopsStopId + " " + stopsStopName);
			}

			csvReaderStops.close();

			FileInputStream input = new FileInputStream(new File(csvFiles.get(i)));
			CsvListReader csvReader = new CsvListReader(new InputStreamReader(input, "UTF-8"),
					CsvPreference.STANDARD_PREFERENCE);
			List<String> rowAsTokens;
			int stopCount = 1;
			while ((rowAsTokens = csvReader.read()) != null) {
				trip_id = "";
				stop_id = "";
				busStopName = rowAsTokens.get(0).trim();
				stopTime = rowAsTokens.get(1).trim();
				stop_sequence = String.valueOf(stopCount);

				// for (Map.Entry<String, String> entry : stopsMap.entrySet()) {
				// if (busStopName.equals(entry.getValue()))
				// stop_id = entry.getKey();
				// c++;
				// }

				for (BusStop stop : stopsList) {
					if (busStopName.equals(stop.name))
						stop_id = stop.id;
				}
				// System.out.println("Count: "+c+", ListSize: "+stopsList.size());

				// if (stop_id.isEmpty())
				// System.out.println("No stop matches " + busStopName);

				if (!stop_id.isEmpty()) {
					int hour = Integer.parseInt(stopTime.split(":")[0]);
					int minute = Integer.parseInt(stopTime.split(":")[1]);
					if (minute < 10)
						arrival_time = hour + ":0" + minute;
					else
						arrival_time = hour + ":" + minute;
					departure_time = arrival_time;
					trip_id = base_trip_id + 1;
					System.out.println(trip_id + "," + stop_id + "," + arrival_time + ":00," + departure_time + ":00,"
							+ stop_sequence);

					int minutes = hour * 60 + minute;

					int minutesToAdd = 15;
					if (busLineToken.equals("HW") || busLineToken.equals("RW"))
						minutesToAdd = 15;
					else if (busLineToken.equals("HE") || busLineToken.equals("RE"))
						minutesToAdd = 30;

					for (int j = 2; j < numberOfTrips + 1; j++) {
						trip_id = base_trip_id + j;
						minutes += minutesToAdd;
						hour = (int) Math.floor(minutes / 60);
						minute = minutes - (hour * 60);
						if (hour >= 24)
							hour -= 24;
						if (minute < 10)
							arrival_time = hour + ":0" + minute;
						else
							arrival_time = hour + ":" + minute;
						departure_time = arrival_time;
						System.out.println(trip_id + "," + stop_id + "," + arrival_time + ":00," + departure_time
								+ ":00," + stop_sequence);
					}
					stopCount++;
				}
			}
			// System.out.println(stopCount);
			csvReader.close();
		}
	}

	public void listMissingBusstops() throws IOException {
		FileInputStream input = new FileInputStream(new File("VWG-GTFS-Files/stops.txt"));
		CsvListReader csvReader = new CsvListReader(new InputStreamReader(input, "UTF-8"),
				CsvPreference.STANDARD_PREFERENCE);
		List<String> stops = new ArrayList<>();
		String stopsStopName;
		List<String> rowAsTokensAllStops;
		csvReader.read();// skip first line
		while ((rowAsTokensAllStops = csvReader.read()) != null) {
			stopsStopName = rowAsTokensAllStops.get(0).trim();
			stops.add(stopsStopName);
		}
		csvReader.close();

		FileInputStream inputStops = new FileInputStream(new File("VWGTXTSchedules/allStops.txt"));
		CsvListReader csvReaderStops = new CsvListReader(new InputStreamReader(inputStops, "UTF-8"),
				CsvPreference.STANDARD_PREFERENCE);
		int count = 10000000;
		List<String> rowAsTokensStops;
		boolean contains = false;
		while ((rowAsTokensStops = csvReaderStops.read()) != null) {
			stopsStopName = rowAsTokensStops.get(0).trim();
			for (String stop : stops) {
				if (stop.equals(stopsStopName))
					contains = true;
			}
			if (!contains) {
				System.out.println(stopsStopName + "," + count + ",,");
				count++;
			}
			contains = false;
		}
		csvReaderStops.close();
	}

	/**
	 * parses all VWG CSV files from the specified path for a specific busline
	 */
	public void parseVWGCSVFiles(String busLine) throws IOException {
		String busStopName;
		String stopTime = "06:00";
		busstops = new ArrayList<>();

		csvFiles = new ArrayList<>();
		final File folder = new File("VWG-CSV-Files");
		listRelevantFilesForFolder(folder, busLine);

		for (int i = 0; i < csvFiles.size(); i++) {
			FileInputStream input = new FileInputStream(new File(csvFiles.get(i)));
			CsvListReader csvReader = new CsvListReader(new InputStreamReader(input, "UTF-8"),
					CsvPreference.STANDARD_PREFERENCE);

			// BusTrip busTrip = new BusTrip();

			List<String> rowAsTokens;
			while ((rowAsTokens = csvReader.read()) != null) {
				busStopName = rowAsTokens.get(0).trim();
				stopTime = rowAsTokens.get(1).trim();

				System.out.println(busStopName + " " + stopTime);

				int hour = Integer.parseInt(stopTime.split(":")[0]);
				int minute = Integer.parseInt(stopTime.split(":")[1]);

				GregorianCalendar cal = new GregorianCalendar();
				// January 1, 1970
				cal.set(Calendar.YEAR, 1970);
				cal.set(Calendar.MONTH, Calendar.JANUARY);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.HOUR_OF_DAY, hour);
				cal.set(Calendar.MINUTE, minute);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);

				busstops.add(new BusStopInformation(busStopName, cal.getTimeInMillis()));
			}

			String busLineToken = csvFiles.get(i).split("/")[1].substring(0, 5);
			// String line = busLineToken.substring(0, 3);
			busLineToken = busLineToken.substring(3, 5);

			// System.out.println(line+" "+busLineToken);
			// if (busLineToken.equals("HW")) {
			//
			// } else if (busLineToken.equals("HE")) {
			//
			// } else if (busLineToken.equals("RW")) {
			//
			// } else if (busLineToken.equals("RE")) {
			//
			// }
			csvReader.close();
		}

		BusLineInformation info = new BusLineInformation();
		info.setWeekdayTripWayBack(weekdayTripWayBack);
		info.setWeekdayTripWayThere(weekdayTripWayThere);
		info.setWeekendTripWayBack(weekendTripWayBack);
		info.setWeekendTripWayThere(weekendTripWayThere);
	}

	public void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				csvFiles.add(fileEntry.getPath());
			}
		}
	}

	public void listRelevantFilesForFolder(final File folder, String containingString) {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				if (fileEntry.getName().contains(containingString)) {
					// System.out.println(fileEntry.getPath());
					csvFiles.add(fileEntry.getPath());
				}
			}
		}
	}

	private class BusStop {
		private String id;
		private String name;

		public BusStop(String id, String name) {
			this.id = id;
			this.name = name;
		}

		/**
		 * @return the id
		 */
		@SuppressWarnings("unused")
		public String getId() {
			return id;
		}

		/**
		 * @param id
		 *            the id to set
		 */
		@SuppressWarnings("unused")
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * @return the name
		 */
		@SuppressWarnings("unused")
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		@SuppressWarnings("unused")
		public void setName(String name) {
			this.name = name;
		}
	}
}
