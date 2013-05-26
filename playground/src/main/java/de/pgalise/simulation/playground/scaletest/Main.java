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
 
package de.pgalise.simulation.playground.scaletest;

import java.io.IOException;

import de.pgalise.simulation.sensorFramework.NullOutputServer;
import de.pgalise.simulation.sensorFramework.Server;

/**
 * @author mustafa
 *
 */
public class Main {	
	public static void main(String args[]) throws IOException {	
		Server outputServer[] = startOutputServer(new int[]{6666});
		
		run(10);
		
		closeOutputServer(outputServer);
		System.exit(0); 
		
	}
	
	private static void closeOutputServer(Server[] outputServer) {
		for(int i=0; i<outputServer.length; i++) {
			if(outputServer[i]!=null) {
				outputServer[i].waitToOpen();
				outputServer[i].close();
			}
		}
	}

	private static Server[] startOutputServer(int[] ports) throws IOException {
		Server outputServer[] = new NullOutputServer[ports.length];
		for(int i=0; i<ports.length; i++) {
			outputServer[i] = new NullOutputServer(ports[i]);
			outputServer[i].open();
		}
		return outputServer;
	}

	private static void run(int carCount) {
		Simulation sim = new Simulation();
		sim.init();
		sim.run(carCount);
		sim.close();
	}
}
