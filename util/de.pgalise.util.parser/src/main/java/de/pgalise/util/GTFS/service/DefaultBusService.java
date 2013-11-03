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
 
package de.pgalise.util.GTFS.service;

import de.pgalise.simulation.traffic.BusRoute;
import de.pgalise.simulation.traffic.BusTrip;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lena
 */
@Singleton
public class DefaultBusService implements BusService {
	private final static Logger LOGGER = LoggerFactory.getLogger(DefaultBusService.class);
	private Connection con;
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<BusTrip> getBusLineData(BusRoute busRoute, long timeInMs) {
		List<BusTrip<?,?>> trips = new ArrayList<>();
		TypedQuery<BusTrip> query = entityManager.createQuery("SELECT x FROM BusTrip x",
			BusTrip.class);
		List<BusTrip> retValue = query.getResultList();
		return retValue;
	}

	@Override
	public int getTotalNumberOfBusTrips(List<BusRoute<?>> busRoutes, long timeInMs) {
		Query query = entityManager.createQuery("SELECT COUNT(x) FROM BusTrip x");
		int retValue = (int) query.getSingleResult();
		return retValue;
	}

	@Override
	public List<BusRoute> getAllBusRoutes() {
		TypedQuery<BusRoute> query = entityManager.createQuery("SELECT x FROM BusRoute x", BusRoute.class);
		List<BusRoute> retValue = query.getResultList();
		return retValue;
		}
}
