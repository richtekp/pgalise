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
 
package de.pgalise.simulation.sensorFramework.output;

/**
 * Decorator for Output
 * @author marcus
 *
 */
public abstract class OutputDecorator implements Output {
	private static final long serialVersionUID = 1L;
	
	/** the Output to decorate **/
	private Output sensorOutput;

	public OutputDecorator() {
	}
	
	/**
	 * Constructor
	 * @param sensorOutput the Output to decorate
	 */
	protected OutputDecorator(final Output sensorOutput) {
		if(sensorOutput == null) {
			throw new IllegalArgumentException("sensorOutput may not be null");
		}
		if(sensorOutput == this) {
			throw new IllegalStateException("cant pass its own instance");
		}
		this.sensorOutput = sensorOutput;
	}

	/**
	 * Returns the Output of {@link OutputDecorator}
	 * @return
	 */
	protected Output getSensorOutput() {
		return sensorOutput;
	}

	protected void setSensorOutput(Output sensorOutput) {
		this.sensorOutput = sensorOutput;
	}
}
