/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag.factory;

import de.pgalise.simulation.shared.tag.SchoolTag;
import de.pgalise.simulation.shared.tag.SchoolTagCustom;
import de.pgalise.simulation.shared.tag.SchoolTagEnum;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author richter
 */
public class SchoolTagFactory {
	
	private final static Map<String, SchoolTag> USED_VALUES = new HashMap<>();

	public static SchoolTag getInstance(String value) {
		if (USED_VALUES.keySet().contains(value)) {
			return USED_VALUES.get(value);
		}
		SchoolTag retValue = null;
		for (SchoolTagEnum tourismTagEnum : SchoolTagEnum.values()) {
			if (tourismTagEnum.getStringValue().equals(value)) {
				retValue = tourismTagEnum;
			}
			break;
		}
		if (retValue == null) {
			retValue = new SchoolTagCustom(value);
		}
		USED_VALUES.put(value,
			retValue);
		return retValue;
	}

	private SchoolTagFactory() {
	}
}
