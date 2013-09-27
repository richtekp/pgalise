/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
 
package de.pgalise.simulation.traffic.internal.server.scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleStateEnum;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.scheduler.Administration;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleItem;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleHandler;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;

/**
 * Implements a scheduler for the traffic. Uses a sorted list to schedule the items.
 * 
 * @author Andreas
 * @version 1.0 (Nov 8, 2012)
 */
public class SortedListScheduler extends Scheduler {
	/**
	 * Items of the scheduler
	 */
	private List<ScheduleItem> scheduledItems;

	/**
	 * List of vehicles
	 */
	private List<ScheduleItem> vehicles;

	/**
	 * List of handler
	 */
	private List<ScheduleHandler> handlerList;

	/**
	 * Default constructor
	 */
	private SortedListScheduler() {
		this.scheduledItems = new SortedList<>();
		this.vehicles = new ArrayList<>();
		handlerList = new LinkedList<>();
	}

	/**
	 * Creates an Administration with this scheduler
	 * 
	 * @return Administration
	 */
	public static Administration createInstance() {
		return new Administration(new SortedListScheduler());
	}

	@Override
	protected void onScheduleItem(ScheduleItem item) {
		this.scheduledItems.add(item);

		// log.debug("Scheduling item with SortedListScheduler (name=" + item.getVehicle().getName() + ", time="
		// + item.getDepartureTime() + ")");

		onSchedule(item);
	}

	@Override
	public List<ScheduleItem> getExpiredItems(long currentTime) {
		List<ScheduleItem> list = new ArrayList<>();
		for (ScheduleItem item : this.scheduledItems) {
			// item has been expired
			// log.debug("Check item: " + currentTime + " >= " + item.getDepartureTime() + " " + (currentTime >=
			// item.getDepartureTime()) + " " + new Date(item.getDepartureTime()).toString());
			if (currentTime >= item.getDepartureTime()) {
				if (item.getVehicle().getVehicleState() != VehicleStateEnum.DRIVING) {
					// SortedListScheduler.log
					// .debug("Item (\"" + item.getVehicle().getName()
					// + "\") has been expired");
					// if (item.reversePath()) {
					// List<Node> path = item.getVehicle().getPath().getNodePath();
					// Collections.reverse(path);
					// List<Edge> edges = item.getVehicle().getPath().getEdgePath();
					// Collections.reverse(edges);
					// item.getVehicle().setPath(item.getVehicle().getPath());
					// }
					list.add(item);
				} else {
					// kann nicht zurückfahren, da noch gar nicht angekommen
					// abfahrzeit wird pauschal um 1 stunde erhöht
					long time = item.getDepartureTime();
					time += 1000 * 60 * 60;
					item.setDepartureTime(time);
				}
			}
		}
		this.scheduledItems.removeAll(list);
		this.vehicles.addAll(list);

		return new ArrayList<>(this.vehicles);
	}

	@Override
	public List<ScheduleItem> getScheduledItems() {
		return new ArrayList<>(this.scheduledItems);
	}

	@Override
	protected void onRemoveScheduledItems(List<Vehicle<? extends VehicleData>> vehicles) {
		for (Iterator<ScheduleItem> i = scheduledItems.iterator(); i.hasNext();) {
			ScheduleItem item = i.next();
			Vehicle<? extends VehicleData> a = item.getVehicle();
			for (Vehicle<? extends VehicleData> b : vehicles) {
				if (a.getId().equals(b.getId())) {
					i.remove();
					onRemove(item);
				}
			}
		}
	}

	@Override
	protected void onRemoveExpiredItems(List<Vehicle<? extends VehicleData>> list) {
		for (Iterator<ScheduleItem> i = vehicles.iterator(); i.hasNext();) {
			ScheduleItem item = i.next();
			Vehicle<? extends VehicleData> a = item.getVehicle();
			for (Vehicle<? extends VehicleData> b : list) {
				if (a.getId().equals(b.getId())) {
					i.remove();
					onRemove(item);
					break;
				}
			}
		}
	}

	@Override
	protected void onClearScheduledItems() {
		for (Iterator<ScheduleItem> i = this.scheduledItems.iterator(); i.hasNext();) {
			ScheduleItem item = i.next();
			i.remove();
			onRemove(item);
		}
	}

	@Override
	protected void onClearExpiredItems() {
		for (Iterator<ScheduleItem> i = this.vehicles.iterator(); i.hasNext();) {
			ScheduleItem item = i.next();
			i.remove();
			onRemove(item);
		}
	}

	@Override
	protected void onAddScheduleHandler(ScheduleHandler handler) {
		handlerList.add(handler);
	}

	@Override
	protected void onRemoveScheduleHandler(ScheduleHandler handler) {
		handlerList.remove(handler);
	}

	private void onRemove(ScheduleItem v) {
		for (ScheduleHandler s : handlerList) {
			s.onRemove(v);
		}
	}

	private void onSchedule(ScheduleItem v) {
		for (ScheduleHandler s : handlerList) {
			s.onSchedule(v);
		}
	}

	@Override
	protected void onRemoveAllHandler() {
		for (Iterator<ScheduleHandler> i = this.handlerList.iterator(); i.hasNext();) {
			i.remove();
		}
	}

	/**
	 * Sorted list for {@link Item}
	 * 
	 * @author Andreas Rehfeldt
	 * @version 1.0 (Feb 16, 2013)
	 */
	class SortedList<E> implements List<E> {

		/**
		 * Internal list
		 */
		private ArrayList<E> internalList = new ArrayList<>();

		@Override
		public void add(int position, E e) {
			internalList.add(e);
			Collections.sort(internalList, null);
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
		public boolean containsAll(Collection<?> c) {
			return internalList.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends E> c) {
			return internalList.addAll(c);
		}

		@Override
		public boolean addAll(int index, Collection<? extends E> c) {
			return internalList.addAll(index, c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return internalList.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return internalList.retainAll(c);
		}

		@Override
		public void clear() {
			internalList.clear();
		}

		@Override
		public E set(int index, E element) {
			return internalList.set(index, element);
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
		public List<E> subList(int fromIndex, int toIndex) {
			return internalList.subList(fromIndex, toIndex);
		}
	}
}
