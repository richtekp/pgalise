/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.operationCtrl.ctrl;

import gnu.cajo.invoke.Remote;
import gnu.cajo.utils.ItemServer;
import java.io.IOException;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * The controller for the start time and progress of the simulation, the status
 * of the simulation and the connection state (mostly used in the header of the
 * control and operation center)
 *
 * @author richter
 */
/*
 implementation notes:
 - has some field in common with MainCtrl in controlCenter, but sharing 
 is not very fruitful because value are retrieved completely different 
 (abstraction of data in proper class would be an option but effect is very 
 small)
 */
@ManagedBean
@SessionScoped
public class MainCtrl implements Serializable {

  private static final long serialVersionUID = 1L;
  private float simulationProgress = 0.0f;
  private boolean autoUpdate = true;
  /**
   * serves as value for progress bar of start wait dialog
   */
  private int startProgress = 0;
  private float mapRefreshFrequency = 0.0f;
  /*
   the rpcThread runs a
   */
  private Thread rmiOperationCenterThread = new Thread() {
    @Override
    public void run() {
      try {
        Remote.config(null,
          1198,
          null,
          0);
        ItemServer.bind(new MainCtrl(),
          "operationCenter");
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  };
  private boolean rmiOperationCenterThreadRunning = true;

  public MainCtrl() {
  }

  /**
   * invoked via RMI by operation center RMI client
   */
  public void rmiControlCenterUpdateCallback() {

  }

  public void setStartProgress(int startProgress) {
    this.startProgress = startProgress;
  }

  public int getStartProgress() {
    return startProgress;
  }

  public float getSimulationProgress() {
    return simulationProgress;
  }

  public void setSimulationProgress(float simulationProgress) {
    this.simulationProgress = simulationProgress;
  }

  public boolean getAutoUpdate() {
    return autoUpdate;
  }

  public void setAutoUpdate(boolean autoUpdate) {
    this.autoUpdate = autoUpdate;
  }

  public float getMapRefreshFrequency() {
    return mapRefreshFrequency;
  }

  public void setMapRefreshFrequency(float mapRefreshFrequency) {
    this.mapRefreshFrequency = mapRefreshFrequency;
  }
}
