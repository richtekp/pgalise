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
public interface BusStop extends Identifiable, NavigationNode {
 

	public Coordinate getLocation() ;

	public void setLocation(Coordinate location) ;

/**
	 * @return the stopName
 */
	public String getStopName() ;

	/**
	 * @param stopName the stopName to set
	 */
	public void setStopName(String stopName) ;

	public String getStopCode() ;

	public void setStopCode(String stopCode) ;

	public String getZoneId() ;

	public void setZoneId(String zoneId) ;

	public String getStopUrl() ;

	public void setStopUrl(String stopUrl) ;

	public String getLocationType() ;

	public void setLocationType(String locationType) ;

	public String getParentStation() ;

	public void setParentStation(String parentStation) ;

	public String getStopTimezone() ;

	public void setStopTimezone(String stopTimezone) ;

	public String getWheelchairBoarding() ;

	public void setWheelchairBoarding(String wheelchairBoarding) ;

}
