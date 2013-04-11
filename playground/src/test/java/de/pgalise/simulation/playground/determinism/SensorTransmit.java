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
 
package de.pgalise.simulation.playground.determinism;

/**
 * Test class for sensor transmit
 * 
 * @author Andreas
 * @version 1.0
 */
public class SensorTransmit {

	/**
	 * Timestamp
	 */
	private long timestamp;

	/**
	 * Sensor ID
	 */
	private int sensorId;

	/**
	 * Sensor type ID
	 */
	private int sensortypeId;

	/**
	 * Transmitted value
	 */
	private double value1;

	/**
	 * Transmitted value
	 */
	private double value2;

	/**
	 * Transmitted value
	 */
	private byte value3;

	/**
	 * Transmitted value
	 */
	private short value4;

	/**
	 * Transmitted value
	 */
	private short value5;

	/**
	 * Transmitted value
	 */
	private short value6;

	/**
	 * Default constructor
	 */
	public SensorTransmit() {

	}

	/**
	 * Constructor
	 */
	public SensorTransmit(long timestamp, int sensorId, int sensortypeId, double value1, double value2, byte value3,
			short value4, short value5, short value6) {
		this.timestamp = timestamp;
		this.sensorId = sensorId;
		this.sensortypeId = sensortypeId;
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
		this.value4 = value4;
		this.value5 = value5;
		this.value6 = value6;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SensorTransmit other = (SensorTransmit) obj;
		if (sensorId != other.sensorId)
			return false;
		if (sensortypeId != other.sensortypeId)
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (Double.doubleToLongBits(value1) != Double.doubleToLongBits(other.value1))
			return false;
		if (Double.doubleToLongBits(value2) != Double.doubleToLongBits(other.value2))
			return false;
		if (value3 != other.value3)
			return false;
		if (value4 != other.value4)
			return false;
		if (value5 != other.value5)
			return false;
		if (value6 != other.value6)
			return false;
		return true;
	}

	public int getSensorId() {
		return sensorId;
	}

	public int getSensortypeId() {
		return sensortypeId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public double getValue1() {
		return value1;
	}

	public double getValue2() {
		return value2;
	}

	public byte getValue3() {
		return value3;
	}

	public short getValue4() {
		return value4;
	}

	public short getValue5() {
		return value5;
	}

	public short getValue6() {
		return value6;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + sensorId;
		result = prime * result + sensortypeId;
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		long temp;
		temp = Double.doubleToLongBits(value1);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(value2);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + value3;
		result = prime * result + value4;
		result = prime * result + value5;
		result = prime * result + value6;
		return result;
	}

	public void setSensorId(int sensorId) {
		this.sensorId = sensorId;
	}

	public void setSensortypeId(int sensortypeId) {
		this.sensortypeId = sensortypeId;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setValue1(double value1) {
		this.value1 = value1;
	}

	public void setValue2(double value2) {
		this.value2 = value2;
	}

	public void setValue3(byte value3) {
		this.value3 = value3;
	}

	public void setValue4(short value4) {
		this.value4 = value4;
	}

	public void setValue5(short value5) {
		this.value5 = value5;
	}

	public void setValue6(short value6) {
		this.value6 = value6;
	}

	@Override
	public String toString() {
		return "SensorTransmit [timestamp=" + timestamp + ", sensorId=" + sensorId + ", sensortypeId=" + sensortypeId
				+ ", value1=" + value1 + ", value2=" + value2 + ", value3=" + value3 + ", value4=" + value4
				+ ", value5=" + value5 + ", value6=" + value6 + "]";
	}
}
