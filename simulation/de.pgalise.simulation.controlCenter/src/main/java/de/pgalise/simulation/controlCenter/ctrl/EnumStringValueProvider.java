package de.pgalise.simulation.controlCenter.ctrl;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import de.pgalise.simulation.controlCenter.StringValueType;
import java.util.LinkedList;
import java.util.List;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 *
 * @author richter
 */
@ManagedBean
@ApplicationScoped
public class EnumStringValueProvider {
	
	public List<String> retrieveStringValues(List<? extends StringValueType> stringValueTypes) {
		List<String> retValue = new LinkedList<>();
		for(StringValueType stringValueType : stringValueTypes) {
			retValue.add(stringValueType.getStringValue());
		}
		return retValue;
	}
}
