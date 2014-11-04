/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListenerAdapter;
import org.apache.commons.fileupload.util.Streams;

/**
 *
 * @author richter
 */
/*
transformation between files and InputStream can't take place here because 
there's no event for getting element, so elements can't be manipulated at 
retrieval. Therefore implementation of transformation should take place as a 
whole in the StreamedFileCache class.
*/
public class StreamCacheEventListener extends CacheEventListenerAdapter {

  private File fileDataDir;

  public StreamCacheEventListener(File fileDataDir) {
    this.fileDataDir = fileDataDir;
  }

  @Override
  public void notifyElementEvicted(Ehcache cache,
    Element element) {
    if (!(element.getObjectKey() instanceof String)) {
      throw new IllegalArgumentException();
    }
    String key = (String) element.getObjectKey();
    File file = new File(fileDataDir,
      key);
    if (!file.delete()) {
      throw new RuntimeException(String.format("file %s could not be deleted",
        file.getAbsolutePath()));
    }
  }

}
