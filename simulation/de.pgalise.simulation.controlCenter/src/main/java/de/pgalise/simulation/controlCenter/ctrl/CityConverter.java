/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.shared.entity.Identifiable;
import de.pgalise.simulation.traffic.entity.OsmCity;
import java.util.HashMap;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author richter
 */
@FacesConverter(value = "cityConverter")
public class CityConverter implements Converter {

//	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * initialization of Converters is not clear (multiple instances are used)
	 */
	private static Map<Long,Identifiable> cityConverterUsedCities = new HashMap<>();

	/**
	 * queries entity manager based on OSM id
	 *
	 * @param osmId
	 * @return
	 */
	public OsmCity getOsmCity(long osmId) {
		Query query = entityManager.createQuery(
			"SELECT c FROM OsmCity c WHERE c.osmId = ?");
		return (OsmCity) query.getSingleResult();
	}

	@Override
	public Object getAsObject(FacesContext context,
		UIComponent component,
		String value) {
		if (value.trim().isEmpty()) {
			return null;
		}
		try {
			Long number = Long.parseLong(value);
			return cityConverterUsedCities.get(number);
		} catch (NumberFormatException ex) {
			throw new ConverterException(ex);
		}
	}
	
	@Override
	public String getAsString(FacesContext context,
		UIComponent component,
		Object value) {
		if (value == null || value.equals("")) {
			return "";
		}
		Identifiable identifiable = (Identifiable) value;
		cityConverterUsedCities.put(identifiable.getId(),identifiable);
		return String.valueOf(identifiable.getId());
	}

}
