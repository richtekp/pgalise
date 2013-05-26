package de.pgalise.simulation.operationCenter.internal;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InputStreamDecoder {
	private BufferedInputStream input;
	private static final Logger log = LoggerFactory.getLogger(InputStreamDecoder.class);
	public InputStreamDecoder(BufferedInputStream in) {
		input = in;
	}
	
	public String[] getLine() throws IOException {
		// 4+13 bytes = odysseus header + sensor meta data 
		byte metaData[] = new byte[17];
		if(input.read(metaData)==-1)
			return null;
		
		int tupleSize = ByteBuffer.wrap(copyBytes(metaData, 0, 4)).getInt();
		
		long timestamp = ByteBuffer.wrap(copyBytes(metaData, 4, 8)).getLong();
		int id = ByteBuffer.wrap(copyBytes(metaData, 12, 4)).getInt();
		int sensorTypeId = (int)ByteBuffer.wrap(copyBytes(metaData, 16, 1)).get();
		
		byte data[] = new byte[tupleSize-13];
		input.read(data);

		String result[] = null;
		if(sensorTypeId==2 || sensorTypeId==3 || sensorTypeId==4 ||
				sensorTypeId==19 || sensorTypeId==20) {
			result = getGPSOutput(timestamp, id, sensorTypeId, data);
		}
		else if(sensorTypeId==18) {
			result = getTopoRadarOutput(timestamp, id, sensorTypeId, data); 
		}
		else if(sensorTypeId==14) {
			result = getTrafficLightOutput(timestamp, id, sensorTypeId, data);
		}
		else {
			result = getNormalOutput(timestamp, id, sensorTypeId, data);
		}
		log.debug("Received data: "+StringUtils.join(result, ","));
		return result;
	}
	
	private String[] getTrafficLightOutput(long timestamp, int id,
			int sensorTypeId, byte[] tuple) {
		double measureValue1 = ByteBuffer.wrap(copyBytes(tuple, 0, 8)).getDouble();
		double measureValue2 = ByteBuffer.wrap(copyBytes(tuple, 8, 8)).getDouble();
		byte axleCount = ByteBuffer.wrap(copyBytes(tuple, 16, 1)).get();
		return new String[] {
				""+timestamp, ""+id, ""+sensorTypeId,
				""+measureValue1, ""+measureValue2,	""+axleCount};
	}

	private String[] getTopoRadarOutput(long timestamp, int id, int sensorTypeId,
			byte[] tuple) {
		byte axleCount = ByteBuffer.wrap(copyBytes(tuple, 0, 1)).get();
		short length = ByteBuffer.wrap(copyBytes(tuple, 1, 2)).getShort();
		short axialDistance = ByteBuffer.wrap(copyBytes(tuple, 3, 2)).getShort();
		short axialDistance2 = ByteBuffer.wrap(copyBytes(tuple, 5, 2)).getShort();
		return new String[] {
				""+timestamp, ""+id, ""+sensorTypeId,
				""+axleCount,""+length, ""+axialDistance, ""+axialDistance2};
	}

	private String[] getGPSOutput(long timestamp, int id, int sensorTypeId, byte tuple[]) {
		double measureValue1 = ByteBuffer.wrap(copyBytes(tuple, 0, 8)).getDouble();
		double measureValue2 = ByteBuffer.wrap(copyBytes(tuple, 8, 8)).getDouble();
		return new String[] {
				""+timestamp, ""+id, ""+sensorTypeId,
				""+measureValue1, ""+measureValue2 };
	}

	private String[] getNormalOutput(long timestamp, int id, int sensorTypeId, byte tuple[]) {
		double measureValue1 = ByteBuffer.wrap(copyBytes(tuple, 0, 8)).getDouble();
		return new String[] {
				""+timestamp, ""+id, ""+sensorTypeId,
				""+measureValue1};
	}

	public byte[] copyBytes(final byte bytes[], int beginIndex, int count) {
		if(count>(bytes.length-beginIndex))
			throw new IllegalArgumentException(
					String.format("Can't copy input byte array of size %s from %s to %s",
							bytes.length, beginIndex, beginIndex+count));
		byte copy[] = new byte[count];
		for(int i=0; i<count; i++) {
			copy[i] = bytes[beginIndex+i];
		}
		return copy;
	}
}
