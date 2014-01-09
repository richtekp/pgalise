/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag.factory;

import de.pgalise.simulation.shared.tag.BaseTag;
import de.pgalise.simulation.shared.tag.TourismTag;
import de.pgalise.simulation.shared.tag.TourismTagCustom;
import de.pgalise.simulation.shared.tag.TourismTagEnum;
import java.util.HashMap;
import java.util.Map;

/**
 * Produces custom tags 
 * @author richter
 */
public class ReflectionCustomTagFactory<T extends BaseTag> {
	
	private final Map<Class<? extends T>, Map<String, T>> USED_VALUES = new HashMap<>();

	public T getInstance(Class<? extends T> clazz, String value) {
		throw new UnsupportedOperationException();
//		Map<String, T> map = USED_VALUES.get(clazz);
//		if(map == null) {
//			map = new HashMap<>();
//			USED_VALUES.put(clazz,
//				map);
//		}
//		if (map.keySet().contains(value)) {
//			return map.get(value);
//		}
//		T retValue = clazz.getConstructor().newInstance();
//		USED_VALUES.put(value,
//			retValue);
//		return retValue;
	}
}
