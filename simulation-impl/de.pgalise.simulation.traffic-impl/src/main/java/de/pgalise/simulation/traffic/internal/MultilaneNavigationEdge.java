//package de.pgalise.simulation.traffic.internal;
//
//import de.pgalise.simulation.shared.city.Coordinate;
//import de.pgalise.simulation.traffic.NavigationEdge;
//import de.pgalise.simulation.traffic.model.vehicle.BaseVehicle;
//import de.pgalise.simulation.traffic.model.vehicle.Bicycle;
//import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
//import java.util.Collections;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.ListIterator;
//import java.util.Map;
//import java.util.Set;
//import javax.persistence.Entity;
//import javax.persistence.OneToMany;
//import org.apache.commons.lang3.tuple.MutablePair;
//
///**
// * This represents a logical multilane edge.<br/>
// * The distance between vehicle on different lanes is not represented in their positions.
// * @author richter
// */
//@Entity
//public class MultilaneNavigationEdge extends DefaultExtendedNavigationEdge {
//	private static final long serialVersionUID = 1L;
//	
//	private static <T> boolean isCompletelyProcessed(List<ListIterator<T>> its) {
//		for(ListIterator<?> it : its) {
//			if(it.hasNext()) {
//				return false;
//			}
//		}
//		return true;
//	}
//	
//	private static <T> boolean checkNoProgress(List<ListIterator<T>> its, List<Integer> progressStore) {
//		if(its.size() != progressStore.size()) {
//			throw new IllegalArgumentException("internal error");
//		}
//		int i=0;
//		for(ListIterator<?> it : its) {
//			if(it.previousIndex() != progressStore.get(i)) {
//				return false;
//			}
//			i++;
//		}
//		return true;
//	}
//	
////	public static  Coordinate calculateNewPosition(Coordinate currentPosition, Vector2D direction, double velocity, long time) {
////		double distance = BaseVehicle.velocityToDistance(velocity, time);
////		Vector2D difference = new Vector2D(direction);
////		difference = difference.normalize();
////		difference = difference.multiply(VectorUtils.scaleFactorForLength(difference, distance));
////		Vector2D currentPositionVector = new Vector2D(currentPosition.getX(), currentPosition.getY());
////		currentPositionVector.add(difference);
////		return currentPositionVector.toCoordinate();
////	}
//	
//	private List<Double> maxSpeedOnLane;
//	
//	private boolean hasBikeLane = false;
//	
//	/**
//	 * indicates whether lurching into oncoming traffic is allowed
//	 */
//	private boolean lurchingAllowed = true;
//	
//	/**
//	 * indicates whether vehicles can lurch both left and right (e.g. in the city)
//	 */
//	private boolean lurchingRightAllowed = false;
//	
//	/**
//	 * A list of lanes which contains a list of pairs of vehicles and their distance on this lane.
//	 */
//	/*
//	 * implementation notes:
//	 * - is a list because for calculating distances goes from end to beginning a ListIterator can be used and List provides an iterator with deletion, i.e. ListIterator.
//	 */
//	private List<List<MutablePair<BaseVehicle<D>, Double>>> vehiclesOnLanes = new LinkedList<>();
//
//	@OneToMany
//	private List<Bicycle> bikesOnBikeLane = new LinkedList<>();
//	private List<Double> distanceAvailable = new LinkedList<>();
//
//	/**
//	 * calculates the distance between <tt>backwardsVehicle</tt> and <tt>advancedVehicle</tt>. Doesn't take into account the distance which results from distances between lanes.
//	 * @param backwardsVehicle
//	 * @param advancedVehicle
//	 * @param azimuth
//	 * @return the distance in the orientation or a <code>-1</code> if <code>position1</code> is not in the orientation
//	 */
//	private double calculateDistanceInOrientation(Coordinate backwardsVehicle, Coordinate advancedVehicle) {
//		throw new UnsupportedOperationException("");
////		return this.getEdgeLine().distance(backwardsVehicle, advancedVehicle);
////		Vector2D position0Vector = new Vector2D(position0.getX(), position0.getY());
////		Vector2D position1Vector = new Vector2D(position1.getX(), position1.getY());
////		position1Vector = position1Vector.subtract(position0Vector); //this.sub(t1) means this = this - t1
////		if(azimuth.angle(position1Vector) >= Math.PI/2) {
////			return -1;
////		}
////		return position0.distance(position1);
//	}
//
//	protected void setVehiclesOnLanes(List<List<MutablePair<BaseVehicle<D>, Double>>> vehiclesOnLanes) {
//		this.vehiclesOnLanes = vehiclesOnLanes;
//	}
//
//	public List<List<MutablePair<BaseVehicle<D>, Double>>> getVehiclesOnLanes() {
//		return vehiclesOnLanes;
//	}
//
//	public void setMaxSpeedOnLane(List<Double> maxSpeedOnLane) {
//		this.maxSpeedOnLane = maxSpeedOnLane;
//	}
//
//	public List<Double> getMaxSpeedOnLane() {
//		return maxSpeedOnLane;
//	}
//
//	public List<Bicycle> getBikesOnBikeLane() {
//		return bikesOnBikeLane;
//	}
//
//	public void setBikesOnBikeLane(List<Bicycle> bikesOnBikeLane) {
//		this.bikesOnBikeLane = bikesOnBikeLane;
//	}
//
//	public boolean getHasBikeLane() {
//		return hasBikeLane;
//	}
//
//	public void setHasBikeLane(boolean hasBikeLane) {
//		this.hasBikeLane = hasBikeLane;
//	}
//	
//	/**
//	 * moves all bikes form the lane register of the specified orientation into the bike lane register
//	 *
//	 */
//	public void updateBikeLaneAdded() {
//		for(List<MutablePair<BaseVehicle<D>, Double>> lane : vehiclesOnLanes) {
//			for(ListIterator<MutablePair<BaseVehicle<D>, Double>> it = lane.listIterator(); it.hasNext(); ) {
//				MutablePair<BaseVehicle<D>, Double> next = it.next();
//				if(next.getLeft() instanceof Bicycle) {
//					it.remove();
//					bikesOnBikeLane.add( (Bicycle) next.getLeft());
//				}
//			}
//		}
//	}
//
//	public void addLanes(int count) {
//		vehiclesOnLanes.addAll(Collections.nCopies(count, new LinkedList<MutablePair<BaseVehicle<D>,Double>>()));
//	}
//
//	@Override
//	public boolean takeVehicle(BaseVehicle<D> vehicle, long timestamp) {
//		double securityDistance = BaseVehicle.calculateSecurityDistance(vehicle.getCurrentVelocity());
//		double securityDistanceInM = securityDistance;
//		for(List<MutablePair<BaseVehicle<D>, Double>> lane : vehiclesOnLanes) {
//			if(lane.isEmpty()) {
//				lane.add(new MutablePair<BaseVehicle<D>, Double>(vehicle, 0.0));
//				vehicle.setCurrentEdge(this);
//				vehicle.getCurrentEdge().setRight(timestamp);
//				vehicle.getCurrentPosition().setLeft(getSource().getGeoLocation());
//				vehicle.getCurrentPosition().setRight(timestamp);
//				getVehicles().add(vehicle);
//				return true;
//			}
//			MutablePair<BaseVehicle<D>, Double> firstVehicle = lane.get(0);
//			double distance = getSource().getGeoLocation().distance(firstVehicle.getLeft().getCurrentPosition().getLeft());
//			double space = distance-securityDistanceInM;
//			if(space >= 0) {
//				lane.add(new MutablePair<BaseVehicle<D>, Double>(vehicle, 0.0));
//				vehicle.getCurrentEdge().setLeft(this);
//				vehicle.getCurrentEdge().setRight(timestamp);
//				vehicle.getCurrentPosition().setLeft(getSource().getGeoLocation());
//				vehicle.getCurrentPosition().setRight(timestamp);
//				getVehicles().add(vehicle);
//				return true;
//			}
//		}
//		return false;
//	}
//
//	@Override
//	public Set<BaseVehicle<D>> getLeavingVehicles() {
//		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//	}
//
//	@Override
//	public Map<BaseVehicle<D>, List<NavigationEdge>> updateVehicles(long timestamp) {
////		Vector2D targetSourceOrientationVector = new Vector2D(
////			getTarget().getGeoLocation().getX(), 
////			getTarget().getGeoLocation().y
////		);
////		targetSourceOrientationVector = targetSourceOrientationVector.subtract(
////			new Vector2D(
////				getSource().getGeoLocation().getX(), 
////				getSource().getGeoLocation().y
////			)
////		);
////		Vector2D sourceTargetOrientationVector = new Vector2D(targetSourceOrientationVector);
////		sourceTargetOrientationVector = sourceTargetOrientationVector.negate();
//		List<ListIterator<MutablePair<BaseVehicle<D>,Double>>> laneInverseIts = new LinkedList<>();
//		for(List<MutablePair<BaseVehicle<D>, Double>> lane : vehiclesOnLanes) {
//			laneInverseIts.add(lane.listIterator(lane.size()));
//		}
//		boolean start = true;
//		List<Integer> progressStore = new LinkedList<>(); //stores the index of the iterator of every lane to see whether there has been progress
//		complete:
//		while(!isCompletelyProcessed(laneInverseIts)) {
//			if(!start) {
//				int processedOrWaitingCount = 0;
//				outer:
//				for(int i=0; i< laneInverseIts.size(); i++) {
//					ListIterator<MutablePair<BaseVehicle<D>,Double>> laneInverseIt = laneInverseIts.get(i);
//					if(!laneInverseIt.hasPrevious()) {
//						//completely processed
//						processedOrWaitingCount++;
//						continue;
//					}
//					MutablePair<BaseVehicle<D>, Double> vehicleDistancePair = laneInverseIt.previous();
//					BaseVehicle<D> vehicle = vehicleDistancePair.getLeft();
//					double distance = vehicleDistancePair.getRight();
//					Measure<Integer,Length> securityDistance = BaseVehicle.calculateSecurityDistance(vehicle.getCurrentVelocity().getLeft());
//					double securityDistanceInM = securityDistance.getUnit().getConverterTo(SI.METER).convert(securityDistance.getValue());
//					//check lane switch possible
//					if(i+1 < laneInverseIts.size()) {
//						//has left neighbour lane -> find behind neighbour (has to check all vehicles on neighbour lane)
//						ListIterator<MutablePair<BaseVehicle<D>, Double>> leftLaneIt = vehiclesOnLanes.get(i+1).listIterator(vehiclesOnLanes.size());
//						while(leftLaneIt.hasPrevious()) {
//							if(leftLaneIt.nextIndex() < laneInverseIts.get(i+1).nextIndex()) {
//								//position is not updated yet
//								break;
//							}
//							MutablePair<BaseVehicle<D>, Double> leftLaneBehindNeighbourPair = leftLaneIt.previous();
//							BaseVehicle<D> leftLaneBehindNeighbour = leftLaneBehindNeighbourPair.getLeft();
//							double leftLaneAdvancedNeighbourDistance = leftLaneBehindNeighbourPair.getRight();
//							if(leftLaneAdvancedNeighbourDistance-securityDistanceInM < 0) {
//								continue;
//							}
//							double distanceInBackwardsOrientation = calculateDistanceInOrientation(vehicle.getCurrentPosition().getLeft(), leftLaneBehindNeighbour.getCurrentPosition().getLeft());
//							if(distanceInBackwardsOrientation != -1 || distanceInBackwardsOrientation < securityDistanceInM) {
//								continue;
//							}
//							Measure<Integer,Length> leftLaneBehindNeighbourSecurityDistance = BaseVehicle.calculateSecurityDistance(leftLaneBehindNeighbour.getCurrentVelocity().getLeft());
//							double leftLaneBehindNeighbourSecurityDistanceInM = leftLaneBehindNeighbourSecurityDistance.getUnit().getConverterTo(SI.METER).convert(leftLaneBehindNeighbourSecurityDistance.getValue()); //velocity has been updated if necessary (which would make the switch impossible anyway, but we can know that now)
//							if(distanceInBackwardsOrientation - securityDistanceInM - leftLaneBehindNeighbourSecurityDistanceInM < 0) {
//								continue;
//							}
//							//lane switch possible (just like normal advancing except change in lane register
//							MutablePair<BaseVehicle<D>, Double> leftLaneAdvancedNeighbourPair = leftLaneIt.next();
//							leftLaneIt.previous();
//							BaseVehicle<D> leftLaneAdvancedNeighbour = leftLaneAdvancedNeighbourPair.getLeft();
//							long deltaTime = timestamp -leftLaneAdvancedNeighbour.getCurrentPosition().getRight();
//							advanceVehicle(vehicle, leftLaneAdvancedNeighbour, deltaTime, timestamp);
//							laneInverseIt.remove();
//							leftLaneIt.add(vehicleDistancePair);
//							break complete;
//						}
//					}
//					if(i-1 > 0 && lurchingRightAllowed) {
//						//has right neighbour lane -> find behind neighbour (has to check all vehicles on neighbour lane)
//						ListIterator<MutablePair<BaseVehicle<D>, Double>> rightLaneIt = vehiclesOnLanes.get(i-1).listIterator(vehiclesOnLanes.size());
//						while(rightLaneIt.hasPrevious()) {
//							if(rightLaneIt.nextIndex() < laneInverseIts.get(i-1).nextIndex()) {
//								//position is not updated yet
//								break;
//							}
//							MutablePair<BaseVehicle<D>, Double> rightLaneBehindNeighbourPair = rightLaneIt.previous();
//							BaseVehicle<D> rightLaneBehindNeighbour = rightLaneBehindNeighbourPair.getLeft();
//							double rightLaneAdvancedNeighbourDistance = rightLaneBehindNeighbourPair.getRight();
//							if(rightLaneAdvancedNeighbourDistance-securityDistanceInM < 0) {
//								continue;
//							}
//							double distanceInBackwardsOrientation = calculateDistanceInOrientation(vehicle.getCurrentPosition().getLeft(), rightLaneBehindNeighbour.getCurrentPosition().getLeft());
//							if(distanceInBackwardsOrientation != -1 || distanceInBackwardsOrientation < securityDistanceInM) {
//								continue;
//							}
//							Measure<Integer,Length> rightLaneBehindNeighbourSecurityDistance = BaseVehicle.calculateSecurityDistance(rightLaneBehindNeighbour.getCurrentVelocity().getLeft());
//							double rightLaneBehindNeighbourSecurityDistanceInM = rightLaneBehindNeighbourSecurityDistance.getUnit().getConverterTo(SI.METER).convert(rightLaneBehindNeighbourSecurityDistance.getValue()); //velocity has been updated if necessary (which would make the switch impossible anyway, but we can know that now)
//							if(distanceInBackwardsOrientation - securityDistanceInM - rightLaneBehindNeighbourSecurityDistanceInM < 0) {
//								continue;
//							}
//							//lane switch possible (just like normal advancing except change in lane register
//							MutablePair<BaseVehicle<D>, Double> rightLaneAdvancedNeighbourPair = rightLaneIt.next();
//							rightLaneIt.previous();
//							BaseVehicle<D> rightLaneAdvancedNeighbour = rightLaneAdvancedNeighbourPair.getLeft();
//							long deltaTime = timestamp-rightLaneAdvancedNeighbour.getCurrentPosition().getRight();
//							advanceVehicle(vehicle, rightLaneAdvancedNeighbour, deltaTime, timestamp);
//							laneInverseIt.remove();
//							rightLaneIt.add(vehicleDistancePair);
//							break complete;
//						}
//					}
//				}
//				if(processedOrWaitingCount == laneInverseIts.size()) {
//					//zero or more lanes are process, except at least one; the latest will be processed -> find latest non-processed -> has to break
//					ListIterator<ListIterator<MutablePair<BaseVehicle<D>,Double>>> itsIt = laneInverseIts.listIterator(laneInverseIts.size());
//					while(itsIt.hasPrevious()) {
//						ListIterator<MutablePair<BaseVehicle<D>,Double>> it = itsIt.previous();
//						if(it.hasPrevious()) {
//							MutablePair<BaseVehicle<D>,Double> vehicleDistancePair = it.previous();
//							BaseVehicle<D> vehicle = vehicleDistancePair.getLeft();
//							BaseVehicle<D> advanced = it.next().getLeft();
//							it.previous();
//							double newDistance = calculateDistanceInOrientation(vehicle.getCurrentPosition().getLeft(), vehicle.getCurrentPosition().getLeft());
//							long deltaTime = timestamp -advanced.getCurrentPosition().getRight();
//							advanceVehicle(vehicle, advanced, deltaTime, timestamp, newDistance);
//							it.previous();
//							break complete;
//						}
//					}
//				}
//			}
//			for(ListIterator<MutablePair<BaseVehicle<D>,Double>> laneInverseIt : laneInverseIts) {
//				//updates as long as current iterator position is not updatable fast forward (i.e. lane switch has to be checked)
//			}
//			start = false;
//		}
//		return null;
//	}
//	
//	/**
//	 * advances the given vehicle and moves it on the next navigation edge if possible or reduces velocity according to security distance.
//	 * @param advanced the more advanced vehicle on the same lane whose position is already updated
//	 * @return the new distance to the advanced vehicle
//	 */
//	private double advanceVehicle(BaseVehicle<D> vehicle, BaseVehicle<D> advanced, long deltaTime, long timestamp) {
////		Coordinate newPosition = calculateNewPosition(vehicle.getCurrentPosition().getLeft(), orientation, vehicle.getCurrentVelocity().getLeft(), deltaTime);
//		
//		throw new UnsupportedOperationException();
//		
////		vehicle.getCurrentPosition().setLeft(newPosition);
////		vehicle.getCurrentPosition().setRight(timestamp);
////		return calculateDistanceInOrientation(vehicle.getCurrentPosition().getLeft(), advanced.getCurrentPosition().getLeft(), orientation);
//	}
//	
//	/**
//	 * does what {@link #advanceVehicle(de.pgalise.simulation.traffic.model.vehicle.Vehicle, de.pgalise.simulation.traffic.model.vehicle.Vehicle, javax.vecmath.Vector2D, long, long) } does, but updates current velocity of <tt>vehicle</tt> according to <tt>timeMillis</tt> and <tt>maxDistance</tt>
//	 * @param vehicle
//	 * @param advanced
//	 * @param orientation
//	 * @param deltaTime
//	 * @param timestamp
//	 * @param maxDistance
//	 * @return 
//	 */
//	private double advanceVehicle(BaseVehicle<D> vehicle, BaseVehicle<D> advanced, long deltaTime, long timestamp, double maxDistance) {
//		throw new UnsupportedOperationException();
//		
////		vehicle.getCurrentVelocity().setLeft((int)(maxDistance/deltaTime));
////		vehicle.getCurrentVelocity().setRight(timestamp);
////		return advanceVehicle(vehicle, advanced, deltaTime, timestamp);
//	}
//}
