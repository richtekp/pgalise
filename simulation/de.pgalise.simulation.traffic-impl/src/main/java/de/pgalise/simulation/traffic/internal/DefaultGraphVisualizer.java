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

import de.pgalise.simulation.shared.JaxRSCoordinate;
import de.pgalise.simulation.traffic.entity.TrafficEdge;
import de.pgalise.simulation.traffic.TrafficGraph;
import de.pgalise.simulation.traffic.entity.TrafficNode;
import de.pgalise.simulation.traffic.internal.graphextension.DefaultGraphExtensions;
import de.pgalise.simulation.traffic.entity.VehicleData;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.pgalise.util.generic.function.Function;
import de.pgalise.util.graph.GraphVisualizer;
import de.pgalise.simulation.shared.JaxbVector2d;

/**
 * @param <D>
 * @author Mustafa
 * @version 1.0 (Nov 22, 2012)
 */
public class DefaultGraphVisualizer<D extends VehicleData> extends JPanel
	implements GraphVisualizer<TrafficGraph>, WindowListener, MouseMotionListener,
	MouseListener, MouseWheelListener {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -3410547090601096528L;

	private List<Function> closeListener;
	protected TrafficGraph graph;
	protected Graphics2D g2d;
	protected AffineTransform transform;
	protected AffineTransform origin;
	private long lastCall;
	private JaxbVector2d srcClick;
	protected double scaleX, scaleY;
	protected double translateX, translateY;

	private DefaultGraphExtensions nodeExtensions;

	public DefaultGraphVisualizer(int width,
		int height) {
		init(width,
			height,
			null);
	}

	public DefaultGraphVisualizer(int width,
		int height,
		TrafficGraph graph) {
		init(width,
			height,
			graph);
		this.nodeExtensions = new DefaultGraphExtensions(graph);
	}

	private void init(int width,
		int height,
		TrafficGraph graph) {
		JFrame window = new JFrame("GraphVisualizer");
		window.setSize(width,
			height);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.addWindowListener(this);

		// JScrollPane scrollPanel =new JScrollPane(this);
		// this.setPreferredSize(new Dimension(width, height));
		// this.setBorder(BorderFactory.createLineBorder(Color.red));
		// window.add(scrollPanel);
		// scrollPanel.setVisible(true);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.addMouseWheelListener(this);

		window.add(this);
		window.setLocationRelativeTo(null);
		closeListener = new LinkedList<>();
		transform = new AffineTransform();
		this.graph = graph;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		JaxbVector2d v = new JaxbVector2d(e.getX(),
			e.getY());
		v.sub(srcClick);
		this.translateX += v.getX();
		this.translateY += v.getY();
		srcClick.add(v);
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2d = (Graphics2D) g;

		RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHints(rh);

		transform = new AffineTransform();
		transform.translate(translateX,
			translateY);
		transform.scale(scaleX,
			scaleY);

		for (Iterator<TrafficNode> i = graph.vertexSet().iterator(); i.hasNext();) {
			TrafficNode node = i.next();
			JaxRSCoordinate vec0 = this.nodeExtensions.getPosition(node);
			JaxbVector2d vec = new JaxbVector2d(
				(vec0.getX() * transform.getScaleX() + transform.getTranslateX()),
				(vec0.getY()
				* transform.getScaleY() + transform.getTranslateY()));
			// log.debug(String.format("Node %s pos: (%s, %s)", node.getId(), vec.getX(), vec.getY()));
			Ellipse2D.Double circle = new Ellipse2D.Double(vec.getX() - 5,
				vec.getY() - 5,
				10,
				10);
			g2d.fill(circle);

			Font font = new Font("Arial",
				Font.PLAIN,
				12);
			g2d.setFont(font);
			g2d.drawString(String.valueOf(node.getId()),
				(int) (vec.getX() + 5),
				(int) (vec.getY() - 5));
		}

		for (TrafficEdge edge : graph.edgeSet()) {
			JaxRSCoordinate a0 = this.nodeExtensions.getPosition(edge.getSource());
			JaxbVector2d a = new JaxbVector2d(a0.getX() * transform.getScaleX() + transform.
				getTranslateX(),
				a0.getY() * transform.getScaleY() + transform.getTranslateY());
			JaxRSCoordinate b0 = this.nodeExtensions.getPosition(edge.getTarget());
			JaxbVector2d b = new JaxbVector2d(b0.getX() * transform.getScaleX() + transform.
				getTranslateX(),
				b0.getY() * transform.getScaleY() + transform.getTranslateY());
			g2d.drawLine((int) a.getX(),
				(int) a.getY(),
				(int) b.getX(),
				(int) b.getY());
		}
	}

	public void setGraph(TrafficGraph graph) {
		this.graph = graph;
	}

	@Override
	public TrafficGraph getGraph() {
		return graph;
	}

	@Override
	public void draw() {
		long delta = System.currentTimeMillis() - lastCall;
		if (lastCall == 0 || delta > 33) {
			this.repaint();
			lastCall = System.currentTimeMillis();
		}
	}

	@Override
	public void translate(double x,
		double y) {
		translateX = x;
		translateY = y;
	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		for (Function l : closeListener) {
			l.delegate();
		}
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}

	@Override
	public void addWindowCloseListener(Function func) {
		closeListener.add(func);
	}

	@Override
	public void scale(double sx,
		double sy) {
		scaleX = sx;
		scaleY = sy;
	}

	@SuppressWarnings("unused")
	private void pushMatrix(final Graphics2D g2d,
		final AffineTransform transform) {
		origin = g2d.getTransform();
		g2d.setTransform(transform);
	}

	@SuppressWarnings("unused")
	private void popMatrix(final Graphics2D g2d) {
		g2d.setTransform(origin);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// log.debug(String.format("Source: (%s, %s) ", e.getX(), e.getY()));
		srcClick = new JaxbVector2d(e.getX(),
			e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {

			// make it a reasonable amount of zoom
			// .1 gives a nice slow transition
			scaleX -= (1 * e.getWheelRotation());
			scaleY -= (1 * e.getWheelRotation());
			// don't cross negative threshold.
			// also, setting scale to 0 has bad effects
			scaleX = Math.max(0.00001,
				scaleX);
			scaleY = Math.max(0.00001,
				scaleY);
		}
	}

	@Override
	public List<Function> getWindowCloseListener() {
		return this.closeListener;
	}

}
