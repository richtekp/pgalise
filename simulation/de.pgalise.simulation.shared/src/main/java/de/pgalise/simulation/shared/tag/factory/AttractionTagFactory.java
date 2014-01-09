/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag.factory;

import de.pgalise.simulation.shared.tag.AttractionTag;
import de.pgalise.simulation.shared.tag.AttractionTagCustom;
import de.pgalise.simulation.shared.tag.AttractionTagEnum;
import de.pgalise.simulation.shared.tag.TourismTag;
import de.pgalise.simulation.shared.tag.TourismTagCustom;
import de.pgalise.simulation.shared.tag.TourismTagEnum;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author richter
 */
public class AttractionTagFactory {
	
	private final static Map<String, AttractionTag> USED_VALUES = new HashMap<>();

	public static AttractionTag getInstance(String value) {
		if (USED_VALUES.keySet().contains(value)) {
			return USED_VALUES.get(value);
		}
		AttractionTag retValue = null;
		for (AttractionTagEnum tourismTagEnum : AttractionTagEnum.values()) {
			if (tourismTagEnum.getStringValue().equals(value)) {
				retValue = tourismTagEnum;
			}
			break;
		}
		if (retValue == null) {
			retValue = new AttractionTagCustom(value);
		}
		USED_VALUES.put(value,
			retValue);
		return retValue;
	}

	private AttractionTagFactory() {
	}
}
