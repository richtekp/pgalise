/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.model;

import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import java.sql.Date;
import java.sql.Time;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author richter
 */
@MappedSuperclass
public abstract class AbstractTimeSensitive extends AbstractIdentifiable implements MutableTimeSensitive {
	
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
	
	public AbstractTimeSensitive(Date measureDate, Time measureTime) {
		this.measureDate = measureDate;
		this.measureTime = measureTime;
	}

	@Override
	public Time getMeasureTime() {
		return measureTime;
	}

	@Override
	public Date getMeasureDate() {
		return measureDate;
	}

	@Override
	public void setMeasureTime(Time measureTime) {
		this.measureTime = measureTime;
	}

	@Override
	public void setMeasureDate(Date measureDate) {
		this.measureDate = measureDate;
	}

	public static Long generateId(Date measureDate, Time measureTime) {
		if(measureDate == null) {
			throw new IllegalArgumentException("measureDate");
		}
		if(measureTime == null) {
			throw new IllegalArgumentException("measureTime");
		}
		return measureDate.getTime()+measureTime.getTime();
	}
}