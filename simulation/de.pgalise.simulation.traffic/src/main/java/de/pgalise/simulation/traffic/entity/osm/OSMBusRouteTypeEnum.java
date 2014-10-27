/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.entity.osm;

/**
 * OSM allows three types of bus routes (bus, trolleybus and shared_taxi).
 * See http://wiki.openstreetmap.org/wiki/Tag:type%3Droute#Bus_routes_.28also_trolley_bus.29
 * @author richter
 */
public enum OSMBusRouteTypeEnum {
  BUS, TROLLEY_BUS, SHARED_TAXI;
}
