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
package iristk.flow;

import iristk.system.Event;
import iristk.util.ColorGenerator;
import iristk.util.Record;

import java.awt.Color;
import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class FlowLogger implements FlowListener {

	private PrintStream out = null;
	private HashSet<Event> pastEvents = new HashSet<>();
	private FlowModule flowModule;
	private int idCounter = 0;

	public FlowLogger(FlowModule flowModule) {
		this.flowModule = flowModule;
		flowModule.addFlowListener(this);
	}
	
	public FlowModule getFlowModule() {
		return flowModule;
	}

	public synchronized void startLogging(File file) {
		try {
			startLogging(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void startLogging(OutputStream stream) {
		out = new PrintStream(stream);
		out.println("<html><head><script>" +
		"function toggle(id) {"
		+ " var e = document.getElementById(id);"
		+ " if(e.style.display == 'none') e.style.display = 'block';"
		+ " else e.style.display = 'none';"
		+ "}" +  
		"</script></head>" +
		"<style>table {font-family:Helvetica;font-size:9pt}</style>" +
		"<body>" +
		"<table cellspacing=\"0\" cellpadding=\"2\">");	
	}
	
	public synchronized void stopLogging() {
		if (out != null) {
			out.close();
			out = null;
		}
	}
	
	private synchronized void log(FlowEventInfo info, String action, String name, Record param) {
		if (out != null && info != null && info.getTriggeringState() != null) {
			out.print("<tr>");
			out.println("<td valign=top>" +  new SimpleDateFormat("HH:mm:ss").format(new Timestamp(System.currentTimeMillis())) + "</td>");
			List<Class> stateStack = new ArrayList<Class>();
			State state = info.getTriggeringState();
			while (state != null) {
				Class parent = state.getClass();
				stateStack.add(0, parent);
				//while (parent != null && parent != State.class) {
				//	stateStack.add(0, parent);
				//	parent = parent.getSuperclass();
				//}
				state = state.caller;
				if (state != null)
					stateStack.add(0, null);
			}
			int depth = 0;
			int nstates = 0;
			boolean call = false;
			for (Class sc : stateStack) {
				if (sc == null) {
					call = true;
					depth++;
				} else {
					out.print("<td valign=top style=\"background:" + color(sc.getSimpleName()));
					if (call)
						out.print(";border-left:1px solid black");
					out.print("\">");
					if (info.getTriggeringState().getClass() == sc) {
						out.print("<b>" + sc.getSimpleName() + "</b>");
					} else {
						out.print(sc.getSimpleName());
					}
					out.print("</td>");
					nstates++;
					call = false;
				}
			}
			for (int i = nstates; i < 10; i++) {
				out.print("<td></td>");
			}
			
			//for (int i = 0; i < depth; i++)
			//	out.print("<td valign=top style=\"border-left:1px solid black\">&nbsp;&nbsp;</td>");
			
			//out.print("<td valign=top style=\"border-left:1px solid black\" colspan=\"" + (5-depth) + "\">");
			
			out.print("<td valign=top>");
			
			if (info.getTriggeringEvent() != null) {
				if (!pastEvents.contains(info.getTriggeringEvent())) {
					out.print(paramTable(info.getTriggeringEvent().getName(), info.getTriggeringEvent().getEventParams()));
					pastEvents.add(info.getTriggeringEvent());
				} else {
				}
			} else {
				out.print("state.exit");
			}
			
			out.print("</td>");
			
			if (info.getXmlLocation() != null) {
				out.println("<td valign=top><a href=\"" + info.getXmlLocation().getFile().getAbsolutePath() + "\" title=\"Line: " + info.getXmlLocation().getLineNumber() + "\">" + action + "</td>");
			} else {
				out.println("<td valign=top>" + action + "</td>");
			}
			
			out.println("<td valign=top>" + paramTable(name, param) + "</td>");
				
			out.println("</tr>");
			
			if (action.equals("GOTO")) {
				out.println("<tr height=10><td></td></tr>");
			}
			
			out.flush();
			
		}
	}

	private String paramTable(String name, Record param) {
		if (param == null || param.size() == 0) {
			return name;
		} else {
			String id = "table-" + idCounter++;
			StringBuilder table = new StringBuilder();
			table.append("<a href=\"javascript:toggle('" + id + "')\">" + name + "</span>");
			table.append("<table id=\"" + id + "\" style=\"display:none\">");
			for (String key : param.getFields()) {
				table.append("<tr><td style=\"font-weight:bold;margin-right:5px\">" + key + "</td><td>" + param.getString(key) + "</td></tr>");
			}
			table.append("</table>");
			return table.toString();
		}
	}

	private String color(String name) {
		Color c = ColorGenerator.getColor(name);
		String col = String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
		return col;
	}

	private void logEvent(FlowEventInfo info, String action, Event event) {
		log(info, action, event.getName(), event.getEventParams());
	}
	
	private void logTrans(FlowEventInfo info, String action, State toState) {
		String name = toState.getClass().getSimpleName();
		log(info, action, "<span style=\"padding:2px;background:" + color(name) + "\">" + name + "</span>", toState.params);
	}
	
	@Override
	public void onFlowEvent(Event event, FlowEventInfo info) {
		logEvent(info, "RAISE", event);
	}

	@Override
	public void onSendEvent(Event event, FlowEventInfo info) {
		logEvent(info, "SEND", event);
	}

	@Override
	public void onGotoState(State toState, FlowEventInfo info) {
		logTrans(info, "GOTO", toState);
	}

	@Override
	public void onCallState(State toState, FlowEventInfo info) {
		logTrans(info, "CALL", toState);
	}

	@Override
	public void onReturnState(State fromState, State toState, FlowEventInfo info) {
		if (toState != null) {
			logTrans(info, "RETURN", toState);
		}
	}

	@Override
	public void onFlowException(Exception e, FlowEventInfo flowEventInfo) {
		log(flowEventInfo, "ERROR", "<span style=\"color:red\">" + e.getMessage() + "</span>", null);
	}

	
}
