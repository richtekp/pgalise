/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag.factory;

import de.pgalise.simulation.shared.tag.WayTag;
import de.pgalise.simulation.shared.tag.WayTagCustom;
import de.pgalise.simulation.shared.tag.WayTagEnum;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author richter
 */
public class WayTagFactory {
	
	private final static Map<String, WayTag> USED_VALUES = new HashMap<>();

	public static WayTag getInstance(String value) {
		if (USED_VALUES.keySet().contains(value)) {
			return USED_VALUES.get(value);
		}
		WayTag retValue = null;
		for (WayTagEnum tourismTagEnum : WayTagEnum.values()) {
			if (tourismTagEnum.getStringValue().equals(value)) {
				retValue = tourismTagEnum;
			}
			break;
		}
		if (retValue == null) {
			retValue = new WayTagCustom(value);
		}
		USED_VALUES.put(value,
			retValue);
		return retValue;
	}

	private WayTagFactory() {
	}
}
