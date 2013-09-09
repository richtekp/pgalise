/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.weather.internal.dataloader.entity;

import de.pgalise.simulation.shared.persistence.AbstractIdentifiable;
import de.pgalise.simulation.weather.model.Condition;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Entity;

/**
 *
 * @author richter
 */
@Entity
public class DefaultCondition extends AbstractIdentifiable implements Condition {
	private final static Map<Integer,DefaultCondition> CODE_CONDITION_MAP = new HashMap<>(
		16);
	private static final long serialVersionUID = 1L;
	/**
	 * factory method for <tt>DefaultCondition</tt>. Lazily initializes a <tt>DefaultCondition</tt> for the specified code with the specified string representation. If the method is invoked twice with the same code, but different string representations the instance with the first string representation is returned. In fact, the string representation can never be changed once created. 
	 * @param code
	 * @param stringRepresentation
	 * @return 
	 */
	public static DefaultCondition retrieveCondition(Integer code, String stringRepresentation) {
		DefaultCondition condition = CODE_CONDITION_MAP.get(code);
		if(condition == null) {
			condition = new DefaultCondition(code, stringRepresentation);
			CODE_CONDITION_MAP.put(code,
				condition);
		}
		return condition;
	}
	public final static Condition UNKNOWN_CONDITION = retrieveCondition(
		UNKNOWN_CONDITION_CODE,
		"unknown condition");
	public static DefaultCondition retrieveCondition(int code) {
		DefaultCondition condition = CODE_CONDITION_MAP.get(code);
		if(condition == null) {
			condition = new DefaultCondition(code,
				"");
			CODE_CONDITION_MAP.put(code,
				condition);
		}
		return condition;
	}
	
	private int code;
	private String stringRepresentation;

	protected DefaultCondition() {
	}

	private DefaultCondition(int code,
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

	@Override
	public String getStringRepresentation() {
		return stringRepresentation;
	}

	@Override
	public int getCode() {
		return code;
	}
	
}
