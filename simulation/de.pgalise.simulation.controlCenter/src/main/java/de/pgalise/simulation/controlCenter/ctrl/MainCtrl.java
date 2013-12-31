/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.controlCenter.InitDialogCtrlInitialType;
import de.pgalise.simulation.controlCenter.InitDialogCtrlInitialTypeEnum;
import de.pgalise.simulation.controlCenter.MapParsedStateEnum;
import de.pgalise.simulation.controlCenter.internal.message.ControlCenterMessage;
import de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter;
import de.pgalise.simulation.controlCenter.model.MapAndBusstopFileData;
import de.pgalise.simulation.service.GsonService;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.shared.event.AbstractEvent;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

/**
 *
 * @author richter
 */
@ManagedBean
@SessionScoped
@Path("/mainCtrl")
public class MainCtrl implements Serializable {
	private static final long serialVersionUID = 1L;
	@EJB
	private GsonService gsonService;
	@EJB
	private IdGenerator idGenerator;
	private Map<Date, ControlCenterMessage<?>> sentMessages = new HashMap<>();
	private MapParsedStateEnum mapParsedStateEnum;
	private MapAndBusstopFileData mapAndBusstopFileData;
	private ControlCenterStartParameter importedStartParameter;
	private InitDialogCtrlInitialType chosenInitialType = InitDialogCtrlInitialTypeEnum.CREATE;
	private boolean automaticallyOpenSensorDialog;
	private List<AbstractEvent> uncommittedEvents = new LinkedList<>();
	private List<AbstractEvent> selectedUncommittedEvents = new LinkedList<>();
	private List<ControlCenterMessage<?>> unsentMessages = new LinkedList<>();
	private List<ControlCenterMessage<?>> selectedUnsentMessages = new LinkedList<>();
	@ManagedProperty(value = "#{ControlCenterStartParameter}")
	private ControlCenterStartParameter startParameter;

	/**
	 * Creates a new instance of NewJSFManagedBean
	 */
	public MainCtrl() {
	}

	public void setStartParameter(ControlCenterStartParameter startParameter) {
		this.startParameter = startParameter;
	}

	public ControlCenterStartParameter getStartParameter() {
		return startParameter;
	}

	public void setUnsentMessages(
		List<ControlCenterMessage<?>> unsentMessages) {
		this.unsentMessages = unsentMessages;
	}

	public List<ControlCenterMessage<?>> getUnsentMessages() {
		return unsentMessages;
	}

	public void setSelectedUnsentMessages(
		List<ControlCenterMessage<?>> selectedUnsentMessages) {
		this.selectedUnsentMessages = selectedUnsentMessages;
	}

	public List<ControlCenterMessage<?>> getSelectedUnsentMessages() {
		return selectedUnsentMessages;
	}
	
	public void commitEvents() {
		throw new UnsupportedOperationException();
	}

	public void setSelectedUncommittedEvents(
		List<AbstractEvent> selectedUncommittedEvents) {
		this.selectedUncommittedEvents = selectedUncommittedEvents;
	}

	public List<AbstractEvent> getSelectedUncommittedEvents() {
		return selectedUncommittedEvents;
	}

	public void setUncommittedEvents(List<AbstractEvent> uncommittedEvents) {
		this.uncommittedEvents = uncommittedEvents;
	}

	public List<AbstractEvent> getUncommittedEvents() {
		return uncommittedEvents;
	}

	public void setAutomaticallyOpenSensorDialog(
		boolean automaticallyOpenSensorDialog) {
		this.automaticallyOpenSensorDialog = automaticallyOpenSensorDialog;
	}

	public boolean getAutomaticallyOpenSensorDialog() {
		return automaticallyOpenSensorDialog;
	}

	public void setSentMessages(
		Map<Date, ControlCenterMessage<?>> sentMessages) {
		this.sentMessages = sentMessages;
	}

	public Map<Date, ControlCenterMessage<?>> getSentMessages() {
		return sentMessages;
	}

	public void setChosenInitialType(InitDialogCtrlInitialType chosenInitialType) {
		this.chosenInitialType = chosenInitialType;
	}

	public InitDialogCtrlInitialType getChosenInitialType() {
		return chosenInitialType;
	}

	/**
	 * @return the mapParsedStateEnum
	 */
	public MapParsedStateEnum getMapParsedStateEnum() {
		return mapParsedStateEnum;
	}

	/**
	 * @param mapParsedStateEnum the mapParsedStateEnum to set
	 */
	public void setMapParsedStateEnum(
					MapParsedStateEnum mapParsedStateEnum) {
		this.mapParsedStateEnum = mapParsedStateEnum;
	}

	/**
	 * @return the mapAndBusstopFileData
	 */
	public MapAndBusstopFileData getMapAndBusstopFileData() {
		return mapAndBusstopFileData;
	}

	/**
	 * @param mapAndBusstopFileData the mapAndBusstopFileData to set
	 */
	public void setMapAndBusstopFileData(
					MapAndBusstopFileData mapAndBusstopFileData) {
		this.mapAndBusstopFileData = mapAndBusstopFileData;
	}

	/**
	 * @return the importedStartParameter
	 */
	public ControlCenterStartParameter getImportedStartParameter() {
		return importedStartParameter;
	}

	/**
	 * @param importedStartParameter the importedStartParameter to set
	 */
	public void setImportedStartParameter(
					ControlCenterStartParameter importedStartParameter) {
		this.importedStartParameter = importedStartParameter;
	}
	
	@GET()
	@Produces("text/plain")
	@Path("/parseOSMAndBusstop/")	
	public void parseOSMAndBusstop() {
		System.out.println("kfldjs222");
	}	
	
	public void export() {
		throw new UnsupportedOperationException();
	}
	
	private String parameterUploadData;

	public void setParameterUploadData(String parameterUploadData) {
		this.parameterUploadData = parameterUploadData;
	}

	public String getParameterUploadData() {
		return parameterUploadData;
	}
	
	public void onStartParameterUpload() {
		throw new UnsupportedOperationException("implement start parameter deserialization");
	}
	
	private String parameterDownloadData;
	
	public void prepareExport() {
		this.parameterDownloadData = null;
		throw new UnsupportedOperationException("serialize");
	}
	
	public String retrieveExportDownloadLink() {
		if (parameterDownloadData == null) {
			throw new IllegalStateException("export not prepared");
		}
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath()+"/"+UUID.randomUUID().toString();
	}
	
	public void deleteUncommittedEvent(AbstractEvent event) {
		uncommittedEvents.remove(event);
	}
	
	public void deleteUnSentMessage(ControlCenterMessage<?> unsentMessage) {
	}
	
	public void sendMessages() {
		
	}
	
	public void removeTrafficServerIP(String trafficServerIP) {
		startParameter.getTrafficServerIPs().remove(trafficServerIP);
	}
	
	public void addTrafficServerIP() {
		startParameter.getTrafficServerIPs().add("");
	}

	private TreeNode startParameterTreeRoot = new DefaultTreeNode("StartParameter",
			null);

	public void generateTree( )  {
		Queue<Pair<TreeNode,Field>> queue = new LinkedList<>();
		for(Field field : startParameter.getClass().getDeclaredFields()) {
			queue.add(new MutablePair<>(startParameterTreeRoot,
				field));
		}
		while(!queue.isEmpty()) {
			Pair<TreeNode,Field> current = queue.poll();
			TreeNode node = new DefaultTreeNode(current.getRight().getName(),
				current.getLeft());
			for(Field field : current.getRight().getDeclaringClass().getDeclaredFields()) {
				queue.add(new MutablePair<>(current.getLeft(),
	field));
			}
		}
	}

	/*
	@TODO: implements PropertyChangeListener for StartParameter and regenerate tree lazily on change
	 */
	public TreeNode getStartParameterTreeRoot() {
		startParameterTreeRoot = new DefaultTreeNode("StartParameter",
			null);
		generateTree();
		return startParameterTreeRoot;
	}

	public void setStartParameterTreeRoot(TreeNode startParameterTreeRoot) {
		this.startParameterTreeRoot = startParameterTreeRoot;
	}
}
