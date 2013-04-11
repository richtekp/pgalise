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
 
package de.pgalise.util.graph.disassembler;

import java.util.List;

import de.pgalise.simulation.shared.geometry.Geometry;

/**
 * Interface for disassembling the graph
 * 
 * @author Mustafa
 * @version 1.0 (Sep 6, 2012)
 */
public interface Disassembler {
	/**
	 * Disassembles the graph
	 * 
	 * @param mapper
	 *            Geometry
	 * @param numServers
	 *            Number of servers
	 * @return List with Geometry objects
	 */
	public List<Geometry> disassemble(Geometry mapper, int numServers);
}
