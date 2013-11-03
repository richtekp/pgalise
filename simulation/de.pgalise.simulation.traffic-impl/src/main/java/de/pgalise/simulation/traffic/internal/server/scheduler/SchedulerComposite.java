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
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import de.pgalise.simulation.shared.traffic.VehicleTypeEnum;
import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficNode;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleHandler;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleItem;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleModus;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;

/**
 * A TrafficServer may use a Scheduler for each type of vehicle. 
 * The SchedulerComposite can be used to manage these Scheduler easily.
 * 
 * @param <D> 
 * @param <N> 
 * @param <E> 
 * @param <V> 
 * @param <I> 
 * @see Scheduler
 * @author mustafa
 *
 */
public class SchedulerComposite<
	D extends VehicleData,
	N extends TrafficNode<N,E,D,V>, 
	E extends TrafficEdge<N,E,D,V>, 
	V extends Vehicle<D,N,E,V>,
	I extends ScheduleItem<D,N,E,V>
> implements Scheduler<D,N,E,V,I> {
	private EnumMap<VehicleTypeEnum, Scheduler<D,N,E,V,I>> schedulerMap;
	private List<Scheduler<D,N,E,V,I>> list;

	public SchedulerComposite() {
		schedulerMap = new EnumMap<>(VehicleTypeEnum.class);
		list = new LinkedList<>();
	}

	public void addScheduler(EnumSet<VehicleTypeEnum> criteria, Scheduler<D,N,E,V,I> scheduler) {
		for (VehicleTypeEnum e : criteria) {
			schedulerMap.put(e, scheduler);
		}
		list.add(scheduler);
	}

	public Scheduler<D,N,E,V,I> getScheduler(EnumSet<VehicleTypeEnum> criteria) {
		Scheduler<D,N,E,V,I> foundScheduler = null;
		int i = 0;
		for (VehicleTypeEnum e : criteria) {
			if (i == 0) {
				foundScheduler = schedulerMap.get(e);
			}

			if (foundScheduler != schedulerMap.get(e)) {
				return null;
			}
			i++;
		}

		return foundScheduler;
	}

	public Scheduler<D,N,E,V,I> getScheduler(VehicleTypeEnum type) {
		return schedulerMap.get(type);
	}

	@Override
	public void onScheduleItem(I item) {
		try {
			schedulerMap.get(item.getVehicle().getData().getType()).scheduleItem(item);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<I> getExpiredItems(long currentTime) {
		List<I> items = new ArrayList<>();
		for (Scheduler<D,N,E,V,I> s : list) {
			items.addAll(s.getExpiredItems(currentTime));
		}
		return items;
	}

	@Override
	public List<I> getScheduledItems() {
		List<I> items = new ArrayList<>();
		for (Scheduler<D,N,E,V,I> s : list) {
			items.addAll(s.getScheduledItems());
		}
		return items;
	}

	@Override
	public void onRemoveScheduledItems(List<V> vehicles) {
		for (Scheduler<D,N,E,V,I> s : list) {
			try {
				s.removeScheduledItems(vehicles);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClearScheduledItems() {
		for (Scheduler<D,N,E,V,I> s : list) {
			try {
				s.clearScheduledItems();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onRemoveExpiredItems(List<V> vehicles) {
		for (Scheduler<D,N,E,V,I> s : list) {
			try {
				s.removeExpiredItems(vehicles);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClearExpiredItems() {
		for (Scheduler<D,N,E,V,I> s : list) {
			try {
				s.clearExpiredItems();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onAddScheduleHandler(ScheduleHandler<D,N,E,V,I> handler) {
		for (Scheduler<D,N,E,V,I> s : list) {
			try {
				s.addScheduleHandler(handler);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onRemoveScheduleHandler(ScheduleHandler<D,N,E,V,I> handler) {
		for (Scheduler<D,N,E,V,I> s : list) {
			try {
				s.removeScheduleHandler(handler);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onRemoveAllHandler() {
		for (Scheduler<D,N,E,V,I> s : list) {
			try {
				s.removeAllHandler();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void changeModus(ScheduleModus modus) {
		super.changeModus(modus);
		for (Scheduler<D,N,E,V,I> s : list) {
			s.changeModus(modus);
		}
	}
}
