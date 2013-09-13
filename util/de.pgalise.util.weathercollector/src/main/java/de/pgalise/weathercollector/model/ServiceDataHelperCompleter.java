/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.weathercollector.model;

/**
 *
 * @author richter
 */
public interface ServiceDataHelperCompleter<S extends ExtendedServiceDataCurrent, T extends ExtendedServiceDataForecast>{
	void complete(ServiceDataHelper<S,T> serviceDataHelper);
}
