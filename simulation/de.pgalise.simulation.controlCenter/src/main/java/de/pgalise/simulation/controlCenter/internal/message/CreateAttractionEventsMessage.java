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

import java.util.Collection;

import de.pgalise.simulation.controlCenter.internal.model.AttractionData;

/**
 * Message to create attraction events.
 * 
 * @author Timo
 */
public class CreateAttractionEventsMessage extends CCWebSocketMessage<Collection<AttractionData>> {

	/**
	 * Constructor
	 * 
	 * @param messageID
	 *            ID
	 * @param content
	 *            List of Attraction data
	 */
	public CreateAttractionEventsMessage(int messageID, Collection<AttractionData> content) {
		super(messageID, CCWebSocketMessage.MessageType.CREATE_ATTRACTION_EVENTS_MESSAGE, content);
	}
}
