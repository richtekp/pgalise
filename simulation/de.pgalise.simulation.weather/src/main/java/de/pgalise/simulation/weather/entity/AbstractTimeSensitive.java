/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.entity;

import de.pgalise.simulation.shared.entity.Identifiable;
import java.sql.Date;
import java.sql.Time;
import java.util.Objects;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author richter
 */
/*
 As long as JPA is incapable to specifying useful query of date, we have to 
 save duplicate information in a date field (providing fast query of date) and a time field
 */
@MappedSuperclass
public abstract class AbstractTimeSensitive extends Identifiable {

  /**
   * Timestamp
   */
//	@Column(name = "MEASURE_DATE")
//	@Temporal(TemporalType.DATE)
  private java.util.Date measureDate;

//	@Temporal(TemporalType.TIME)
  private Time measureTime;

  protected AbstractTimeSensitive() {
  }

  public AbstractTimeSensitive(Long id,
    java.util.Date measureDate,
    Time measureTime) {
    super(id);
    this.measureDate = measureDate;
    this.measureTime = measureTime;
  }

  public Time getMeasureTime() {
    return measureTime;
  }

  public java.util.Date getMeasureDate() {
    return measureDate;
  }

  public void setMeasureTime(Time measureTime) {
    this.measureTime = measureTime;
  }

  public void setMeasureDate(java.util.Date measureDate) {
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

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + Objects.hashCode(this.measureDate);
		hash = 31 * hash + Objects.hashCode(this.measureTime);
		return hash;
	}
	
	protected boolean equalsTransitive(AbstractTimeSensitive other) {
		if (!Objects.equals(this.measureDate,
			other.measureDate)) {
			return false;
		}
		if (!Objects.equals(this.measureTime,
			other.measureTime)) {
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
		final AbstractTimeSensitive other = (AbstractTimeSensitive) obj;
		return equalsTransitive(other);
	}
}
