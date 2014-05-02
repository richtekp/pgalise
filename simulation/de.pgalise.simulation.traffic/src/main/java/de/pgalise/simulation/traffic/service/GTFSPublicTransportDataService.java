/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.traffic.service;

import de.pgalise.simulation.traffic.entity.gtfs.GTFSBusAgency;
import de.pgalise.simulation.traffic.entity.gtfs.GTFSBusCalendar;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.zip.ZipInputStream;

/**
 *
 * @author richter
 */
public interface GTFSPublicTransportDataService extends PublicTransportDataService<ZipInputStream> {

  Set<GTFSBusAgency> getBusAgencys();

  Set<GTFSBusCalendar> getBusCalendars();

  @SuppressWarnings(value = "NestedAssignment")
  void parseFiles(InputStream agency_txt, InputStream calendar_txt, InputStream routes_txt, InputStream stops_txt, InputStream trips_txt, InputStream stop_times_txt) throws UnsupportedEncodingException, IOException;
  
}
