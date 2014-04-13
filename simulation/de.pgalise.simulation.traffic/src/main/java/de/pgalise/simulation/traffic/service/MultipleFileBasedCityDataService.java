/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 *
 * @author richter
 */
public interface MultipleFileBasedCityDataService extends
  CityDataService {

  void parseFiles(Set<File> osmFile) throws IOException;

  void parseStreams(Set<InputStream> osmIN) throws IOException;
}
