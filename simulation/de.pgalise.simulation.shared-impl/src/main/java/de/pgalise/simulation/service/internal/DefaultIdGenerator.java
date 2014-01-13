/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.service.internal;

import de.pgalise.simulation.service.IdGenerator;
import java.util.concurrent.atomic.AtomicLong;
import javax.ejb.Local;
import javax.ejb.Singleton;

/**
 *
 * @author richter
 */
@Singleton
@Local
public class DefaultIdGenerator implements IdGenerator {

	private AtomicLong atomicLong = new AtomicLong(0);

	@Override
	public Long getNextId() {
		return atomicLong.incrementAndGet();
	}

}
