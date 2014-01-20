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
 *//* 
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
 *//* 
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
 *//* 
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


package de.pgalise.simulation.shared.entity;

import de.pgalise.simulation.shared.JaxRSCoordinate;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

/**
 * Represents the city from the simulation
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Sep 11, 2012)
 */
/*
 * has identical properties boundaries and centerPoint and Building, but sharing 
 * code is not possible in this inheritance hierarchy (all NavigationNode would have to be AbstractGeometricObjects
 */
@Entity
@NamedQuery(name = "City.getAll",
  query = "SELECT i FROM City i")
public class City extends Identifiable {

  /**
   * Serial
   */
  private static final long serialVersionUID = 3576972145732461552L;

  /**
   * Altitude (in m over normal null)
   */
  @Column(name = "ALTITUDE")
  private int altitude = 4;

  /**
   * Name
   */
  @Column(name = "NAME")
  private String name = "Oldenburg (Oldb)";

  /**
   * Option that the city is near a river
   */
  @Column(name = "NEAR_RIVER")
  private boolean nearRiver = false;

  /**
   * Option that the city is near the sea
   */
  @Column(name = "NEAR_SEA")
  private boolean nearSea = false;

  /**
   * Population
   */
  @Column(name = "POPULATION")
  private int population = 162481;

  /**
   * Rate for reference evaluation
   */
  @Transient
  private int rate = 0;
  @OneToOne
  private BaseGeoInfo geoInfo;
  /**
   * a point which is considered the most important in the geometry which is not
   * forcibly always the geographical center of the referenced area
   */
  @Embedded
  private JaxRSCoordinate referencePoint;

  /**
   * Default constructor
   */
  public City() {
  }

  /**
   * Constructor
   *
   * @param name Name
   * @param population Population
   * @param altitude Altitude
   * @param nearRiver Option that the city is near a river
   * @param nearSea Option that the city is near the sea
   * @param geoInfo
   * @param referencePoint explicit reference point which might be different
   * from the center point of <tt>geoInfo</tt>
   */
  public City(Long id,
    String name,
    int population,
    int altitude,
    boolean nearRiver,
    boolean nearSea,
    BaseGeoInfo geoInfo,
    JaxRSCoordinate referencePoint) {
    super(id);
    this.geoInfo = geoInfo;
    this.name = name;
    this.population = population;
    this.altitude = altitude;
    this.nearRiver = nearRiver;
    this.nearSea = nearSea;
  }

  /**
   * Creates a <tt>City</tt> with center point of <tt>geoInfo</tt> as reference
   * point
   *
   * @param name Name
   * @param population Population
   * @param altitude Altitude
   * @param nearRiver Option that the city is near a river
   * @param nearSea Option that the city is near the sea
   * @param geoInfo
   * @throws NullPointerException if <tt>geoInfo</tt> is <code>null</code>
   */
  public City(Long id,
    String name,
    int population,
    int altitude,
    boolean nearRiver,
    boolean nearSea,
    BaseGeoInfo geoInfo) {
    this(id,
      name,
      population,
      altitude,
      nearRiver,
      nearSea,
      geoInfo,
      geoInfo.getCenterPoint());
  }

  public int getAltitude() {
    return this.altitude;
  }

  public String getName() {
    return this.name;
  }

  public int getPopulation() {
    return this.population;
  }

  public int getRate() {
    return this.rate;
  }

  public boolean isNearRiver() {
    return this.nearRiver;
  }

  public boolean isNearSea() {
    return this.nearSea;
  }

  public void setAltitude(int altitude) {
    this.altitude = altitude;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setNearRiver(boolean nearRiver) {
    this.nearRiver = nearRiver;
  }

  public void setNearSea(boolean nearSea) {
    this.nearSea = nearSea;
  }

  public void setPopulation(int population) {
    this.population = population;
  }

  public void setRate(int rate) {
    this.rate = rate;
  }

  public void setGeoInfo(BaseGeoInfo geoInfo) {
    this.geoInfo = geoInfo;
  }

  public BaseGeoInfo getGeoInfo() {
    return geoInfo;
  }

  public void setReferencePoint(JaxRSCoordinate referencePoint) {
    this.referencePoint = referencePoint;
  }

  public JaxRSCoordinate getReferencePoint() {
    return referencePoint;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 29 * hash + this.altitude;
    hash = 29 * hash + Objects.hashCode(this.name);
    hash = 29 * hash + (this.nearRiver ? 1 : 0);
    hash = 29 * hash + (this.nearSea ? 1 : 0);
    hash = 29 * hash + this.population;
    hash = 29 * hash + this.rate;
    hash = 29 * hash + Objects.hashCode(this.geoInfo);
    hash = 29 * hash + Objects.hashCode(this.referencePoint);
    return hash;
  }

  protected boolean equalsTransitive(City other) {
    if (this.altitude != other.altitude) {
      return false;
    }
    if (!Objects.equals(this.name,
      other.name)) {
      return false;
    }
    if (this.nearRiver != other.nearRiver) {
      return false;
    }
    if (this.nearSea != other.nearSea) {
      return false;
    }
    if (this.population != other.population) {
      return false;
    }
    if (this.rate != other.rate) {
      return false;
    }
    if (!Objects.equals(this.geoInfo,
      other.geoInfo)) {
      return false;
    }
    if (!Objects.equals(this.referencePoint,
      other.referencePoint)) {
      return false;
    }
    return true;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final City other = (City) obj;
    return equalsTransitive(other);
  }
}
