/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag.factory;

import de.pgalise.simulation.shared.tag.ServiceTag;
import de.pgalise.simulation.shared.tag.ServiceTagCustom;
import de.pgalise.simulation.shared.tag.ServiceTagEnum;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author richter
 */
public class ServiceTagFactory {
	
	private final static Map<String, ServiceTag> USED_VALUES = new HashMap<>();

	public static ServiceTag getInstance(String value) {
		if (USED_VALUES.keySet().contains(value)) {
			return USED_VALUES.get(value);
		}
		ServiceTag retValue = null;
		for (ServiceTagEnum tourismTagEnum : ServiceTagEnum.values()) {
			if (tourismTagEnum.getStringValue().equals(value)) {
				retValue = tourismTagEnum;
			}
			break;
		}
		if (retValue == null) {
			retValue = new ServiceTagCustom(value);
		}
		USED_VALUES.put(value,
			retValue);
		return retValue;
	}

	private ServiceTagFactory() {
	}
}
