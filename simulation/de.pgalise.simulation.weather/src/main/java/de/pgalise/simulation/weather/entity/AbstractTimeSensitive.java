/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.entity;

import de.pgalise.simulation.shared.entity.Identifiable;
import java.sql.Date;
import java.sql.Time;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author richter
 */
@MappedSuperclass
public abstract class AbstractTimeSensitive extends Identifiable {

  /**
   * Timestamp
   */
//	@Column(name = "MEASURE_DATE")
//	@Temporal(TemporalType.DATE)
  private Date measureDate;

//	@Temporal(TemporalType.TIME)
  private Time measureTime;

  protected AbstractTimeSensitive() {
  }

  public AbstractTimeSensitive(Long id,
    Date measureDate,
    Time measureTime) {
    super(id);
    this.measureDate = measureDate;
    this.measureTime = measureTime;
  }

  public Time getMeasureTime() {
    return measureTime;
  }

  public Date getMeasureDate() {
    return measureDate;
  }

  public void setMeasureTime(Time measureTime) {
    this.measureTime = measureTime;
  }

  public void setMeasureDate(Date measureDate) {
    this.measureDate = measureDate;
  }

  public static Long generateId(Date measureDate,
    Time measureTime) {
    if (measureDate == null) {
      throw new IllegalArgumentException("measureDate");
    }
    if (measureTime == null) {
      throw new IllegalArgumentException("measureTime");
    }
    return measureDate.getTime() + measureTime.getTime();
  }
}
