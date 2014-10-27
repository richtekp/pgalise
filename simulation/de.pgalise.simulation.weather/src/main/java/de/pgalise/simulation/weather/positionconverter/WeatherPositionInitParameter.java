/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.positionconverter;

import com.vividsolutions.jts.geom.Polygon;
import de.pgalise.simulation.service.InitParameter;

/**
 *
 * @author richter
 */
public class WeatherPositionInitParameter extends InitParameter {

  private static final long serialVersionUID = 1L;

  private Polygon grid;

  public WeatherPositionInitParameter(Polygon grid) {
    this.grid = grid;
  }

	public void setGrid(Polygon polygon) {
		this.grid = polygon;
	}

	public Polygon getGrid() {
		return grid;
	}

}
