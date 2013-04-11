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
 
package de.pgalise.simulation.operationCenter.internal.hqf.persistence;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "PGALISE.HQF_DATA")
public class HQFData implements Serializable {

	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = -1211059766938141064L;

	@Id @GeneratedValue
    private long id;
	
	@Column(name = "SIMULATIONTIMESTAMP")
	private Timestamp simulationTimestamp;
	
	@Column(name = "SENSORTYPEID")
	private int sensorTypeID;
	
	@Column(name = "AMOUNT")
	private int amount;
	
	public HQFData(){
		
	}
	
	public HQFData(Timestamp timestamp, Integer sensorTypeID, Integer amount) {
		super();
		this.simulationTimestamp = timestamp;
		this.sensorTypeID = sensorTypeID;
		this.amount = amount;
	}

	public Timestamp getSimulationTimestamp() {
		return simulationTimestamp;
	}

	public void setSimulationTimestamp(Timestamp simulationTimestamp) {
		this.simulationTimestamp = simulationTimestamp;
	}

	public int getSensorTypeID() {
		return sensorTypeID;
	}

	public void setSensorTypeID(int sensorTypeID) {
		this.sensorTypeID = sensorTypeID;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public String toString(){
		return "HQF DATA Instance [SimulationTimestamp: "+this.getSimulationTimestamp()+"; SensorTypeID:"+this.getSensorTypeID()+"; Amount:"+this.getAmount()+" ]";
	}
}
