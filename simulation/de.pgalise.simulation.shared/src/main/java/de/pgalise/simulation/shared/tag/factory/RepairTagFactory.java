/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag.factory;

import de.pgalise.simulation.shared.tag.RepairTag;
import de.pgalise.simulation.shared.tag.RepairTagCustom;
import de.pgalise.simulation.shared.tag.RepairTagEnum;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author richter
 */
public class RepairTagFactory {
	
	private final static Map<String, RepairTag> USED_VALUES = new HashMap<>();

	public static RepairTag getInstance(String value) {
		if (USED_VALUES.keySet().contains(value)) {
			return USED_VALUES.get(value);
		}
		RepairTag retValue = null;
		for (RepairTagEnum tourismTagEnum : RepairTagEnum.values()) {
			if (tourismTagEnum.getStringValue().equals(value)) {
				retValue = tourismTagEnum;
			}
			break;
		}
		if (retValue == null) {
			retValue = new RepairTagCustom(value);
		}
		USED_VALUES.put(value,
			retValue);
		return retValue;
	}

	private RepairTagFactory() {
	}
}
