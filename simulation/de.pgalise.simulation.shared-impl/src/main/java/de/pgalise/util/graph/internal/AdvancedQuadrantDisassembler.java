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

import com.vividsolutions.jts.geom.Envelope;
import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import de.pgalise.util.graph.disassembler.AdvancedDisassembler;
import javax.ejb.Local;
import javax.ejb.Stateless;
import org.geotools.geometry.jts.JTS;

/**
 * Divides a Geometry into smaller pieces.
 *
 * @author Timo
 */
@Stateless
@Local(AdvancedDisassembler.class)
public class AdvancedQuadrantDisassembler implements AdvancedDisassembler {

  @Override
  public List<Geometry> disassemble(Geometry mapper,
    int numServers) {
    List<Geometry> GeometryList = new ArrayList<>();
    GeometryList.add(mapper);

    for (int i = 1; i < numServers; i++) {
      List<Geometry> tmpGeometryList = new ArrayList<>();
      Geometry tmpBiggestGeometry = null;

      for (Geometry geometry : GeometryList) {
        if ((tmpBiggestGeometry == null)
          || ((tmpBiggestGeometry.getEnvelopeInternal().getWidth() * tmpBiggestGeometry.
          getEnvelopeInternal().getHeight()) <= (geometry.getEnvelopeInternal().
          getWidth() * geometry
          .getEnvelopeInternal().getHeight()))) {
          tmpBiggestGeometry = geometry;
        }
      }

      tmpGeometryList.addAll(this.halveGeometry(tmpBiggestGeometry));

      for (Geometry Geometry : GeometryList) {
        if (!Geometry.equals(tmpBiggestGeometry)) {
          tmpGeometryList.add(Geometry);
        }
      }

      GeometryList = tmpGeometryList;
    }

    return new ArrayList<>(GeometryList);
  }

  /**
   * Divides the Geometry into two parts.
   *
   * @param Geometry
   * @return List with Geometry objects
   */
  private List<Geometry> halveGeometry(Geometry geometry) {
    List<Geometry> dividedGeometryList = new ArrayList<>();

    /* horizontal or vertical cut? */
    if (geometry.getEnvelopeInternal().getWidth() <= geometry.
      getEnvelopeInternal().getHeight()) {

      dividedGeometryList.add(
        JTS.toGeometry(new Envelope(geometry.getEnvelopeInternal().getMinX(),
            geometry.getEnvelopeInternal().getMinY(),
            geometry.getEnvelopeInternal().getMaxX(),
            geometry.getEnvelopeInternal().getMaxY() / 2)));
      dividedGeometryList.add(JTS.toGeometry(new Envelope(geometry.
        getEnvelopeInternal().getMinX(),
        geometry.getEnvelopeInternal().getMaxY() / 2,
        geometry.getEnvelopeInternal().getMaxX(),
        geometry.getEnvelopeInternal().getMaxY())));
    } else {
      dividedGeometryList.add(JTS.toGeometry(new Envelope(geometry.
        getEnvelopeInternal().getMinX(),
        geometry.getEnvelopeInternal().getMinY(),
        geometry.getEnvelopeInternal().getMaxX() / 2,
        geometry.getEnvelopeInternal().getMaxY())));
      dividedGeometryList.add(JTS.toGeometry(new Envelope(geometry.
        getEnvelopeInternal().getMaxX() / 2,
        geometry.getEnvelopeInternal().getMinY(),
        geometry.getEnvelopeInternal().getMaxX(),
        geometry.getEnvelopeInternal().getMaxY())));
    }

    return dividedGeometryList;
  }
}
