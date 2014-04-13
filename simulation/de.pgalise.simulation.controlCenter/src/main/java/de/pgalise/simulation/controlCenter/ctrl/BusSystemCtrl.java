/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import com.google.common.collect.Sets;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.MultiPoint;
import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
import de.pgalise.simulation.traffic.entity.BusRoute;
import de.pgalise.simulation.traffic.entity.BusStop;
import de.pgalise.simulation.traffic.service.PublicTransportDataService;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.validation.constraints.Size;
import net.sf.ehcache.Element;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.LatLngBounds;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Rectangle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author richter
 */
@ManagedBean
@SessionScoped
public class BusSystemCtrl implements Serializable {

  private static final long serialVersionUID = 1L;
  private final static Logger LOGGER = LoggerFactory.getLogger(BusSystemCtrl.class);

  private Set<BusRoute> busRoutes = new HashSet<>(Arrays.asList(
    new BusRoute(1L,
      "shortN",
      "longN")));
  @Size(min = 1)
  private Set<String> busStopFileNamesParsing = new HashSet<>();
  private Set<String> busStopFileNamesParsed = new HashSet<>();
  /**
   * selected bus stop file names (selected out of busStopFileNamesPared)
   */
  private Set<String> busStopFileNames = new HashSet<>();
  @EJB
  private PublicTransportDataService busStopDataService;
  private MapModel previewMapModel = new DefaultMapModel();

  /**
   * Creates a new instance of BusSystemCtrl
   */
  public BusSystemCtrl() {
  }

  public BusSystemCtrl(PublicTransportDataService busStopDataService) {
    this.busStopDataService = busStopDataService;
  }

  @PostConstruct
  public void initialize() {
    this.initializeBusStopParsing();
  }
  
  //////////////////////////////
  // event handling 
  //////////////////////////////
  /**
   * updates the preview map overlay to a union of all selected parsed bus stop 
   * files (clears existing overlay and replaces with a new one)
   * @param valueChangeEvent 
   */
  public void onBusStopFileNamesValueChange(ValueChangeEvent valueChangeEvent) throws InterruptedException, ExecutionException {
    Set<String> newBusStopFileNames = (Set<String>) valueChangeEvent.getNewValue();
    //assume that all names in newBusStopFileNames have been parsed and 
    //unified in busStops property of class
    Set<Coordinate> newBusStopFileNamesBoundaryCoordinates = new HashSet<>();
    for(BusRoute busRoute : busRoutes) {
      Collection<BusStop> busRouteBusStops = busRoute.getSchedule().keySet();
      for(BusStop busRouteBusStop : busRouteBusStops) {
        newBusStopFileNamesBoundaryCoordinates.add(busRouteBusStop);
      }
    }
    MultiPoint newBusStopFileNamesBoundaryMultipoint = GeoToolsBootstrapping.getGEOMETRY_FACTORY().createMultiPoint(newBusStopFileNamesBoundaryCoordinates.toArray(new Coordinate[newBusStopFileNamesBoundaryCoordinates.size()]));
    Envelope newBusStopFileNamesBoundaryEnvelope = newBusStopFileNamesBoundaryMultipoint.getEnvelopeInternal();
    Rectangle newBusStopFileNamesBoundaryOverlay = new Rectangle(new LatLngBounds(new LatLng(newBusStopFileNamesBoundaryEnvelope.getMinX(),newBusStopFileNamesBoundaryEnvelope.getMinY()), new LatLng(newBusStopFileNamesBoundaryEnvelope.getMaxX(),
        newBusStopFileNamesBoundaryEnvelope.getMaxY())));
    previewMapModel.getRectangles().clear();
    previewMapModel.getRectangles().add(newBusStopFileNamesBoundaryOverlay);
  }
  
  /**
   * Adds file to available files, i.e. cache, not to selected files. Files are
   * overwritten in the server side cache no matter what.
   *
   * @param event
   */
  /*
   @TODO: implement check for name with p:remoteCommand and onStart of p:fileUpload
   */
  public void onGTFSFileUpload(FileUploadEvent event) {
    try {
      MainCtrlUtils.BUS_STOP_FILE_CACHE.put(event.getFile().
        getFileName(),
        event.getFile().getInputstream());
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
  }

  //////////////////////////////
  // settet and getter
  //////////////////////////////
  public Set<String> getBusStopFileNames() {
    return busStopFileNames;
  }

  public void setBusStopFileNames(Set<String> busStopFileNames) {
    this.busStopFileNames = busStopFileNames;
  }

  public void setBusStopFileNamesParsing(
    Set<String> busStopFileNamesParsing) {
    this.busStopFileNamesParsing = busStopFileNamesParsing;
  }

  public Set<String> getBusStopFileNamesParsing() {
    return busStopFileNamesParsing;
  }

  public void setBusStopFileNamesParsed(Set<String> busStopFileNamesParsed) {
    this.busStopFileNamesParsed = busStopFileNamesParsed;
  }

  public Set<String> getBusStopFileNamesParsed() {
    return busStopFileNamesParsed;
  }

  public Set<BusRoute> getBusRoutes() {
    return busRoutes;
  }

  public void setBusRoutes(Set<BusRoute> busRoutes) {
    this.busRoutes = busRoutes;
  }

  public MapModel getPreviewMapModel() {
    return previewMapModel;
  }

  public void checkAll() {
    for (BusRoute busRoute : busRoutes) {
      busRoute.setUsed(true);
    }
  }

  public void checkNone() {
    for (BusRoute busRoute : busRoutes) {
      busRoute.setUsed(false);
    }
  }

  public void useBusRouteChange(BusRoute a) {
    a.setUsed(!a.getUsed());
  }

  public void usedChanged(AjaxBehaviorEvent event) {
//    HtmlSelectBooleanCheckbox source = (HtmlSelectBooleanCheckbox) event.
//      getSource();
    throw new UnsupportedOperationException("not implemented yet");
  }

  //////////////////////////////
  // Parsing
  //////////////////////////////
  /**
   * Scans classpath for resource names specified in 
   * {@link MainCtrlUtils#INITIAL_BUS_STOP_FILE_NAMES}
   * 
   * saves an instance of FutureTask info the parseTask property which indicates
   * that the parsing is currently running. The task is executed with an
   * {@link Executor} and it's status can be polled using {@link FutureTask#isDone()
   * }
   */
  public void initializeBusStopParsing() {
    for (String initialBusStopFileName : MainCtrlUtils.INITIAL_BUS_STOP_FILE_NAMES) {
      Element initialBusStopFileElement = MainCtrlUtils.BUS_STOP_PARSING_CACHE.get(initialBusStopFileName);
      if (initialBusStopFileElement == null) {
        MainCtrlUtils.BUS_STOP_FILE_CACHE.put(
          initialBusStopFileName,
          Thread.currentThread().getContextClassLoader().getResourceAsStream(initialBusStopFileName)
        ); //has to occur before generateBusStopParseTask
        FutureTask<Void> osmParseTask = generateBusStopParseTask(
          initialBusStopFileName);
        MainCtrlUtils.EXECUTOR.execute(osmParseTask);
        MainCtrlUtils.BUS_STOP_PARSING_CACHE.put(new Element(initialBusStopFileName,
          osmParseTask));
        busStopFileNamesParsing.add(initialBusStopFileName);
        LOGGER.info("stared OSM parse thread");
      } else {
        if (((FutureTask<?>) initialBusStopFileElement.getObjectValue()).isDone()) {
          LOGGER.debug("OSM parse task for %s already finished");
        } else {
          LOGGER.debug("OSM parse task for %s already running");
        }
      }

    }
  }
  
  /*
  Don't provide a return value because it might be invalid at the nextretrieval 
  from cache (all information are in class property busRoutes
  */
  private FutureTask<Void> generateBusStopParseTask(
    final String busStopFileName) {
    FutureTask<Void> retValue = new FutureTask<>(
      new Callable<Void>() {
        @Override
        public Void call() throws Exception {
          InputStream busStopFileBytes = MainCtrlUtils.BUS_STOP_FILE_CACHE.get(
            busStopFileName);
          busStopDataService.parse(
            busStopFileBytes);
          busRoutes = Sets.union(busRoutes, busStopDataService.getBusRoutes());
          busStopFileNamesParsing.remove(busStopFileName);
          busStopFileNamesParsed.add(busStopFileName);
          return null;
        }
      });
    return retValue;
  }
}
