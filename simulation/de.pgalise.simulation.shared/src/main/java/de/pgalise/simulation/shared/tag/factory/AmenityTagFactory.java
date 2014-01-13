/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag.factory;

import de.pgalise.simulation.shared.tag.AmenityBaseTag;
import de.pgalise.simulation.shared.tag.AmenityTagCustom;
import de.pgalise.simulation.shared.tag.AmenityTagEnum;
import de.pgalise.simulation.shared.tag.TourismTag;
import de.pgalise.simulation.shared.tag.TourismTagCustom;
import de.pgalise.simulation.shared.tag.TourismTagEnum;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author richter
 */
public class AmenityTagFactory {
	
	private final static Map<String, AmenityBaseTag> USED_VALUES = new HashMap<>();

	public static AmenityBaseTag getInstance(String value) {
		if (USED_VALUES.keySet().contains(value)) {
			return USED_VALUES.get(value);
		}
		AmenityBaseTag retValue = null;
		for (AmenityTagEnum tourismTagEnum : AmenityTagEnum.values()) {
			if (tourismTagEnum.getStringValue().equals(value)) {
				retValue = tourismTagEnum;
			}
			break;
		}
		if (retValue == null) {
			retValue = new AmenityTagCustom(value);
		}
		USED_VALUES.put(value,
			retValue);
		return retValue;
	}

	private AmenityTagFactory() {
	}
}
