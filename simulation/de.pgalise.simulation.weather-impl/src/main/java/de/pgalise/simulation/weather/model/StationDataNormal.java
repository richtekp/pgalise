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
 
package de.pgalise.simulation.weather.model;

import de.pgalise.simulation.shared.persistence.Identifiable;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.util.HashSet;
import java.util.Set;
import javax.measure.Measure;
import javax.measure.quantity.Temperature;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Weather data of weather stations. This class uses EclipseLink.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (01.07.2012)
 */
@Entity
//@Table(name = "PGALISE.STATION_DATA_NORMAL")
@NamedQueries({
		@NamedQuery(name = "StationDataNormal.findByDate", query = "SELECT x FROM StationDataNormal x WHERE x.measureDate = :date"),
		@NamedQuery(name = "StationDataNormal.findFirstEntryByDate", query = "SELECT i FROM StationDataNormal i WHERE i.measureDate = :date ORDER BY i.measureTime ASC"),
		@NamedQuery(name = "StationDataNormal.findLastEntryByDate", query = "SELECT i FROM StationDataNormal i WHERE i.measureDate = :date ORDER BY i.measureTime DESC") })
public class StationDataNormal extends AbstractStationData implements Identifiable {
	private static final long serialVersionUID = 1L;

	protected StationDataNormal() {
	}

	public StationDataNormal(Date date, Time time, Integer airPressure, Integer lightIntensity, Float perceivedTemperature, Measure<Float, Temperature> temperature, Float precipitationAmount, Integer radiation, Float relativHumidity, Float windDirection, Float windVelocity) {
		super(date, time, airPressure, lightIntensity, perceivedTemperature, temperature, precipitationAmount, radiation, relativHumidity, windDirection, windVelocity);
	}

}
