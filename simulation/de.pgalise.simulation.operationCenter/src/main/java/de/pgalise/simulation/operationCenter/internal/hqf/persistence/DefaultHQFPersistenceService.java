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
 
package de.pgalise.simulation.operationCenter.internal.hqf.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import de.pgalise.simulation.operationCenter.internal.hqf.PersistenceConnection;

/**
 * Implementation for saving aggregated data for the high quality data to a
 * database. Requires a connection to use the sequel statements
 * 
 * @author Kamil
 * @version 1.0 (Feb 27, 2013)
 */
public class DefaultHQFPersistenceService implements HQFPersistenceService {

	private Connection conn = null;

	private PreparedStatement pstmtInsert = null;
	private PreparedStatement pstmtDelete = null;

	private final String INSERT_HQF_DATA = "INSERT INTO pgalise.hqf_data (SIMULATIONTIMESTAMP,SENSORTYPEID,AMOUNT) VALUES (?,?,?)";
	private final String DELETE_HQF_DATA = "delete from pgalise.hqf_data";

	/**
	 * Default constructor
	 * 
	 * Establishes connection to the database and prepares all statements
	 */
	public DefaultHQFPersistenceService(PersistenceConnection pconn) {
		this.conn = pconn.getConnection();
		try {
			if (conn != null) {
				this.pstmtInsert = this.conn.prepareStatement(INSERT_HQF_DATA);
				this.pstmtDelete = this.conn.prepareStatement(DELETE_HQF_DATA);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * method inserts hqfdata object into the database 
	 * 
	 * @param hqfData object to write into the database 
	 */
	public void insertData(HQFData hqfData) {
		try {
			this.pstmtInsert.setTimestamp(1, hqfData.getSimulationTimestamp());
			this.pstmtInsert.setInt(2, hqfData.getSensorTypeID());
			this.pstmtInsert.setInt(3, hqfData.getAmount());
			this.pstmtInsert.executeUpdate();
		} catch (final RuntimeException ex) {
			ex.printStackTrace();
			throw new UnsupportedOperationException();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * method deletes all data form hqfdata table in database
	 */
	public void deleteData() {
		try {
			this.pstmtDelete.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void finalize(){
		try {
			if(pstmtDelete!=null){
				pstmtDelete.close();
			}	
			if(pstmtInsert!=null){
				pstmtInsert.close();
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
