/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.sensorFramework.output.tcpip;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.sensorFramework.output.OutputStateEnum;
import java.io.DataOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Semaphore;

/**
 *
 * @author richter
 */
public interface TcpIpOutput extends Output {

	/**
	 * Returns the address to connect on of this output.
	 *
	 * @return the address to connect on of this output
	 */
	InetSocketAddress getAddress();

	/**
	 * Returns the {@link TcpIpConnectionStrategyEnum} for this {@link TcpIpOutput}.
	 *
	 * @return the {@link TcpIpConnectionStrategyEnum} for this {@link TcpIpOutput}
	 */
	TcpIpConnectionStrategyEnum getConnectionStrategyEnum();
	
	SocketChannel getSocketChannel();
	
	void setSocketChannel(SocketChannel socketChannel);
	
	DataOutputStream getDataOutputStream();
	
	void setDataOutputStream(DataOutputStream dataOutputStream);
	
	Semaphore getSemaphore();
	
	void openConnection();
	
	void closeConnection();
	
	TcpIpOutputState getReadyState();
	
	TcpIpOutputState getTransmittingState();
	
	void setCurrentState(TcpIpOutputState outputState);
}
