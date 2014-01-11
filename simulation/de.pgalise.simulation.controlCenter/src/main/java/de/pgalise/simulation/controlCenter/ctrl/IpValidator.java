/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("IpValidator")
public class IpValidator implements Validator {

	private static final String IP_ADDRESS_PATTERN
		= "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
		+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
		+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
		+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	@Override
	public void validate(FacesContext context,
		UIComponent component,
		Object value) {
		if (!((String) value).matches(IP_ADDRESS_PATTERN)) {
			throw new ValidatorException(
				new FacesMessage("IP address does not valid!"));
		}
	}
}
