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
package de.pgalise.simulation.traffic.entity;

import de.pgalise.simulation.shared.entity.Identifiable;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Wraps a stop name and its first stop time in millis.
 *
 * @author Lena
 */
@Entity
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BusStopInformation extends Identifiable {

  private static final long serialVersionUID = 1L;
  @OneToOne
  @XmlTransient
  private BusStop busStop;
  private long firstStopTimeInMillis;

  protected BusStopInformation() {
  }

  public BusStopInformation(BusStop busStop,
    long time) {
    this.busStop = busStop;
    this.firstStopTimeInMillis = time;
  }

  public void setBusStop(BusStop busStop) {
    this.busStop = busStop;
  }

  public BusStop getBusStop() {
    return busStop;
  }

  /**
   * @return the stopTimeInMillis
   */
  public long getFirstStopTimeInMillis() {
    return firstStopTimeInMillis;
  }

  /**
   * @param stopTimeInMillis the stopTimeInMillis to set
   */
  public void setFirstStopTimeInMillis(long stopTimeInMillis) {
    this.firstStopTimeInMillis = stopTimeInMillis;
  }
}
