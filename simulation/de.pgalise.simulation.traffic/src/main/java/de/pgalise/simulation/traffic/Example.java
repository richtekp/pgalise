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
 
package de.pgalise.simulation.traffic;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import de.pgalise.simulation.shared.sensor.SensorHelper;
import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.model.vehicle.CarData;

public class Example {
	public static void main(String args[]) throws FileNotFoundException, IOException {
		CarData data = new CarData(Color.BLACK,
				0, 0, 0, 0, 0, 0, 0, 0.0d, 0, 0, "description",
				new SensorHelper(), VehicleTypeEnum.CAR);
		
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("/home/mustafa/test.bin"));
		out.writeObject(data);
		out.close();
	}
}
