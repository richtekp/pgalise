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

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Weather data of weather stations. This class uses EclipseLink.
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (01.07.2012)
 */
@Entity
@Table(name = "PGALISE.WEATHER_STATION_DATA")
@NamedQueries({
		@NamedQuery(name = "StationDataNormal.findByDate", query = "SELECT i FROM StationDataNormal i WHERE i.measureDate = :date"),
		@NamedQuery(name = "StationDataNormal.findFirstEntryByDate", query = "SELECT i FROM StationDataNormal i WHERE i.measureDate = :date ORDER BY i.measureTime ASC"),
		@NamedQuery(name = "StationDataNormal.findLastEntryByDate", query = "SELECT i FROM StationDataNormal i WHERE i.measureDate = :date ORDER BY i.measureTime DESC"), })
public final class StationDataNormal extends StationData {

}
