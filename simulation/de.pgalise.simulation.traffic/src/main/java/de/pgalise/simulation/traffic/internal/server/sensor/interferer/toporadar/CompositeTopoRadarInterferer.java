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
package de.pgalise.simulation.traffic.internal.server.sensor.interferer.toporadar;

import de.pgalise.simulation.shared.exception.ExceptionMessages;
import de.pgalise.simulation.traffic.server.sensor.interferer.gps.TopoRadarInterferer;
import java.util.LinkedList;
import java.util.List;
import org.jgroups.util.Triple;

/**
 * Implementation of an {@link TopoRadarInterferer} that hold several other
 * {@link TopoRadarInterferer}s
 *
 * @author Marcus
 * @version 1.0
 */
public class CompositeTopoRadarInterferer implements TopoRadarInterferer {

  private static final long serialVersionUID = 1L;

  /**
   * the composite {@link TopoRadarInterferer}s
   */
  private final List<TopoRadarInterferer> interferers;

  /**
   * Creates a {@link CompositeTopoRadarInterferer} with no composite
   * {@link TopoRadarInterferer}s attached.
   */
  public CompositeTopoRadarInterferer() {
    this(new LinkedList<TopoRadarInterferer>());
  }

  /**
   * Creates a {@link CompositeTopoRadarInterferer} with the passed
   * {@link TopoRadarInterferer}s attached.
   *
   * @param interferers the {@link CompositeTopoRadarInterferer}s to attach
   * @throws IllegalArgumentException if argument 'interferers' is 'null'
   */
  public CompositeTopoRadarInterferer(List<TopoRadarInterferer> interferers) throws IllegalArgumentException {
    if (interferers == null) {
      throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull(
        "interferers"));
    }
    this.interferers = new LinkedList<>(interferers);
  }

  /**
   * Attaches an {@link TopoRadarInterferer} to this
   * {@link CompositeTopoRadarInterferer}
   *
   * @param interferer the {@link TopoRadarInterferer} to be attached to this
   * {@link CompositeTopoRadarInterferer}
   * @return 'true' whether the passed {@link TopoRadarInterferer} could have
   * been attached to this {@link CompositeTopoRadarInterferer}, otherwise
   * 'false'
   * @throws UnsupportedOperationException
   */
  public boolean attach(final TopoRadarInterferer interferer) throws UnsupportedOperationException {
    if (interferer instanceof CompositeTopoRadarInterferer) {
      throw new UnsupportedOperationException(
        "Argument 'interferer' may not be an instance of CompositeTopoRadarInterferer");
    }
    return this.interferers.add(interferer);
  }

  /**
   * Detaches the passed {@link TopoRadarInterferer} from this
   * {@link CompositeTopoRadarInterferer}
   *
   * @param interferer the {@link TopoRadarInterferer} to be detached from this
   * {@link CompositeTopoRadarInterferer}
   * @return true if the {@link TopoRadarInterferer} is detached
   */
  public boolean detach(final TopoRadarInterferer interferer) {
    return this.interferers.remove(interferer);
  }

  @Override
  public Triple<Integer, Integer, List<Integer>> interfere(int mutableAxleCount,
    final int mutableLength,
    List<Integer> wheelbases,
    final long simTime) {
    Triple<Integer, Integer, List<Integer>> result = new Triple<>(
      mutableAxleCount,
      mutableLength,
      wheelbases);
    for (final TopoRadarInterferer interferer : this.interferers) {
      result = interferer.interfere(result.getVal1(),
        result.getVal2(),
        result.getVal3(),
        simTime);
    }
    // Returns the last value
    return result;
  }
}
