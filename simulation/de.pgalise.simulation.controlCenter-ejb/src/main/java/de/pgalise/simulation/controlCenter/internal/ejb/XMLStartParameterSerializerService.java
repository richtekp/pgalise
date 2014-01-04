/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.internal.ejb;

import com.google.gson.Gson;
import de.pgalise.simulation.controlCenter.internal.util.service.StartParameterSerializerService;
import de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter;
import java.io.InputStream;
import java.io.OutputStream;
import javax.ejb.Stateful;
import javolution.xml.XMLObjectWriter;
import javolution.xml.stream.XMLStreamException;

/**
 *
 * @author richter
 */
@Stateful
public class XMLStartParameterSerializerService implements
	StartParameterSerializerService {

	@Override
	public ControlCenterStartParameter deserialize(String content) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public ControlCenterStartParameter deserialize(InputStream inputStream) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public String serialize(ControlCenterStartParameter cCSimulationStartParameter,
		String fileName) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void serialize(ControlCenterStartParameter controlCenterStartParameter,
		OutputStream outputStream) {
		InputStream inputStream;
		try {
			XMLObjectWriter writer = new XMLObjectWriter().setOutput(outputStream);
			writer.write(controlCenterStartParameter);
			writer.flush();
			writer.close();
		} catch (XMLStreamException ex) {
			throw new RuntimeException(ex);
		}

//		try {
//			JAXBContext jaxbContext = JAXBContext.newInstance(
//				ControlCenterStartParameter.class);
//			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//
//			// for getting nice formatted output
//			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
//				Boolean.TRUE);
//
//			// Writing to XML file
//			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//			jaxbMarshaller.marshal(startParameter,
//				outputStream);
//			inputStream = new ByteArrayInputStream(outputStream.
//				toByteArray());
//		} catch (JAXBException e) {
//			throw new RuntimeException(e);
//		}
	}

	@Override
	public void init(Gson gson) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
