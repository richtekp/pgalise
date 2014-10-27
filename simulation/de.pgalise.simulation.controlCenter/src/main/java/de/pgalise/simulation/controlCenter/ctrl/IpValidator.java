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
public class IpValidator extends BaseIpValidator implements Validator {
  private static final long serialVersionUID = 1L;

	@Override
	public void validate(FacesContext context,
		UIComponent component,
		Object value) {
    if(!(value instanceof String)) {
      throw new RuntimeException(String.format("value %s isn't an instance of %s", value, String.class.getName()));
    }
    String valueCast = (String) value;
		if (!validIp((valueCast))) {
      throw new ValidatorException(
				new FacesMessage("IP address does not valid!"));
    }
	}
}
