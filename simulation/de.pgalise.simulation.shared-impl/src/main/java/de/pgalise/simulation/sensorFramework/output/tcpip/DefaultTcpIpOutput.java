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
package de.pgalise.simulation.sensorFramework.output.tcpip;

import javax.ejb.Stateful;

/**
 * Class for a TCP/IP sensor output.
 *
 * @author Marcus
 * @version 1.0 (Aug 29, 2012)
 * @version 1.1 (Oct 16, 2012) Added state pattern and null checks.
 * @version 1.2 (Oct 16, 2012) Added transmission strategies
 */
@Stateful
public class DefaultTcpIpOutput extends AbstractTcpIpOutput implements TcpIpOutput {
	private static final long serialVersionUID = 1L;

}
