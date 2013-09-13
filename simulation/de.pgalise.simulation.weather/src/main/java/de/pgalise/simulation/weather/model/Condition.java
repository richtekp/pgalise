/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.model;

import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class Condition extends AbstractIdentifiable  {
	/**
	 * Condition code for unknown weather condition
	 */
	public static final int UNKNOWN_CONDITION_CODE = 3200;
	private final static Map<Integer,Condition> CODE_CONDITION_MAP = new HashMap<>(
		16);
	private static final long serialVersionUID = 1L;
	/**
	 * factory method for <tt>DefaultCondition</tt>. Lazily initializes a <tt>DefaultCondition</tt> for the specified code with the specified string representation. If the method is invoked twice with the same code, but different string representations the instance with the first string representation is returned. In fact, the string representation can never be changed once created. 
	 * @param code
	 * @param stringRepresentation
	 * @return 
	 */
	public static Condition retrieveCondition(Integer code, String stringRepresentation) {
		Condition condition = CODE_CONDITION_MAP.get(code);
		if(condition == null) {
			condition = new Condition(code, stringRepresentation);
			CODE_CONDITION_MAP.put(code,
				condition);
		}
		return condition;
	}
	public final static Condition UNKNOWN_CONDITION = retrieveCondition(
		UNKNOWN_CONDITION_CODE,
		"unknown condition");
	public static Condition retrieveCondition(int code) {
		Condition condition = CODE_CONDITION_MAP.get(code);
		if(condition == null) {
			condition = new Condition(code,
				"");
			CODE_CONDITION_MAP.put(code,
				condition);
		}
		return condition;
	}
	
	private int code;
	private String stringRepresentation;

	protected Condition() {
	}

	private Condition(int code,
		String stringRepresentation
	) {
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
	
}
