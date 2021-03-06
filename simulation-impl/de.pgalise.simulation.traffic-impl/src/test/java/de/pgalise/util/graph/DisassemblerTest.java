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
 *//* 
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
 *//* 
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
 *//* 
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
package de.pgalise.util.graph;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.BaseCoordinate;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
import de.pgalise.testutils.TestUtils;
import de.pgalise.util.graph.disassembler.Disassembler;
import de.pgalise.util.graph.internal.QuadrantDisassembler;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import org.geotools.geometry.jts.JTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link QuadrantDisassembler}
 *
 * @author Marina
 * @version 1.0 (Nov 22, 2012)
 */
@LocalClient
@ManagedBean
public class DisassemblerTest {

  @EJB
  private IdGenerator idGenerator;

  public DisassemblerTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContext().bind("inject",
      this);
  }

  @Test
  public void disassemble() {
    Disassembler dis = new QuadrantDisassembler();
    TrafficGraph graph = new DefaultTrafficGraph();

    // erster qudrant
    TrafficNode a = new TrafficNode(
      new Coordinate(10,
        10));
    TrafficNode b = new TrafficNode(
      new Coordinate(25,
        25));
    TrafficNode c = new TrafficNode(
      new Coordinate(20,
        25));
    graph.addVertex(a);
    graph.addVertex(b);
    graph.addVertex(c);

    // zweiter quadrant
    TrafficNode d = new TrafficNode(
      new Coordinate(18,
        45));
    TrafficNode e = new TrafficNode(
      new Coordinate(40,
        50));
    graph.addVertex(d);
    graph.addVertex(e);

    // dritter quadrant
    TrafficNode f = new TrafficNode(
      new Coordinate(75,
        30));
    TrafficNode g = new TrafficNode(
      new Coordinate(80,
        45));
    graph.addVertex(f);
    graph.addVertex(g);

    List<Geometry> quadrants = dis.disassemble(JTS.toGeometry(new Envelope(
      new Coordinate(0,
        0),
      new Coordinate(100,
        60))),
      4);

    // es gibt 3 quadranten
    assertEquals(4,
      quadrants.size());

    // erster quadrant hat 3 punkte
    assertEquals(3,
      this.getNodes(graph.getVertexSet(),
        quadrants.get(0)).size());
    assertTrue(this.getNodes(graph.getVertexSet(),
      quadrants.get(0)).contains(a)
      && this.getNodes(graph.getVertexSet(),
        quadrants.get(0)).contains(b)
      && this.getNodes(graph.getVertexSet(),
        quadrants.get(0)).contains(c));

    // erster quadrant hat 2 punkte
    assertEquals(2,
      this.getNodes(graph.getVertexSet(),
        quadrants.get(1)).size());
    assertTrue(this.getNodes(graph.getVertexSet(),
      quadrants.get(1)).contains(d)
      && this.getNodes(graph.getVertexSet(),
        quadrants.get(1)).contains(e));

    // dritter quadrant hat 0 punkte
    assertEquals(1,
      this.getNodes(graph.getVertexSet(),
        quadrants.get(2)).size());

    // vierter hat 2
    assertEquals(2,
      this.getNodes(graph.getVertexSet(),
        quadrants.get(3)).size());
    assertTrue(this.getNodes(graph.getVertexSet(),
      quadrants.get(3)).contains(f));
    assertTrue(this.getNodes(graph.getVertexSet(),
      quadrants.get(3)).contains(g));
  }

  /**
   * Returns the nodes as String
   *
   * @param nodes Nodes
   * @param geometry Position
   * @return Nodes as String
   */
  public static Set<TrafficNode> getNodes(Collection<TrafficNode> nodes,
    Geometry geometry) {
    Set<TrafficNode> str = new HashSet<>();
    for (TrafficNode node : nodes) {
      if (geometry.covers(GeoToolsBootstrapping.getGeometryFactory().
        createPoint(node))) {
        str.add(node);
      }
    }
    return str;
  }
}
