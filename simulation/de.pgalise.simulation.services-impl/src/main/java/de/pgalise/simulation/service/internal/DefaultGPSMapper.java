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
 
package de.pgalise.simulation.service.internal;

import javax.ejb.Local;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import de.pgalise.simulation.service.GPSMapper;
import de.pgalise.simulation.shared.geolocation.GeoLocation;
import de.pgalise.simulation.shared.geolocation.Latitude;
import de.pgalise.simulation.shared.geolocation.Longitude;
import javax.vecmath.Vector2d;

/**
 * Converts GPS values to vector units and vice versa
 * 
 * @author Mustafa
 * @author Timo
 * @version 1.0 (Oct 1, 2012)
 */
@Lock(LockType.READ)
@Singleton(name="de.pgalise.simulation.service.GPSMapper")
@Local
public class DefaultGPSMapper implements GPSMapper {
	public static final double VECTOR_UNIT = 100; // 1vu = 100m

	private static final double a = (6378137d / 1000d);
	private static final double f = (1 / 298.257223563);
	private double HEIGHT = 1371.09147843289; // 13.71km in der höhe
	// private static final Logger log =
	// LoggerFactory.getLogger(SimpleGPSMapper.class);
	private double WIDTH = 1234.1215016201254; // 12.34ve in der breite

	private static double convert(GeoLocation origin, GeoLocation gps) {

		final double F = (((origin.getLatitude().getDegree() + gps.getLatitude().getDegree()) / 2) * Math.PI) / 180;
		final double G = (((origin.getLatitude().getDegree() - gps.getLatitude().getDegree()) / 2) * Math.PI) / 180;
		final double l = (((origin.getLongitude().getDegree() - gps.getLongitude().getDegree()) / 2) * Math.PI) / 180;

		final double S = (Math.pow(Math.sin(G), 2) * Math.pow(Math.cos(l), 2)) + (Math.pow(Math.cos(F), 2) * Math.pow(Math.sin(l), 2));

		final double C = (Math.pow(Math.cos(G), 2) * Math.pow(Math.cos(l), 2)) + (Math.pow(Math.sin(F), 2) * Math.pow(Math.sin(l), 2));

		final double w = Math.atan(Math.sqrt(S / C));

		final double D = 2 * w * a;

		final double R = Math.sqrt(S * C) / w;

		final double H1 = ((3 * R) - 1) / (2 * C);

		final double H2 = ((3 * R) + 1) / (2 * S);

		final double s = D
				* ((1 + (f * H1 * Math.pow(Math.sin(F), 2) * Math.pow(Math.cos(G), 2))) - (f * H2 * Math.pow(Math.cos(F), 2) * Math.pow(Math.sin(G), 2)));
		// log.debug("b1 = "+origin.getLatitude().getDegree());
		// log.debug("b2 = "+gps.getLatitude().getDegree());
		// log.debug("l1 = "+origin.getLongitude().getDegree());
		// log.debug("l2 = "+gps.getLongitude().getDegree());
		// log.debug("f = "+f);
		// log.debug("a = "+a);
		// log.debug("F = "+F);
		// log.debug("G = "+G);
		// log.debug("l = "+l);
		// log.debug("S = "+S);
		// log.debug("C = "+C);
		// log.debug("w = "+w);
		// log.debug("R = "+R);
		// log.debug("D = "+D);
		// log.debug("H1 = "+H1);
		// log.debug("H2 = "+H2);
		// log.debug("s = "+s);
		return s;
	}

	private GeoLocation origin;

	public DefaultGPSMapper(double width, double height) {
		this.WIDTH = width;
		this.HEIGHT = height;
		this.origin = new GeoLocation(new Latitude(53.2056), new Longitude(8.1289));
	}

	public DefaultGPSMapper() {
		this.origin = new GeoLocation(new Latitude(53.2056), new Longitude(8.1289));
	}

	/**
	 * Converts distance from meter to vector units(vu)
	 * 
	 * @param meter
	 * @return vector units
	 */
	public double convertDistance(double meter) {
		return meter / VECTOR_UNIT;
	}

	@Override
	public Vector2d convertToVector(GeoLocation gps) {
		if(gps.equals(this.origin)) {
			return new Vector2d(0, 0);
		}
		double x = convert(this.origin, new GeoLocation(this.origin.getLatitude(), gps.getLongitude()));
		double y = convert(this.origin, new GeoLocation(gps.getLatitude(), this.origin.getLongitude()));
		return new Vector2d(x * VECTOR_UNIT, y * VECTOR_UNIT);
	}

	@Override
	public GeoLocation convertVectorToGPS(Vector2d vector) {
		Latitude north = new Latitude(((-0.1232 / HEIGHT) * vector.y) + 53.2056);
		Longitude east = new Longitude(((0.1847 / WIDTH) * vector.x) + 8.1289);
		return new GeoLocation(north, east);
	}

	@Override
	public double convertVelocity(double vel) {
		return vel * (VECTOR_UNIT / 10 / 3600);
	}

	@Override
	public double convertVUStoKMH(double vel) {
		return vel * VECTOR_UNIT * 3.6;
	}

	@Override
	public Vector2d getCenterPoint() {
		return new Vector2d(this.getWidth() / 2.0, this.getHeight() / 2.0);
	}

	@Override
	public double getDistanceFromCenterToFarhestPoint() {
		Vector2d topLeft = new Vector2d(0.0, 0.0);
		Vector2d center = this.getCenterPoint();
		center.sub(topLeft);

		return center.length();
	}

	@Override
	public double getHeight() {
		return HEIGHT;
	}

	@Override
	public GeoLocation getOrigin() {
		return this.origin;
	}

	@Override
	public double getVectorUnit() {
		return VECTOR_UNIT;
	}

	@Override
	public double getWidth() {
		return WIDTH;
	}

	@Override
	public void setOrigin(GeoLocation gps) {
		this.origin = gps;
	}
}
