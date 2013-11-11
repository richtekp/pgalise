/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

/**
 *
 * @author richter
 */
public enum WayTagEnum implements WayTag {
	HIGHWAY("highway"), LANDUSE("landuse"), RAILWAY("railway"), CYCLEWAY("cycleway"),BUS_STOP("busstop"), UNCLASSIFIED("unclassified"), pedestrian("pedestrian"), STEPS("steps"), FOOTPATH("footpath"), PATH("path"), service("service"), natural("natural"), track("track");
	
	private String stringValue;

	private WayTagEnum(String stringValue) {
		this.stringValue = stringValue;
	}
	
	@Override
	public String getStringValue() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
