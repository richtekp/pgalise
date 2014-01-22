package de.pgalise.simulation.operationCenter.internal;


import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.sensorFramework.output.tcpip.DefaultTcpIpOutput;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpForceCloseStrategy;

/**
 *
 * @author richter
 */
public class MainCtrlUtils {

  public final static Output OUTPUT = new DefaultTcpIpOutput("localhost",
    6666,
    TcpIpForceCloseStrategy.getInstance());

  private MainCtrlUtils() {
  }
}
