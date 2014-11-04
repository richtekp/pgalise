/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.controlCenter.ControlCenterConstants;
import gnu.cajo.invoke.Remote;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 * XML-RPC clients are expected to implement {@link ControlCenterXMLRPCClient} 
 * @author richter
 */
/*internal implementation notes:
- +++@TODO: check this when clients are added
*/
@ManagedBean
@SessionScoped
public class XMLRPCCtrl implements Serializable {
  private static final long serialVersionUID = 1L;
  private List<Remote> remotes = new LinkedList<>();
  private boolean rmiControlCenterThreadRunning = true;

  public XMLRPCCtrl() {
  }
  
  @PostConstruct
  public void initialize() {
    startRmiOperationCenterThread();
  }
  
  @PreDestroy
  public void destroy() {
    stopRmiOperationCenterThread();
  }

  public List<Remote> getRemotes() {
    return remotes;
  }

  public void setRemotes(List<Remote> remotes) {
    this.remotes = remotes;
  }
  
  /**
   * a wrapper around functionality to add {@link Remote}s from JSF
   * @param itemName
   * @param host
   * @param port
   * @throws RemoteException 
   */
  public void addRemote(String itemName, String host, int port) throws RemoteException {
    remotes.add(new Remote(itemName, host, port));
  }
  
  private Thread rmiControlCenterThread = new Thread() {
    @Override
    public void run() {
      for (Remote remote : remotes) {      
        try {
          remote.invoke(
            ControlCenterConstants.XML_RPC_METHOD_NAME,
            "Wiki");
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }
  };

  public void startRmiOperationCenterThread() {
    rmiControlCenterThread.start();
  }

  public void stopRmiOperationCenterThread() {
    rmiControlCenterThreadRunning = false;
  }
}
