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
 
package de.pgalise.simulation.shared.geometry;

import java.io.Serializable;
import javax.vecmath.Vector2d;




/**
 * Interface for geometry objects
 * 
 * @author Mustafa
 * @version 1.0 (Sep 26, 2012)
 */
public interface Geometry extends Serializable {

	/**
	 * Check's if the point [x,y] lies in the area of this geometry.
	 * 
	 * @param point
	 *            Vector2d point
	 * @return true if the point [x,y] lies in the area of this geometry
	 */
	public boolean covers(Vector2d point);

	public double getCenterX();

	public double getCenterY();

	public double getEndX();

	public double getEndY();

	public double getHeight();

	public double getStartX();

	public double getStartY();

	public double getWidth();

	public void setEndX(double endX);

	public void setEndY(double endY);

	public void setStartX(double startX);

	public void setStartY(double StartY);

}
