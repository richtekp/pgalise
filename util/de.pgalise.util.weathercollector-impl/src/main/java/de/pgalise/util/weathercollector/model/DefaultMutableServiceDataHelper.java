/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.model;

import de.pgalise.simulation.shared.city.City;
import de.pgalise.weathercollector.model.ExtendedServiceDataCurrent;
import de.pgalise.weathercollector.model.MutableExtendedServiceDataCurrent;
import de.pgalise.weathercollector.model.MutableServiceDataHelper;

/**
 *
 * @author richter
 */
public class DefaultMutableServiceDataHelper extends AbstractServiceDataHelper<MutableExtendedServiceDataCurrent>  {

	public DefaultMutableServiceDataHelper(City city,
		String apiSource) {
		super(city,
			apiSource);
	}
	
}
