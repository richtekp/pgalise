package de.pgalise.simulation.operationCenter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.pgalise.simulation.operationCenter.internal.InputStreamDecoder;
import org.junit.Ignore;

// IntegrationTest with Odysseus
@Ignore
public class InputStreamDecoderTest {
	private static final Logger log = LoggerFactory.getLogger(InputStreamDecoderTest.class);
	private InputStreamDecoder parser;

	
	@Test
	public void receiveData() throws UnknownHostException, IOException {
		Socket socket = new Socket("localhost", 6001);
		parser = new InputStreamDecoder(new BufferedInputStream(socket.getInputStream()));
		for(String[] col = parser.getLine(); col!=null; col = parser.getLine()) {
		}
	}
}
