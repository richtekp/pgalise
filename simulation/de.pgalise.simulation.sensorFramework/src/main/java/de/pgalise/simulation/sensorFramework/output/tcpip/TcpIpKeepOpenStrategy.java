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

import java.io.DataOutputStream;
import java.nio.channels.SocketChannel;

/**
 * This class extends {@link TcpIpConnectionStrategy} and keeps the socket open after each tuple transmission.
 * 
 * @author Marcus
 */
public final class TcpIpKeepOpenStrategy extends TcpIpConnectionStrategy {

	/**
	 * Returns always TcpIpConnectionStrategyEnum.KEEP_OPEN.
	 * 
	 * @return always TcpIpConnectionStrategyEnum.KEEP_OPEN
	 */
	@Override
	TcpIpConnectionStrategyEnum getConnectionStrategyEnum() {
		return TcpIpConnectionStrategyEnum.KEEP_OPEN;
	}

	@Override
	void handleOpen(final TcpIpOutput sensorOutput) {
		try {
			if (sensorOutput.getSocketChannel() == null) {
				sensorOutput.setSocketChannel(SocketChannel.open(sensorOutput.getAddress()));
				sensorOutput.setDataOutputStream(new DataOutputStream(sensorOutput.getSocketChannel().socket()
						.getOutputStream()));
			}
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	void handleClose(final TcpIpOutput sensorOutput) {
		// Nothing to do here
	}
}
