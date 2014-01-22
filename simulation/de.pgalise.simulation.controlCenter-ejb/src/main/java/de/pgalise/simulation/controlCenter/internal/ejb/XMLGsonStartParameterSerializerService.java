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
package de.pgalise.simulation.controlCenter.internal.ejb;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;

import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import de.pgalise.simulation.controlCenter.internal.util.service.StartParameterSerializerService;

import de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter;
import de.pgalise.simulation.service.GsonService;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;

/**
 * A XML control center startParameter serializer service using gson
 * serialization in order to avoid duplicate mappings.
 *
 * Does nothing but throw UnsupportedoperationException due to
 * StackOverflowError in gson.
 *
 * @author Timo
 */
public class XMLGsonStartParameterSerializerService implements
  StartParameterSerializerService {

  private static final Logger log = LoggerFactory.getLogger(
    XMLGsonStartParameterSerializerService.class);
  private final static String FILE_PATH;

  static {
    if (System.getProperty("os.name").toLowerCase().contains("win")) {
      FILE_PATH = Thread.currentThread().getContextClassLoader().getResource("").
        getPath().substring(1).replaceAll("WEB-INF/classes",
          "");
    } else {
      FILE_PATH = Thread.currentThread().getContextClassLoader().getResource("").
        getPath().replaceAll("WEB-INF/classes",
          "");
    }
  }

  private Gson gson;
  @EJB
  private GsonService gsonService;
  private XMLSerializer xmlSerializer;

  /**
   * Default
   */
  public XMLGsonStartParameterSerializerService() {
    this.xmlSerializer = new XMLSerializer();
  }

  @PostConstruct
  public void initialize() {
    this.gson = gsonService.getGson();
  }

  @Override
  public ControlCenterStartParameter deserialize(InputStream inputStream) {
    if (this.gson == null) {
      throw new IllegalStateException(
        "Gson is null. Service must be initialized!");
    }
    return this.gson.fromJson(this.xmlSerializer.readFromStream(inputStream).
      toString(),
      ControlCenterStartParameter.class);
  }

  @Override
  public String serialize(ControlCenterStartParameter cCSimulationStartParameter,
    String fileName) {
    String filePath = XMLGsonStartParameterSerializerService.FILE_PATH + fileName;
    if (new File(filePath).exists()) {
      throw new RuntimeException("file name: " + fileName + " already in use!");
    }

    PrintWriter pw = null;

    try {
      pw = new PrintWriter(filePath,
        "UTF-8");
    } catch (FileNotFoundException | UnsupportedEncodingException e) {
      log.error("Exception",
        e);
      throw new RuntimeException(e);
    } finally {
      if (pw != null) {
        try {
          pw.flush();
          pw.close();
        } catch (Exception e) {
          log.error("Exception",
            e);
        }
      }
    }

    return filePath.replaceAll(XMLGsonStartParameterSerializerService.FILE_PATH,
      "");
  }

  @Override
  public ControlCenterStartParameter deserialize(String content) {
    return this.gson.fromJson(this.xmlSerializer.read(content).toString(),
      ControlCenterStartParameter.class);
  }

  @Override
  public void serialize(
    ControlCenterStartParameter controlCenterStartParameter,
    OutputStream outputStream) {
    if (this.gson == null) {
      throw new IllegalStateException(
        "Gson is null. Service must be initialized!");
    }
    if (outputStream == null) {
      throw new IllegalArgumentException("outputStream mustn't be null");
    }
    try {
      outputStream.write(xmlSerializer.write(JSONSerializer.toJSON(this.gson.
        toJson(controlCenterStartParameter))).getBytes());
    } catch (Exception e) {
      log.error("Exception",
        e);
      throw new RuntimeException(e);
    } finally {
      try {
        outputStream.flush();
        outputStream.close();
      } catch (Exception e) {
        log.error("Exception",
          e);
      }
    }
  }
}
