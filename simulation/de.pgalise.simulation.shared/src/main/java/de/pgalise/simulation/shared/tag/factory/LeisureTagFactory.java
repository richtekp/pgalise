/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag.factory;

import de.pgalise.simulation.shared.tag.LeisureTag;
import de.pgalise.simulation.shared.tag.LeisureTagCustom;
import de.pgalise.simulation.shared.tag.LeisureTagEnum;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author richter
 */
public class LeisureTagFactory {
	
	private final static Map<String, LeisureTag> USED_VALUES = new HashMap<>();

	public static LeisureTag getInstance(String value) {
		if (USED_VALUES.keySet().contains(value)) {
			return USED_VALUES.get(value);
		}
		LeisureTag retValue = null;
		for (LeisureTagEnum tourismTagEnum : LeisureTagEnum.values()) {
			if (tourismTagEnum.getStringValue().equals(value)) {
				retValue = tourismTagEnum;
			}
			break;
		}
		if (retValue == null) {
			retValue = new LeisureTagCustom(value);
		}
		USED_VALUES.put(value,
			retValue);
		return retValue;
	}

	private LeisureTagFactory() {
	}
}
