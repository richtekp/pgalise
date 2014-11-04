///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package de.pgalise.simulation.controlCenter.model;
//
//import de.pgalise.simulation.shared.city.CityInfrastructureData;
//import de.pgalise.simulation.traffic.OSMCityInfrastructureData;
//import de.pgalise.simulation.traffic.TrafficGraph;
//import de.pgalise.simulation.traffic.TrafficInfrastructureData;
//import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;
//import java.io.FileInputStream;
//import java.io.InputStream;
//
///**
// *
// * @author richter
// */
//public class OsmFileGeoDataSource implements GeoDataSource {
//	private MapAndBusstopFileData mapAndBusstopFileData;
//	private BuildingEnergyProfileStrategy buildingEnergyProfileStrategy;
//	private TrafficGraph trafficGraph;
//
//	@Override
//	public TrafficInfrastructureData generateInfrastructureData() {
//		InputStream osmFileInputStream = new FileInputStream(mapAndBusstopFileData.getOsmFileName());
//		InputStream busStopInputStream = new FileInputStream(mapAndBusstopFileData.getBusStopFileName());
//		TrafficInfrastructureData retValue = new OSMCityInfrastructureData(osmFileInputStream,
//			busStopInputStream,
//			buildingEnergyProfileStrategy,
//			null)
//	}
//	
//}
