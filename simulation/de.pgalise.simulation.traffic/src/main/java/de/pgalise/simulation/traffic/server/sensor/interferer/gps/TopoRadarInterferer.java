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
package de.pgalise.simulation.traffic.server.sensor.interferer.gps;

import de.pgalise.simulation.shared.sensor.SensorInterferer;
import java.util.List;
import org.jgroups.util.Triple;

/**
 * Interface for an interferer which manipulates the toporadar input
 *
 * @author Marcus
 * @author Andreas
 * @version 1.0
 */
public interface TopoRadarInterferer extends SensorInterferer {

  /**
   * Interferes the sensor value
   *
   * @param mutableAxleCount axle value to change
   * @param mutableLength length value to change
   * @param mutableWheelbases wheelbase1 value to change
   * @param simTime Simulation timestamp
   * @return array with new values
   */
  Triple<Integer, Integer, List<Integer>> interfere(final int mutableAxleCount,
    final int mutableLength,
    List<Integer> mutableWheelbases,
    final long simTime);
}
