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

import de.pgalise.simulation.shared.entity.NavigationNode;
import de.pgalise.simulation.traffic.entity.TrafficNode;

/**
 * Will answer with a node with a valid position.
 * 
 * @author Timo
 */
public class AskForValidNodeMessage extends IdentifiableControlCenterMessage<TrafficNode> {

	/**
	 * Constructor
	 * 
	 * @param messageID
	 *            ID
	 * @param content
	 *            the node that is asked.
	 */
	public AskForValidNodeMessage(Long messageID, TrafficNode content) {
		super(messageID, MessageTypeEnum.ASK_FOR_VALID_NODE, content);
	}
}
