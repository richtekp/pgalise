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

import de.pgalise.simulation.controlCenter.internal.model.OSMAndBusstopFileData;

/**
 * Holds information about osm and busstop file. The user can select between several files
 * and the server will load the selected files for the simulation scenario.
 * 
 * @author Timo
 */
public class OSMAndBusstopFileMessage extends CCWebSocketMessage<OSMAndBusstopFileData> {

	/**
	 * Constructor
	 * 
	 * @param messageID
	 *            ID
	 * @param content
	 *            Holds information about osm and busstop file.
	 */
	public OSMAndBusstopFileMessage(int messageID, OSMAndBusstopFileData content) {
		super(messageID, CCWebSocketMessage.MessageType.OSM_AND_BUSSTOP_FILE_MESSAGE, content);
	}
}
