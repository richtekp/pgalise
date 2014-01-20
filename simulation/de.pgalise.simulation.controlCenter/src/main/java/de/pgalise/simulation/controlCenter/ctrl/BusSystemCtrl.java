/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.traffic.entity.BusRoute;
import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.html.HtmlSelectBooleanCheckbox;
import javax.faces.event.AjaxBehaviorEvent;

/**
 *
 * @author richter
 */
@ManagedBean
@SessionScoped
public class BusSystemCtrl implements Serializable {

  private static final long serialVersionUID = 1L;

  private LinkedList<BusRoute> busRoutes = new LinkedList<>(Arrays.asList(
    new BusRoute(1L,
      "shortN",
      "longN")));

  /**
   * Creates a new instance of BusSystemCtrl
   */
  public BusSystemCtrl() {
//		dataTable.
  }

  public LinkedList<BusRoute> getBusRoutes() {
    return busRoutes;
  }

  public void setBusRoutes(LinkedList<BusRoute> busRoutes) {
    this.busRoutes = busRoutes;
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
    HtmlSelectBooleanCheckbox source = (HtmlSelectBooleanCheckbox) event.
      getSource();
    System.out.println(event);
  }
}
