/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.internal.message;

/**
 *
 * @author richter
 */
public class IdentifiableControlCenterMessage<T> extends ControlCenterMessage<T> {
	private Long id;

	public IdentifiableControlCenterMessage(Long id,
		T content) {
		super(
			content);
		this.id = id;
	}

	public IdentifiableControlCenterMessage(Long id,
		MessageTypeEnum messageType,
		T content) {
		super(messageType,
			content);
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}
}
