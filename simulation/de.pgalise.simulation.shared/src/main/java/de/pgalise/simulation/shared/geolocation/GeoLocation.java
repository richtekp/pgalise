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
 
package de.pgalise.simulation.shared.geolocation;

import java.io.Serializable;

/**
 * Class to store geo information
 * 
 * @author Marcus
 */
public class GeoLocation implements Serializable {

	/**
	 * generated Id for {@link Serializable}
	 */
	private static final long serialVersionUID = -794557192923070654L;

	/**
	 * format string (i.e. 12,32°)
	 */
	public final static String FORMAT_DECIMALDEGREE = "formatDecimalDegree";

	/**
	 * format string (i.e. 43° 12' 32'')
	 */
	public final static String FORMAT_DEGREE_MINUTE_SECOND = "formatDegreeMinuteSeconds";

	/**
	 * format string (i.e. 43° 12' 32.23'')
	 */
	public final static String FORMAT_DEGREE_MINUTE_DECIMALSECOND = "formatDegreeMinuteDecimalSecond";

	/**
	 * format string (i.e. 12° 33,102')
	 */
	public final static String FORMAT_DEGREE_DECIMALMINUTES = "formatDegreeMinuteDecimalMinutes";

	/**
	 * Parses a string and returns a {@link GeoLocation} object.
	 * 
	 * @param string
	 *            the string from which the {@link GeoLocation} object shall be created and returned
	 * @return a {@link GeoLocation} object created from the passed string.
	 * @throws NumberFormatException
	 *             if the string has the wrong format.
	 */
	public static GeoLocation parse(final String string) throws NumberFormatException {
		try {
			final String trimmedString = string.replace(" ", "");
			if (string.replace(" ", "").matches("^(.*)(.*)$")) {
				final String[] result = trimmedString.split("[(]|[)]");
				return new GeoLocation(Latitude.parse(result[1]), Longitude.parse(result[3]));
			} else if (string.replace(" ", "").matches("^(.*),(.*)$")) {
				final String[] result = trimmedString.split("[(]|[)]|[,]");
				return new GeoLocation(Latitude.parse(result[1]), Longitude.parse(result[4]));
			} else if (string.replace(" ", "").matches("^[,]?$")) {
				final String[] result = trimmedString.split("[,]");
				return new GeoLocation(Latitude.parse(result[0]), Longitude.parse(result[1]));
			} else if (string.matches(".*[ ].*")) {
				final String[] result = trimmedString.split("\\s");
				return new GeoLocation(Latitude.parse(result[0]), Longitude.parse(result[1]));
			}
		} catch (Exception e) {
			throw e;
			// throw new NumberFormatException("Couldn't parse '" + string + "' to create a GeoLocation.");
		}
		throw new NumberFormatException("Couldn't parse '" + string + "' to create a GeoLocation.");
	}

	/**
	 * the {@link Latitude} object belonging to this {@link GeoLocation} object
	 */
	private Latitude latitude;

	/**
	 * the {@link Longitude} object belonging to this {@link GeoLocation} object
	 */
	private Longitude longitude;

	/**
	 * Creates a {@link GeoLocation} object with default {@link Latitude} and default {@link Longitude}.
	 */
	public GeoLocation() {
		this.latitude = new Latitude();
		this.longitude = new Longitude();
	}

	/**
	 * Creates a {@link GeoLocation} whereas its {@link Latitude} and {@link Longitude} is created from the passed
	 * double values.
	 * 
	 * @param latitude
	 *            the double for the {@link Latitude}
	 * @param longitude
	 *            the double for the {@link Longitude}
	 * @throws IllegalArgumentException
	 *             if at least one of the arguments has the false range
	 */
	public GeoLocation(final double latitude, final double longitude) throws IllegalArgumentException {
		this.setLatitude(new Latitude(latitude));
		this.setLongitude(new Longitude(longitude));
	}

	/**
	 * Creates a {@link GeoLocation} with the passed {@link Latitude} and {@link Longitude}.
	 * 
	 * @param latitude
	 *            the {@link Latitude} of the {@link GeoLocation} object to create
	 * @param longitude
	 *            the {@link Longitude} of the {@link GeoLocation} object to create
	 * @throws IllegalArgumentException
	 *             if at least one of the passed arguments is 'null'
	 */
	public GeoLocation(final Latitude latitude, final Longitude longitude) throws IllegalArgumentException {
		this.setLatitude(latitude);
		this.setLongitude(longitude);
	}

	/**
	 * Returns the {@link Latitude} of this {@link GeoLocation}.
	 * 
	 * @return the {@link Latitude} of this {@link GeoLocation}
	 */
	public Latitude getLatitude() {
		return this.latitude;
	}

	/**
	 * Returns the {@link Longitude} of this {@link GeoLocation}.
	 * 
	 * @return the {@link Longitude} of this {@link GeoLocation}
	 */
	public Longitude getLongitude() {
		return this.longitude;
	}

	/**
	 * Sets the {@link Latitude} for this {@link GeoLocation}.
	 * 
	 * @param latitude
	 *            the {@link Latitude} for this {@link GeoLocation}
	 * @throws IllegalArgumentException
	 *             if argument 'latitude' is 'null'
	 */
	public void setLatitude(Latitude latitude) throws IllegalArgumentException {
		if (latitude == null) {
			throw new IllegalArgumentException("latitude");
		}
		this.latitude = latitude;
	}

	/**
	 * Sets the {@link Longitude} for this {@link GeoLocation}.
	 * 
	 * @param latitude
	 *            the {@link Longitude} for this {@link GeoLocation}
	 * @throws IllegalArgumentException
	 *             if argument 'longitude' is 'null'
	 */
	public void setLongitude(Longitude longitude) throws IllegalArgumentException {
		if (longitude == null) {
			throw new IllegalArgumentException("longitude");
		}
		this.longitude = longitude;
	}

	/**
	 * Returns the same hash code for {@link GeoLocation}s with equal values.
	 * 
	 * @throws the
	 *             same hash code for {@link GeoLocation}s with equal values
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
		return result;
	}

	/**
	 * Checks whether an passed object is equal to to this {@link GeoLocation} object. <br>
	 * Instead of reference equality value equality is performed.
	 * 
	 * @return true if the passed argument is an instance of {@link GeoLocation} and matches all values
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeoLocation other = (GeoLocation) obj;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		return true;
	}

	/**
	 * Returns a string representation for this {@link GeoLocation}.
	 * 
	 * @return a string representation for this {@link GeoLocation}
	 */
	@Override
	public String toString() {
		return this.toString(GeoLocation.FORMAT_DECIMALDEGREE);
	}

	/**
	 * Returns a formatted string representation
	 * 
	 * @param formatString
	 *            the string determining how to format this {@link GeoLocation} object
	 * @return the string determining how to format this {@link GeoLocation} object
	 */
	public String toString(final String formatString) {
		return this.getLatitude().toString(formatString) + ", " + this.getLongitude().toString(formatString);
	}
	
	/**
	 * Calculates the distance in km.
	 * @param otherPoint
	 * @return
	 */
	public double getDistanceInKM(GeoLocation otherPoint) {

		if ((this.getLatitude().getDegree() == otherPoint.getLatitude().getDegree()) 
				&& (this.getLongitude().getDegree() == otherPoint.getLongitude().getDegree())) {
			return 0.0;
		}

		double f = 1 / 298.257223563;
		double a = 6378.137;
		double F = ((this.getLatitude().getDegree() + otherPoint.getLatitude().getDegree()) / 2) * (Math.PI / 180);
		double G = ((this.getLatitude().getDegree() - otherPoint.getLatitude().getDegree()) / 2) * (Math.PI / 180);
		double l = ((this.getLongitude().getDegree() - otherPoint.getLongitude().getDegree()) / 2) * (Math.PI / 180);
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
}
