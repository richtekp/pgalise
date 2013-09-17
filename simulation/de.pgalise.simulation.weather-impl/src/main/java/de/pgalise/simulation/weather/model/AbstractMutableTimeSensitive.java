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
public class AbstractMutableTimeSensitive extends AbstractIdentifiable implements TimeSensitive, MutableTimeSensitive {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Timestamp
	 */
//	@Column(name = "MEASURE_DATE")
//	@Temporal(TemporalType.DATE)
	private Date measureDate;
	
//	@Temporal(TemporalType.TIME)
	private Time measureTime;

	protected AbstractMutableTimeSensitive() {
	}

	public AbstractMutableTimeSensitive(Date measureDate,
		Time measureTime
	) {
		this();
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

	
}
