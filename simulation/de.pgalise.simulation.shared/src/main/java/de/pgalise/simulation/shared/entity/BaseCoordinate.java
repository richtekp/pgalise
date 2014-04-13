/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.pgalise.simulation.shared.entity;

import com.vividsolutions.jts.geom.Coordinate;
import de.pgalise.simulation.shared.JaxRSCoordinate;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <tt>BaseCoordinate</tt> contains any geographical information for a point. 
 * It should not be used for navigation - use {@link NavigationNode} for that.
 * @author richter
 */
@Entity
public class BaseCoordinate extends JaxRSCoordinate {
	private static final long serialVersionUID = 1L;
  @Id
  private Long id;

  protected BaseCoordinate() {
  }

  public BaseCoordinate(Long id) {
    super();
    this.id = id;
  }

  /**
	 * Constructs a <code>Coordinate</code> having the same (x,y,z) values as
	 * <code>other</code>.
	 *
   * @param id
	 * @param c the <code>Coordinate</code> to copy.
	 */
  public BaseCoordinate(Long id,
    Coordinate c) {
    super(
      c); 
    this.id = id;
  }

	/**
	 * Constructs a <code>Coordinate</code> at (x,y,NaN).
	 *
   * @param id
	 * @param x the x-value
	 * @param y the y-value
	 */
	public BaseCoordinate(Long id,double x,
		double y) {
		super(x,
			y);
    this.id = id;
	}

  protected void setId(Long id) {
    this.id = id;
  }

  public Long getId() {
    return id;
  }
}
