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
 
package de.pgalise.simulation.playground;

import java.io.IOException;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.pgalise.simulation.SimulationController;
import de.pgalise.simulation.shared.controller.Controller;

public class TestEjbdProtocol {
	public static void main(String args[]) throws IOException, NamingException {
		Context ctx;
		Properties props = new Properties();
		props.load(Controller.class.getResourceAsStream("/jndi.properties"));
		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.RemoteInitialContextFactory");
		props.put(Context.PROVIDER_URL, "ejbd://127.0.0.1:4201");
		
		ctx = new InitialContext(props);
		
		SimulationController simulationController = (SimulationController) ctx
				.lookup("de.pgalise.simulation.SimulationControllerRemote");
		
		System.out.println(simulationController.getStatus());
	}
}
