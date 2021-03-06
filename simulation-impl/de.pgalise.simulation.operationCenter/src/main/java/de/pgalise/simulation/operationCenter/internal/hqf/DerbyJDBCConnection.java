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
 
package de.pgalise.simulation.operationCenter.internal.hqf;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DerbyJDBCConnection implements PersistenceConnection {

	private Connection conn = null;
	
	@Override
	public Connection getConnection() {
		if (conn==null) {
			this.conn = this.establishConnection();
		} 
		return this.conn;
	}

	/**
	 * method establishes connection to database using jdbc
	 * 
	 * @return connection object
	 */
	public Connection establishConnection() {
		Connection con = null;
		try {
			Properties properties = new Properties();
			properties.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("/properties.props"));

			Class.forName(properties.getProperty("JdbcDerbyClassName"));

			con = DriverManager.getConnection(getConnectionString(properties),
					properties.getProperty("JdbcDerbyDbUser"),
					properties.getProperty("JdbcDerbyDbPassword"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return con;
	}

	/**
	 * Method creates the connection string for the jdbc connection 
	 * 
	 * @param properties
	 * @return connection string
	 */
	private String getConnectionString(Properties properties) {

		return "jdbc:" + properties.getProperty("JdbcDerbyDatabaseTyp") + "://"
				+ properties.getProperty("JdbcDerbyDbIp") + ":"
				+ properties.getProperty("JdbcDerbyDbPort") + "/"
				+ properties.getProperty("JdbcDerbyDbName");
	}
}
