/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.persistence;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

/**
 *
 * @author richter
 */
public class AbstractIdentifiableTest {
	
	public AbstractIdentifiableTest() {
	}

	/**
	 * Test of getId method, of class AbstractIdentifiable.
	 * 
	 * @throws InterruptedException 
	 */
	@Test
	public void testInit() throws InterruptedException {
		AbstractIdentifiable instance = new AbstractIdentifiableImpl();
		final int count = 500;
		int threadCount = 20;
		final Set<Long> usedIds = new HashSet<>();
		Queue<Callable<Void>> threads = new LinkedList<>();
		for(int i=0; i<threadCount; i++) {
			Callable<Void> thread = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					for(int i=0; i<count; i++) {
						Identifiable identifiable = new AbstractIdentifiableImpl();
						usedIds.add(identifiable.getId());
					}
					return null;
				}
			};
			threads.add(thread);
		}
		//start in extra loop in order to avoid the time gap between the object allocations
		ExecutorService executorService = Executors.newCachedThreadPool();
		executorService.invokeAll(threads);
		executorService.shutdown();
//			throw new UnsupportedOperationException("adjust the timeout for this unit test because it shouldn't take longer than 5 seconds");
//		}
	}

	@SuppressWarnings("serial")
	private class AbstractIdentifiableImpl extends AbstractIdentifiable {
	}
}