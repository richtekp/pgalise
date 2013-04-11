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
 
package de.pgalise.simulation.controlCenter.internal.util.service;

import com.google.inject.AbstractModule;

import de.pgalise.simulation.controlCenter.internal.CCWebSocketUser;
import de.pgalise.simulation.internal.visualizationcontroller.DefaultControlCenterController;
import de.pgalise.util.GTFS.service.BusService;
import de.pgalise.util.GTFS.service.DefaultBusService;
import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;
import de.pgalise.util.cityinfrastructure.impl.DefaultBuildingEnergyProfileStrategy;

/**
 * Module for {@link CCWebSocketUser}. It contains all the service bindings. If new implementations
 * are created and shall be used, then the bindings need to be updated here.
 * The bindings in the control center are not done via EJB, because the control center is separated
 * from the simulation and doesn't provide an API. Updated which are received directly from the
 * simulation are done via {@link DefaultControlCenterController} and HTTP. This is useful, because 
 * not everyone needs the web interfaces for his purposes.
 * @author Timo
 */
public class ControlCenterModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(BuildingEnergyProfileStrategy.class).to(DefaultBuildingEnergyProfileStrategy.class);
		bind(OSMCityInfrastructureDataService.class).to(DefaultOSMCityInfrastructureDataService.class);
		bind(CreateRandomVehicleService.class).to(DefaultCreateRandomVehicleService.class);
		bind(StartParameterSerializerService.class).to(XMLStartParameterSerializerService.class);
		bind(SensorInterfererService.class).to(DefaultSensorInterfererService.class);
		bind(BusService.class).to(DefaultBusService.class);
		bind(CreateAttractionEventService.class).to(DefaultCreateAttractionEventService.class);
	}
}
