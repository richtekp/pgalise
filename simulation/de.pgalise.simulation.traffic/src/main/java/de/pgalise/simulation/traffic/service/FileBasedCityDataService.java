/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.traffic.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * An extension of {@link CityDataService} to parseFile one File.
 * For possibility to parseFile multiple files see
 *
 * @author richter
 */
public interface FileBasedCityDataService extends
  CityDataService {

  void parseFile(File osmFile) throws IOException;

  void parseStream(InputStream osmIN) throws IOException;

}
