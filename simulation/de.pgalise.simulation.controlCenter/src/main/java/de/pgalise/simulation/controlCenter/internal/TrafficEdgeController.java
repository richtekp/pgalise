package de.pgalise.simulation.controlCenter.internal;

import de.pgalise.simulation.traffic.TrafficEdge;
import de.pgalise.simulation.controlCenter.internal.util.JsfUtil;
import de.pgalise.simulation.controlCenter.internal.util.PaginationHelper;

import java.io.Serializable;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@ManagedBean(name = "trafficEdgeController")
@SessionScoped
public class TrafficEdgeController implements Serializable {

	private TrafficEdge current;
	private DataModel items = null;
	@EJB
	private de.pgalise.simulation.controlCenter.internal.TrafficEdgeFacade ejbFacade;
	private PaginationHelper pagination;
	private int selectedItemIndex;

	public TrafficEdgeController() {
	}

	public TrafficEdge getSelected() {
		if (current == null) {
			current = new TrafficEdge(current,
				selectedItemIndex,
				null,
				true,
				true,
				true,
				selectedItemIndex);
			selectedItemIndex = -1;
		}
		return current;
	}

	private TrafficEdgeFacade getFacade() {
		return ejbFacade;
	}

	public PaginationHelper getPagination() {
		if (pagination == null) {
			pagination = new PaginationHelper(10) {

				@Override
				public int getItemsCount() {
					return getFacade().count();
				}

				@Override
				public DataModel createPageDataModel() {
					return new ListDataModel(getFacade().findRange(
							new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
				}
			};
		}
		return pagination;
	}

	public String prepareList() {
		recreateModel();
		return "List";
	}

	public String prepareView() {
		current = (TrafficEdge) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
		return "View";
	}

	public String prepareCreate() {
		current = new TrafficEdge(current,
			selectedItemIndex,
			null,
			true,
			true,
			true,
			selectedItemIndex);
		selectedItemIndex = -1;
		return "Create";
	}

	public String create() {
		try {
			getFacade().create(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString(
				"TrafficEdgeCreated"));
			return prepareCreate();
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e,
				ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
			return null;
		}
	}

	public String prepareEdit() {
		current = (TrafficEdge) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
		return "Edit";
	}

	public String update() {
		try {
			getFacade().edit(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString(
				"TrafficEdgeUpdated"));
			return "View";
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e,
				ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
			return null;
		}
	}

	public String destroy() {
		current = (TrafficEdge) getItems().getRowData();
		selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
		performDestroy();
		recreatePagination();
		recreateModel();
		return "List";
	}

	public String destroyAndView() {
		performDestroy();
		recreateModel();
		updateCurrentItem();
		if (selectedItemIndex >= 0) {
			return "View";
		} else {
			// all items were removed - go back to list
			recreateModel();
			return "List";
		}
	}

	private void performDestroy() {
		try {
			getFacade().remove(current);
			JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString(
				"TrafficEdgeDeleted"));
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e,
				ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
		}
	}

	private void updateCurrentItem() {
		int count = getFacade().count();
		if (selectedItemIndex >= count) {
			// selected index cannot be bigger than number of items:
			selectedItemIndex = count - 1;
			// go to previous page if last page disappeared:
			if (pagination.getPageFirstItem() >= count) {
				pagination.previousPage();
			}
		}
		if (selectedItemIndex >= 0) {
			current = getFacade().findRange(
					new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
		}
	}

	public DataModel getItems() {
		if (items == null) {
			items = getPagination().createPageDataModel();
		}
		return items;
	}

	private void recreateModel() {
		items = null;
	}

	private void recreatePagination() {
		pagination = null;
	}

	public String next() {
		getPagination().nextPage();
		recreateModel();
		return "List";
	}

	public String previous() {
		getPagination().previousPage();
		recreateModel();
		return "List";
	}

	public SelectItem[] getItemsAvailableSelectMany() {
		return JsfUtil.getSelectItems(ejbFacade.findAll(),
			false);
	}

	public SelectItem[] getItemsAvailableSelectOne() {
		return JsfUtil.getSelectItems(ejbFacade.findAll(),
			true);
	}

	@FacesConverter(forClass = TrafficEdge.class)
	public static class TrafficEdgeControllerConverter implements Converter {

		@Override
		public Object getAsObject(FacesContext facesContext,
			UIComponent component,
			String value) {
			if (value == null || value.length() == 0) {
				return null;
			}
			TrafficEdgeController controller = (TrafficEdgeController) facesContext.
				getApplication().getELResolver().
				getValue(facesContext.getELContext(),
					null,
					"trafficEdgeController");
			return controller.ejbFacade.find(getKey(value));
		}

		java.lang.Long getKey(String value) {
			java.lang.Long key;
			key = Long.valueOf(value);
			return key;
		}

		String getStringKey(java.lang.Long value) {
			StringBuilder sb = new StringBuilder();
			sb.append(value);
			return sb.toString();
		}

		@Override
		public String getAsString(FacesContext facesContext,
			UIComponent component,
			Object object) {
			if (object == null) {
				return null;
			}
			if (object instanceof TrafficEdge) {
				TrafficEdge o = (TrafficEdge) object;
				return getStringKey(o.getId());
			} else {
				throw new IllegalArgumentException(
					"object " + object + " is of type " + object.getClass().getName() + "; expected type: " + TrafficEdge.class.
					getName());
			}
		}

	}

}
