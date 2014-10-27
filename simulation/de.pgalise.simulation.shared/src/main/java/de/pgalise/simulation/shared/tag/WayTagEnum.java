/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag;

import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author richter
 */
@XmlRootElement
public enum WayTagEnum {

	HIGHWAY("highway"), LANDUSE("landuse"), RAILWAY("railway"), CYCLEWAY(
		"cycleway"), BUS_STOP("busstop"), UNCLASSIFIED("unclassified"), pedestrian(
			"pedestrian"), STEPS("steps"), FOOTPATH("footpath"), PATH("path"), service(
			"service"), natural("natural"), track("track");

	private final String STRING_VALUE;

	private final static Set<String> STRING_VALUES = new HashSet<>(values().length);

	static {
		for (WayTagEnum wayTagEnum : values()) {
			STRING_VALUES.add(wayTagEnum.STRING_VALUE);
		}
	}

	private WayTagEnum(String stringValue) {
		this.STRING_VALUE = stringValue;
	}

	public String getStringValue() {
		return STRING_VALUE;
	}

	public static Set<String> getStringValues() {
		return STRING_VALUES;
	}
}
