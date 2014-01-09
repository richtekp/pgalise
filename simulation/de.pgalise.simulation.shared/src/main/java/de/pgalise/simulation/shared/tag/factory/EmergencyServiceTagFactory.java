/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag.factory;

import de.pgalise.simulation.shared.tag.EmergencyServiceTag;
import de.pgalise.simulation.shared.tag.EmergencyServiceTagCustom;
import de.pgalise.simulation.shared.tag.EmergencyServiceTagEnum;
import de.pgalise.simulation.shared.tag.TourismTag;
import de.pgalise.simulation.shared.tag.TourismTagCustom;
import de.pgalise.simulation.shared.tag.TourismTagEnum;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author richter
 */
public class EmergencyServiceTagFactory {
	
	private final static Map<String, EmergencyServiceTag> USED_VALUES = new HashMap<>();

	public static EmergencyServiceTag getInstance(String value) {
		if (USED_VALUES.keySet().contains(value)) {
			return USED_VALUES.get(value);
		}
		EmergencyServiceTag retValue = null;
		for (EmergencyServiceTagEnum tourismTagEnum : EmergencyServiceTagEnum.values()) {
			if (tourismTagEnum.getStringValue().equals(value)) {
				retValue = tourismTagEnum;
			}
			break;
		}
		if (retValue == null) {
			retValue = new EmergencyServiceTagCustom(value);
		}
		USED_VALUES.put(value,
			retValue);
		return retValue;
	}

	private EmergencyServiceTagFactory() {
	}
}
