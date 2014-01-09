/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag.factory;

import de.pgalise.simulation.shared.tag.LanduseTag;
import de.pgalise.simulation.shared.tag.LanduseTagCustom;
import de.pgalise.simulation.shared.tag.LanduseTagEnum;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author richter
 */
public class LanduseTagFactory {
	
	private final static Map<String, LanduseTag> USED_VALUES = new HashMap<>();

	public static LanduseTag getInstance(String value) {
		if (USED_VALUES.keySet().contains(value)) {
			return USED_VALUES.get(value);
		}
		LanduseTag retValue = null;
		for (LanduseTagEnum tourismTagEnum : LanduseTagEnum.values()) {
			if (tourismTagEnum.getStringValue().equals(value)) {
				retValue = tourismTagEnum;
			}
			break;
		}
		if (retValue == null) {
			retValue = new LanduseTagCustom(value);
		}
		USED_VALUES.put(value,
			retValue);
		return retValue;
	}

	private LanduseTagFactory() {
	}
}
