/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;
import net.sf.ehcache.loader.CacheLoader;
import net.sf.ehcache.loader.CacheLoaderFactory;

/**
 *
 * @author richter
 */
public class StreamCacheLoaderFactory extends CacheLoaderFactory {

  /**
   * Parameterless constructor is required by implementation (either specified 
   * or a bug)
   */
  public StreamCacheLoaderFactory() {
  }

  @Override
  public CacheLoader createCacheLoader(Ehcache cache,
    Properties properties) {
    final String fileDataDir = properties.getProperty("fileDataDir");
    return new CacheLoader() {
      @Override
      public Object load(Object key) throws CacheException {
        if (!(key instanceof String)) {
          throw new IllegalArgumentException();
        }
        String keyCast = (String) key;
        try {
          return new FileInputStream(new File(fileDataDir,
            keyCast));
        } catch (FileNotFoundException ex) {
          throw new RuntimeException(ex);
        }
      }

      @Override
      public Map loadAll(Collection keys) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }

      @Override
      public Object load(Object key,
        Object argument) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }

      @Override
      public Map loadAll(Collection keys,
        Object argument) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }

      @Override
      public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }

      @Override
      public CacheLoader clone(Ehcache cache) throws CloneNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }

      @Override
      public void init() {

      }

      @Override
      public void dispose() throws CacheException {

      }

      @Override
      public Status getStatus() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
      }
    };
  }

}
