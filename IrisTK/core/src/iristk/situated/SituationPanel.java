/*******************************************************************************
 * Copyright (c) 2014 Gabriel Skantze.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Gabriel Skantze - initial API and implementation
 ******************************************************************************/
package iristk.situated;

import iristk.system.Event;
import iristk.system.InitializationException;
import iristk.system.IrisGUI;
import iristk.system.IrisSystem;
import iristk.util.ColorGenerator;
import iristk.util.Record;
import iristk.util.RepaintThread;
import iristk.util.SwingUtils;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.util.HashMap;

public class SituationPanel extends JPanel implements SituationListener  {

	public static final int TOPVIEW = 0;
	public static final int SIDEVIEW = 1;
	
	private static final String SITWIN = "sitwin";

	public Color backgroundColor = Color.white;

	public Color supportLineOrigoColor = Color.MAGENTA;
	public Color supportLineMajorColor = Color.darkGray;
	public double supportLineMajorInterval = 1;
	public Color supportLineMinorColor = Color.lightGray;
	public double supportLineMinorInterval = 0.1;

	public Color gazeColor = Color.red;

	public Color audioSourceColor = Color.black;

	public double worldTop = -1.5f;
	public double worldLeft = -1.5f;

	public double zoom = 150;
	private int buttonPressed;

	private int bodyCount = 0;

	private int view;
	private File positionsFile = null;

	private final Stroke gazeStroke = new BasicStroke(1.0f, // line width
			BasicStroke.CAP_BUTT,  //cap style 
			BasicStroke.JOIN_BEVEL,  //join style
			1.0f,  //miter limit 
			new float[] { 8.0f, 3.0f, 2.0f, 3.0f },  //the dash pattern, on 8, off 3, on 2, off 3
			0.0f); // the dash phase
	private final Stroke armStroke = new BasicStroke(3);

	private Point lastMousePoint;

	private String selected = null;

	private HashMap<String,Long> lastReported = new HashMap<>();
	//private SenseBodyThread senseBodyThread;
	private String statusLine;
	private Graphics2D g2d;
	private RepaintThread repaintThread;
	private Color agentFill;
	private Color agentOutline;
	private Situation situation;
	private SituationModule situationModule;

	public SituationPanel(IrisGUI gui, IrisSystem system, int view) throws InitializationException {
		this(system, view);
		gui.addDockPanel(
				"situation-" + (view == TOPVIEW ? "top" : "side"),
				"Situation " + (view == TOPVIEW ? "Top" : "Side"),
				this, true);
	}

	public SituationPanel(IrisSystem system, int view) throws InitializationException {
		this.view = view;
		situationModule = new SituationModule();
		situation = situationModule.getSituation();
		situation.addSituationListener(this);
		if (view == TOPVIEW) {
			worldTop = -0.5;
			worldLeft = -1.5;
		} else {
			worldTop = -1.5;
			worldLeft = -0.5;
		}
		repaintThread = new RepaintThread(this);
		addMouseWheelListener(new MyMouseWheelListener());
		addMouseMotionListener(new MyMouseMotionListener());
		addMouseListener(new MyMouseListener());
		system.addModule("situation-" + (view == TOPVIEW ? "top" : "side"), situationModule);
	}

	private class MyMouseWheelListener implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			int notches = e.getWheelRotation();
			zoom -= notches * 10;
			if (zoom < 20) zoom = 20;
			repaintThread.repaint();
		}
	};

	private class MyMouseMotionListener implements MouseMotionListener {

		@Override
		public void mouseMoved(MouseEvent e) {
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (buttonPressed == MouseEvent.BUTTON1) {
				if (selected != null) {
					if (lastReported.get(selected) == null || System.currentTimeMillis() - lastReported.get(selected) > 100) {
						Sensor sensor = situation.getSensors().get(selected);
						Body userBody = situation.getBody(selected);
						SystemAgent sysAgent = situation.getSystemAgents().get(selected);
						if (sensor != null) {
							Location newLocation = pixelCoordsToLocation(e.getX(), e.getY(), sensor.location);
							updateSensorPosition(selected, newLocation, sensor.rotation);
							statusLine = "location: " + newLocation;
						} else if (userBody != null) {
							Location newLocation = pixelCoordsToLocation(e.getX(), e.getY(), userBody.getHeadLocation());
							senseBody(userBody.sensor, newLocation, null, -1);
							statusLine = "location: " + newLocation;
						} else if (sysAgent != null) {
							Location newLocation = pixelCoordsToLocation(e.getX(), e.getY(), sysAgent.getHeadLocation());
							senseSystemAgent(selected, newLocation, sysAgent.rotation);
							statusLine = "location: " + newLocation;
						}
					}
				} else {
					Point newMousePoint = e.getPoint();

					int xDrag = newMousePoint.x - lastMousePoint.x;
					int yDrag = newMousePoint.y - lastMousePoint.y;

					lastMousePoint = newMousePoint;

					double xDragMeters = pixelsToMeters(xDrag);
					worldLeft -= xDragMeters;

					double yDragMeters = pixelsToMeters(yDrag);
					worldTop -= yDragMeters;
				}

				repaintThread.repaint();
			} 
		}
	};

	private class MyMouseListener extends MouseAdapter {

		@Override
		public void mouseReleased(MouseEvent arg0) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			buttonPressed = e.getButton();
			if (buttonPressed == MouseEvent.BUTTON1) {
				lastMousePoint = e.getPoint();
				//if (SituationPanel.this.system != null) {
				selected = getSensor(e.getX(), e.getY());
				Body body = getBody(e.getX(), e.getY()); 
				if (selected == null && body != null)
					selected = body.id;
				if (selected == null)
					selected = getSystemAgent(e.getX(), e.getY());
				if (selected == null)
					statusLine = null;

				repaintThread.repaint();
				//}
			} else if (buttonPressed == MouseEvent.BUTTON3 && e.getModifiers() == 6) {
				//if (SituationPanel.this.system != null) 
				senseRotation(e.getX(), e.getY());
			} else if (buttonPressed == MouseEvent.BUTTON3) {
				showContextMenu(e);
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
				selected = null;
				Body body = getBody(e.getX(), e.getY());
				if (body != null) {
					senseBody(body.sensor, null, null, 0);
				} else {
					senseBody(null, pixelCoordsToLocation(e.getX(), e.getY(), null), null, -1);
				}
			}
		}
	};

	private void showContextMenu(MouseEvent e) {
		JPopupMenu menu = new JPopupMenu();
		JMenu sensorMenu = new JMenu("Sensors");
		menu.add(sensorMenu);
		JMenuItem item = new JMenuItem("Save positions");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (positionsFile != null)
					situationModule.savePositions(positionsFile);
			}
		});
		sensorMenu.add(item);
		item = new JMenuItem("Load positions");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (positionsFile != null)
					situationModule.loadPositions(positionsFile);
			}
		});
		sensorMenu.add(item);
		sensorMenu.addSeparator();
		for (final String sensor : situation.getSensors().keySet()) {
			item = new JMenuItem(sensor);
			item.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println(situation.getSensors().get(sensor));
				}
			});
			sensorMenu.add(item);
		}
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	/*
	private class SenseBodyThread extends Thread {

		private Event event;

		public SenseBodyThread() {
			start();
		}

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized (senseBodyThread) {
					if (event != null) {
						SituationPanel.this.system.send(event, "SituationWindow");
						event = null;
					}
				}
			}
		}

		public void newEvent(Event event) {
			synchronized (senseBodyThread) {
				this.event = event;
			}
		}

	}
	 */

	public void setPositionsFile(File file) {
		positionsFile = file;
	}

	private void senseRotation(int x, int y) {
		if (selected != null) {
			Sensor sensor = situation.getSensors().get(selected);
			Body body = situation.getBody(selected);
			SystemAgent sysAgent = situation.getSystemAgents().get(selected);
			if (body != null) {
				senseBody(body.sensor, body.head.location, pixelCoordsToLocation(x, y, body.gaze), -1);
			} else if (sysAgent != null) {
				Rotation rot = pixelCoordsToLocation(x, y, null).subtract(sysAgent.getHeadLocation()).toRotation();
				if (view == TOPVIEW) {
					sysAgent.rotation.y = rot.y;
				} else {
					sysAgent.rotation.x = rot.x;
				}
				senseSystemAgent(sysAgent.id, sysAgent.location, sysAgent.rotation);
				statusLine = "rotation: " + rot;
			} else if (sensor != null) {
				Rotation rot = pixelCoordsToLocation(x, y, null).subtract(sensor.location).toRotation();
				if (view == TOPVIEW) {
					sensor.rotation.y = rot.y;
				} else {
					sensor.rotation.x = rot.x;
				}
				updateSensorPosition(selected, sensor.location, sensor.rotation);
				statusLine = "rotation: " + rot;
			}
		}
	}

	private void updateSensorPosition(String sensorId, Location location, Rotation rotation) {
		lastReported.put(sensorId, System.currentTimeMillis());
		Event event = new Event("sense.situation");
		event.put(sensorId + ":rotation", rotation);
		event.put(sensorId + ":location", location);
		situationModule.send(event);
	}

	private void senseBody(String sensorBodyId, Location location, Location gaze, int expire) {
		if (sensorBodyId == null) {
			sensorBodyId = "body-" + bodyCount++;
		} else {
			sensorBodyId = sensorBodyId.replace(SITWIN + "-", "");
		}
		lastReported.put(sensorBodyId, System.currentTimeMillis());
		Body oldBody = situation.getBodyBySensorId(SITWIN, sensorBodyId);
		Body body = new Body();
		body.expire = expire;
		if (location != null)
			body.head.location = location;
		if (gaze != null) {
			Rotation headrot = gaze.subtract(oldBody.getHeadLocation()).toRotation();
			body.head.rotation = headrot;
			body.gaze = gaze;
			statusLine = "rotation: " + headrot;
		} else if (oldBody != null && oldBody.gaze != null && location != null) {
			body.head.rotation = oldBody.gaze.subtract(location).toRotation();
			body.gaze = oldBody.gaze;
		}
		body.put("id", sensorBodyId);
		Event event = new Event("sense.body");
		event.put("sensor", SITWIN);
		event.put("bodies:" + sensorBodyId, body);
		situationModule.send(event);
	}

	private void senseSystemAgent(String agentId, Location location, Rotation rotation) {
		lastReported.put(agentId, System.currentTimeMillis());
		Event event = new Event("sense.situation");
		Record data = new Record();
		data.put("location", location);
		data.put("rotation", rotation);
		event.put(agentId, data);
		situationModule.send(event);
	}

	private String getSensor(int x, int y) {
		double mindist = 20;
		String minId = null;
		for (String sensorId : situation.getSensors().keySet()) {
			Sensor sensor = situation.getSensors().get(sensorId);
			if (sensor.hasPosition() && sensor.location != null) {
				int bx, by;
				if (view == TOPVIEW) {
					bx = metersToHorizontalPixel(sensor.location.x);
					by = metersToVerticalPixel(sensor.location.z);
				} else {
					bx = metersToHorizontalPixel(sensor.location.z);
					by = metersToVerticalPixel(-sensor.location.y);
				}
				double dist = Math.sqrt(Math.pow(x - bx, 2) + Math.pow(y - by, 2));
				if (dist < mindist) {
					mindist = dist;
					minId = sensorId;
				}
			}
		}
		return minId;
	}

	private Body getBody(int x, int y) {
		double mindist = 20;
		Body minbody = null;
		for (String bodyId : situation.getBodies().keySet()) {
			Body agent = situation.getBodies().get(bodyId);
			//String bodyId = situation.getSensorBodyId(SITWIN, agent);
			//Check that this agent is manually inserted
			if (agent.sensor.startsWith(SITWIN)) {
				Location location = agent.getHeadLocation();
				int bx, by;
				if (view == TOPVIEW) {
					bx = metersToHorizontalPixel(location.x);
					by = metersToVerticalPixel(location.z);
				} else {
					bx = metersToHorizontalPixel(location.z);
					by = metersToVerticalPixel(-location.y);
				}
				double dist = Math.sqrt(Math.pow(x - bx, 2) + Math.pow(y - by, 2));
				if (dist < mindist) {
					mindist = dist;
					minbody = agent;
				}
			}
		}
		return minbody;
	}

	private String getSystemAgent(int x, int y) {
		double mindist = 20;
		String minbody = null;
		for (String agentId : situation.getSystemAgents().keySet()) {
			Body agent = situation.getSystemAgents().get(agentId);
			Location location = agent.getHeadLocation();
			int bx, by;
			if (view == TOPVIEW) {
				bx = metersToHorizontalPixel(location.x);
				by = metersToVerticalPixel(location.z);
			} else {
				bx = metersToHorizontalPixel(location.z);
				by = metersToVerticalPixel(-location.y);
			}
			double dist = Math.sqrt(Math.pow(x - bx, 2) + Math.pow(y - by, 2));
			if (dist < mindist) {
				mindist = dist;
				minbody = agent.id;
			}
		}
		return minbody;
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		g2d = (Graphics2D)graphics;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		paintBackground();

		synchronized (situation) {

			for (String id : situation.getSystemAgents().keySet()) {
				SystemAgent agent = situation.getSystemAgents().get(id);
				for (Space space : agent.getInteractionSpaces()) {
					paintInteractionSpace(agent, space);
				}
			}

			for (Sensor sensor : situation.getSensors().values()) {
				if (sensor.hasPosition())
					paintItem(sensor);
			}

			for (String itemId : situation.getItems().keySet()) {
				paintItem(situation.getItems().get(itemId));
			}

			for (String id : situation.getSystemAgents().keySet()) {
				Body agent = situation.getSystemAgents().get(id);
				if (view == TOPVIEW)
					paintBodyTop(g2d, agent);
				else if (view == SIDEVIEW)
					paintBodySide(g2d, agent);
			}
			for (String id : situation.getBodies().keySet()) {
				Body agent = situation.getBodies().get(id);

				FIND_USER:
					for (SystemAgent a : situation.getSystemAgents().values()) {
						for (Agent u : a.getUsers()) {
							if (u.sensor.equals(agent.sensor)) {
								agent = u;
								break FIND_USER;
							}
						}
					}	

				if (view == TOPVIEW)
					paintBodyTop(g2d, agent);
				else if (view == SIDEVIEW)
					paintBodySide(g2d, agent);
			}
		}

		printStatus();

	}

	private void printStatus() {
		if (statusLine != null) {
			g2d.setColor(Color.black); 
			g2d.drawString(statusLine, 10, getHeight() - 20);
		}
	}

	private double pixelsToMeters(int pixels) {
		return pixels / zoom;
	}

	private int metersToPixels(double meters) {
		return (int) Math.round(meters * zoom);
	}

	private int metersToVerticalPixel(double meters) {
		return metersToPixels(meters - worldTop);
	}

	private int metersToHorizontalPixel(double meters) {
		return metersToPixels(meters - worldLeft);
	}

	private Location pixelCoordsToLocation(int x, int y, Location oldLocation) {
		if (view == TOPVIEW) {
			return new Location(pixelsToMeters(x) + worldLeft, oldLocation == null ? 0 : oldLocation.y, pixelsToMeters(y) + worldTop);
		} else {
			return new Location(oldLocation == null ? 0 : oldLocation.x, -(pixelsToMeters(y) + worldTop), pixelsToMeters(x) + worldLeft);
		}
	}

	private void paintBackground() {
		Color originalColor = g2d.getColor();
		g2d.setColor(backgroundColor);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.setColor(originalColor);

		paintGrid(0, 0, supportLineMinorInterval, supportLineMinorColor);
		paintGrid(0, 0, supportLineMajorInterval, supportLineMajorColor);
		paintGrid(0, 0, 100, supportLineOrigoColor);
	}

	private void paintGrid(double x, double y, double interval, Color color) {
		Color originalColor = g2d.getColor();
		g2d.setColor(color);

		int lineCount = 0;
		boolean drewLine;

		do {
			drewLine = false;

			int lineX;
			int lineY;

			// Left
			lineX = metersToHorizontalPixel(x - interval * lineCount);
			if ( lineX >= 0 ) {
				g2d.drawLine(lineX,  0,  lineX, this.getHeight());
				drewLine = true;
			}

			// Up
			lineY = metersToVerticalPixel(y - interval * lineCount);
			if ( lineY >= 0 ) {
				g2d.drawLine(0,  lineY,  this.getWidth(), lineY);
				drewLine = true;
			}

			// Right
			lineX = metersToHorizontalPixel(x + interval * lineCount);
			if ( lineX <= this.getWidth() ) {
				g2d.drawLine(lineX,  0,  lineX, this.getHeight());
				drewLine = true;
			}

			// Down
			lineY = metersToVerticalPixel(y + interval * lineCount);
			if ( lineY <= this.getHeight() ) {
				g2d.drawLine(0,  lineY,  this.getWidth(), lineY);
				drewLine = true;
			}

			++lineCount;
		} while (drewLine);
		g2d.setColor(originalColor);
	}

	private void paintInteractionSpace(SystemAgent agent, Space space) {
		AffineTransform origTransform = g2d.getTransform();

		//System.out.println(space.getClass());
		
		Location center = space.getCenter().add(agent.getHeadLocation());

		if (view == TOPVIEW) {
			g2d.translate(metersToHorizontalPixel(center.x), metersToVerticalPixel(center.z));
		} else if (view == SIDEVIEW){
			g2d.translate(metersToHorizontalPixel(center.z), metersToVerticalPixel(-center.y));
		}
		space.draw(this, view);

		g2d.setTransform(origTransform);
	}

	private void paintItem(Item item) {
		if (item.location != null) {

			AffineTransform origTransform = g2d.getTransform();

			if (view == TOPVIEW) {
				g2d.translate(metersToHorizontalPixel(item.location.x), metersToVerticalPixel(item.location.z));
				g2d.rotate(Math.toRadians(-item.rotation.y));
			} else if (view == SIDEVIEW){
				g2d.translate(metersToHorizontalPixel(item.location.z), metersToVerticalPixel(-item.location.y));
				double rotadj;

				if (item.rotation.y < 90 || item.rotation.y > 270) {
					rotadj = item.rotation.x;
				} else {
					rotadj = 180 - item.rotation.x;
				}

				g2d.rotate(Math.toRadians(rotadj));
			}

			item.draw(this, view);

			if (selected != null && selected.equals(item.id))
				g2d.setColor(Color.red);
			else
				g2d.setColor(Color.blue);

			g2d.drawString(item.id, 0, 0);

			g2d.setTransform( origTransform );
		}
	}

	private void setAgentColors(Body agent) {
		float hue;
		float sat = 0.2f;
		if (agent instanceof SystemAgent) {
			hue = ColorGenerator.getHue(Color.RED);
			agentOutline = ((SystemAgent)agent).hasUsers() ? Color.RED : Color.GRAY;
			if (((Agent)agent).isSpeaking()) sat = 0.5f;
		} else {
			hue = ColorGenerator.getHue(Color.BLUE);
			agentOutline = Color.GRAY;
			for (SystemAgent a : situation.getSystemAgents().values()) {
				for (Agent u : a.getUsers()) {
					if (u.sensor.equals(agent.sensor)) {
						agentOutline = Color.RED;
						if (u.isSpeaking()) sat = 0.5f;
					}
				}
			}
		}
		agentFill = Color.getHSBColor(hue, sat, 0.9f);
	}

	private void paintBodyTop(Graphics2D g2d, Body agent) {
		AffineTransform origTransform = g2d.getTransform();

		int headX = metersToHorizontalPixel(agent.getHeadLocation().x);
		int headZ = metersToVerticalPixel(agent.getHeadLocation().z);

		setAgentColors(agent);

		/*
		Location attendingLoc = null;
		Agent attendingAgent = (Agent) situation.getAgents().get(agent.attending);
		if (attendingAgent != null) {
			attendingLoc = attendingheadLocation;
		} else {
			Item attendingItem = (Item) situation.getItems().get(agent.attending);
			if (attendingItem != null) {
				attendingLoc = attendingItem.location;
			} else if (agent.gaze != null) {
				attendingLoc = agent.gaze;
			}
		}
		 */

		Location attendingLoc = null;
		if (agent.gaze != null) {
			attendingLoc = agent.gaze;
		} else if (agent.hasHeadRotation()) {
			attendingLoc = new Location(0, 0, 2).rotate(agent.getHeadRotation()).add(agent.getHeadLocation());
		}

		if (attendingLoc != null) {
			drawLine(Color.RED, gazeStroke, headX, headZ, metersToHorizontalPixel(attendingLoc.x), metersToVerticalPixel(attendingLoc.z));
		}

		int handWidth = metersToPixels(0.08);
		if (agent.handLeft != null) {
			//TODO: should take agent.location into account also
			int handX = metersToHorizontalPixel(agent.handLeft.location.x);
			int handZ = metersToVerticalPixel(agent.handLeft.location.z);
			drawLine(Color.gray, armStroke, handX, handZ, headX, headZ);
			drawOval(agentFill, Color.gray, handX-handWidth/2, handZ-handWidth/2, handWidth, handWidth);
		}
		if (agent.handRight != null) {
			int handX = metersToHorizontalPixel(agent.handRight.location.x);
			int handZ = metersToVerticalPixel(agent.handRight.location.z);
			drawLine(Color.gray, armStroke, handX, handZ, headX, headZ);
			drawOval(agentFill, Color.gray, handX-handWidth/2, handZ-handWidth/2, handWidth, handWidth);
		}

		int headDepth = metersToPixels(0.18);
		int headWidth = metersToPixels(0.15);
		int noseWidth = metersToPixels(0.03);
		int noseDepth = metersToPixels(0.03);

		g2d.translate(headX, headZ);

		g2d.rotate(Math.toRadians(-agent.rotation.y));

		if (agent instanceof SystemAgent) {
			// Draw torso
			drawOval(agentFill, agentOutline, -headWidth,  -headDepth/3, headWidth*2, headDepth*2/3);
		}

		double headRotY = 180;		
		if (agent.hasHeadRotation()) 
			headRotY = agent.head.rotation.y;
		g2d.rotate(Math.toRadians(-headRotY));
		// Draw nose
		if (agent.hasHeadRotation()) 
			drawOval(agentFill, agentOutline, -noseDepth/2,  headWidth/2-noseWidth/2, noseWidth, 2*noseDepth);
		// Draw head
		drawOval(agentFill, agentOutline, -headWidth/2,  -headDepth/2, headWidth, headDepth);
		g2d.rotate(Math.toRadians(headRotY));

		if (agent.id.equals(selected))
			g2d.setColor(Color.red);
		else
			g2d.setColor(Color.blue);
		Record a = new Record(agent);
		a.remove("head");
		a.remove("handLeft");
		a.remove("handRight");
		a.remove("interactionSpaces");
		a.remove("location");
		a.remove("rotation");
		a.remove("expire");
		a.remove("speaking");
		a.remove("gaze");
		a.remove("id");
		SwingUtils.drawString(g2d, agent.id + "\n" + a.toStringIndent(), 0, 0);

		g2d.setTransform( origTransform );
	}

	private void drawLine(Color color, Stroke stroke, int x1, int y1, int x2, int y2) {
		Stroke originalStroke = g2d.getStroke();
		g2d.setStroke(stroke);		
		g2d.setColor(color);
		g2d.drawLine(x1, y1, x2, y2);
		g2d.setStroke(originalStroke);	
	}

	private void paintBodySide(Graphics2D g2d, Body agent) {
		int headZ = metersToHorizontalPixel(agent.getHeadLocation().z);
		int headY = metersToVerticalPixel(-agent.getHeadLocation().y);

		setAgentColors(agent);

		/*Location attendingLoc = null;
		Agent attendingAgent = (Agent) situation.getAgents().get(agent.attending);
		if (attendingAgent != null) {
			attendingLoc = attendingAgent.head.location;
		} else {
			Item attendingItem = (Item) situation.getItems().get(agent.attending);
			if (attendingItem != null) {
				attendingLoc = attendingItem.location;
			} else if (agent.gaze != null) {
				attendingLoc = agent.gaze;
			}
		}*/

		Location attendingLoc = null;
		if (agent.gaze != null) {
			attendingLoc = agent.gaze;
		} else if (agent.head.rotation != null) {
			attendingLoc = new Location(0, 0, 2).rotate(agent.head.rotation).add(agent.getHeadLocation());
		}

		if (attendingLoc != null) {
			drawLine(Color.RED, gazeStroke, headZ, headY, metersToHorizontalPixel(attendingLoc.z), metersToVerticalPixel(-attendingLoc.y));
		}

		int headHeight = metersToPixels(0.23);
		int headDepth = metersToPixels(0.18);
		int noseHeight = metersToPixels(0.03);
		int noseDepth = metersToPixels(0.04);

		AffineTransform origTransform = g2d.getTransform();

		g2d.translate(headZ, headY);

		if (agent.head.rotation != null) {
			double rotadj;
			if (agent.head.rotation.y < 90 || agent.head.rotation.y > 270) {
				rotadj = agent.head.rotation.x;
			} else {
				rotadj = 180 - agent.head.rotation.x;
			}
			g2d.rotate(Math.toRadians(rotadj));
			drawOval(agentFill, agentOutline, headDepth/2 - noseDepth/2, -noseHeight/2, noseDepth, noseHeight);
			drawOval(agentFill, agentOutline, -headDepth/2,  -headHeight/2, headDepth, headHeight);
			g2d.rotate(Math.toRadians(-rotadj));
		} else {
			drawOval(agentFill, agentOutline, -headDepth/2, -headHeight/2, headDepth, headHeight);
		}

		if (agent.id.equals(selected))
			g2d.setColor(Color.red);
		else
			g2d.setColor(Color.blue);
		g2d.drawString(agent.id, 0, 0);

		g2d.setTransform(origTransform);
	}

	@Override
	public void situationUpdated() {
		//TODO: should be able to switch this off (if streaming the panel)
		repaintThread.repaint();
	}

	/* 
	 * Draw oval with pixel references
	 */
	private void drawOval(Color fill, Color outline, int x, int y, int w, int h) {
		if (fill != null) {
			g2d.setColor(fill);
			g2d.fillOval(x, y, w, h);
		}
		if (outline != null) {
			g2d.setColor(outline);
			g2d.drawOval(x, y, w, h);
		}
	}

	/* 
	 * Draw oval with meter references
	 */
	public void drawOval(Color fill, Color outline, double x, double y, double width, double height) {
		drawOval(fill, outline, metersToPixels(x), metersToPixels(y), metersToPixels(width), metersToPixels(height));
	}

	/* 
	 * Draw polygon with meter references
	 */
	public void drawPolygon(Color fill, Color outline, double[] poly) {
		Polygon polygon = new Polygon();
		for (int i = 0; i < poly.length; i+=2) {
			polygon.addPoint(metersToPixels(poly[i]), metersToPixels(poly[i+1]));
		}
		if (fill != null) {
			g2d.setColor(fill);
			g2d.fillPolygon(polygon);
		}
		if (outline != null) {
			g2d.setColor(outline);
			g2d.drawPolygon(polygon);
		}
	}

	/* 
	 * Draw line with meter references
	 */
	public void drawLine(double x1, double y1, double x2, double y2) {
		g2d.setColor(Color.BLUE);
		g2d.drawLine(metersToPixels(x1), metersToPixels(y1), metersToPixels(x2), metersToPixels(y2));
	}


}
