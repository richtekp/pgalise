/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.util.weathercollector.util;

import de.pgalise.util.weathercollector.model.Condition;

/**
 *
 * @author richter
 */
public interface DatabaseManager {
	Condition getCondition(String condition) ;
}
