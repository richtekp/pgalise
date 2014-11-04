/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.UUID;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author richter
 */
public class StreamedFileCacheTest {
  
  public StreamedFileCacheTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
  }
  
  @AfterClass
  public static void tearDownClass() {
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }
  
  @Test
  public void testInit() throws IOException {
    try {
      new StreamedFileCache(null, UUID.randomUUID().toString());
      Assert.fail("fileDataDir == null not allowed");
    }catch(IllegalArgumentException ex) {
      //expected
    }
    File tmpDir = File.createTempFile("pgalise-test", null);
    tmpDir.delete();
    tmpDir.mkdir();
    try {
      new StreamedFileCache(tmpDir, null);
      Assert.fail("cacheName == null not allowed");
    }catch(IllegalArgumentException ex) {
      //expected
    }
    File tmpFile = File.createTempFile("pgalise-test", null);
    try {
      new StreamedFileCache(tmpFile, null);
      Assert.fail("fileDataDir file not allowed");
    }catch(IllegalArgumentException ex ) {
      //expected
    }
    new StreamedFileCache(tmpDir, "someName");
  }

  /**
   * Test of get method, of class StreamedFileCache.
   */
  @Test
  public void testGet() throws IOException {
    System.out.println("get");
    File tmpDir = File.createTempFile("pgalise-test", null);    
    tmpDir.delete();
    tmpDir.mkdir();
    StreamedFileCache instance = new StreamedFileCache(tmpDir, UUID.randomUUID().toString());
    String key = null;
    try {
      instance.get(key);
      Assert.fail("excpetion expected");
    }catch(IllegalArgumentException ex) {
      //expected
    }
    instance.put("a", null);
    instance.put("b", new ByteArrayInputStream(new byte[] {21}));
    instance.put("c", null);
    Assert.assertNull(instance.get("a"));
    Assert.assertNull(instance.get("c"));
    assertEquals(21, instance.get("b").read());
  }

  /**
   * Test of put method, of class StreamedFileCache.
   */
  @Test
  public void testPut() throws IOException {
    System.out.println("put");
    File tmpDir = File.createTempFile("pgalise-test", null);    
    tmpDir.delete();
    tmpDir.mkdir();
    StreamedFileCache instance = new StreamedFileCache(tmpDir, UUID.randomUUID().toString());
    String key = null;
    try {
      instance.put(key, new ByteArrayInputStream(new byte[] {21}));
      Assert.fail("excpetion expected");
    }catch(IllegalArgumentException ex) {
      //expected
    }
    //test null value (allowed)
    key = "someFileName";
    InputStream dataStream = null;
    instance.put(key, dataStream);
    //test value != null
    key = "someFileName";
    dataStream = new ByteArrayInputStream(new byte[] {17});
    instance.put(key, dataStream);
    InputStream result = instance.get(key);
    Assert.assertEquals(result.read(), 17);
  }

  /**
   * Test of getKeys method, of class StreamedFileCache.
   */
  @Test
  public void testGetKeys() throws IOException {
    System.out.println("getKeys");
    File tmpDir = File.createTempFile("pgalise-test", null);    
    tmpDir.delete();
    tmpDir.mkdir();
    StreamedFileCache instance = new StreamedFileCache(tmpDir, UUID.randomUUID().toString());
    instance.put("a", null);
    instance.put("b", new ByteArrayInputStream(new byte[] {21}));
    instance.put("c", null);
    Assert.assertEquals(new LinkedList<>(Arrays.asList("a","b","c")), instance.getKeys());
  }

  /**
   * Test of getSize method, of class StreamedFileCache.
   */
  @Test
  public void testGetSize() throws IOException {
    System.out.println("getSize");
    File tmpDir = File.createTempFile("pgalise-test", null);    
    tmpDir.delete();
    tmpDir.mkdir();
    StreamedFileCache instance = new StreamedFileCache(tmpDir, UUID.randomUUID().toString());
    instance.put("a", null);
    instance.put("b", new ByteArrayInputStream(new byte[] {21}));
    instance.put("c", null);
    Assert.assertEquals(3, instance.getSize());
  }
  
}
