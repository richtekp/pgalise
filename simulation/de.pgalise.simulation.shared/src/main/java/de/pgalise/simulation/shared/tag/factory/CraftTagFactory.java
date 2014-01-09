/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag.factory;

import de.pgalise.simulation.shared.tag.CraftTag;
import de.pgalise.simulation.shared.tag.CraftTagCustom;
import de.pgalise.simulation.shared.tag.CraftTagEnum;
import de.pgalise.simulation.shared.tag.TourismTag;
import de.pgalise.simulation.shared.tag.TourismTagCustom;
import de.pgalise.simulation.shared.tag.TourismTagEnum;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author richter
 */
public class CraftTagFactory {
	
	private final static Map<String, CraftTag> USED_VALUES = new HashMap<>();

	public static CraftTag getInstance(String value) {
		if (USED_VALUES.keySet().contains(value)) {
			return USED_VALUES.get(value);
		}
		CraftTag retValue = null;
		for (CraftTagEnum tourismTagEnum : CraftTagEnum.values()) {
			if (tourismTagEnum.getStringValue().equals(value)) {
				retValue = tourismTagEnum;
			}
			break;
		}
		if (retValue == null) {
			retValue = new CraftTagCustom(value);
		}
		USED_VALUES.put(value,
			retValue);
		return retValue;
	}

	private CraftTagFactory() {
	}
}
