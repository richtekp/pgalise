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
 
package de.pgalise.util.graph;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 * @author Mustafa
 * @version 1.0 (Nov 22, 2012)
 */
public class WindowExample extends JFrame implements WindowListener, ComponentListener {
	/**
	 * Serial
	 */
	private static final long serialVersionUID = -7305238716647619331L;
	private JSplitPane splitPane;
	private static final int SETTINGS_PANEL_SIZE = 200;

	public WindowExample(String title, int width, int height) {
		super(title);
		this.setSize(width, height);

		JPanel settingsPanel = createSettingsPanel();
		JPanel surfacePanel = createSurfacePanel();

		// Create a split pane with the two scroll panes in it.
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, surfacePanel, settingsPanel);

		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(width - SETTINGS_PANEL_SIZE);

		this.add(splitPane);
		this.addComponentListener(this);
	}

	private JPanel createSettingsPanel() {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Settings");
		panel.add(label);

		return panel;
	}

	private JPanel createSurfacePanel() {
		JPanel panel = new JPanel();
		JLabel label = new JLabel("Surface");
		panel.add(label);
		return panel;
	}

	public static void main(String args[]) {
		WindowExample window = new WindowExample("test", 500, 300);
		window.setVisible(true);
	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {

	}

	@Override
	public void windowClosed(WindowEvent e) {
		System.exit(0);
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
	public void componentResized(ComponentEvent e) {
		splitPane.setDividerLocation(this.getWidth() - SETTINGS_PANEL_SIZE);
	}

	@Override
	public void componentMoved(ComponentEvent e) {

	}

	@Override
	public void componentShown(ComponentEvent e) {

	}

	@Override
	public void componentHidden(ComponentEvent e) {

	}
}
