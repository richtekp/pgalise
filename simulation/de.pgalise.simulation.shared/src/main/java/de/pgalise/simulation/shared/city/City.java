/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import com.vividsolutions.jts.geom.Coordinate;

/**
 *
 * @author richter
 */
public interface City extends Shaped {

	public int getAltitude() ;

	public String getName() ;

	public int getPopulation() ;

	public int getRate() ;

	public boolean isNearRiver() ;

	public boolean isNearSea() ;

	public void setAltitude(int altitude) ;

	public void setName(String name) ;

	public void setNearRiver(boolean nearRiver) ;

	public void setNearSea(boolean nearSea) ;

	public void setPopulation(int population) ;
	
	public void setRate(int rate) ;
	
	Coordinate getReferencePoint();
	
	void setReferencePoint(Coordinate referencePoint);
}
