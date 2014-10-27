/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.entity;

import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.entity.Identifiable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class WeatherCondition extends Identifiable {

  private final static Map<Integer, WeatherCondition> CODE_CONDITION_MAP = new HashMap<>(
    16);
  /**
   * Condition code for unknown weather condition
   */
  public static final int UNKNOWN_CONDITION_CODE = 3200;
  private static final long serialVersionUID = 1L;

  /**
   * factory method for <tt>DefaultCondition</tt>. Lazily initializes a
   * <tt>DefaultCondition</tt> for the specified code with the specified string
   * representation. If the method is invoked twice with the same code, but
   * different string representations the instance with the first string
   * representation is returned. In fact, the string representation can never be
   * changed once created.
   *
   * @param idGenerator
   * @param code
   * @param stringRepresentation
   * @return
   */
  public static WeatherCondition retrieveCondition(IdGenerator idGenerator,
    Integer code,
    String stringRepresentation) {
    WeatherCondition condition = CODE_CONDITION_MAP.get(code);
    if (condition == null) {
      condition = new WeatherCondition(idGenerator.getNextId(),
        code,
        stringRepresentation);
      CODE_CONDITION_MAP.put(code,
        condition);
    }
    return condition;
  }

  /**
   *
   * @param idGenerator
   * @param code
   * @return
   */
  public static WeatherCondition retrieveCondition(IdGenerator idGenerator,
    int code) {
    WeatherCondition condition = CODE_CONDITION_MAP.get(code);
    if (condition == null) {
      condition = new WeatherCondition(idGenerator.getNextId(),
        code,
        "");
      CODE_CONDITION_MAP.put(code,
        condition);
    }
    return condition;
  }

  private int code;
  private String stringRepresentation;

  protected WeatherCondition() {
  }

  private WeatherCondition(Long id,
    int code,
    String stringRepresentation
  ) {
    super(id);
    this.code = code;
    this.stringRepresentation = stringRepresentation;
  }

  protected void setStringRepresentation(String stringRepresentation) {
    this.stringRepresentation = stringRepresentation;
  }

  protected void setCode(int code) {
    this.code = code;
  }

  public String getStringRepresentation() {
    return stringRepresentation;
  }

  public int getCode() {
    return code;
  }

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 31 * hash + this.code;
		hash = 31 * hash + Objects.hashCode(this.stringRepresentation);
		return hash;
	}
	
	protected boolean equalsTransitive(WeatherCondition other) {
		if (this.code != other.code) {
			return false;
		}
		if (!Objects.equals(this.stringRepresentation,
			other.stringRepresentation)) {
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
		final WeatherCondition other = (WeatherCondition) obj;
		return equalsTransitive(other);
	}

}
