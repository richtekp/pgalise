/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import java.io.Serializable;
import javax.persistence.Embeddable;

/**
 * super class for interfaces which make static predefined tags and custom tags (i.e. tags with custom values; subclasses of AbstractCustomTag) interoperable
 * @author richter
 */
public interface BaseTag extends Serializable {
	String getStringValue();
}
