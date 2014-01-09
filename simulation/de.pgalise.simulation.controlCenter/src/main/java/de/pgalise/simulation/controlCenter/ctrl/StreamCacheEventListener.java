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
public class StreamCacheEventListener extends CacheEventListenerAdapter {

	@Override
	public void notifyElementEvicted(Ehcache cache,
		Element element) {
		if (!(element.getObjectKey() instanceof String)) {
			throw new IllegalArgumentException();
		}
		String key = (String) element.getObjectKey();
		File file = new File(MainCtrlUtils.CACHE_DATA_DIR,
			key);
		if (!file.delete()) {
			throw new RuntimeException(String.format("file %s could not be deleted",
				file.getAbsolutePath()));
		}
	}

	@Override
	public void notifyElementPut(Ehcache cache,
		Element element) throws CacheException {
		if (!(element.getObjectKey() instanceof String)) {
			throw new IllegalArgumentException();
		}
		if (!(element.getObjectValue() instanceof InputStream)) {
			throw new IllegalArgumentException();
		}
		String key = (String) element.getObjectKey();
		InputStream inputStream = (InputStream) element.getObjectValue();
		File file = new File(MainCtrlUtils.CACHE_DATA_DIR,
			key);
		try {
			file.createNewFile(); //returns false if the file already exists
			Streams.copy(inputStream,
				new FileOutputStream(file),
				true);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

}
