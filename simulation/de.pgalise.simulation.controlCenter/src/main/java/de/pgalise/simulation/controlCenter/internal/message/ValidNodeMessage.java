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
 
package de.pgalise.simulation.controlCenter.internal.message;

import de.pgalise.simulation.shared.city.NavigationNode;

/**
 * Returns a valid node.
 * 
 * @author Timo
 */
public class ValidNodeMessage extends CCWebSocketMessage<NavigationNode> {

	/**
	 * Constructor
	 * 
	 * @param messageID
	 *            ID
	 * @param content
	 *            new valid node on the graph
	 */
	public ValidNodeMessage(int messageID, NavigationNode content) {
		super(messageID, CCWebSocketMessage.MessageType.VALID_NODE, content);
	}
}
