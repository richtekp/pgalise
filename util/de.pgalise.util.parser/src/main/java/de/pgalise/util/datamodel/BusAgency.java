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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author marcus
 *
 */
@Entity
//@Table(name = "PGALISE.BUS_AGENCY")
public final class BusAgency {
	
	@Id
	@Column(name = "AGENCY_ID")
	private String agencyId;
	
	@Column(name = "AGENCY_NAME")
	private String agencyName;
	
	@Column(name = "AGENCY_URL")
	private String agencyUrl;
	
	@Column(name = "AGENCY_TIMEZONE")
	private String agencyTimezone;
	
	@Column(name = "AGENCY_LANG")
	private String agencyLang;
	
	@Column(name = "AGENCY_PHONE")
	private String agencyPhone;
	
	@Column(name = "AGENCY_FARE_URL")
	private String agencyFareUrl;
	
	public BusAgency() {}
	
	public BusAgency(final String agencyId, final String agencyName, final String agencyUrl, final String agencyTimezone, final String agencyLang,
			final String agencyPhone, final String agencyFareUrl) {
		
		if(agencyId == null) {
			throw new IllegalArgumentException("agencyId may not be null");
		}
		this.agencyId = agencyId;
		this.agencyName = agencyName;
		this.agencyUrl = agencyUrl;
		this.agencyTimezone = agencyTimezone;
		this.agencyLang = agencyLang;
		this.agencyPhone = agencyPhone;
		this.agencyFareUrl = agencyFareUrl;
	}

	public String getAgencyId() {
		return agencyId;
	}

	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
	}

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	public String getAgencyUrl() {
		return agencyUrl;
	}

	public void setAgencyUrl(String agencyUrl) {
		this.agencyUrl = agencyUrl;
	}

	public String getAgencyTimezone() {
		return agencyTimezone;
	}

	public void setAgencyTimezone(String agencyTimezone) {
		this.agencyTimezone = agencyTimezone;
	}

	public String getAgencyLang() {
		return agencyLang;
	}

	public void setAgencyLang(String agencyLang) {
		this.agencyLang = agencyLang;
	}

	public String getAgencyPhone() {
		return agencyPhone;
	}

	public void setAgencyPhone(String agencyPhone) {
		this.agencyPhone = agencyPhone;
	}

	public String getAgencyFareUrl() {
		return agencyFareUrl;
	}

	public void setAgencyFareUrl(String agencyFareUrl) {
		this.agencyFareUrl = agencyFareUrl;
	}
	
	
}
