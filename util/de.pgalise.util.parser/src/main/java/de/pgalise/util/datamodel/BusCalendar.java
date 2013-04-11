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
 
package de.pgalise.util.datamodel;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * @author marcus
 *
 */
@Entity
@Table(name = "PGALISE.BUS_CALENDAR")
public final class BusCalendar {
	
	@Id
	@Column(name = "SERVICE_ID")
	private String serviceId;
	
	@Column(name = "MONDAY")
	private char monday;
	
	@Column(name = "TUESDAY")
	private char thuesday;
	
	@Column(name = "WEDNESDAY")
	private char wednesday;
	
	@Column(name = "THURSDAY")
	private char thursday;
	
	@Column(name = "FRIDAY")
	private char friday;
	
	@Column(name = "SATURDAY")
	private char saturday;
	
	@Column(name = "SUNDAY")
	private char sunday;
	
	@Column(name = "START_DATE")
	@Temporal(TemporalType.DATE)
	private Date startDate;
	
	@Column(name = "END_DATE")
	@Temporal(TemporalType.DATE)
	private Date endDate;
	
	public BusCalendar() {}

	public BusCalendar(String serviceId, char monday, char thuesday,
			char wednesday, char thursday, char friday, char saturday,
			char sunday, Date startDate, Date endDate) {
		this.serviceId = serviceId;
		this.monday = monday;
		this.thuesday = thuesday;
		this.wednesday = wednesday;
		this.thursday = thursday;
		this.friday = friday;
		this.saturday = saturday;
		this.sunday = sunday;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public char getMonday() {
		return monday;
	}

	public void setMonday(char monday) {
		this.monday = monday;
	}

	public char getThuesday() {
		return thuesday;
	}

	public void setThuesday(char thuesday) {
		this.thuesday = thuesday;
	}

	public char getWednesday() {
		return wednesday;
	}

	public void setWednesday(char wednesday) {
		this.wednesday = wednesday;
	}

	public char getThursday() {
		return thursday;
	}

	public void setThursday(char thursday) {
		this.thursday = thursday;
	}

	public char getFriday() {
		return friday;
	}

	public void setFriday(char friday) {
		this.friday = friday;
	}

	public char getSaturday() {
		return saturday;
	}

	public void setSaturday(char saturday) {
		this.saturday = saturday;
	}

	public char getSunday() {
		return sunday;
	}

	public void setSunday(char sunday) {
		this.sunday = sunday;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	
}
