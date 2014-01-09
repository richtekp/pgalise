/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag.factory;

import de.pgalise.simulation.shared.tag.GamblingTag;
import de.pgalise.simulation.shared.tag.GamblingTagCustom;
import de.pgalise.simulation.shared.tag.GamblingTagEnum;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author richter
 */
public class GamblingTagFactory {
	
	private final static Map<String, GamblingTag> USED_VALUES = new HashMap<>();

	public static GamblingTag getInstance(String value) {
		if (USED_VALUES.keySet().contains(value)) {
			return USED_VALUES.get(value);
		}
		GamblingTag retValue = null;
		for (GamblingTagEnum tourismTagEnum : GamblingTagEnum.values()) {
			if (tourismTagEnum.getStringValue().equals(value)) {
				retValue = tourismTagEnum;
			}
			break;
		}
		if (retValue == null) {
			retValue = new GamblingTagCustom(value);
		}
		USED_VALUES.put(value,
			retValue);
		return retValue;
	}

	private GamblingTagFactory() {
	}
}
