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
public class AbstractIdentifiableCCWebSocketMessage<T> extends AbstractCCWebSocketMessage<T> implements IdentifiableCCWebSocketMessage<T>{
	private Long id;

	public AbstractIdentifiableCCWebSocketMessage(Long id,
		T content) {
		super(
			content);
		this.id = id;
	}

	public AbstractIdentifiableCCWebSocketMessage(Long id,
		MessageTypeEnum messageType,
		T content) {
		super(messageType,
			content);
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

	protected void setId(Long id) {
		this.id = id;
	}
}
