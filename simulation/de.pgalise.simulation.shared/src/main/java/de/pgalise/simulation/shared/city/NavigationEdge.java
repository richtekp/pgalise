/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.city;

import com.vividsolutions.jts.geom.LineString;
import de.pgalise.simulation.shared.persistence.Identifiable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.vecmath.Vector2d;

/**
 * In this most abstract formulation <tt>NavigationEdge</tt> is not supposed to
 * have immutable start and end point (and dependet edge line), but creating an
 * immutable subclass is encouraged due to performance.
 *
 * @param <N> the type of the source and target node
 * @param <E> the type of the oncoming edge (to be used in more complex traffic
 * simulation models with allow lurching)
 * @author richter
 */
public interface NavigationEdge<N extends NavigationNode, E extends NavigationEdge<N,E>> extends Identifiable {
	
	/**
	 * Indicates when the last update has been transformed. This should be used to
	 * check whether a preceeding/dependent edge has been updates (as is should
	 * have been done by the {@link TrafficManager}.
	 *
	 * @return
	 */
	long getUpdateTimestamp();

	/*
	 * implementation notes:
	 * - LineSegment is not a Geometry and intersection can't be checked out-of-the--box (getCurve and getBoundary return null) and has always linear interpolation
	 * - math.Line is only linear
	 */
	LineString getEdgeLine();
	
	double getEdgeLength();
	
	/**
	 * forces the recalculation of length before the next call to {@link #getEdgeLength() }
	 */
	void invalidateLength();
	
	/**
	 * angle of the edge line vector between -180° and 180°
	 * @see GeodeticCalculator#getAzimuth() 
	 * @return 
	 */
	double getLineAzimuth();
	
	Vector2d getVector();
	
	void invalidateVector();
	
	N getSource();
	
	/*
	 * implementation notes: see setTarget
	 */
	void setSource(N source);
	
	N getTarget();
	
	/*
	 * implementation notes:
	 * - necessary for custom EdgeFactory which is necessary due to NavigationEdge being an AbstractIdentifiable rather than a DefaultEdge (can only inherit from one)
	 */
	void setTarget(N target);
	
	N getOpposite(NavigationNode node);
	
	/**
	 * legacy (to be removed in favour of two edged model)
	 * @return 
	 */
	boolean isOneWay();
	
	
	/**
	 * legacy (to be removed in favour of two edged model)
	 * 
	 * @param oneWay 
	 */
	void setOneWay(boolean oneWay);
}
