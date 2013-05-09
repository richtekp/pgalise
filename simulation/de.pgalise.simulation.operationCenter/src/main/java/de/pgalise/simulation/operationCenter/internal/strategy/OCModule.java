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
 
package de.pgalise.simulation.operationCenter.internal.strategy;

import com.google.inject.AbstractModule;

import de.pgalise.simulation.operationCenter.internal.DefaultOCSensorStreamController;
import de.pgalise.simulation.operationCenter.internal.DefaultOCSimulationController;
import de.pgalise.simulation.operationCenter.internal.OCSensorStreamController;
import de.pgalise.simulation.operationCenter.internal.OCSimulationController;
import de.pgalise.simulation.operationCenter.internal.OCWebSocketService;
import de.pgalise.simulation.operationCenter.internal.OCWebSocketServlet;
import de.pgalise.simulation.operationCenter.internal.hqf.NoOCHQFDataStreamController;
import de.pgalise.simulation.operationCenter.internal.hqf.OCHQFDataStreamController;
/**
 * Contains all bindings for the Operation Center
 * @author Timo
 */
public class OCModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(OCWebSocketService.class).to(OCWebSocketServlet.class);
		bind(GPSSensorTimeoutStrategy.class).to(DefaultGPSTimeoutStrategy.class);
		bind(OCSensorStreamController.class).to(DefaultOCSensorStreamController.class);
		bind(OCSimulationController.class).to(DefaultOCSimulationController.class);
		bind(SendSensorDataStrategy.class).to(DefaultSendSensorDataStrategy.class);
		bind(OCHQFDataStreamController.class).to(NoOCHQFDataStreamController.class);
//		bind(OCHQFDataStreamController.class).to(DefaultHQFStreamController.class);
//		bind(GPSGateStrategy.class).to(DefaultGPSGateStrategy.class); // für Infosphere
		bind(GPSGateStrategy.class).to(NoGPSGateStrategy.class); // für Odysseus 
	}
}
