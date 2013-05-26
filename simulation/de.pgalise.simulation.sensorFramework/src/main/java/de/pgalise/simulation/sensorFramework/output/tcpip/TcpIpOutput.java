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
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Semaphore;

import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.sensorFramework.output.OutputStateEnum;
import de.pgalise.simulation.shared.exception.ExceptionMessages;

/**
 * Class for a TCP/IP sensor output.
 * 
 * @author Marcus
 * @version 1.0 (Aug 29, 2012)
 * @version 1.1 (Oct 16, 2012) Added state pattern and null checks.
 * @version 1.2 (Oct 16, 2012) Added transmission strategies
 */
public class TcpIpOutput implements Output {

	/**
	 * the {@link SocketChannel} of this {@link TcpIpConnectionStrategy}
	 */
	private SocketChannel socketChannel;

	/**
	 * the {@link DataOutputStream} of this {@link TcpIpConnectionStrategy}
	 */
	private DataOutputStream dataOutputStream;

	/**
	 * this variable holds the {@link TcpIpConnectionStrategy} for this {@link TcpIpOutput}
	 */
	private final TcpIpConnectionStrategy connectionStrategy;

	/**
	 * the address where to connect
	 */
	private final InetSocketAddress address;

	/**
	 * the current {@link TcpIpOutputState} this {@link TcpIpOutput} adopts
	 */
	private TcpIpOutputState currentState;

	/**
	 * this variable holds the {@link TcpIpOutputReadyState}
	 * 
	 * @see TcpIpOutputReadyState
	 */
	private final TcpIpOutputState readyState;

	/**
	 * {@link Semaphore} to keep an eye on who is using the {@link TcpIpOutput}
	 */
	private final Semaphore semaphore = new Semaphore(1, true);

	/**
	 * this variable holds the {@link TcpIpSensorOutputTransmittigState}
	 * 
	 * @see TcpIpOutputReadyState
	 */
	private final TcpIpOutputState transmittingState;

	/**
	 * Creates an {@link TcpIpOutput} from the passed address argument. <br>
	 * The constructor's modifier is set to 'private' because the static method <br>
	 * 'getOutputFor' is supposed to control the instantiation process.
	 * 
	 * @param address
	 *            the address where the {@link TcpIpOutput} has to connect
	 * @param connectionStrategy
	 *            the {@link TcpIpConnectionStrategy} this {@link TcpIpOutput} uses for opening and closing the
	 *            connection
	 * @exception IllegalArgumentException
	 *                if argument at least one passed argument is null
	 */
	public TcpIpOutput(final InetSocketAddress address, TcpIpConnectionStrategy connectionStrategy)
			throws IllegalArgumentException {
		if (address == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("address"));
		}
		if (connectionStrategy == null) {
			throw new IllegalArgumentException(ExceptionMessages.getMessageForNotNull("transmissionStrategy"));
		}
		// Set the state
		this.address = address;
		this.connectionStrategy = connectionStrategy;

		this.readyState = new TcpIpOutputReadyState(this);
		this.transmittingState = new TcpIpOutputTransmittingState(this);
		this.setCurrentState(this.getReadyState());
	}

	/**
	 * Creates an {@link TcpIpOutput} from the passed address argument. <br>
	 * The constructor's modifier is set to 'private' because the static method <br>
	 * 'getOutputFor' is supposed to control the instantiation process.
	 * 
	 * @param hostname
	 * @param port
	 * @param connectionStrategy
	 *            the {@link TcpIpConnectionStrategy} this {@link TcpIpOutput} uses for opening and closing the
	 *            connection
	 * @exception IllegalArgumentException
	 *                if argument at least one passed argument is null
	 */
	public TcpIpOutput(String hostname, int port, TcpIpConnectionStrategy connectionStrategy)
			throws IllegalArgumentException {
		this(new InetSocketAddress(hostname, port), connectionStrategy);
	}

	/**
	 * Starts the transmission process.
	 * 
	 * @exception IllegalStateException
	 *                if output is not in \"Transmitting\" state
	 */
	@Override
	public void beginTransmit() throws IllegalStateException {
		this.getCurrentState().beginTransmit();
	}

	/**
	 * Ends the transmission process.
	 * 
	 * @exception IllegalStateException
	 *                if output is not in \"Transmitting\" state
	 */
	@Override
	public void endTransmit() throws IllegalStateException {
		this.getCurrentState().endTransmit();
	}

	/**
	 * Returns the address to connect on of this output.
	 * 
	 * @return the address to connect on of this output
	 */
	public InetSocketAddress getAddress() {
		return this.address;
	}

	/**
	 * Returns the current state of the SensorOutput as an enum {@link OutputStateEnum}.
	 * 
	 * @return the current state of the SensorOutput as an enum
	 */
	@Override
	public OutputStateEnum getState() {
		return this.getCurrentState().getState();
	}

	/**
	 * Transmits the passed double value.
	 * 
	 * @param value
	 *            any double value
	 * @exception IllegalStateException
	 *                if output is not in 'Transmitting' state
	 */
	@Override
	public final void transmitDouble(final double value) throws IllegalStateException {
		this.getCurrentState().transmitDouble(value);
	}

	/**
	 * Transmits the passed float value.
	 * 
	 * @param value
	 *            any float value
	 * @exception IllegalStateException
	 *                if output is not in 'Transmitting' state
	 */
	@Override
	public final void transmitFloat(final float value) throws IllegalStateException {
		this.getCurrentState().transmitFloat(value);
	}

	/**
	 * Transmits the passed byte value.
	 * 
	 * @param value
	 *            any byte value
	 * @exception IllegalStateException
	 *                if output is not in 'Transmitting' state
	 */
	@Override
	public void transmitByte(final byte value) throws IllegalStateException {
		this.getCurrentState().transmitByte(value);
	}

	/**
	 * Transmits the passed integer value.
	 * 
	 * @param value
	 *            any int value
	 * @exception IllegalStateException
	 *                if output is not in 'Transmitting' state
	 */
	@Override
	public final void transmitInt(final int value) throws IllegalStateException {
		this.getCurrentState().transmitInt(value);
	}

	/**
	 * Transmits the passed long value.
	 * 
	 * @param value
	 *            any long value
	 * @exception IllegalStateException
	 *                if output is not in 'Transmitting' state
	 */
	@Override
	public final void transmitLong(final long value) throws IllegalStateException {
		this.getCurrentState().transmitLong(value);
	}

	/**
	 * Transmits the passed short value.
	 * 
	 * @param value
	 *            any short value
	 * @exception IllegalStateException
	 *                if output is not in 'Transmitting' state
	 */
	@Override
	public final void transmitShort(final short value) throws IllegalStateException {
		this.getCurrentState().transmitShort(value);
	}

	/**
	 * Transmits the passed string.
	 * 
	 * @param value
	 *            any string except null
	 * @exception IllegalArgumentException
	 *                if value is null
	 * @exception IllegalStateException
	 *                if output is not in \"Transmitting\" state
	 */
	@Override
	public final void transmitString(final String value) throws IllegalArgumentException, IllegalStateException {
		this.getCurrentState().transmitString(value);
	}

	/**
	 * Transmits the passed boolean value.
	 * 
	 * @param value
	 *            any string except null
	 * @exception IllegalStateException
	 *                if output is not in \"Transmitting\" state
	 */
	@Override
	public void transmitBoolean(final boolean value) throws IllegalStateException {
		this.getCurrentState().transmitBoolean(value);
	}

	/**
	 * Returns the current state of the SensorOutput as an object {@link TcpIpOutputState}.
	 * 
	 * @return the current state of the SensorOutput as an object
	 */
	TcpIpOutputState getCurrentState() {
		return this.currentState;
	}

	/**
	 * Returns the {@link TcpIpOutputReadyState} instance for this output.
	 * 
	 * @return the {@link TcpIpOutputReadyState} instance
	 * @see TcpIpOutputReadyState
	 */
	TcpIpOutputState getReadyState() {
		return this.readyState;
	}

	/**
	 * Returns the Semaphore of this output.
	 * 
	 * @return the Semaphore of this output
	 * @see Semaphore
	 */
	Semaphore getSemaphore() {
		return this.semaphore;
	}

	/**
	 * Returns the socket channel for this output.
	 * 
	 * @return the socket channel for this output
	 * @throws RuntimeException
	 *             if something went wrong
	 */
	void openConnection() throws RuntimeException {
		this.connectionStrategy.handleOpen(this);
	}

	/**
	 * Closes the connection in order to send the tuple to the server.
	 * 
	 * @throws RuntimeException
	 *             if something went wrong
	 */
	void closeConnection() throws RuntimeException {
		this.connectionStrategy.handleClose(this);
	}

	/**
	 * Returns the {@link DataOutputStream} for this {@link TcpIpOutput} to send data.
	 * 
	 * @return the {@link DataOutputStream} for this {@link TcpIpOutput} to send data
	 */
	DataOutputStream getSocketOutputStream() {
		return this.dataOutputStream;
	}

	/**
	 * Returns the {@link TcpIpOutputTransmittingState} instance for this output.
	 * 
	 * @return the {@link TcpIpOutputTransmittingState} instance
	 * @see TcpIpOutputTransmittingState
	 */
	TcpIpOutputState getTransmittingState() {
		return this.transmittingState;
	}

	/**
	 * Sets the current state of this output. One can use <br>
	 * 'getReadyState' or 'getTransmittingState' to set the current state. <br>
	 * For example use 'setCurrentSate(getReadyState())'.
	 * 
	 * @param currentState
	 *            the current state this output shall adopt
	 * @exception IllegalArgumentException
	 *                if argument 'currentState' is null
	 */
	void setCurrentState(final TcpIpOutputState currentState) {
		if (currentState == null) {
			throw new IllegalArgumentException("Argument \"state\" must not be null.");
		}
		this.currentState = currentState;
	}

	/**
	 * Returns the {@link TcpIpConnectionStrategyEnum} for this {@link TcpIpOutput}.
	 * 
	 * @return the {@link TcpIpConnectionStrategyEnum} for this {@link TcpIpOutput}
	 */
	public TcpIpConnectionStrategyEnum getConnectionStrategyEnum() {
		return this.connectionStrategy.getConnectionStrategyEnum();
	}

	/**
	 * Returns the {@link SocketChannel} for this {@link TcpIpOutput}.
	 * 
	 * @return the {@link SocketChannel} for this {@link TcpIpOutput}
	 */
	SocketChannel getSocketChannel() {
		return this.socketChannel;
	}

	/**
	 * Sets the {@link SocketChannel} for this {@link TcpIpOutput}.
	 * 
	 * @param socketChannel
	 *            the {@link SocketChannel} for this {@link TcpIpOutput}
	 */
	void setSocketChannel(final SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	/**
	 * Returns the {@link DataOutputStream} for this {@link TcpIpOutput}.
	 * 
	 * @return the {@link DataOutputStream} for this {@link TcpIpOutput}
	 */
	DataOutputStream getDataOutputStream() {
		return this.dataOutputStream;
	}

	/**
	 * Sets the {@link DataOutputStream} for this {@link TcpIpOutput}.
	 * 
	 * @param dataOutputStream
	 *            the {@link DataOutputStream} for this {@link TcpIpOutput}
	 */
	void setDataOutputStream(final DataOutputStream dataOutputStream) {
		this.dataOutputStream = dataOutputStream;
	}

	@Override
	public void transmitByteArray(byte[] value) throws IllegalStateException {
		this.getCurrentState().transmitByteArray(value);
	}
}
