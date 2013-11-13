package de.pgalise.simulation.mavenproject1;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.sensorFramework.SensorFactory;
import de.pgalise.simulation.sensorFramework.SensorRegistry;
import de.pgalise.simulation.sensorFramework.internal.DefaultSensorRegistry;
import de.pgalise.simulation.sensorFramework.output.Output;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpKeepOpenStrategy;
import de.pgalise.simulation.sensorFramework.output.tcpip.TcpIpOutput;
import de.pgalise.simulation.service.ServiceDictionary;
import de.pgalise.simulation.service.internal.DefaultServiceDictionary;
import de.pgalise.simulation.shared.event.EventType;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.internal.DefaultTrafficGraph;
import de.pgalise.simulation.traffic.internal.server.DefaultTrafficServer;
import de.pgalise.simulation.traffic.server.TrafficServer;
import de.pgalise.simulation.traffic.server.TrafficServerLocal;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandler;
import de.pgalise.simulation.traffic.server.eventhandler.TrafficEventHandlerManager;
import de.pgalise.simulation.traffic.server.eventhandler.vehicle.VehicleEvent;
import de.pgalise.staticsensor.internal.DefaultSensorFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
			ServiceDictionary serviceDictionary = new DefaultServiceDictionary();
			EntityManager em = Persistence.createEntityManagerFactory("test").createEntityManager();
			SensorRegistry sensorRegistry = new DefaultSensorRegistry(em);
//			DefaultVehicleEventHandlerManager eventHandlerManager = new DefaultVehicleEventHandlerManager();
			TrafficServerLocal<VehicleEvent> trafficServerLocal = new DefaultTrafficServer();
			List<TrafficServerLocal<VehicleEvent>> s = new LinkedList<>(Arrays.asList(trafficServerLocal));
			Output o = new TcpIpOutput("127.0.0.1",
				55554,
				new TcpIpKeepOpenStrategy());
			TrafficGraph trafficGraph = new DefaultTrafficGraph();
			SensorFactory x = new DefaultSensorFactory(null,
				null,
				null,
				null);
        TrafficServer a = new DefaultTrafficServer(new Coordinate(1,1),
					serviceDictionary,
					sensorRegistry,
					new TrafficEventHandlerManager<TrafficEventHandler<VehicleEvent>, VehicleEvent>() {

				@Override
				public <J extends TrafficEventHandler<VehicleEvent>> void init(
					InputStream config,
					Class<? extends J> clazz) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
					throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
				}

				@Override
				public void addHandler(TrafficEventHandler<VehicleEvent> handler) {
					throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
				}

				@Override
				public void remoteHandler(EventType type) {
					throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
				}

				@Override
				public void handleEvent(VehicleEvent event) {
					throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
				}

				@Override
				public TrafficEventHandler<VehicleEvent> getEventHandler(EventType type) {
					throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
				}

				@Override
				public void clear() {
					throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
				}

				@Override
				public Iterator<TrafficEventHandler<VehicleEvent>> iterator() {
					throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
				}
			},
					s,
					x,
					trafficGraph);
    }
}
