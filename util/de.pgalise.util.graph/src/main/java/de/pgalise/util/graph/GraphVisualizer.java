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
 
package de.pgalise.util.graph;

import java.util.List;

import de.pgalise.util.generic.function.Function;
import org.jgrapht.graph.DefaultDirectedGraph;

/**
 * Visualizes a graph with its nodes and edges.
 * It is a premise that the nodes and edges have a 'position' attribute 
 * which returns a {@link de.pgalise.util.vector.Vector2d}.
 *  
 * @author mustafa
 *
 */
public interface GraphVisualizer<G extends DefaultDirectedGraph> {
	public void setGraph(G graph);
	
	public G getGraph();

	public void draw();

	public void scale(double x, double y);

	public void translate(double x, double y);

	public void addWindowCloseListener(Function func);

	public List<Function> getWindowCloseListener();
}
