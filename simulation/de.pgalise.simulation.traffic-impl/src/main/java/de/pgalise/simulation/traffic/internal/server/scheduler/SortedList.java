/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.internal.server.scheduler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Sorted list for {@link Item}
 *
 * @author Andreas Rehfeldt
 * @version 1.0 (Feb 16, 2013)
 */
class SortedList<E> implements List<E>, Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * Internal list
	 */
	private final ArrayList<E> internalList = new ArrayList<>();

	@Override
	public void add(int position,
		E e) {
		internalList.add(e);
		Collections.sort(internalList,
			null);
	}

	@Override
	public E get(int i) {
		return internalList.get(i);
	}

	@Override
	public int size() {
		return internalList.size();
	}

	@Override
	public Iterator<E> iterator() {
		return internalList.iterator();
	}

	@Override
	public boolean isEmpty() {
		return internalList.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return internalList.contains(o);
	}

	@Override
	public Object[] toArray() {
		return internalList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return internalList.toArray(a);
	}

	@Override
	public boolean add(E e) {
		return internalList.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return internalList.remove(o);
	}

	@Override
	public boolean containsAll(
		Collection<?> c) {
		return internalList.containsAll(c);
	}

	@Override
	public boolean addAll(
		Collection<? extends E> c) {
		return internalList.addAll(c);
	}

	@Override
	public boolean addAll(int index,
		Collection<? extends E> c) {
		return internalList.addAll(index,
			c);
	}

	@Override
	public boolean removeAll(
		Collection<?> c) {
		return internalList.removeAll(c);
	}

	@Override
	public boolean retainAll(
		Collection<?> c) {
		return internalList.retainAll(c);
	}

	@Override
	public void clear() {
		internalList.clear();
	}

	@Override
	public E set(int index,
		E element) {
		return internalList.set(index,
			element);
	}

	@Override
	public E remove(int index) {
		return internalList.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return internalList.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return internalList.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return internalList.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return internalList.listIterator(index);
	}

	@Override
	public List<E> subList(int fromIndex,
		int toIndex) {
		return internalList.subList(fromIndex,
			toIndex);
	}

}
