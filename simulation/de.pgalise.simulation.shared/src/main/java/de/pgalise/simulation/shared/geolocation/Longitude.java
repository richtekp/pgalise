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
 * Class to represent a {@link Longitude}
 * @author Marcus
 *
 */
public class Longitude implements Serializable {

	/**
	 * generated Id for {@link Serializable}
	 */
	private static final long serialVersionUID = -3800583872955014667L;
	
	/**
	 * Parses a string and returns a {@link Longitude} object.
	 * @param string the string from which the {@link Longitude} object shall be created and returned
	 * @return a {@link Longitude} object created from the passed string.
	 * @throws NumberFormatException if the string has the wrong format.
	 */
	public final static Longitude parse(final String string) throws NumberFormatException {
		try {
			final String trimmedString = string.replaceAll(" ", "");
			//First try to parse as an double value
			if(trimmedString.matches("^[-]?\\d+([.]+\\d+)?[°]?$")) {
				return new Longitude(Double.parseDouble(trimmedString.replaceAll("°", "")));
			} else if(trimmedString.matches("^[-]?\\d+[°]\\d+[']{1}\\d+([.]\\d+)?[']{2}$")) {
				final int sign = trimmedString.charAt(0) == '-' ? -1 : 1;
				final String[] values = trimmedString.split("[°]|[']");
				final double decimaldegee =  (Integer.parseInt(values[1]) * 60 + Double.parseDouble(values[2])) / 3600D;
				return new Longitude(Double.parseDouble(values[0]) + sign * decimaldegee);
			} else if(trimmedString.matches("^[-]?\\d+[°]\\d+([.]\\d+)?[']$")) {
				final int sign = trimmedString.charAt(0) == '-' ? -1 : 1;
				final String[] values = trimmedString.split("[°]|[']");
				return new Longitude(Double.parseDouble(values[0]) + sign * Double.parseDouble(values[1]) / 60D);
			}
		} catch(Exception e) {
			throw new NumberFormatException("Couldn't parse '" + string + "' to create a Longitude.");
		}
		throw new NumberFormatException("Couldn't parse '" + string + "' to create a Longitude.");
	}
	
	/**
	 * the degree of the {@link Longitude} as a double value.
	 */
	private double degree;

	/**
	 * Creates a {@link Longitude} with degree := 0;
	 */
	public Longitude() {
	}

	/**
	 * Creates a {@link Longitude} with the passed double value
	 * @param degree the value for the {@link Longitude} 
	 */
	public Longitude(double degree) {
		if ((degree < -180) || (degree > 180)) {
			throw new IllegalArgumentException("degree");
		}
		this.setDegree(degree);
	}

	/**
	 * Returns the degree for this {@link Longitude}.
	 * @return the degree for this {@link Longitude}
	 */
	public double getDegree() {
		return this.degree;
	}

	/**
	 * Sets the degree for this {@link Longitude}.
	 * @param degree the degree for this {@link Longitude}
	 */
	public void setDegree(double degree) {
		if ((degree < -180) || (degree > 180)) {
			throw new IllegalArgumentException("Argument 'degree' must be between -180 and 180");
		}
		this.degree = degree;
	}
	
	/**
	 * Returns the same hash code for {@link Longitude}s with equal values.
	 * @throws the same hash code for {@link Longitude}s with equal values
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(this.degree);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * Checks whether an passed object is equal to to this {@link Longitude} object. <br>
	 * Instead of reference equality value equality is performed.
	 * @return true if the passed argument is an instance of {@link Longitude} and matches all values
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Longitude other = (Longitude) obj;
		if (Double.doubleToLongBits(this.degree) != Double
				.doubleToLongBits(other.degree))
			return false;
		return true;
	}

	/**
	 * Returns a string representation for this {@link Longitude}.
	 * @return a string representation for this {@link Longitude}
	 */
	@Override
	public String toString() {
		return this.toString(GeoLocation.FORMAT_DECIMALDEGREE);
	}
	
	/**
	 * Returns a formatted string representation
	 * @param formatString the string determining how to format this {@link Longitude} object
	 * @return the string determining how to format this {@link Longitude} object
	 */
	public String toString(final String formatString) {
		switch (formatString) {
		case GeoLocation.FORMAT_DECIMALDEGREE: 
			return this.toDecimaldegree();
		case GeoLocation.FORMAT_DEGREE_MINUTE_SECOND:
			return this.toDegreeMinuteSecond();
		case GeoLocation.FORMAT_DEGREE_DECIMALMINUTES:	
			return this.toDegreeDecimalminutes();
		case GeoLocation.FORMAT_DEGREE_MINUTE_DECIMALSECOND: 
			return this.toDegreeMinuteDecimalsecond();
		}
		throw new IllegalArgumentException("Argument formatString='" + formatString + "' is no valid format string.");
	}
	
	/**
	 * Returns this {@link Longitude} as a decimal degree (i.e 32.31°). <br>
	 * @return this {@link Longitude} as a decimal degree (i.e 32.31°)
	 */
	public String toDecimaldegree() {
		return this.degree + "°";
	}
	
	/**
	 * Returns this {@link Longitude} as a degree minute second (i.e 21° 31'). <br>
	 * @return this {@link Longitude} as a degree minute second (i.e 21° 31')
	 */
	public String toDegreeMinuteSecond() {
		final int degree = (int)this.degree;
		final int minute = (int)((this.degree - degree) * 60);
		final int second =  (int)Math.round(((((this.degree - degree) * 60) - minute) * 60));
		return degree + "° " + minute + "' " + second + "''";
	}
	
	/**
	 * Returns this {@link Longitude} as a degree decimal minute (i.e 21° 31.02'). <br>
	 * @return this {@link Longitude} as a degree decimal minute (i.e 21° 31.02')
	 */
	public String toDegreeDecimalminutes() {
		final int degree = (int)this.degree;
		final double decimalminute = (this.degree - degree) * 60;
		return degree + "° " + decimalminute + "'";
	}
	
	/**
	 * Returns this {@link Longitude} as a degree minute decimal second (i.e 21° 31' 21.56''). <br>
	 * @return this {@link Longitude} as a degree minute decimal second (i.e 21° 31' 21.56'')
	 */
	public String toDegreeMinuteDecimalsecond() {
		final int degree = (int)this.degree;
		final int minute = (int)((this.degree - degree) * 60);
		final double decimalsecond = (((this.degree - degree) * 60) - minute) * 60;
		return degree + "° " + minute + "' " + decimalsecond + "''";
	}
}
