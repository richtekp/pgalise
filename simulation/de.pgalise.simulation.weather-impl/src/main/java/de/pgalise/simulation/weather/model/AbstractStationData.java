/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.model;

import java.sql.Date;
import java.sql.Time;
import javax.measure.Measure;
import javax.measure.quantity.Temperature;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author richter
 */
@MappedSuperclass
public abstract class AbstractStationData extends AbstractTimeSensitive implements MutableStationData, Comparable<AbstractStationData>  {
	private static final long serialVersionUID = 1L;

	/**
	 * air pressure
	 */
	@Column(name = "AIR_PRESSURE")
	private Integer airPressure;

	/**
	 * light intensity
	 */
	@Column(name = "LIGHT_INTENSITY")
	private Integer lightIntensity;

	/**
	 * perceived temperature
	 */
	@Column(name = "PERCEIVED_TEMPERATURE")
	private Float perceivedTemperature;

	/**
	 * precipitation amount
	 */
	@Column(name = "PRECIPITATION_AMOUNT")
	private Float precipitationAmount;

	/**
	 * radiation
	 */
	@Column(name = "RADIATION")
	private Integer radiation;

	/**
	 * relativ humidity
	 */
	@Column(name = "RELATIV_HUMIDITY")
	private Float relativHumidity;

	/**
	 * temperature
	 */
	@Column(name = "TEMPERATURE")
	private Measure<Float, Temperature> temperature;

	/**
	 * wind direction
	 */
	@Column(name = "WIND_DIRECTION")
	private Float windDirection;

	/**
	 * wind velocity
	 */
	@Column(name = "WIND_VELOCITY")
	private Float windVelocity;

	/**
	 * Default constructor
	 */
	protected AbstractStationData() {
	}

	/**
	 * Constructor
	 * 
	 * @param date
	 *            Date
	 * @param time
	 *            Time
	 * @param airPressure
	 *            air pressure
	 * @param lightIntensity
	 *            light intensity
	 * @param perceivedTemperature
	 *            perceived temperature
	 * @param temperature
	 *            temperature
	 * @param precipitationAmount
	 *            precipitation amount
	 * @param radiation
	 *            radiation
	 * @param relativHumidity
	 *            relativ humidity
	 * @param windDirection
	 *            wind direction
	 * @param windVelocity
	 *            wind velocity
	 */
	public AbstractStationData(
		Date date, 
		Time time, 
		Integer airPressure, 
		Integer lightIntensity, 
		Float perceivedTemperature,
		Measure<Float, Temperature> temperature, 
		Float precipitationAmount, 
		Integer radiation, 
		Float relativHumidity,
		Float windDirection, 
		Float windVelocity
	) {
		super(date,
			time);
		this.airPressure = airPressure;
		this.lightIntensity = lightIntensity;
		this.perceivedTemperature = perceivedTemperature;
		this.temperature = temperature;
		this.precipitationAmount = precipitationAmount;
		this.radiation = radiation;
		this.relativHumidity = relativHumidity;
		this.windDirection = windDirection;
		this.windVelocity = windVelocity;
	}

	@Override
	public int compareTo(AbstractStationData data) {
		long thisTime = this.getMeasureDate().getTime() + this.getMeasureTime().getTime();
		long anotherTime = data.getMeasureDate().getTime() + data.getMeasureTime().getTime();
		return (thisTime < anotherTime ? -1 : (thisTime == anotherTime ? 0 : 1));
	}

	@Override
	public Integer getAirPressure() {
		return this.airPressure;
	}

	@Override
	public Integer getLightIntensity() {
		return this.lightIntensity;
	}

	@Override
	public Float getPerceivedTemperature() {
		return this.perceivedTemperature;
	}

	@Override
	public Float getPrecipitationAmount() {
		return this.precipitationAmount;
	}

	@Override
	public Integer getRadiation() {
		return this.radiation;
	}

	@Override
	public Float getRelativHumidity() {
		return this.relativHumidity;
	}

	@Override
	public Measure<Float, Temperature> getTemperature() {
		return this.temperature;
	}

	@Override
	public Float getWindDirection() {
		return this.windDirection;
	}

	@Override
	public Float getWindVelocity() {
		return this.windVelocity;
	}

	@Override
	public void setAirPressure(Integer airPressure) {
		this.airPressure = airPressure;
	}

	@Override
	public void setLightIntensity(Integer lightIntensity) {
		this.lightIntensity = lightIntensity;
	}

	@Override
	public void setPerceivedTemperature(Float perceivedTemperature) {
		this.perceivedTemperature = perceivedTemperature;
	}

	@Override
	public void setPrecipitationAmount(Float precipitationAmount) {
		this.precipitationAmount = precipitationAmount;
	}

	@Override
	public void setRadiation(Integer radiation) {
		this.radiation = radiation;
	}

	@Override
	public void setRelativHumidity(Float relativHumidity) {
		this.relativHumidity = relativHumidity;
	}

	@Override
	public void setTemperature(Measure<Float, Temperature> temperature) {
		this.temperature = temperature;
	}

	@Override
	public void setWindDirection(Float windDirection) {
		this.windDirection = windDirection;
	}

	@Override
	public void setWindVelocity(Float windVelocity) {
		this.windVelocity = windVelocity;
	}
}
