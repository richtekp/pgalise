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
package de.pgalise.simulation.shared.exception;

import javax.ejb.ApplicationException;

/**
 * This exception can be thrown, if no valid controller could be found to create
 * a sensor.
 *
 * @author Kamil Knefel
 */
@ApplicationException
public class NoValidControllerForSensorException extends RuntimeException {

  /**
   * Serial
   */
  private static final long serialVersionUID = 4336527983767575416L;

  /**
   * Constructor
   */
  public NoValidControllerForSensorException() {
    super("No valid controller found.");
  }

  /**
   * Constructor
   *
   * @param text text for exception
   */
  public NoValidControllerForSensorException(String text) {
    super(text);
  }

}
