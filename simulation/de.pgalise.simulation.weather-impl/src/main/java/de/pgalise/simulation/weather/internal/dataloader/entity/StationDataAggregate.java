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
 
package de.pgalise.simulation.weather.internal.dataloader.entity;

import java.sql.Date;
import java.sql.Time;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Aggregate weather data of weather services. This class uses EclipseLink.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (01.07.2012)
 */
@Entity
//@Table(name = "PGALISE.WEATHER_AGGREGATE_STATION")
@NamedQueries({
		@NamedQuery(name = "StationDataAggregate.findByDate", query = "SELECT i FROM StationDataAggregate i WHERE i.measureDate = :date"),
		@NamedQuery(name = "StationDataAggregate.findFirstEntryByDate", query = "SELECT i FROM StationDataAggregate i WHERE i.measureDate = :date ORDER BY i.measureTime ASC"),
		@NamedQuery(name = "StationDataAggregate.findLastEntryByDate", query = "SELECT i FROM StationDataAggregate i WHERE i.measureDate = :date ORDER BY i.measureTime DESC"), })
public class StationDataAggregate extends StationData {
	private static final long serialVersionUID = 1L;

	protected StationDataAggregate() {
	}

	public StationDataAggregate(Date date, Time time, Integer airPressure, Integer lightIntensity, Float perceivedTemperature, Float temperature, Float precipitationAmount, Integer radiation, Float relativHumidity, Integer windDirection, Float windVelocity) {
		super(date, time, airPressure, lightIntensity, perceivedTemperature, temperature, precipitationAmount, radiation, relativHumidity, windDirection, windVelocity);
	}

}
