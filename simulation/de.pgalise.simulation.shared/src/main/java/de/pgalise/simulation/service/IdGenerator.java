/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.service;

/**
 * basically a wrapper around Hazelcast's cluster wide implementation of {@AtomicNumber}
 * @author richter
 */
public interface IdGenerator {
	Long getNextId();
}
