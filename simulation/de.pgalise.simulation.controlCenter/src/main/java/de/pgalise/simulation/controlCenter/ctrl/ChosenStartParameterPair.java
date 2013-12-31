/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter;
import javax.faces.bean.ManagedBean;
import org.apache.commons.lang3.tuple.MutablePair;

/**
 *
 * @author richter
 */
@ManagedBean
public class ChosenStartParameterPair extends MutablePair<String, ControlCenterStartParameter> {
	private static final long serialVersionUID = 1L;

	public ChosenStartParameterPair() {
	}

	@Override
	public void setRight(ControlCenterStartParameter right) {
		super.setRight(right); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setLeft(String left) {
		super.setLeft(left); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ControlCenterStartParameter getRight() {
		return super.getRight(); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String getLeft() {
		return super.getLeft(); //To change body of generated methods, choose Tools | Templates.
	}

	
}
