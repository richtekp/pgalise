/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag.factory;

import de.pgalise.simulation.shared.tag.PublicTransportTag;
import de.pgalise.simulation.shared.tag.PublicTransportTagCustom;
import de.pgalise.simulation.shared.tag.PublicTransportTagEnum;
import de.pgalise.simulation.shared.tag.TourismTag;
import de.pgalise.simulation.shared.tag.TourismTagCustom;
import de.pgalise.simulation.shared.tag.TourismTagEnum;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author richter
 */
public class PublicTransportTagFactory {
	
	private final static Map<String, PublicTransportTag> USED_VALUES = new HashMap<>();

	public static PublicTransportTag getInstance(String value) {
		if (USED_VALUES.keySet().contains(value)) {
			return USED_VALUES.get(value);
		}
		PublicTransportTag retValue = null;
		for (PublicTransportTagEnum publicTransportTagEnum : PublicTransportTagEnum.values()) {
			if (publicTransportTagEnum.getStringValue().equals(value)) {
				retValue = publicTransportTagEnum;
			}
			break;
		}
		if (retValue == null) {
			retValue = new PublicTransportTagCustom(value);
		}
		USED_VALUES.put(value,
			retValue);
		return retValue;
	}
}
