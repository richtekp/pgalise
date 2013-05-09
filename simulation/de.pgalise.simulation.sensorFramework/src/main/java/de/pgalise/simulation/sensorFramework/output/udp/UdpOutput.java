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
 
package de.pgalise.simulation.sensorFramework.output.udp;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.sensorFramework.output.OutputStateEnum;

/**
 * Output for UDP transmission (alpha)
 * @author marcus
 * @version 0.1
 *
 */
public final class UdpOutput implements Output {

	/** datagram to send */
	private DatagramSocket datagramSocket;
	
	/** address where the data is sent to */
	private InetSocketAddress address;
	
	/** stores the bytes to send */
	private List<byte[]> bytesToSend = new LinkedList<>();
	
	/** current buffer size */
	private int currentBufSize = 0;
	
	/** ready state */
	private final UdpOutputState readyState = new UdpOutputReadyState(this);
	
	/** transmitting state */
	private final UdpOutputState transmittingState = new UdpOutputTransmittingState(this);
	
	/** stores the current state of the Output */
	private UdpOutputState currentState;
	
	/** Semaphore to enable access on the Output */
	private final Semaphore semaphore = new Semaphore(1);

	/**
	 * Constructor
	 * @param hostnamevthe host name to connect on
	 * @param port the port to connect on
	 * @throws SocketException if address could not be resolved
	 */
	public UdpOutput(final String hostname, final int port) throws SocketException {
		this.address = new InetSocketAddress(hostname, port);
		this.datagramSocket = new DatagramSocket();
	}
	
	@Override
	public void beginTransmit() throws IllegalStateException {
		this.getCurrentState().beginTransmit();
	}

	@Override
	public void endTransmit() throws IllegalStateException {
		this.getCurrentState().endTransmit();
	}

	@Override
	public OutputStateEnum getState() {
		return this.getCurrentState().getState();
	}

	@Override
	public void transmitBoolean(boolean value) throws IllegalStateException {
		this.getCurrentState().transmitBoolean(value);
	}

	@Override
	public void transmitByte(byte value) throws IllegalStateException {
		this.getCurrentState().transmitByte(value);
	}

	@Override
	public void transmitShort(short value) throws IllegalStateException {
		this.getCurrentState().transmitShort(value);
	}

	@Override
	public void transmitInt(int value) throws IllegalStateException {
		this.getCurrentState().transmitInt(value);
	}

	@Override
	public void transmitLong(long value) throws IllegalStateException {
		this.getCurrentState().transmitLong(value);
	}

	@Override
	public void transmitFloat(float value) throws IllegalStateException {
		this.getCurrentState().transmitFloat(value);
	}

	@Override
	public void transmitDouble(double value) throws IllegalStateException {
		this.getCurrentState().transmitDouble(value);
	}

	@Override
	public void transmitString(String value) throws IllegalArgumentException,
			IllegalStateException {
		this.getCurrentState().transmitString(value);
	}

	/**
	 * Returns the {@link DatagramSocket} of this Output.
	 * @return
	 */
	DatagramSocket getDatagramSocket() {
		return datagramSocket;
	}

	/**
	 * returns the destination address of this Output.
	 * @return the destination address of this Output 
	 */
	InetSocketAddress getAddress() {
		return address;
	}

	/**
	 * Returns the bytes to be sent.
	 * @return the bytes to be sent
	 */
	List<byte[]> getBytesToSend() {
		return bytesToSend;
	}

	/**
	 * Sets the bytes to be sent.
	 * @param bytesToSend the bytes to be sent
	 */
	void setBytesToSend(List<byte[]> bytesToSend) {
		this.bytesToSend = bytesToSend;
	}

	/**
	 * Returns the current buffer of this Output.
	 * @return the current buffer of this Output
	 */
	int getCurrentBufSize() {
		return currentBufSize;
	}

	/**
	 * Sets the current buffer size for this Output.
	 * @param currentBufSize the buffer size to be set for this Output
	 */
	void setCurrentBufSize(int currentBufSize) {
		this.currentBufSize = currentBufSize;
	}

	/**
	 * Returns the current UdpOutputState for this Output.
	 * @return the current UdpOutputState for this Output
	 */
	UdpOutputState getCurrentState() {
		return currentState;
	}

	/**
	 * Sets the current UdpOutputSate for this Output.
	 * @param currentState the UdpOutputSate to be set for this Output
	 */
	void setCurrentState(UdpOutputState currentState) {
		this.currentState = currentState;
	}

	/**
	 * Returns the Ready State of this Output.
	 * @return the Ready State of this Output
	 */
	UdpOutputState getReadyState() {
		return readyState;
	}

	/**
	 * Returns the Transmitting State of this Output.
	 * @return the Transmitting State of this Output
	 */
	UdpOutputState getTransmittingState() {
		return transmittingState;
	}

	/**
	 * Returns the Semaphore of this Output.
	 * @return the Semaphore of this Output
	 */
	Semaphore getSemaphore() {
		return semaphore;
	}

	@Override
	public void transmitByteArray(byte[] value) throws IllegalStateException {
		this.getCurrentState().transmitByteArray(value);
	}
}
