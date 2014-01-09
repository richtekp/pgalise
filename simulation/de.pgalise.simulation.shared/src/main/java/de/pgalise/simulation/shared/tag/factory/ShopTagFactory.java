/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag.factory;

import de.pgalise.simulation.shared.tag.ShopTag;
import de.pgalise.simulation.shared.tag.ShopTagCustom;
import de.pgalise.simulation.shared.tag.ShopTagEnum;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author richter
 */
public class ShopTagFactory {
	
	private final static Map<String, ShopTag> USED_VALUES = new HashMap<>();

	public static ShopTag getInstance(String value) {
		if (USED_VALUES.keySet().contains(value)) {
			return USED_VALUES.get(value);
		}
		ShopTag retValue = null;
		for (ShopTagEnum tourismTagEnum : ShopTagEnum.values()) {
			if (tourismTagEnum.getStringValue().equals(value)) {
				retValue = tourismTagEnum;
			}
			break;
		}
		if (retValue == null) {
			retValue = new ShopTagCustom(value);
		}
		USED_VALUES.put(value,
			retValue);
		return retValue;
	}

	private ShopTagFactory() {
	}
}
