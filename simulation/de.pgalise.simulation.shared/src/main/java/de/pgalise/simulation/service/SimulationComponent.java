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

import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.shared.event.EventList;
import java.io.Serializable;

/**
 * Interface for every updateable simulation component.
 *
 * @param <E>
 * @author Marcus
 * @author Timo
 */
public interface SimulationComponent<E extends Event> extends Serializable {

  Long getId();

  /**
   * Updates the SimulationComponent
   *
   * @param simulationEventList list with the current timestamp and possible
   * events.
   */
  void update(EventList<E> simulationEventList);
}
