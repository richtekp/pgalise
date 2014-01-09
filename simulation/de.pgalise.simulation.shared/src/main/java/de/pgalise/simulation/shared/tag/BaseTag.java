/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.tag;

import java.io.Serializable;

/**
 * super class for interfaces which make static predefined tags and custom tags (i.e. tags with custom values; subclasses of AbstractCustomTag) interoperable
 * @author richter
 */
public interface BaseTag extends Serializable {
	String getStringValue();
}
