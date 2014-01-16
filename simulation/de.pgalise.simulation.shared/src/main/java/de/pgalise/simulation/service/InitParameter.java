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
package de.pgalise.simulation.service;

import com.vividsolutions.jts.geom.Envelope;
import java.io.Serializable;

import de.pgalise.simulation.shared.controller.TrafficFuzzyData;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.validation.constraints.NotNull;

/**
 * The init parameters will be send to every controller on init. It contains all
 * information needed in the init state.
 *
 * @author Mustafa
 * @author Timo
 */
@ManagedBean
public class InitParameter implements Serializable {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -39986353888978216L;

	public InitParameter() {
	}

}
