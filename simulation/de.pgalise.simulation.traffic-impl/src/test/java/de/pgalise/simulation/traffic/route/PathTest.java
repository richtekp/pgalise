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

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.JaxbVector2d;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
import de.pgalise.testutils.TestUtils;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import org.jgrapht.traverse.ClosestFirstIterator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mustafa
 * @version 1.0 (Nov 22, 2012)
 */
@LocalClient
@ManagedBean
public class PathTest {

  /**
   * Logger
   */
  private static final Logger log = LoggerFactory.getLogger(PathTest.class);

  @EJB
  private IdGenerator idGenerator;

  public PathTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContext().bind("inject",
      this);
  }

  @Test
  public void test() {
    TrafficGraph graph = new DefaultTrafficGraph();

    double velocity = 50;
    double distance = 200;

    // log.debug("velocity = " + velocity);
    // log.debug("distance 2000m = " + distance);
    TrafficNode a = new TrafficNode(idGenerator.getNextId(),
      new Coordinate(0,
        0));
    graph.addVertex(a);
    TrafficNode b = new TrafficNode(idGenerator.getNextId(),
      new Coordinate(distance,
        0));
    graph.addVertex(b);
    TrafficNode c = new TrafficNode(idGenerator.getNextId(),
      new Coordinate(distance,
        distance));
    graph.addVertex(c);

    TrafficEdge ab = graph.addEdge(a,
      b);
    ab.setMaxSpeed(velocity);
    graph.setEdgeWeight(ab,
      distance / velocity);
    PathTest.log.debug("Weight of ab: " + distance / velocity);

    TrafficEdge bc = graph.addEdge(b,
      c);
    bc.setMaxSpeed(velocity);
    graph.setEdgeWeight(bc,
      distance / velocity);
    PathTest.log.debug("Weight of bc: " + distance / velocity);

    TrafficEdge ac = graph.addEdge(a,
      c);
    JaxbVector2d v = new JaxbVector2d(a.getX(),
      a.getY());
    v.sub(new JaxbVector2d(c.getX(),
      c.getY()));
    double length = v.length();

    ac.setMaxSpeed(velocity);
    graph.setEdgeWeight(ac,
      length / velocity);
    PathTest.log.debug("Weight of ac: " + length / velocity);

    ClosestFirstIterator<TrafficNode, TrafficEdge> astar = new ClosestFirstIterator<>(
      graph,
      a);
    List<TrafficNode> astarList = new LinkedList<>();
    while (astar.hasNext()) {
      astarList.add(astar.next());
    }
    Assert.assertEquals(astarList,
      new LinkedList<>(Arrays.asList(a,
          b,
          c)));
  }
}
