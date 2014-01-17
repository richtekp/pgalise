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
package de.pgalise.simulation.traffic.route;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.shared.JaxbVector2d;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
import de.pgalise.testutils.TestUtils;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Marcus
 * @version 1.0 (Mar 20, 2013)
 */
@Ignore
@LocalClient
@ManagedBean
public class ParalellableDijkstraTest {

  @EJB
  private IdGenerator idGenerator;

  public ParalellableDijkstraTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContainer().getContext().bind("inject",
      this);
  }

  /**
   * Logger
   */
  private static final Logger log = LoggerFactory.getLogger(PathTest.class);

  @Test
  public void test() {
    TrafficGraph graph = new DefaultTrafficGraph();

    double velocity = 50;
    double distance = 200;

    TrafficNode a = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(0,
        0));
    TrafficNode b = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(distance,
        0));
    TrafficNode c = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(distance,
        distance));

    TrafficEdge ab = graph.addEdge(a,
      b);
    ab.setMaxSpeed(velocity);
    graph.setEdgeWeight(ab,
      distance / velocity);
    log.debug("Weight of ab: " + distance / velocity);

    TrafficEdge bc = graph.addEdge(b,
      c);
    ab.setMaxSpeed(velocity);
    graph.setEdgeWeight(bc,
      distance / velocity);
    log.debug("Weight of bc: " + distance / velocity);

    TrafficEdge ac = graph.addEdge(a,
      c);
    JaxbVector2d v = new JaxbVector2d(a.getGeoLocation().getX(),
      a.getGeoLocation().getY());
    v.sub(new JaxbVector2d(c.getGeoLocation().getX(),
      c.getGeoLocation().getY()));
    double length = v.length();

    ac.setMaxSpeed(velocity);
    graph.setEdgeWeight(ac,
      length / velocity);
    log.debug("Weight of ac: " + length / velocity);
  }
}
