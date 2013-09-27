///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//package de.pgalise.simulation.traffic.internal;
//
//import com.vividsolutions.jts.geom.Coordinate;
//import com.vividsolutions.jts.operation.distance.DistanceOp;
//import de.pgalise.simulation.shared.geotools.GeoToolsBootstrapping;
//import de.pgalise.simulation.traffic.NavigationEdge;
//import de.pgalise.simulation.traffic.NavigationNode;
//import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
//import java.text.MessageFormat;
//import java.util.ConcurrentModificationException;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//import javax.measure.Measure;
//import javax.measure.quantity.Duration;
//import javax.measure.quantity.Length;
//import javax.measure.unit.SI;
//import javax.persistence.Entity;
//import javax.vecmath.Vector2d;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
///**
// * A {@link NavigationEdge} which takes vehicles if the sum of security distances of vehicles on this edge is less than the length of the edge multiplied with the lane count. This means that the position of the vehicles on the edge, the sequence in which they appear and driving behavior is ignored.<br/>
// * Furthermore this edge assumes that vehicles don't change their velocity and keeps the vehicles at least for the time they need to pass the length of the edge. If the following edge can't take the vehicle, they stay on this edge, but they're status remains as if they where driving on the edge.
// * @param <N> 
// * @param <E> 
// * @author richter
// */
///*
// * implementation notes:
// * - default constructor is necessary and needs to be public for EdgeFactory 
// */
//@Entity
//public class DefaultExtendedNavigationEdge<N extends DefaultNavigationNode, E extends DefaultExtendedNavigationEdge<N,E>> extends AbstractNavigationEdge<N,E> {
//	private static final long serialVersionUID = 1L;
//	private final static Logger LOGGER = LoggerFactory.getLogger(DefaultExtendedNavigationEdge.class);
//	private int laneCount = 1;
//	
//	/**
//	 * initializes a <tt>DefaultNavigationEdge</tt> with 1 lane
//	 */
//	public DefaultExtendedNavigationEdge() {
//	}
//
//	public DefaultExtendedNavigationEdge(N source, N target, int laneCount) {
//		super(source, target);
//		this.laneCount = laneCount;
//		
//	}
//
//	protected void setLaneCount(int laneCount) {
//		this.laneCount = laneCount;
//	}
//
//	public int getLaneCount() {
//		return laneCount;
//	}
//
//	@Override
//	public boolean takeVehicle(Vehicle<?> vehicle, long timestamp) {
//		if(getVehicles().contains(vehicle)) {
//			throw new IllegalArgumentException("vehicle is already on this edge");
//		}
//		vehicle.setVelocity(vehicle.getData().getMaxVelocity());
//		vehicle.getCurrentVelocity().setRight(timestamp);
////		DoubleArrayList securityDistances = new DoubleArrayList(vehicles.size()+1);
////		for(Vehicle<?> vehicle0 : vehicles) {
////			double securityDistance = BaseVehicle.calculateSecurityDistance(vehicle0.getCurrentVelocity().getLeft());
////			securityDistances.add(securityDistance);
////		}
////		double securityDistance = BaseVehicle.calculateSecurityDistance(vehicle.getCurrentVelocity().getLeft());
////		securityDistances.add(securityDistance);
////		double averageVelocity = Descriptive.mean(securityDistances);
////		double averageSecurityDistance = BaseVehicle.calculateSecurityDistance(averageVelocity);
//		double securityDistanceSumInM = 0;
//		for(Vehicle<?> vehicle0 : getVehicles()) {
//			Measure<Integer,Length> securityDistance = BaseVehicle.calculateSecurityDistance(vehicle0.getCurrentVelocity().getLeft());
//			securityDistanceSumInM += securityDistance.getUnit().getConverterTo(SI.METER).convert(securityDistance.getValue());
//		}
//		double remainingSpace = getEdgeLength()*laneCount-securityDistanceSumInM;
//		double securityDistance = BaseVehicle.calculateSecurityDistance(vehicle.getCurrentVelocity().getLeft()).to(SI.METER).getValue();
//		if(remainingSpace-securityDistance < 0) {
//			return false;
//		}
//		getVehicles().add(vehicle);
//		vehicle.getCurrentEdge().setLeft(this);
//		vehicle.getCurrentEdge().setRight(timestamp);
//		vehicle.getCurrentPosition().setLeft(this.getSource().getGeoLocation());
//		vehicle.getCurrentPosition().setRight(timestamp);
//		return true;
//	}
//
//	@Override
//	public Set<Vehicle<?>> getLeavingVehicles() {
//		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//	}
//
//	/**
//	 * Expects current position of vehicle to be set.<br/>
//	 * Removes vehicles from vehicles collections of passed edges.
//	 * @param timestamp 
//	 */
//	@Override
//	public Map<Vehicle<?>, List<E>> updateVehicles(long timestamp) {
//		LOGGER.debug(String.format("updating %d vehicles", getVehicles().size()));
//		Map<Vehicle<?>, List<E>> retValue = new HashMap<>(getVehicles().size());
//		for(Vehicle<?> vehicle : getVehicles()) {
//			if(vehicle.getCurrentSimulationSteps().isEmpty()) {
//				LOGGER.debug(String.format("%s has no simulation steps left", vehicle));
//				continue;
//			}
//			long deltaTime = timestamp-vehicle.getCurrentPosition().getRight();
//			if(deltaTime < 0) {
//				throw new IllegalArgumentException("requested simulation timestamp is behind last update timestamp of vehicle "+vehicle);
//			}
//			vehicle.getCurrentVelocity().setLeft(vehicle.getData().getMaxVelocity());
//			vehicle.getCurrentVelocity().setRight(timestamp);
//			List<E> passedEdges = moveOnRoute(vehicle, new LongMeasure<>(deltaTime, SI.MILLI(SI.SECOND)));
//			retValue.put(vehicle, passedEdges);
//		}
//		for(Entry<Vehicle<?>,List<E>> entry : retValue.entrySet()) {
//			for(E passedEge : entry.getValue()) {
//				passedEge.getVehicles().remove(entry.getKey());
//			}
//		}
//		return retValue;
//	}
//	
//	private final static double EDGE_LENGTH_PASSED_TOLERANCE = 30.0;
//	
//	/**
//	 * calculates the distance between <tt>position</tt> and the start point of 
//	 * <tt>edge</tt>
//	 * @param edge
//	 * @param position
//	 * @return 
//	 */
//	private double edgeLengthPassedInM(E edge, Coordinate position) {
//		Coordinate[] nearestPoints = DistanceOp.nearestPoints(edge.getEdgeLine(), GeoToolsBootstrapping.getGEOMETRY_FACTORY().createPoint(new Coordinate(position.x, position.y)));
//		if(nearestPoints.length != 2) {
//			throw new RuntimeException(new IllegalArgumentException("nearest points of a line and a position haves to be a pair of coordinates"));
//		}
//		if(AbstractNavigationEdge.distanceHaversineInM(nearestPoints[0], nearestPoints[1]) > EDGE_LENGTH_PASSED_TOLERANCE) {
//			throw new IllegalArgumentException("position is not on edge");
//		}
//		double retValue = AbstractNavigationEdge.distanceHaversineInM(edge.getSource().getGeoLocation(), position);
//		return retValue;
//		
////		if(GeometryUtil.averageDistance(edge.getEdgeLine(), currentSimulationStep)
////			
////			edge.getEdgeLine().distance(GeoToolsBootstrapping.GEOMETRY_BUILDER.createPoint(position)) > 0.1) {
////			throw new IllegalArgumentException("position is not on edge line (distance is more than 0.1 m)");
////		}
////		return AbstractNavigationEdge.distanceHaversineInM(new Coordinate(edge.getSource().getGeoLocation().x, edge.getSource().getGeoLocation().y), new Coordinate(position.x, position.y));//edge.getSource().getGeoLocation().distance(position);
//	}
//	
//	/**
//	 * moves the distance calculated from velocity and time over the nodes and edges and tells which nodes have been visited by the return value.<br/>
//	 * Assumes that there're simulation steps left on the route.<br/>
//	 * This doesn't modify the vehicles collection of the passed edge(s) in order to avoid {@link ConcurrentModificationException}, so this has to be done somewhere else, but vehicles are added to new edges.
//	 * @param vehicle 
//	 * @param deltaTime 
//	 * @return a {@link List} with passed node (possibly empty) if the vehicle moved on the save {@link NavigationEdge} without reaching a node
//	 */
//	public List<E> moveOnRoute(Vehicle<?> vehicle, Measure<Long, Duration> deltaTime) {
//		LOGGER.debug(String.format("moving vehicle %s", this.getId()));
//		long newTimestamp = vehicle.getPosition().getRight()+deltaTime.longValue(SI.MILLI(SI.SECOND));
//		List<E> retValue = new LinkedList<>();
//		double distanceToDriveInM = BaseVehicle.velocityToDistanceInM(vehicle.getVelocity(), deltaTime);
//		double currentEdgeLength = vehicle.getCurrentEdge().getEdgeLength();
//		double currentEdgeRemainingLengthInM = currentEdgeLength;
//		double currentEdgeLengthPassedInM = edgeLengthPassedInM(vehicle.getCurrentEdge().getLeft(), vehicle.getCurrentPosition().getLeft());
//		currentEdgeRemainingLengthInM -= currentEdgeLengthPassedInM; //treat the first edge like vehicle is at the beginning and the edge is only currentEdgeLength-edgeLengthPassed long
//		LOGGER.debug(String.format("delta time: %f; current edge length: %f, current edge remaining length: %f, passed on edge: %f; distance to drive: %f", deltaTime.doubleValue(SI.SECOND), vehicle.getCurrentEdge().getLeft().getEdgeLength().doubleValue(SI.METER), currentEdgeRemainingLengthInM, currentEdgeLengthPassedInM, distanceToDriveInM));
//		
//		//moving on edge(s) until either all remaining edges have been passed completely (in this current step of deltaTime) or the distanceToDrive is not sufficient to reach a following edge (which mustn't be on the route plan because we can also be on the last edge already)
//		while(
//			distanceToDriveInM-currentEdgeRemainingLengthInM > 0 
//		) {
//			//go to next edge
//			LOGGER.debug(String.format("go to next edge (%d remaining)", vehicle.getCurrentSimulationSteps().peek().getRoute().size()));
//			distanceToDriveInM -= currentEdgeRemainingLengthInM;
//			E passedEdge = vehicle.getCurrentSimulationSteps().peek().getRoute().removeFirst(); 
//			retValue.add(passedEdge);
//			E newEdge = vehicle.getCurrentSimulationSteps().peek().getRoute().peekFirst();
//			if(newEdge == null) {
//				vehicle.getCurrentPosition().setLeft(passedEdge.getTarget().getGeoLocation());
//				vehicle.getCurrentPosition().setRight(newTimestamp);
//				//position on last edge on the route will be set under while loop
//				break;
//			}
//			newEdge.getVehicles().add(vehicle);
//			vehicle.getCurrentEdge().setLeft(newEdge);
//			vehicle.getCurrentEdge().setRight(newTimestamp);
//			currentEdgeRemainingLengthInM = vehicle.getCurrentEdge().getLeft().getEdgeLength().to(SI.METER).getValue();
//		}
//		if(!vehicle.getCurrentSimulationSteps().peek().getRoute().isEmpty()) {
//			//vehicle is now at beginning of edge which will not be passed completely -> set position on edge
//			Vector2d edgeSourceVector = new Vector2d(vehicle.getCurrentEdge().getLeft().getSource().getGeoLocation().x, 
//				vehicle.getCurrentEdge().getLeft().getSource().getGeoLocation().y);
//			Vector2d edgeTargetVector = new Vector2d(vehicle.getCurrentEdge().getLeft().getTarget().getGeoLocation().x, vehicle.getCurrentEdge().getLeft().getTarget().getGeoLocation().y);
//			
//			Coordinate newPosition;
////			if(distanceToDrive-currentEdgeRemainingLengthInM < 0 ) {
//				//Vector2d edgeVector = new Vector2d(); edgeVector.sub(edgeTarget, currentPositionVector);  //doesn't work because there's no difference in which order you put the arguments -> very strange
//				Vector2d edgeVector = new Vector2d(edgeTargetVector);
//				edgeVector.sub(edgeSourceVector);
//				edgeVector.scale(distanceToDriveInM/vehicle.getCurrentEdge().getLeft().getEdgeLength().doubleValue(SI.METER));
//
//				Vector2d currentPositionVector = new Vector2d(vehicle.getCurrentPosition().getLeft().x, vehicle.getCurrentPosition().getLeft().y);
//				currentPositionVector.add(edgeVector);
//				newPosition = new Coordinate(currentPositionVector.x, currentPositionVector.y);
////			}else {
////				distanceToDrive -= currentEdgeRemainingLengthInM;
////				
////			}
//			
//			LOGGER.debug(String.format("new position: %s; distance to last position: %f; distance to edge source: %f", newPosition, AbstractNavigationEdge.distanceHaversineInM(vehicle.getCurrentPosition().getLeft(), newPosition), AbstractNavigationEdge.distanceHaversineInM(vehicle.getCurrentEdge().getLeft().getSource().getGeoLocation(), newPosition)));
//			vehicle.getCurrentPosition().setLeft(newPosition);
//			vehicle.getCurrentPosition().setRight(newTimestamp);
//			vehicle.getGpsSensor().getPosition().setLeft(newPosition);
//			vehicle.getGpsSensor().getPosition().setRight(newTimestamp);
//		}else {
//			LOGGER.debug(String.format("processed route"));
//		}
//		
//		//check nodes' distances (whether vehicle can be considered to be on a node)
//		double sourceNodeDistance = AbstractNavigationEdge.distanceHaversineInM(vehicle.getCurrentEdge().getLeft().getSource().getGeoLocation(), vehicle.getCurrentPosition().getLeft());
//		if(sourceNodeDistance <= DefaultNavigationNode.NODE_RADIUS.intValue(SI.METER)) {
//			vehicle.getCurrentNode().setLeft(vehicle.getCurrentEdge().getLeft().getSource());
//			vehicle.getCurrentNode().setRight(newTimestamp);
//		}else {
//			double targetNodeDistance = AbstractNavigationEdge.distanceHaversineInM(vehicle.getCurrentEdge().getLeft().getTarget().getGeoLocation(), vehicle.getCurrentPosition().getLeft());
//			if(targetNodeDistance <= DefaultNavigationNode.NODE_RADIUS.intValue(SI.METER)) {
//				vehicle.getCurrentNode().setLeft(vehicle.getCurrentEdge().getLeft().getTarget());
//				vehicle.getCurrentNode().setRight(newTimestamp);
//			}
//		}
//		return retValue;		
//	}
//	
//	/**
//	 * Moves on route for <tt>timeOnRoute</tt> (in 10^-3 s) and then leaves route and moves for <tt>timeOffRoute</tt> in <tt>azimuth</tt> or has to stay on route in the way can't be skipped (in this case 
//	 * @param awayFrom
//	 * @param velocity
//	 * @param direction
//	 * @return 
//	 */
//	private Coordinate moveOffRoute(long timeOnRoute, long timeOffRoute, double azimuth) {
//		throw new UnsupportedOperationException();
////		GeodeticCalculator calculator = new GeodeticCalculator();
////		cal
////		
////		
////		Vector2D awayFromVector = new Vector2D(awayFrom);
////		Vector2D directionClone = new Vector2D(direction);
////		double distance = velocityToDistance(this.getCurrentVelocity().getLeft(), timeOnRoute);
////		directionClone = directionClone.multiply(VectorUtils.scaleFactorForLength(directionClone, distance));
////		awayFromVector.add(directionClone);
////		return awayFromVector.toCoordinate();
//	}
//	
//}
