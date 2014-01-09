 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.controlCenter.ctrl;

import de.pgalise.simulation.SimulationControllerLocal;
import de.pgalise.simulation.controlCenter.internal.message.ControlCenterMessage;
import de.pgalise.simulation.controlCenter.internal.util.service.StartParameterSerializerService;
import de.pgalise.simulation.controlCenter.model.ControlCenterStartParameter;
import de.pgalise.simulation.controlCenter.model.MapAndBusstopFileData;
import de.pgalise.simulation.service.GsonService;
import de.pgalise.simulation.service.IdGenerator;
import de.pgalise.simulation.service.ServerConfiguration;
import de.pgalise.simulation.shared.event.AbstractEvent;
import de.pgalise.simulation.shared.event.Event;
import de.pgalise.simulation.traffic.OSMCityInfrastructureData;
import de.pgalise.simulation.traffic.TrafficInfrastructureData;
import de.pgalise.simulation.traffic.TrafficInitParameter;
import de.pgalise.util.cityinfrastructure.BuildingEnergyProfileStrategy;
import de.pgalise.util.cityinfrastructure.DefaultBuildingEnergyProfileStrategy;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.TreeNode;

/**
 *
 * @author richter
 */
@ManagedBean
@SessionScoped
//@Path("/mainCtrl")
public class MainCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	@EJB
	private GsonService gsonService;
	@EJB
	private IdGenerator idGenerator;
	@EJB
	private SimulationControllerLocal simulationController;
	private Map<Date, ControlCenterMessage<?>> sentMessages = new HashMap<>();
	private MapParsedStateEnum mapParsedStateEnum;
	private MapAndBusstopFileData mapAndBusstopFileData;
	private ControlCenterStartParameter importedStartParameter;
	private InitDialogCtrlInitialType chosenInitialType = InitDialogCtrlInitialTypeEnum.create;
	private boolean automaticallyOpenSensorDialog = false;
	private List<AbstractEvent> uncommittedEvents = new LinkedList<>();
	private List<AbstractEvent> selectedUncommittedEvents = new LinkedList<>();
	private List<ControlCenterMessage<?>> unsentMessages = new LinkedList<>();
	private List<ControlCenterMessage<?>> selectedUnsentMessages = new LinkedList<>();
	@ManagedProperty(value = "#{controlCenterStartParameter}")
	private ControlCenterStartParameter startParameter = new ControlCenterStartParameter();
	private String connectionState = ConnectionStateEnum.DISCONNECTED.
		getStringValue();
	private String simulationState = SimulationStateEnum.STOPPED.getStringValue();
	private Date simulationDate = new GregorianCalendar().getTime();
	private String mapParsedState = MapParsedStateEnum.IN_PROGRESS.
		getStringValue();
	private TreeNode startParameterTreeRoot;
	@EJB
	private StartParameterSerializerService startParameterSerializerService;
	private CityCtrl cityCtrl;

	public MainCtrl() {
	}

	@PostConstruct
	public void init() {
		startParameterTreeRoot = new DefaultTreeNode("StartParameter",
			null);
		cityCtrl = (CityCtrl) FacesContext.getCurrentInstance().
			getELContext()
			.getELResolver().
			getValue(FacesContext.getCurrentInstance().getELContext(),
				null,
				"cityCtrl");
	}

	public void startSimulation() throws IOException {
		if (cityCtrl.getOsmFileNames().size() > 1) {
			throw new UnsupportedOperationException(
				"multiple osm files not supported yet");
		}
		if (cityCtrl.getBusStopFileNames().size() > 1) {
			throw new UnsupportedOperationException(
				"multiple bus stop files not supported yet");
		}
		String osmFileKey = cityCtrl.getOsmFileNames().
			iterator().next();
		String busStopFileKey = cityCtrl.getBusStopFileNames().iterator().next();
		InputStream osmFileBytes = new FileInputStream(new File(
			MainCtrlUtils.CACHE_DATA_DIR,
			osmFileKey));
		InputStream busStopFileBytes = new FileInputStream(new File(
			MainCtrlUtils.CACHE_DATA_DIR,
			busStopFileKey));
		BuildingEnergyProfileStrategy buildingEnergyProfileStrategy = new DefaultBuildingEnergyProfileStrategy();

		TrafficInfrastructureData trafficInfrastructureData = new OSMCityInfrastructureData(
			osmFileBytes,
			busStopFileBytes,
			buildingEnergyProfileStrategy);
		Long startTimestamp = startParameter.getStartTimestamp().getTime();
		long endTimestamp = startParameter.getEndTimestamp().getTime();
		ServerConfiguration serverConfiguration = ServerConfiguration.DEFAULT_SERVER_CONFIGURATION;
		simulationController.init(
			new TrafficInitParameter(trafficInfrastructureData,
				serverConfiguration,
				startTimestamp,
				endTimestamp,
				startParameter.getInterval(),
				startParameter.getClockGeneratorInterval(),
				startParameter.getOperationCenterAddress(),
				startParameter.getControlCenterAddress(),
				startParameter.getTrafficFuzzyData(),
				cityCtrl.retrieveBoundaries(trafficInfrastructureData)));
		simulationController.start(startParameter);
	}

	public void stopSimulation() {
		simulationController.stop();
	}

	public String getConnectionState() {
		return connectionState;
	}

	public void setConnectionState(String connectionState) {
		this.connectionState = connectionState;
	}

	public void setSimulationState(String simulationState) {
		this.simulationState = simulationState;
	}

	public String getSimulationState() {
		return simulationState;
	}

	public void setSimulationDate(Date simulationDate) {
		this.simulationDate = simulationDate;
	}

	public Date getSimulationDate() {
		return simulationDate;
	}

	public void setMapParsedState(String oSMParsedState) {
		this.mapParsedState = oSMParsedState;
	}

	public String getMapParsedState() {
		return mapParsedState;
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

//	@GET()
//	@Produces("text/plain")
//	@Path("/parseOSMAndBusstop/")	
	public void parseOSMAndBusstop() {
		System.out.println("kfldjs222");
	}

	private String parameterUploadData;

	public void setParameterUploadData(String parameterUploadData) {
		this.parameterUploadData = parameterUploadData;
	}

	public String getParameterUploadData() {
		return parameterUploadData;
	}

	public void onStartParameterUpload(FileUploadEvent event) {
		throw new UnsupportedOperationException(
			"implement start parameter deserialization");
	}

	private String parameterDownloadData;

	public void prepareExport() {
		this.parameterDownloadData = null;
		throw new UnsupportedOperationException("serialize");
	}

	public void deleteUncommittedEvent(Event event) {
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

	public void generateTree() {
		if (startParameter == null) {
			return;
		}
		Set<Field> visitedFields = new HashSet<>();
		Queue<Pair<TreeNode, Field>> queue = new LinkedList<>();
		for (Field field : startParameter.getClass().getDeclaredFields()) {
			queue.add(new MutablePair<>(startParameterTreeRoot,
				field));
		}
		while (!queue.isEmpty()) {
			Pair<TreeNode, Field> current = queue.poll();
			if (visitedFields.contains(current.getRight())) {
				continue;
			}
			TreeNode node = new DefaultTreeNode(current.getRight().getName(),
				current.getLeft());
			for (Field field : current.getRight().getDeclaringClass().
				getDeclaredFields()) {
				queue.add(new MutablePair<>(current.getLeft(),
					field));
			}
			visitedFields.add(current.getRight());
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

	/**
	 *
	 * @return
	 */
	/*
	 @TODO: make async
	 @TODO: improve streaming
	 */
	public StreamedContent retrieveExportDownloadLink() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		startParameterSerializerService.serialize(startParameter,
			outputStream);
		InputStream inputStream = new ByteArrayInputStream(outputStream.
			toByteArray());
		return new DefaultStreamedContent(inputStream,
			"xml",
			String.format("pgalise_start_parameter-%s.xml",
				new Date().toString()));
	}

	public StartParameterSerializerService getStartParameterSerializerService() {
		return startParameterSerializerService;
	}

	protected void setStartParameterSerializerService(
		StartParameterSerializerService startParameterSerializerService) {
		this.startParameterSerializerService = startParameterSerializerService;
	}
}
