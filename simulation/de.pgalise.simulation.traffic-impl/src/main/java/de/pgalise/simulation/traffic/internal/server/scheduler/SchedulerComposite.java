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
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleHandler;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleItem;
import de.pgalise.simulation.traffic.server.scheduler.ScheduleModus;
import de.pgalise.simulation.traffic.server.scheduler.Scheduler;

/**
 * A TrafficServer may use a Scheduler for each type of vehicle. The
 * SchedulerComposite can be used to manage these Scheduler easily.
 *
 * @param <D>
 * @see Scheduler
 * @author mustafa
 *
 */
public class SchedulerComposite<
	D extends VehicleData> extends BaseScheduler {

	private static final long serialVersionUID = 1L;
	private EnumMap<VehicleTypeEnum, Scheduler> schedulerMap;
	private List<Scheduler> list;

	public SchedulerComposite() {
		schedulerMap = new EnumMap<>(VehicleTypeEnum.class);
		list = new LinkedList<>();
	}

	public void addScheduler(EnumSet<VehicleTypeEnum> criteria,
		Scheduler scheduler) {
		for (VehicleTypeEnum e : criteria) {
			schedulerMap.put(e,
				scheduler);
		}
		list.add(scheduler);
	}

	public Scheduler getScheduler(EnumSet<VehicleTypeEnum> criteria) {
		Scheduler foundScheduler = null;
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

	public Scheduler getScheduler(VehicleTypeEnum type) {
		return schedulerMap.get(type);
	}

	@Override
	public void onScheduleItem(ScheduleItem item) {
		try {
			schedulerMap.get(item.getVehicle().getData().getType()).scheduleItem(item);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<ScheduleItem> getExpiredItems(long currentTime) {
		List<ScheduleItem> items = new ArrayList<>();
		for (Scheduler s : list) {
			items.addAll(s.getExpiredItems(currentTime));
		}
		return items;
	}

	@Override
	public List<ScheduleItem> getScheduledItems() {
		List<ScheduleItem> items = new ArrayList<>();
		for (Scheduler s : list) {
			items.addAll(s.getScheduledItems());
		}
		return items;
	}

	@Override
	public void onRemoveScheduledItems(List<Vehicle<?>> vehicles) {
		for (Scheduler s : list) {
			try {
				s.removeScheduledItems(vehicles);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClearScheduledItems() {
		for (Scheduler s : list) {
			try {
				s.clearScheduledItems();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onRemoveExpiredItems(List<Vehicle<?>> vehicles) {
		for (Scheduler s : list) {
			try {
				s.removeExpiredItems(vehicles);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClearExpiredItems() {
		for (Scheduler s : list) {
			try {
				s.clearExpiredItems();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onAddScheduleHandler(ScheduleHandler handler) {
		for (Scheduler s : list) {
			try {
				s.addScheduleHandler(handler);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onRemoveScheduleHandler(ScheduleHandler handler) {
		for (Scheduler s : list) {
			s.removeScheduleHandler(handler);
		}
	}

	@Override
	public void onRemoveAllHandler() {
		for (Scheduler s : list) {
			try {
				s.removeAllHandler();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void changeModus(ScheduleModus modus) {
		super.changeModus(modus);
		for (Scheduler s : list) {
			s.changeModus(modus);
		}
	}
}
