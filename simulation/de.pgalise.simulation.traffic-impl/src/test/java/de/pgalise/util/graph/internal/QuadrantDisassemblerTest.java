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
package de.pgalise.util.graph.internal;

import de.pgalise.simulation.shared.JaxRSCoordinate;
import com.vividsolutions.jts.geom.Envelope;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
import de.pgalise.testutils.TestUtils;
import de.pgalise.util.graph.DisassemblerTest;
import de.pgalise.util.graph.disassembler.Disassembler;
import javax.annotation.ManagedBean;
import javax.ejb.EJB;
import javax.naming.NamingException;
import org.apache.openejb.api.LocalClient;
import org.geotools.geometry.jts.JTS;
import org.junit.Before;

/**
 * Tests the {@link QuadrantDisassembler}
 *
 * @author Marina
 * @version 1.0 (Nov 22, 2012)
 */
@LocalClient
@ManagedBean
public class QuadrantDisassemblerTest {

  @EJB
  private IdGenerator idGenerator;

  public QuadrantDisassemblerTest() {
  }

  @Before
  public void setUp() throws NamingException {
    TestUtils.getContainer().bind("inject",
      this);
  }

  @Test
  public void testDisassemble() {
    Disassembler dis = new QuadrantDisassembler();
    TrafficGraph graph = new DefaultTrafficGraph();
    NavigationNode node;

    // erster qudrant
    TrafficNode a = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(10,
        10));
    graph.addVertex(a);
    TrafficNode b = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(25,
        25));
    graph.addVertex(b);
    TrafficNode c = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(20,
        25));
    graph.addVertex(c);

// zweiter quadrant
    TrafficNode d = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(18,
        45));
    graph.addVertex(d);
    TrafficNode e = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(40,
        50));
    graph.addVertex(e);

    // dritter quadrant
    TrafficNode f = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(75,
        30));
    graph.addVertex(f);
    TrafficNode g = new TrafficNode(idGenerator.getNextId(),
      new JaxRSCoordinate(80,
        45));
    graph.addVertex(e);

    List<Geometry> quadrants = dis.disassemble(JTS.toGeometry(new Envelope(0,
      100,
      0,
      60)),
      4);
    // es gibt 3 quadranten
    assertEquals(4,
      quadrants.size());

    // erster quadrant hat 3 punkte
    assertEquals(3,
      DisassemblerTest.getNodes(graph.getVertexSet(),
        quadrants.get(0)).size());
    assertTrue(DisassemblerTest.getNodes(graph.getVertexSet(),
      quadrants.get(0)).contains(a)
      && DisassemblerTest.getNodes(graph.getVertexSet(),
        quadrants.get(0)).contains(b)
      && DisassemblerTest.getNodes(graph.getVertexSet(),
        quadrants.get(0)).contains(c));

    // erster quadrant hat 2 punkte
    assertEquals(2,
      DisassemblerTest.getNodes(graph.getVertexSet(),
        quadrants.get(1)).size());
    assertTrue(DisassemblerTest.getNodes(graph.getVertexSet(),
      quadrants.get(1)).contains(d)
      && DisassemblerTest.getNodes(graph.getVertexSet(),
        quadrants.get(1)).contains(e));

    // dritter quadrant hat 1 Punkte -> see below for check whether all points 
    //belong to exactly one quadrant
    assertEquals(1,
      DisassemblerTest.getNodes(graph.getVertexSet(),
        quadrants.get(2)).size());

    // vierter hat 2
    assertEquals(2,
      DisassemblerTest.getNodes(graph.getVertexSet(),
        quadrants.get(3)).size());
    assertTrue(DisassemblerTest.getNodes(graph.getVertexSet(),
      quadrants.get(3)).contains(f));
    assertTrue(DisassemblerTest.getNodes(graph.getVertexSet(),
      quadrants.get(3)).contains(g));
  }
}
