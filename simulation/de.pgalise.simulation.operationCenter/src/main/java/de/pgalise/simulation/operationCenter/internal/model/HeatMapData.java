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
 
package de.pgalise.simulation.operationCenter.internal.model;

import java.util.List;
/**
 * Not used so far.
 * @author Dennis
 */
public class HeatMapData {
	private double max;
	List<HeatMapDataPoint> data;
	public HeatMapData(double max, List<HeatMapDataPoint> data) {
		this.max = max;
		this.data = data;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public List<HeatMapDataPoint> getData() {
		return data;
	}
	public void setData(List<HeatMapDataPoint> data) {
		this.data = data;
	}
	public void addData(HeatMapDataPoint dataPoint) {
		this.data.add(dataPoint);
	}
}
