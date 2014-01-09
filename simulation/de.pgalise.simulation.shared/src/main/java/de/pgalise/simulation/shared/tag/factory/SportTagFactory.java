/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag.factory;

import de.pgalise.simulation.shared.tag.SportTag;
import de.pgalise.simulation.shared.tag.SportTagCustom;
import de.pgalise.simulation.shared.tag.SportTagEnum;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author richter
 */
public class SportTagFactory {
	
	private final static Map<String, SportTag> USED_VALUES = new HashMap<>();

	public static SportTag getInstance(String value) {
		if (USED_VALUES.keySet().contains(value)) {
			return USED_VALUES.get(value);
		}
		SportTag retValue = null;
		for (SportTagEnum tourismTagEnum : SportTagEnum.values()) {
			if (tourismTagEnum.getStringValue().equals(value)) {
				retValue = tourismTagEnum;
			}
			break;
		}
		if (retValue == null) {
			retValue = new SportTagCustom(value);
		}
		USED_VALUES.put(value,
			retValue);
		return retValue;
	}

	private SportTagFactory() {
	}
}
