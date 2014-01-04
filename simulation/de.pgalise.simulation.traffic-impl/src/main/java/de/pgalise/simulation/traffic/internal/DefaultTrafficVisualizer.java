/* 
 * Copyright 2013 PG Alise (http://www.pg-alise.de/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package de.pgalise.simulation.traffic.internal;

import de.pgalise.simulation.traffic.TrafficGraph;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.List;

import de.pgalise.simulation.traffic.TrafficGraphExtensions;
import de.pgalise.simulation.traffic.TrafficVisualizer;
import de.pgalise.simulation.traffic.model.vehicle.Vehicle;
import de.pgalise.simulation.traffic.model.vehicle.VehicleData;
import de.pgalise.util.generic.async.AsyncHandler;
import de.pgalise.util.generic.async.impl.ThreadPoolHandler;
import de.pgalise.util.generic.function.Function;
import de.pgalise.simulation.shared.city.Vector2d;

/**
 * Default implementation of the TrafficVisualizer.
 *
 * @param <D>
 * @author Mustafa
 * @author Marina
 */
public class DefaultTrafficVisualizer<D extends VehicleData> extends DefaultGraphVisualizer<D>
	implements TrafficVisualizer {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -5898703038599896775L;

	private List<Vehicle<?>> vehicles;

	private AsyncHandler handler;

	private boolean finished;

	private TrafficGraphExtensions trafficGraphExtensions;

	public DefaultTrafficVisualizer(int width,
		int height,
		TrafficGraphExtensions ee) {
		super(width,
			height);
		this.trafficGraphExtensions = ee;
		init();
	}

	public DefaultTrafficVisualizer(int width,
		int height,
		TrafficGraph graph,
		TrafficGraphExtensions ee) {
		super(width,
			height,
			graph);
		this.trafficGraphExtensions = ee;
		init();
	}

	private void init() {
		handler = new ThreadPoolHandler();
		handler.setCallback(new Function() {
			@Override
			public void delegate() {
				finished = true;
			}
		});

		this.addWindowCloseListener(new Function() {

			@Override
			public void delegate() {
				System.exit(0);
			}

		});
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (vehicles == null) {
			return;
		}

		// wieder einkommentieren, wenn randomseedservice wieder korrekt übergeben wird
		// for (final Node node : this.graph) {
		// Vector2d pos = ne.getPosition(node);
		// pos = new Vector2d((pos.getX() * transform.getScaleX() + transform.getTranslateX()), (pos.y
		// * transform.getScaleX() + transform.getTranslateY()));
		// final TrafficRule trafficRule = ne.getTrafficRule(node);
		// if (!(trafficRule instanceof TrafficLightSetof)) {
		// if (trafficRule instanceof Roundabout) {
		// g2d.setColor(Color.RED);
		// } else if (trafficRule instanceof LeftYieldsToRight) {
		// g2d.setColor(Color.GREEN);
		// } else if (trafficRule instanceof StraightForwardRule) {
		// g2d.setColor(Color.BLACK);
		// }
		//
		// Ellipse2D.Double circle = new Ellipse2D.Double(pos.getX() - 5, pos.getY() - 5, 10, 10);
		// g2d.fill(circle);
		// } else {
		// TrafficLightSetof trafficLightSetof = (TrafficLightSetof) trafficRule;
		// for (final Edge edge : node) {
		// final Vector2d vec = ne.getVectorBetween(node, edge.getOpposite(node)).normalize();
		// final double factor = 20;
		// Rectangle2D.Double rect = new Rectangle2D.Double(pos.getX() - vec.normalize().getX() * factor - 5,
		// pos.getY() - vec.normalize().getY() * factor - 10, 10, 22);
		//
		// Ellipse2D.Double red = new Ellipse2D.Double(pos.getX() - vec.normalize().getX() * factor - 3,
		// pos.getY() - vec.normalize().getY() * factor - 9, 6, 6);
		// Ellipse2D.Double yellow = new Ellipse2D.Double(pos.getX() - vec.normalize().getX() * factor - 3,
		// pos.getY() - vec.normalize().getY() * factor - 2, 6, 6);
		// Ellipse2D.Double green = new Ellipse2D.Double(pos.getX() - vec.normalize().getX() * factor - 3,
		// pos.getY() - vec.normalize().getY() * factor + 5, 6, 6);
		//
		// g2d.setColor(Color.BLACK);
		// g2d.fill(rect);
		// g2d.setColor(Color.RED);
		// g2d.setColor(Color.YELLOW);
		// g2d.setColor(Color.GREEN);
		// switch (trafficLightSetof.getTrafficLightForEdge(edge).getState()) {
		// case RED:
		// g2d.setColor(Color.RED);
		// g2d.fill(red);
		// break;
		// case YELLOW:
		// g2d.setColor(Color.YELLOW);
		// g2d.fill(yellow);
		// break;
		// case RED_YELLOW:
		// g2d.setColor(Color.RED);
		// g2d.fill(red);
		// g2d.setColor(Color.YELLOW);
		// g2d.fill(yellow);
		// break;
		// case GREEN:
		// g2d.setColor(Color.GREEN);
		// g2d.fill(green);
		// break;
		// case BLINKING:
		// break;
		// }
		// }
		// }
		// }
		g2d.setColor(Color.BLACK);
		for (Vehicle<?> v : vehicles) {
			Vector2d pos = new Vector2d(v.getPosition().getX(),
				v.getPosition().getY());
			pos = new Vector2d((pos.getX() * transform.getScaleX() + transform.
				getTranslateX()),
				(pos.getY()
				* transform.getScaleX() + transform.getTranslateY()));
			// log.debug(String.format("Car %s pos: (%s, %s)", v.getName(),
			// pos.getX(), pos.getY()));

			// Vector2d opos = pos;
			Vector2d dir = v.getDirection();
			// Orthogonale zeigt nach rechts
			Vector2d ortho = new Vector2d(dir.getY(),
				dir.getX() * (-1));
			Vector2d ortho2 = ortho;
			ortho2.scale(-1);

			dir.normalize();
			ortho.normalize();
			ortho2.normalize();

			Vector2d d = dir;
			d.scale(12);
			pos.sub(d);

			// Skalieren
			dir.scale(24);
			ortho.scale(8);
			ortho2.scale(8);

			dir.add(pos);
			ortho.add(pos);
			ortho2.add(pos);

			// Dreieck erstellen
			Polygon p = new Polygon();
			p.addPoint((int) ortho2.getX(),
				(int) ortho2.getY());
			p.addPoint((int) dir.getX(),
				(int) dir.getY());
			p.addPoint((int) ortho.getX(),
				(int) ortho.getY());

			// log.debug(String.format("ortho: (%s, %s)", ortho.getX(),
			// ortho.getY()));
			// log.debug(String.format("ortho2: (%s, %s)", ortho2.getX(),
			// ortho2.getY()));
			// log.debug(String.format("dir: (%s, %s)", dir.getX(),
			// dir.getY()));
			g2d.setPaint(Color.ORANGE);
			g2d.fillPolygon(p);

			g2d.setPaint(Color.RED);
			Font font = new Font("Arial",
				Font.BOLD,
				12);
			g2d.setFont(font);
			g2d.drawString(v.getName(),
				(int) (pos.getX() + 5),
				(int) (pos.getY() - 5));
		}
	}

	@Override
	public void setVehicles(List<Vehicle<?>> vehicles) {
		this.vehicles = vehicles;
	}

	@Override
	public List<Vehicle<?>> getVehicles() {
		return this.vehicles;
	}

	@Override
	public void addCommand(Function command) {
		handler.addDelegateFunction(command);
	}

	@Override
	public void start() {
		handler.start();
	}

	@Override
	public boolean finishedCommands() {
		return finished;
	}

}
