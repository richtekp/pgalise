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
package de.pgalise.simulation.service;

import de.pgalise.simulation.sensorFramework.output.Output;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;

/**
 * The init parameters will be send to every controller on init. It contains all
 * information needed in the init state.
 *
 * @author Mustafa
 * @author Timo
 */
@ManagedBean
public class InitParameter implements Serializable {

  /**
   * Serial
   */
  private static final long serialVersionUID = -39986353888978216L;
  private Output output;

  public InitParameter() {
  }

  public InitParameter(Output output) {
    this.output = output;
  }

  public Output getOutput() {
    return output;
  }

}
