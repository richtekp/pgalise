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
 
package de.pgalise.simulation.weather.parameter;

/**
 * Enum for all weather parameters
 * 
 * @author Andreas Rehfeldt
 * @version 1.0 (03.07.2012)
 */
public enum WeatherParameterEnum {

	/**
	 * Air pressure. Unit: hPa
	 */
	AIR_PRESSURE(AirPressure.class, Integer.class, false, false, "hPa"),

	/**
	 * Light intensity. Unit: Lux
	 */
	LIGHT_INTENSITY(LightIntensity.class, Integer.class, false, true, "Lux"),

	/**
	 * Perceived temperature. Unit: celsius
	 */
	PERCEIVED_TEMPERATURE(PerceivedTemperature.class, Float.class, false, true, "°C"),

	/**
	 * Precipitation amout. Unit: mm
	 */
	PRECIPITATION_AMOUNT(PrecipitationAmount.class, Float.class, false, true, "mm"),

	/**
	 * Radiation. Unit: W/qm
	 */
	RADIATION(Radiation.class, Integer.class, false, true, "W/qm"),

	/**
	 * Relativ humidity. Unit: %
	 */
	RELATIV_HUMIDITY(RelativHumidity.class, Float.class, false, true, "%"),

	/**
	 * Temperature. Unit: celsius
	 */
	TEMPERATURE(Temperature.class, Float.class, false, true, "°C"),

	/**
	 * Wind direction. Unit: degree
	 */
	WIND_DIRECTION(WindDirection.class, Integer.class, false, false, "°"),

	/**
	 * Wind strength. Unit: Beaufort scale
	 */
	WIND_STRENGTH(WindStrength.class, Integer.class, true, false, "B."),

	/**
	 * Wind velocity. Unit: m/s
	 */
	WIND_VELOCITY(WindVelocity.class, Float.class, false, true, "m/s");

	/**
	 * Option to cache the parameter
	 */
	private boolean cachedParameter;

	/**
	 * Option if the value changes per vector unit
	 */
	private boolean changePerVectorUnit;

	/**
	 * Return type
	 */
	private Class<? extends Number> returnType;

	/**
	 * Parameter class
	 */
	private Class<? extends WeatherParameterBase> valueType;

	/**
	 * Unit
	 */
	private String unit;

	/**
	 * Constructor
	 * 
	 * @param valuetype
	 *            Parameter class
	 * @param returntype
	 *            Return type
	 * @param cache
	 *            Option to cache the parameter
	 * @param change
	 *            Option if the value changes per vector unit
	 * @param unit
	 *            Unit
	 */
	WeatherParameterEnum(Class<? extends WeatherParameterBase> valuetype, Class<? extends Number> returntype,
			boolean cache, boolean change, String unit) {
		this.valueType = valuetype;
		this.returnType = returntype;
		this.cachedParameter = cache;
		this.changePerVectorUnit = change;
		this.unit = unit;
	}

	public Class<? extends Number> getReturnType() {
		return this.returnType;
	}

	public String getUnit() {
		return this.unit;
	}

	public Class<? extends WeatherParameterBase> getValueType() {
		return this.valueType;
	}

	public boolean isCachedParameter() {
		return this.cachedParameter;
	}

	public boolean isChangePerVectorUnit() {
		return this.changePerVectorUnit;
	}

	public void setCachedParameter(boolean cachedParameter) {
		this.cachedParameter = cachedParameter;
	}

	public void setChangePerVectorUnit(boolean changePerVectorUnit) {
		this.changePerVectorUnit = changePerVectorUnit;
	}

	public void setReturnType(Class<? extends Number> type) {
		this.returnType = type;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public void setValueType(Class<? extends WeatherParameterBase> type) {
		this.valueType = type;
	}

}
