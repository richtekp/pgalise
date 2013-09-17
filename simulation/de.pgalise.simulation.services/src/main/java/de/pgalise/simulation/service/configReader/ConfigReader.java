/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
 
package de.pgalise.simulation.service.configReader;

import de.pgalise.simulation.service.Service;

/**
 * The ConfigReader enables the user to read
 * properties found in the simulation configuration.
 * 
 * It depends on the implementations how these configurations are technically backed up
 * and accessed (e.g. table in a database, accessed through JDBC). 
 * 
 * 
 * @author mustafa
 *
 */
public interface ConfigReader extends Service {
	/**
	 * Returns values of properties used to configure the simulation.
	 * 
	 * @param propName identifies a property
	 * @return value of the property 'propName'
	 */
	public String getProperty(Identifier propName);
}
