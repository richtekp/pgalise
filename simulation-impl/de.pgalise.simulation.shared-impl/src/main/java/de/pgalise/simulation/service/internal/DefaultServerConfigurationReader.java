///* 
// * Copyright 2013 PG Alise (http://www.pg-alise.de/)
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License. 
// */
// 
//package de.pgalise.simulation.service.internal;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Properties;
//
//import javax.ejb.EJB;
//import javax.ejb.Local;
//import javax.ejb.Lock;
//import javax.ejb.LockType;
//import javax.ejb.Singleton;
//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//
//import de.pgalise.simulation.service.ServiceDictionary;
//import de.pgalise.simulation.service.configReader.ConfigReader;
//import de.pgalise.simulation.service.ServerConfigurationIdentifier;
//import de.pgalise.simulation.service.manager.ServerConfigurationReader;
//import de.pgalise.simulation.service.manager.ServiceHandler;
//import de.pgalise.simulation.service.ServerConfiguration;
//import de.pgalise.simulation.service.ServerConfigurationEntity;
//import de.pgalise.simulation.service.Service;
//import java.util.ArrayList;
//
///**
// * Default implementation of the SeverConfigurationReader.
// * 
// * @author mustafa
// * @version 1.0 (Apr 5, 2013)
// */
//@Lock(LockType.READ)
//@Local
//@Singleton(mappedName = "de.pgalise.simulation.service.manager.ServerConfigurationReader", name = "de.pgalise.simulation.service.manager.ServerConfigurationReader")
//public class DefaultServerConfigurationReader implements ServerConfigurationReader<Service> {
//
//	/**
//	 * Suffix for remote
//	 */
//	private static final String REMOTE_SUFFIX = "Remote";
//
//	/**
//	 * Suffix for locale
//	 */
//	private static final String LOCAL_SUFFIX = "Local";
//
//	@EJB
//	private ConfigReader localConfigReader;
//
//	/**
//	 * Default constructor
//	 */
//	public DefaultServerConfigurationReader() {
//	}
//
//	/**
//	 * Constructor
//	 * 
//	 * @param reader
//	 *            Reader
//	 */
//	public DefaultServerConfigurationReader(ConfigReader reader) {
//		localConfigReader = reader;
//	}
//
//	@Override
//	public void read(ServerConfiguration serverConfig, List<ServiceHandler<Service>> handlerList) {
//		// remote referenzen beziehen
//		try {
//			String host = localConfigReader.getProperty(ServerConfigurationIdentifier.SERVER_HOST);
//
//			String ejbdProtocolEnabled = localConfigReader.getProperty(ServerConfigurationIdentifier.EJBD_PROTOCOL_ENABLED);
//			boolean useEjbdProtocol = (ejbdProtocolEnabled != null && ejbdProtocolEnabled.equals("true"));
//
//			String lip = host.split(":")[0];
//			String lport = host.split(":")[1];
//
//			Map<String, List<ServerConfigurationEntity>> config = serverConfig.getConfiguration();
//			for (String server : config.keySet()) {
//				String ip = server.split(":")[0];
//				String port = server.split(":")[1];
//
//				if (!((ip.equals("127.0.0.1") || ip.equals(lip)) && port.equals(lport))) {
//					// log.debug("Resolving ejbs on remote host (" + server +") ...");
//					Properties props = new Properties();
//					props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.RemoteInitialContextFactory");
//					if (!useEjbdProtocol) {
//						props.setProperty(Context.PROVIDER_URL, "http://" + server + "/tomee/ejb");
//					} else {
//						props.setProperty(Context.PROVIDER_URL, "ejbd://" + server);
//					}
//					// props.put("java.naming.security.principal", "tomee");
//					// props.put("java.naming.security.credentials", "tomee");
//					Context remoteContext = new InitialContext(props);
//
//					List<ServerConfigurationEntity> entities = config.get(server);
//					for (ServerConfigurationEntity entity : entities) {
//						for (ServiceHandler<Service> handler : handlerList) {
//							if (handler.getName().equals(entity.getName())) {
//								Service o = (Service) remoteContext.lookup(entity.getName() + REMOTE_SUFFIX);
//								handler.handle(server, o);
//							}
//						}
//					}
//				} else {
//					// log.debug("Resolving ejbs on local host: (" + server +") ...");
//					Properties props = new Properties();
//					props.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.LocalInitialContextFactory");
//					Context localContext = new InitialContext(props);
//
//					List<ServerConfigurationEntity> entities = config.get(server);
//					for (ServerConfigurationEntity entity : entities) {
//						for (ServiceHandler<Service> handler : handlerList) {
//							if (handler.getName().equals(entity.getName())) {
//								if (handler.getName().equals(ServiceDictionary.FRONT_CONTROLLER)) {
//									Properties p = new Properties();
//									p.setProperty(Context.INITIAL_CONTEXT_FACTORY,
//											"org.apache.openejb.client.RemoteInitialContextFactory");
//									if (!useEjbdProtocol) {
//										props.setProperty(Context.PROVIDER_URL, "http://" + server + "/tomee/ejb");
//									} else {
//										props.setProperty(Context.PROVIDER_URL, "ejbd://" + server);
//									}
//									Service service = (Service) localContext.lookup(entity.getName() + REMOTE_SUFFIX);
//									handler.handle(server, service);
//								} else {
//									Service service = (Service) localContext.lookup(entity.getName() + LOCAL_SUFFIX);
//									handler.handle(server, service);
//								}
//							}
//						}
//					}
//				}
//			}
//		} catch (NamingException e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	@Override
//	public void read(ServerConfiguration serverConfig, ServiceHandler<Service> handler) {
//		List<ServiceHandler<Service>> list = new ArrayList<>(1);
//		list.add(handler);
//		this.read(serverConfig, list);
//	}
//}
