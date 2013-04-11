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
 
package de.pgalise.simulation.client;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import de.pgalise.simulation.service.RandomSeedService;

public class ClientExample {
	public static void main(String args[]) throws NamingException {
		
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.RemoteInitialContextFactory");
		props.put(Context.PROVIDER_URL, "http://127.0.0.1:8080/tomee/ejb");
//		props.put("java.naming.security.principal", "tomee");
//		props.put("java.naming.security.credentials", "tomee");
		Context remoteContext = new InitialContext(props);			
		
		RandomSeedService rss = (RandomSeedService) remoteContext.lookup("HelloAdvancedBeanRemote");
		System.out.println("rss!=null? "+ (rss!=null));
	}
}
