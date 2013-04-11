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
 
package de.pgalise.ssf.test;

import java.io.DataOutputStream;
import java.net.Socket;

/**
 * @author Marcus
 */
public class OdysseusRaw {

	public static void main(String[] args) {
		try {
			Socket socket = new Socket("127.0.0.1", 6666);
			DataOutputStream outStream = new DataOutputStream(socket.getOutputStream());
			for (int i = 0; i <= 10; i++){
			outStream.writeByte(-1);
			outStream.writeLong(i);
			outStream.writeInt(i);
			outStream.writeByte(i);
			outStream.writeDouble(i);
			outStream.writeDouble(i);
			outStream.writeByte(i);
			outStream.writeShort(i);
			outStream.writeShort(i);
			outStream.writeShort(i);
			outStream.writeByte(-1);
			}

			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
