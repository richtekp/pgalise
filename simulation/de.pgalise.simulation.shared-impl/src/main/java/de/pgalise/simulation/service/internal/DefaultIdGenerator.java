/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.service.internal;

import com.hazelcast.core.AtomicNumber;
import de.pgalise.simulation.service.IdGenerator;
import javax.ejb.Singleton;
import javax.ejb.Stateful;

/**
 *
 * @author richter
 */
@Singleton
public class DefaultIdGenerator implements IdGenerator {
	private AtomicNumber distributedAtomicNumber;

	@Override
	public Long getNextId() {
		return distributedAtomicNumber.incrementAndGet();
	}
	
}
