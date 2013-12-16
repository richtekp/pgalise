/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.service.internal;

import com.hazelcast.core.AtomicNumber;
import com.hazelcast.impl.AtomicNumberProxyImpl;
import de.pgalise.simulation.service.IdGenerator;

/**
 *
 * @author richter
 */
public class HazelcastIdGenerator implements IdGenerator {
	
	private final AtomicNumber distributedAtomicNumber;

	public HazelcastIdGenerator() {
		throw new UnsupportedOperationException();
		//distributedAtomicNumber = new AtomicNumberProxyImpl();
	}

	@Override
	public Long getNextId() {
		return distributedAtomicNumber.incrementAndGet();
	}
}
