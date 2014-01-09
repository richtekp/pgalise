/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag.factory;

import de.pgalise.simulation.shared.tag.TourismTag;
import de.pgalise.simulation.shared.tag.TourismTagCustom;
import de.pgalise.simulation.shared.tag.TourismTagEnum;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author richter
 */
public class TourismTagFactory {

	private final static Map<String, TourismTag> USED_VALUES = new HashMap<>();

	public static TourismTag getInstance(String value) {
		if (USED_VALUES.keySet().contains(value)) {
			return USED_VALUES.get(value);
		}
		TourismTag retValue = null;
		for (TourismTagEnum tourismTagEnum : TourismTagEnum.values()) {
			if (tourismTagEnum.getStringValue().equals(value)) {
				retValue = tourismTagEnum;
			}
			break;
		}
		if (retValue == null) {
			retValue = new TourismTagCustom(value);
		}
		USED_VALUES.put(value,
			retValue);
		return retValue;
	}

	private TourismTagFactory() {
	}
}
