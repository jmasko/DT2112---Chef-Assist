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

import iristk.util.FileFinder;
import iristk.util.Replacer;
import iristk.util.StringUtils;
import iristk.xml.XmlUtils;
import iristk.xml.XmlMarshaller.XMLLocation;
import iristk.xml.flow.Block;
import iristk.xml.flow.Onevent;
import iristk.xml.flow.Ontime;
import iristk.xml.flow.Expr;
import iristk.xml.flow.Select;
import iristk.xml.flow.Str;
import iristk.xml.flow.Onentry;
import iristk.xml.flow.Onexit;
import iristk.xml.flow.Param;
import iristk.xml.flow.Var;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.lang.String;
import java.lang.Object;

public class FlowCompiler {

	private static final String stringPattern = "\".*?\"";
	//protected String classPackage;
	//protected String className;
	protected CodeStream code;
	private boolean useUniqueNames = false;
	private static int uniqueNameSuffix = 0;
	private FlowXml flowXml = new FlowXml();
	private String currentState = null;

	public FlowCompiler(File xmlFile) throws FlowCompilerException {
		flowXml.read(xmlFile);
	}

	public FlowCompiler(iristk.xml.flow.Flow xmlFlow) throws FlowCompilerException {
		flowXml.read(xmlFlow);
	}

	public void compileToFile(File srcFile) throws FlowCompilerException {
		try {
			FileOutputStream fstream = new FileOutputStream(srcFile);
			compileToStream(fstream);
			fstream.close();
		} catch (FileNotFoundException e) {
			throw new FlowCompilerException(e.getMessage());
		} catch (IOException e) {
			throw new FlowCompilerException(e.getMessage());
		}
	}

	public void compileToStream(OutputStream out) throws FlowCompilerException {
		if (useUniqueNames)
			uniqueNameSuffix++;

		code = new CodeStream(out);

		String flowName = getLocalFlowName();

		code.println("package " + flowXml.getPackage() + ";");
		code.println();

		printImports();
		for (String imp : flowXml.getImportedClasses()) {
			code.println("import " + imp + ";");
		}
		code.println();
		
		code.println("public class " + flowName + " extends " + flowXml.getExtends() + " {");
		code.println();

		printFlowContents(flowXml, flowName, true);
		
		code.println("}");
	} 

	/*
	private void printInitTimers() {
		code.println("@Override");
		code.println("public void initTimers(FlowRunner flowRunner) {");
		for (iristk.xml.flow.State state : flowReader.getStates()) {
			for (ActionSequenceType trigger : state.getTrigger()) {
				if (trigger instanceof Ontime) {
					Ontime ontime = (Ontime) trigger;
					String interval = ontime.getInterval();
					if (interval != null) {
						if (interval.contains("-")) {
							String[] cols = interval.split("-");
							int min = Integer.parseInt(cols[0]);
							int max = Integer.parseInt(cols[1]);
							code.println("new EventClock(flowRunner, " + min + ", " + max + ", \"timer_" + trigger.hashCode() + "\");");
						} else {
							int time = Integer.parseInt(interval);
							code.println("new EventClock(flowRunner, " + time + ", \"timer_" + trigger.hashCode() + "\");");
						}
					}
				} 
			}
		}
		code.println("}");
		code.println();
	}

	private void printActionTemplates(List<iristk.xml.flow.Action> actions) throws FlowCompilerException {
		for (iristk.xml.flow.Action action : actions) {
			code.println();
			code.println((action.isPublic() ? "public" : "private") + " class " + action.getId() + " {");
			code.println("Record params = new Record();");
			if (action.getParam() != null) {
				printParameters(action.getParam());
			}
			code.println("public int invoke(FlowRunner.FlowThread flowThread, State currentState, Event event) {");
			code.println("FlowRunner flowRunner = flowThread.getFlowRunner();");
			code.println("int eventResult = EVENT_CONSUMED;");
			code.println("EXECUTION: {");
			printActions(action.getActionGroup());
			code.println("}");
			code.println("return eventResult;");

			code.println("}");
			code.println("}");
		}
	}
	 */

	private void printFlowContents(FlowXml flowReader, String flowName, boolean topFlow) throws FlowCompilerException {

		printVariableDeclarations(flowReader.getVariables(), flowReader.getParameters());
		code.println();
		printInit(flowReader.getVariables());
		code.println();
		printVariableAccessors(flowReader.getVariables(), flowReader.getParameters(), !topFlow);
		code.println();

		if (topFlow && flowReader.getParameters().size() > 0) {
			String paramlist = "";
			String paramnamelist = "";
			for (Param param : flowReader.getParameters()) {
				if (paramlist.length() > 0) {
					paramlist += ", ";
					paramnamelist += ", ";
				}
				paramlist += param.getType() + " " + param.getName();
				paramnamelist += param.getName();
			}
			code.println("public " + flowName + "(" + paramlist + ") {");
			for (Param param : flowReader.getParameters()) {
				code.println("this." + param.getName() + " = " + param.getName() + ";");
			}
			code.println("initVariables();");
			code.println("}");
		} else {
			code.println("public " + flowName + "() {");
			code.println("initVariables();");
			code.println("}");
		}
		code.println();

		if (flowReader.getInitial() != null) {
			code.println("@Override");
			code.println("protected State getInitialState() {return new " + flowReader.getInitial() + "();}");
			code.println();
		}

		List<String> publicStates = new ArrayList<>();
		for (iristk.xml.flow.State state : flowReader.getStates()) {
			if (state.isPublic()) {
				publicStates.add("\"" + state.getId() + "\"");
			}
		}

		code.println("public static String[] getPublicStates() {");
		code.println("return new String[] {" + StringUtils.join(publicStates.toArray(new String[0]), ", ") + "};");
		code.println("}");
	
		//printInitTimers();

		printStates(flowReader.getStates());
		printSubFlows(flowReader.getSubFlows());

		code.println();
	}

	private void printSubFlows(List<FlowXml> subFlows) throws FlowCompilerException {
		for (FlowXml flowReader : subFlows) {
			code.println((flowReader.isPublic() ? "public" : "private") +
					(flowReader.isStatic() ? " static" : "") +
					" class " + flowReader.getName() + " extends " + flowReader.getExtends() + " {");
			code.println();
	
			printFlowContents(flowReader, flowReader.getName(), false);
			
			code.println("}");
		}
	}

	private boolean stateExists(String name) {
		for (iristk.xml.flow.State state: flowXml.getStates()) {
			if (state.getId().equals(name))
				return true;
		}
		return false;
	}

	private void printStates(List<iristk.xml.flow.State> states) throws FlowCompilerException {
		for (iristk.xml.flow.State state : states) {
			currentState  = state.getId();
			String ext = "State";
			if (state.getExtends() != null) {
				ext = state.getExtends();
			}
			code.println();
			code.println((state.isPublic() ? "public" : "private") +
					(state.isStatic() ? " static" : "") +
					" class " + currentState + " extends " + ext + " {");
			code.println();
			code.println("final State currentState = this;");
			if (state.getVar() != null) {
				printVariables(state.getVar());
				code.println();
			}
			if (state.getParam() != null) {
				printParameters(state.getParam());
			}

			code.println("@Override");
			code.println("public void setFlowThread(FlowRunner.FlowThread flowThread) {");
			code.println("super.setFlowThread(flowThread);");
			for (Object trigger : state.getTrigger()) {
				if (trigger instanceof Ontime) {
					Ontime ontime = (Ontime) trigger;
					String interval = ontime.getInterval();
					if (interval != null) {
						if (interval.contains("-")) {
							String[] cols = interval.split("-");
							int min = Integer.parseInt(cols[0]);
							int max = Integer.parseInt(cols[1]);
							code.println("flowThread.addEventClock(" + min + ", " + max + ", \"timer_" + trigger.hashCode() + "\");");
							//code.println("new EventClock(flowRunner, " + min + ", " + max + ", \"timer_" + trigger.hashCode() + "\");");
						} else {
							int time = Integer.parseInt(interval);
							code.println("flowThread.addEventClock(" + time + ", " + time + ", \"timer_" + trigger.hashCode() + "\");");
							//code.println("new EventClock(flowRunner, " + time + ", \"timer_" + trigger.hashCode() + "\");");
						}
					}
				} 
			}
			code.println("}");
			code.println();

			printOnEntry(state.getTrigger());
			code.println();
			printEventTriggers(state.getTrigger());
			printOnExit(state.getTrigger());
			code.println();
			code.println("}");
			code.println();
		}
	}

	protected void printImports() {
		code.println("import java.util.List;");
		code.println("import java.io.File;");
		code.println("import iristk.xml.XmlMarshaller.XMLLocation;");
		code.println("import iristk.system.Event;");
		code.println("import iristk.flow.*;");
		code.println("import iristk.util.Record;");
		code.println("import static iristk.util.Converters.*;");
		code.println("import static iristk.flow.State.*;");
	}

	private void printVariables(List<Var> vars) throws FlowCompilerException {
		for (Var var : vars) {
			code.println("public " + variable(var));
		}
	}

	private void printVariableDeclarations(List<Var> vars, List<Param> params) throws FlowCompilerException {
		for (Param param : params) {
			code.println("private " + param.getType() + " " + param.getName() + ";");
		}
		for (Var var : vars) {
			code.println("private " + var.getType() + " " + var.getName() + ";");
		}
	}

	private void printInit(List<Var> vars) throws FlowCompilerException {
		code.println("private void initVariables() {");
		for (Var var : vars) {
			String ass = variableAssignment(var);
			if (!ass.equals(";"))
				code.println(var.getName() + ass);
		}
		code.println("}");
	}

	private String variableAssignment(Var var) throws FlowCompilerException {
		String line = "";
		if (var.getValue() != null) {
			if (var.getValue().contains("#")) {
				line += " = " + getExternalVar(var) + ";";
			} else {
				String type = var.getType();
				if (type == null)
					type = "String";
				line += " = " + convertType(type, formatAttrExpr(var.getValue())) + ";";
				if (var.getContent() != null && var.getContent().size() > 0) {
					line += "{\n" + formatExec(XmlUtils.nodesToString(var.getContent())) + "\n}";
				}
			}
		} else if (var.getContent() != null && var.getContent().size() > 0) {
			line += " = " +  concatFun(processString(var.getContent())).replaceAll("\n", "").replaceAll("\\s+", " ");
		} else {
			line += ";";
		}
		return line;
	}

	private String variable(Var var) throws FlowCompilerException {
		return var.getType() + " " + var.getName() + variableAssignment(var);
	}

	private String getExternalVar(Var var) {
		String flowName = var.getValue().substring(0, var.getValue().indexOf("#"));
		String varName = var.getValue().substring(var.getValue().indexOf("#") + 1);
		return flowName + ".getFlow(flowPool).get" + ucFirst(varName) + "()";
	}

	private void printVariableAccessors(List<Var> vars, List<Param> params, boolean paramSetters) {
		for (Var var : vars) {
			code.println("public " + var.getType() + " get" + ucFirst(var.getName()) + "() {");
			code.println("return this." + var.getName() + ";");
			code.println("}");
			code.println();
			code.println("public void set" + ucFirst(var.getName()) + "(" + var.getType() + " value) {");
			code.println("this." + var.getName() + " = value;");
			code.println("}");
			code.println();
		}
		for (Param param : params) {
			code.println("public " + param.getType() + " get" + ucFirst(param.getName()) + "() {");
			code.println("return this." + param.getName() + ";");
			code.println("}");
			code.println();
			if (paramSetters) {
				code.println("public void set" + ucFirst(param.getName()) + "(" + param.getType() + " value) {");
				code.println("this." + param.getName() + " = value;");
				code.println("}");
				code.println();
			}
		}
		code.println("@Override");
		code.println("public Object getVariable(String name) {");
		for (Var var : vars) {
			code.println("if (name.equals(\"" + var.getName() + "\")) return this." + var.getName() + ";");
		}
		for (Param param : params) {
			code.println("if (name.equals(\"" + param.getName() + "\")) return this." + param.getName() + ";");
		}
		code.println("return null;");
		code.println("}");
		code.println();
	}

	private void printParameters(List<Param> params) throws FlowCompilerException {
		for (Param param : params) {
			String var = "public " + param.getType() + " " + param.getName() + " = ";
			if (param.getDefault() != null)
				var += convertType(param.getType(), formatAttrExpr(param.getDefault()));
			else {
				var += "null";
			}
			var += ";";
			code.println(var);
		}
		code.println();
		for (Param param : params) {
			code.println("public void set" + ucFirst(param.getName()) + "(Object value) {");
			code.println("if (value != null) {");
			code.println(param.getName() + " = " + convertType(param.getType(), "value") + ";");
			code.println("params.put(\"" + param.getName() + "\", value);");
			code.println("}");
			code.println("}");
			code.println();
		}
	}

	private String convertType(String type, String value) {
		if (type.equals("String")) {
			return "asString(" + value + ")";
		} else if (type.equals("Record")) {
			return "asRecord(" + value + ")";
		} else if (type.equals("Boolean")) {
			return "asBoolean(" + value + ")";
		} else if (type.equals("Integer") || type.equals("int")) {
			return "asInteger(" + value + ")";
		} else if (type.equals("Float") || type.equals("float")) {
			return "asFloat(" + value + ")";
		} else if (type.equals("List")) {
			return "asList(" + value + ")";
		} else if (type.equals("Object")) {
			return value;
		}  else {
			return "(" + type + ") " + value;
		}
	}

	private void printOnEntry(List<?> eventHandlers) throws FlowCompilerException {
		for (Object trigger : eventHandlers) {
			if (trigger instanceof Ontime) {
				String afterentry = ((Ontime)trigger).getAfterentry();
				if (afterentry != null) {
					code.println("iristk.util.DelayedEvent timer_" + trigger.hashCode() + ";");
				}
			}
		}

		code.println("@Override");
		code.println("public void onentry() {");
		code.println("int eventResult;");
		code.println("Event event = new Event(\"state.enter\");");
		for (Object trigger : eventHandlers) {
			if (trigger instanceof Ontime) {
				String afterentry = ((Ontime)trigger).getAfterentry();
				if (afterentry != null) {
					code.println("if (timer_" + trigger.hashCode() + " != null) timer_" + trigger.hashCode() + ".forget();");
					code.println("timer_" + trigger.hashCode() + " = flowThread.raiseEvent(new Event(\"timer_" + trigger.hashCode() + "\"), " + formatAttrExpr(afterentry) + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(trigger)) + "));");
					code.println("forgetOnExit(timer_" + trigger.hashCode() + ");");
				}
			}
		}
		
		for (Object eventHandler : eventHandlers) {
			if (eventHandler instanceof Onentry) {
				Onentry onentry = (Onentry) eventHandler;
				code.println("EXECUTION: {");
				code.println("int count = getCount(" + eventHandler.hashCode() + ") + 1;");
				code.println("incrCount(" + eventHandler.hashCode() + ");");
				printActions(onentry.getActionGroup(), onentry);
				code.println("}");
				break;
			}
		}

		code.println("}");
		return;
	}


	private void printOnExit(List<?> eventHandlers) throws FlowCompilerException {
		for (Object eventHandler : eventHandlers) {
			if (eventHandler instanceof Onexit) {
				Onexit onexit = (Onexit) eventHandler;
				code.println("@Override");
				code.println("public void onexit() {");
				code.println("int eventResult;");
				code.println("Event event = new Event(\"state.exit\");");
				if (onexit != null) {
					code.println("EXECUTION: {");
					printActions(onexit.getActionGroup(), onexit);
					code.println("}");
				}
				code.println("super.onexit();");
				code.println("}");
				return;
			}
		}
	}

	private void printEventTriggers(List<?> triggers) throws FlowCompilerException {
		code.println("@Override");
		code.println("public int onFlowEvent(Event event) {");
		code.println("int eventResult;");
		code.println("int count;");
		for (Object trigger : triggers) {
			if (trigger instanceof Onevent) {
				code.println("count = getCount(" + trigger.hashCode() + ") + 1;");
				Onevent onEventElem = (Onevent) trigger;
				if (onEventElem.getName() != null)
					code.println("if (event.triggers(\"" + onEventElem.getName() + "\")) {");
				if (onEventElem.getCond() != null)
					code.println("if ("+ formatCondExpr(onEventElem.getCond()) + ") {");

				code.println("incrCount(" + trigger.hashCode() + ");");
				
				code.println("eventResult = EVENT_CONSUMED;");
				code.println("EXECUTION: {");
				printActions(onEventElem.getActionGroup(), trigger);
				code.println("}");
				code.println("if (eventResult != EVENT_IGNORED) return eventResult;");

				//for (int i = 0; i < onEventElem.getOtherAttributes().keySet().size(); i++) 
				//	code.println("}");
				if (onEventElem.getCond() != null)
					code.println("}");
				if (onEventElem.getName() != null)
					code.println("}");
			} else if (trigger instanceof Ontime) {
				Ontime onTimeElem = (Ontime) trigger;
				code.println("count = getCount(" + trigger.hashCode() + ") + 1;");
				code.println("if (event.triggers(\"timer_" + trigger.hashCode() + "\")) {");
				code.println("incrCount(" + trigger.hashCode() + ");");
				code.println("eventResult = EVENT_CONSUMED;");
				code.println("EXECUTION: {");
				printActions(onTimeElem.getActionGroup(), trigger);
				code.println("}");
				code.println("if (eventResult != EVENT_IGNORED) return eventResult;");
				code.println("}");
			} 
		}
		code.println("eventResult = super.onFlowEvent(event);");
		code.println("if (eventResult != EVENT_IGNORED) return eventResult;");
		code.println("eventResult = callerHandlers(event);");
		code.println("if (eventResult != EVENT_IGNORED) return eventResult;");
		code.println("return EVENT_IGNORED;");
		code.println("}");
	}

	private String ucFirst(String text) {
		return text.substring(0, 1).toUpperCase() + text.substring(1); 
	}

	private String listToString(List<?> list) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			if (i > 0) result.append(", ");
			result.append(list.get(i));
		}
		return result.toString();
	}

	private List<String> processString(List<?> children) throws FlowCompilerException {
		List<String> result = new ArrayList<String>();
		for (Object child : children) {
			if (child instanceof String) {
				String str = child.toString().trim();
				if (str.length() > 0)
					result.add("\"" + str + "\"");
			} else if (child instanceof Expr) {
				Expr expr = (Expr) child;
				result.add(formatExpr(expr.getValue()));
			} else if (child instanceof Str) {
				Str item = (Str) child;
				if (item.getRef() != null) 
					item = (Str) item.getRef();
				if (item.getProb() != null || item.getCond() != null)
					result.add("str(" + formatCondExpr(item.getCond()) + ", " + item.getProb() + ", " + concatFun(processString(item.getContent())) + ")");
				else
					result.addAll(processString(item.getContent()));
			} else if (child instanceof Select) {
				Select select = (Select) child;
				String options = null;
				if (select.getStr().size() > 0) {
					Select choice = (Select) child;
					List<String> choices = new ArrayList<String>();
					for (Str item : choice.getStr()) {
						if (item.getRef() != null) 
							item = (Str) item.getRef();
						if (item.getCond() != null)
							choices.add("str(" + formatCondExpr(item.getCond()) + ", null, " + concatFun(processString(item.getContent())) + ")");
						else
							choices.add(concatFun(processString(item.getContent())));	
					}
					options = listToString(choices);
				} else if (select.getList() != null) {
					options = select.getList();
				}
				if (options != null) {
					if (select.getChoice().equals("random"))
						result.add("randstr(" + options  + ")");
					else
						result.add("firststr(" + options  + ")");
				}
			} else if (child instanceof Element) {
				result.add(concatFun(processElement(child)));
			} else if (child instanceof Text) {
				String str = ((Text)child).getNodeValue().trim();
				if (str.length() > 0)
					result.add("\"" + str + "\"");
			}
		}
		return result;
	}

	private String concatFun(List<?> nodes) {
		if (nodes.size() == 0)
			return "\"\"";
		else if (nodes.size() == 1)
			return nodes.get(0).toString();
		else
			return "concat(" + listToString(nodes) + ")";
	}

	private List<String> processElement(Object node) throws FlowCompilerException {
		ArrayList<String> result = new ArrayList<String>();
		if (node instanceof String) {
			String str = ((String)node).trim();
			if (str.length() > 0)
				result.add("\"" + str + "\"");
		} else if (node instanceof Text) {
			String str = ((Text) node).getTextContent().trim();
			if (str.length() > 0)
				result.add("\"" + str + "\"");
		} else if (node instanceof Element) {
			try {
				Object o = flowXml.unmarshal((Element)node);
				List<Object> objects = new ArrayList<Object>();
				objects.add(o);
				result.addAll(processString(objects));
			} catch (FlowCompilerException e) {
				String estring = "";
				Element en = (Element)node;
				estring += "\"<" + en.getLocalName();
				for (int j = 0; j < en.getAttributes().getLength(); j++) {
					Attr attr = (Attr) en.getAttributes().item(j);
					if (!(attr.getNamespaceURI() != null && attr.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/")) && !attr.getLocalName().equals("xmlns") && !attr.getLocalName().equals("xsi")) {
						estring += " " + attr.getLocalName() + "=\\\"" + attr.getValue() + "\\\"";
					}
				}
				if (en.getChildNodes().getLength() == 0) {
					estring += "/>\"";
					result.add(estring);
				} else {
					estring += ">\"";
					result.add(estring);
					for (int i = 0; i < en.getChildNodes().getLength(); i++) {
						result.addAll(processElement(en.getChildNodes().item(i))); 
					}
					result.add("\"</" + en.getLocalName() + ">\"");
				}
			}
		}
		return result;
	}

	private int varnameCount = 0;
	private String varname(String prefix) {
		return prefix + (varnameCount++);
	}

	protected void printAction(Object action, Object parent) throws FlowCompilerException {
		if (action instanceof JAXBElement<?>) 
			action = ((JAXBElement<?>)action).getValue();
		if (action instanceof iristk.xml.flow.Goto) {
			iristk.xml.flow.Goto gotoAction = (iristk.xml.flow.Goto) action;
			if (!stateExists(gotoAction.getState()))
				throw new FlowCompilerException("State " + gotoAction.getState() + " does not exist", gotoAction.sourceLocation().getLineNumber());
			String stateVar = varname("state");
			code.println(stateClass(gotoAction.getState()) + " " + stateVar + " = " + newState(gotoAction.getState()) + ";");
			printSetStateParameters(stateVar, getParameters(gotoAction.getOtherAttributes(), gotoAction.getContent()));
			code.println("flowThread.gotoState(" + stateVar + ", currentState, new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "));");
			code.println("eventResult = EVENT_ABORTED;");
			code.println("break EXECUTION;");
		} else if (action instanceof iristk.xml.flow.Run) {
			iristk.xml.flow.Run runAction = (iristk.xml.flow.Run) action;
			String stateVar = varname("state");
			code.println(stateClass(runAction.getState()) + " " + stateVar + " = " + newState(runAction.getState()) + ";");
			printSetStateParameters(stateVar, getParameters(runAction.getOtherAttributes(), runAction.getContent()));
			code.println("FlowRunner.FlowThread " + stateVar + "Thread = flowThread.runState(" + stateVar + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "));");
		} else if (action instanceof iristk.xml.flow.Call) {
			iristk.xml.flow.Call callAction = (iristk.xml.flow.Call) action;
			String stateVar = varname("state");
			code.println(stateClass(callAction.getState()) + " " + stateVar + " = " + newState(callAction.getState()) + ";");
			printSetStateParameters(stateVar, getParameters(callAction.getOtherAttributes(), callAction.getContent()));
			code.println("if (!flowThread.callState(" + stateVar + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "))) {");
			code.println("eventResult = EVENT_ABORTED;");
			code.println("break EXECUTION;");
			code.println("}");
		} else if (action instanceof iristk.xml.flow.Return) {
			iristk.xml.flow.Return returnAction = (iristk.xml.flow.Return)action;
			if (returnAction.getEvent() != null || returnAction.getCopy() != null) {
				String returnEvent = varname("returnEvent");
				printInitEvent(returnEvent, returnAction.getCopy(), returnAction.getEvent(), getParameters(returnAction.getOtherAttributes(), returnAction.getContent()));
				//code.println("flowThread.raiseEvent(" + returnEvent + ", new FlowEventInfo(currentState, event, " + locationConstructor(flowReader.getLocation(action)) + ");");
				code.println("flowThread.returnFromCall(this, " + returnEvent + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "));");
			} else {
				code.println("flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "));");
			}
			code.println("eventResult = EVENT_ABORTED;");
			code.println("break EXECUTION;");
		} else if (action instanceof iristk.xml.flow.Reentry) {
			//code.println("flowThread.raiseEvent(new EntryEvent(), new FlowEventInfo(currentState, event, " + locationConstructor(flowReader.getLocation(action)) + ");");
			code.println("flowThread.reentryState(this, new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "));");
			code.println("eventResult = EVENT_ABORTED;");
			code.println("break EXECUTION;");
		} else if (action instanceof iristk.xml.flow.Block) {
			iristk.xml.flow.Block blockAction = (iristk.xml.flow.Block) action;
			code.println("{");
			printActions(blockAction.getActionGroup(), action);
			code.println("}");
		} else if (action instanceof iristk.xml.flow.Wait) {
			iristk.xml.flow.Wait waitAction = (iristk.xml.flow.Wait) action;
			String waitvar = varname("waitState");
			code.println(DialogFlow.class.getName() + ".wait " + waitvar + " = new " + DialogFlow.class.getName() + ".wait();");
			code.println(waitvar + ".setMsec(" + waitAction.getMsec() + ");");
			code.println("if (!flowThread.callState(" + waitvar + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "))) {");
			code.println("eventResult = EVENT_ABORTED;");
			code.println("break EXECUTION;");
			code.println("}");
		} else if (action instanceof iristk.xml.flow.Random) {
			iristk.xml.flow.Random randomAction = (iristk.xml.flow.Random) action;
			int tot = 0;
			for (Object child : randomAction.getActionGroup()) {
				int inc = 1;
				if (child instanceof JAXBElement)
					child = ((JAXBElement)child).getValue();
				if (child instanceof Block && ((Block)child).getWeight() != null)
					inc = ((Block)child).getWeight();
				tot += inc;
			}
			int n = 0;
			int lastn = 0;
			String chosenVar = varname("chosen");
			String matchingVar = varname("matching");
			code.println("boolean " + chosenVar + " = false;");
			code.println("boolean " + matchingVar + " = true;");
			code.println("while (!"+chosenVar+" && "+matchingVar+") {");
			String randVar = varname("rand");
			String model = "iristk.util.RandomList.RandomModel." + randomAction.getModel().toUpperCase();
			code.println("int " + randVar + " = random(" + randomAction.hashCode() + ", " + tot + ", " + model + ");");
			code.println(matchingVar + " = false;");
			for (Object child : randomAction.getActionGroup()) {
				if (n > 0) 
					code.println("}");
				int inc = 1;
				String cond = "true";
				if (child instanceof JAXBElement)
					child = ((JAXBElement)child).getValue();
				if (child instanceof Block) {
					Block block = (Block) child;
					if (block.getWeight() != null)
						inc = block.getWeight();
					if (block.getCond() != null) 
						cond = formatCondExpr(block.getCond());
				}
				n += inc;
				code.println("if (" + cond + ") {");
				code.println(matchingVar + " = true;");
				code.println("if (" + randVar + " >= " + lastn + " && " + randVar + " < " + n + ") {");
				code.println(chosenVar + " = true;");
				printAction(child, randomAction);
				code.println("}");
				lastn = n;
			}
			code.println("}");
			code.println("}");
		} else if (action instanceof iristk.xml.flow.Raise) {
			iristk.xml.flow.Raise raiseAction = (iristk.xml.flow.Raise) action;
			String raiseEvent = varname("raiseEvent");
			printInitEvent(raiseEvent, raiseAction.getCopy(), raiseAction.getEvent(), getParameters(raiseAction.getOtherAttributes(), raiseAction.getContent()));
			if (raiseAction.getDelay() != null) {
				String delayedEvent = "flowThread.raiseEvent(" + raiseEvent + ", " + formatAttrExpr(raiseAction.getDelay()) + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "))";
				if (raiseAction.isForgetOnExit()) {
					code.println("forgetOnExit(" + delayedEvent + ");");
				} else {
					code.println(delayedEvent + ";");
				}
			} else {
				code.println("if (flowThread.raiseEvent(" + raiseEvent + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + ")) == State.EVENT_ABORTED) {");
				code.println("eventResult = EVENT_ABORTED;");
				code.println("break EXECUTION;");
				code.println("}");
			}
		} else if (action instanceof iristk.xml.flow.Send) {
			iristk.xml.flow.Send sendAction = (iristk.xml.flow.Send) action;
			String sendEvent = varname("sendEvent");
			printInitEvent(sendEvent, sendAction.getCopy(), sendAction.getEvent(), getParameters(sendAction.getOtherAttributes(), sendAction.getContent()));
			if (sendAction.getDelay() != null) 
				code.println("flowRunner.sendEvent(" + sendEvent + ", " + formatAttrExpr(sendAction.getDelay()) + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "));");
			else
				code.println("flowRunner.sendEvent(" + sendEvent + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(action)) + "));");
			if (sendAction.getBindId() != null) {
				code.println(sendAction.getBindId() + " = " + sendEvent + ".getId();");
			}
		} else if (action instanceof iristk.xml.flow.If) {
			iristk.xml.flow.If ifAction = (iristk.xml.flow.If) action;
			code.println("if (" + formatCondExpr(ifAction.getCond()) + ") {");
			printActions(ifAction.getActionGroup(), ifAction);
			code.println("}");
		} else if (action instanceof iristk.xml.flow.Else) {
			code.println("} else {");
		} else if (action instanceof iristk.xml.flow.Elseif) {
			iristk.xml.flow.Elseif eifAction = (iristk.xml.flow.Elseif) action;
			code.println("} else if (" + formatCondExpr(eifAction.getCond()) + ") {");
		} else if (action instanceof iristk.xml.flow.Propagate) {
			code.println("eventResult = EVENT_IGNORED;");
			code.println("break EXECUTION;");
		} else if (action instanceof Var) {
			code.println(variable((Var) action));
		} else if (action instanceof iristk.xml.flow.Exec) {
			code.println(formatExec(((iristk.xml.flow.Exec)action).getValue().trim()));
		} else if (action instanceof iristk.xml.flow.Log) {
			code.println("log(" + concatFun(processString(((iristk.xml.flow.Log)action).getContent())).replaceAll("\n", "").replaceAll("\\s+", " ") + ");");
		} else if (action instanceof Element) {
			// Action Template
			Element elem = (Element)action;
			String stateVar = varname("state");
			code.println(stateClass(elem) + " " + stateVar + " = " + newState(elem) + ";");
			printSetStateParameters(stateVar, getParameters(elem.getAttributes(), elem.getChildNodes()));

			code.println("if (!flowThread.callState(" + stateVar + ", new FlowEventInfo(currentState, event, " + location(flowXml.getLocation(parent)) + "))) {");
			code.println("eventResult = EVENT_ABORTED;");
			code.println("break EXECUTION;");
			code.println("}");
			/*
			Element elem = (Element)action;
			String stateVar = varname("action");
			code.println(stateClass(elem) + " " + stateVar + " = " + newState(elem) + ";");
			printSetStateParameters(stateVar, getParameters(elem.getAttributes(), elem.getChildNodes()));
			code.println("if (" + stateVar + ".invoke(flowThread, currentState, event) == EVENT_ABORTED) {");
			code.println("eventResult = EVENT_ABORTED;");
			code.println("break EXECUTION;");
			code.println("}");
			 */
		}
	}

	private String location(XMLLocation location) {
		if (location == null)
			return "null";
		else
			return "new XMLLocation(new File(\"" + StringEscapeUtils.escapeJava(location.getFile().getAbsolutePath()) + "\"), " + location.getLineNumber() + ", " + location.getColumnNumber() + ")";
	}

	private String newState(String state) throws FlowCompilerException {
		if (state.contains("#")) {
			String flowName = state.substring(0, state.indexOf("#"));
			String stateName = state.substring(state.indexOf("#") + 1);
			if (flowName.equals("this")) {
				return "new " + stateName + "()";
			}
			for (Var var : flowXml.getVariables()) {
				if (var.getName().equals(flowName))
					return flowName + ".new " + stateName + "()";
			}
			for (Param param : flowXml.getParameters()) {
				if (param.getName().equals(flowName))
					return flowName + ".new " + stateName + "()";
			}
			return "new " + flowName + "." + stateName + "()";
		} else {
			return "new " + state + "()";
		}
	}

	private String newState(Element elem) {
		if (elem.getPrefix().equals("this")) {
			return "new " + elem.getLocalName() + "()";
		}
		for (Var var : flowXml.getVariables()) {
			if (var.getName().equals(elem.getPrefix()))
				return elem.getPrefix() + ".new " + elem.getLocalName() + "()";
		}
		for (Param param : flowXml.getParameters()) {
			if (param.getName().equals(elem.getPrefix()))
				return elem.getPrefix() + ".new " + elem.getLocalName() + "()";
		}
		return "new " + elem.getNamespaceURI() + "." + elem.getLocalName() + "()";
	}

	private String stateClass(String state) throws FlowCompilerException {
		if (state.contains("#")) {
			String flowName = state.substring(0, state.indexOf("#"));
			String stateName = state.substring(state.indexOf("#") + 1);
			for (Var var : flowXml.getVariables()) {
				if (var.getName().equals(flowName))
					return var.getType() + "." + stateName;
			}
			for (Param param : flowXml.getParameters()) {
				if (param.getName().equals(flowName))
					return param.getType() + "." + stateName;
			}
			throw new FlowCompilerException("Cannot resolve " + state);
		} else {
			return state;
		}
	}

	private String stateClass(Element elem) {
		return elem.getNamespaceURI() + "." + elem.getLocalName();
	}

	private Map<String,String> getParameters(NamedNodeMap attributes, NodeList childNodes) throws FlowCompilerException {
		Map<QName, String> otherAttributes = new HashMap<>();
		for (int i = 0; i < attributes.getLength(); i++) {
			if (attributes.item(i).getNamespaceURI() == null || !attributes.item(i).getNamespaceURI().equals("http://www.w3.org/2000/xmlns/")) {
				otherAttributes.put(new QName("iristk.flow.param", attributes.item(i).getLocalName()), attributes.item(i).getNodeValue());
			}
		}
		ArrayList<Object> content = new ArrayList<>();
		for (int i = 0; i < childNodes.getLength(); i++) {
			content.add(childNodes.item(i));
		}
		return getParameters(otherAttributes, content);
	}

	private Map<String,String> getParameters(Map<QName, String> attributes, List<Object> content) throws FlowCompilerException {
		Map<String,String> result = new HashMap<String,String>();
		for (QName attr : attributes.keySet()) {
			if (attr.getNamespaceURI() != null && attr.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/")) continue;
			String name = attr.getLocalPart();
			String value = formatAttrExpr(attributes.get(attr));
			result.put(name, value);
		}
		if (content.size() > 0) {
			List<Object> textChildren = new ArrayList<Object>();
			HashMap<String,List<String>> paramList = new HashMap<String,List<String>>();
			for (Object child : content) {
				if (child instanceof Element) {
					Element elem = (Element) child;
					if (elem.getNamespaceURI().equals("iristk.flow.param")) {
						String key = elem.getLocalName();
						if (!paramList.containsKey(key))
							paramList.put(key, new ArrayList<String>());
						List<Object> paramChildren = new ArrayList<Object>();
						for (int j = 0; j < elem.getChildNodes().getLength(); j++) {
							paramChildren.add(elem.getChildNodes().item(j));
						}
						String text = concatFun(processString(paramChildren)).replaceAll("\n", "").replaceAll("\\s+", " ").trim();
						paramList.get(key).add(text);
					} else {
						textChildren.add(child);
					}
				} else if (child instanceof Text && ((Text)child).getNodeValue().trim().length() > 0) {
					textChildren.add(child);
				} else if (child instanceof String && ((String)child).trim().length() > 0) {
					textChildren.add(child);
				}
			}
			if (textChildren.size() > 0) {
				if (!paramList.containsKey("text"))
					paramList.put("text", new ArrayList<String>());
				paramList.get("text").add(concatFun(processString(textChildren)).replaceAll("\n", "").replaceAll("\\s+", " "));
			}
			for (String key : paramList.keySet()) {
				if (paramList.get(key).size() > 1) {
					result.put(key, "java.util.Arrays.asList(" + listToString(paramList.get(key)) + ")");
				} else {
					result.put(key, paramList.get(key).get(0));
				}
			}
		}
		return result;
	}

	private void printSetStateParameters(String stateName, Map<String,String> paramMap) throws FlowCompilerException {
		for (String name : paramMap.keySet()) {
			code.println(stateName + ".set" + ucFirst(name) + "(" + paramMap.get(name) + ");");
		}
	}

	private void printInitEvent(String varName, String copy, String eventName, Map<String,String> paramMap) throws FlowCompilerException {
		//if (copy != null) {
		//	code.println("Event copy = " + formatAttrExpr(copy) + ";");
		//}
		if (eventName != null)
			code.println("Event " + varName + " = new Event(\"" + eventName + "\");");
		else if (copy != null)
			code.println("Event " + varName + " = new Event(" + formatAttrExpr(copy) + ".getName());");
		else throw new FlowCompilerException("Must have either copy or event parameter set when sending event");
		if (copy != null)
			code.println(varName + ".copyParams(" + formatAttrExpr(copy) + ");");
		for (String name : paramMap.keySet()) {
			code.println(varName + ".putIfNotNull(\"" + name + "\", " + paramMap.get(name) + ");");
		}
	}
	
	private void printActions(List<Object> list, Object parent) throws FlowCompilerException {
		for (Object action : list) {
			printAction(action, parent);
		}
	}

	private static String replaceIgnoreStrings(String expr, String pattern, final String repl) {
		return new Replacer(pattern, stringPattern) {	
			@Override
			protected String replace(Matcher matcher) {
				return repl;
			}
		}.replaceAll(expr);
	}

	private String formatCondExpr(String cond) throws FlowCompilerException {
		if (cond == null)
			return null;
		cond = formatAttrExpr(cond);
		cond = replaceIgnoreStrings(cond, " and ", " && ");
		cond = replaceIgnoreStrings(cond, " or ", " || ");
		return cond;
		//return "makeBool(" + cond + ")";
	}

	private String formatAttrExpr(String expr) throws FlowCompilerException {
		if (expr == null)
			return null;
		// ' => " (if not preceded by \)
		expr = expr.replaceAll("(?<!\\\\)'", "\"");
		// \' => ' 
		expr = expr.replaceAll("\\\\'", "'");
		// \ => \\ (if not followed by ") 
		expr = expr.replaceAll("\\\\(?!\")", "\\\\\\\\");
		expr = formatExpr(expr);
		return expr;
	}

	private String formatExec(String exec) throws FlowCompilerException {
		exec = formatExpr(exec);
		if (!exec.endsWith(";"))
			return exec + ";";
		else
			return exec;
	}

	public static String formatRecordExpr(String expr) throws FlowCompilerException {
		try {
			// return new Replacer("([A-Za-z0-9_]+)(\\?)?:([A-Za-z0-9_\\:\\.\\(\\)\\*]*)( *=(?!=)[^;]*)?", stringPattern) {
			return new Replacer("(\\?)?:([A-Za-z0-9_\\:\\.\\(\\)\\*]*)( *=(?!=)[^;]*)?", stringPattern) {
				@Override
				protected String replace(Matcher matcher) {
					//String recordVar = matcher.group(1);
					String hasSign = matcher.group(1);
					String getStr = matcher.group(2);
					String assignStr = matcher.group(3);
					String[] split = Replacer.paraSplit(getStr);
					getStr = split[0];
					String rest = split[1];
					boolean dynamic = false;
					if (getStr.endsWith("(")) {
						dynamic = true;
						getStr = getStr.substring(0, getStr.length() - 1);
					}
					int para = 0;
					String getExpr = "";
					String getPart = "";
					getStr += ":";
					for (int i = 0; i < getStr.length(); i++) {
						String c = getStr.substring(i, i + 1); 
						if (c.equals("(")) {
							getPart += c;
							para++;
						} else if (c.equals(")")) {
							getPart += c;
							para--;
						} else if (c.equals(":") && para == 0) {
							if (getPart.startsWith("("))
								getPart = getPart.substring(1, getPart.length() - 1);
							else 
								getPart = "\"" + getPart + "\"";
							if (getExpr.length() == 0)
								getExpr = getPart;
							else
								getExpr += " + \":\" + " + getPart;
							getPart = "";
						} else {
							getPart += c;
						}
					}
					getExpr = "\"\" + " + getExpr;
					getExpr = getExpr.replaceAll("\" \\+ \"", "");
					if (assignStr != null && rest.length() == 0) {
						String put = assignStr.trim().substring(1).trim();
						return ".putIfNotNull(" + getExpr + ", " + put + ")";
					} else if (dynamic) {
						return ".getDynamic(" + getExpr + ", " + rest;
					} else if (hasSign != null) {
						//String sign = boolSign;
						//if (boolSign.equals("?")) sign = "";
						//if (getExpr.contains("*"))
						return ".has(" + getExpr + ")" + rest;
						//else
						//	return sign + "makeBool(" + recordVar + ".get(" + getExpr + "))" + rest;
					} else {
						return ".get(" + getExpr + ")" + rest;
					}
				} 
			}.replaceIter(expr);
		} catch (RuntimeException e) {
			throw new FlowCompilerException(e.getMessage());
		}
	}

	public static String formatExpr(String expr) throws FlowCompilerException {
		expr = formatRecordExpr(expr);
		expr = new Replacer(" *:=([^;]*)", stringPattern) {
			@Override
			protected String replace(Matcher matcher) {
				return ".putAll(" + matcher.group(1).trim() + ")";
			} 
		}.replaceIter(expr);
		expr = formatEqExpr(expr);
		return expr;
	}

	private static int findExpr(String expr, int pos, int dir) {
		int para = 0;
		boolean inQuote = false;
		boolean hasChar = false;
		int i = pos;
		for (; i > 0 && i < expr.length(); i += dir) {
			String c = expr.substring(i, i+1);
			if (c.equals("(")) {
				para += dir;
			} else if (c.equals(")")) {
				para -= dir;
			} else if (c.equals("\"")) {
				inQuote = !inQuote;
			} else if (c.equals(" ") && para <= 0 && !inQuote && hasChar) {
				return i;
			} 
			if (para < 0) {
				return i - dir;
			}
			if (!c.equals(" ")) hasChar = true;
		}
		return i;
	}

	public static String formatEqExpr(String expr) {
		return expr;
	}

	public static void compile(String flowFileName, String dir, boolean binary) throws FlowCompilerException {
		File fdir = new File(dir);
		File flowFile = null;
		if (new File(flowFileName).exists()) {
			flowFile = new File(flowFileName);
		} else if (new File(fdir, flowFileName).exists()) {
			flowFile = new File(fdir, flowFileName);
		} else {
			String f = FileFinder.findFirst(dir + "/src", flowFileName);
			if (f != null) {
				flowFile = new File(f);
			}
		}
		if (flowFile != null) {
			System.out.println("Compiling flow: " + flowFile.getAbsolutePath());
			FlowCompiler fcompiler = new FlowCompiler(flowFile);
			File srcFile = new File(flowFile.getAbsolutePath().replaceFirst("\\.[A-Za-z]+$", "\\.java"));
			fcompiler.compileToFile(srcFile);
			System.out.println("Compiled to source code: " + srcFile.getAbsolutePath());
			if (fcompiler.flowXml.hasPublicStates()) {
				FlowSchemaCompiler.compile(flowFile.getAbsolutePath(), System.getProperty("user.dir"));
			}
			if (binary) {
				JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
				if (compiler == null) {
					throw new RuntimeException("Could not find Java Compiler");
				}
				String binDir = flowFile.getAbsolutePath().replaceFirst("([\\\\/])src[\\\\/].*", "$1") + "bin";
				if (new File(binDir).exists()) {
					if (compiler.run(null, null, null, srcFile.getPath(), "-d", binDir) == 0) {
						System.out.println("Compiled to byte code folder: " + binDir);
					}
				} else {
					throw new RuntimeException("Directory " + binDir + " does not exist");
				}
			}
		}
	}

	public static void main(String[] args)  {
		try {
			boolean compileToBinary = false;
			//boolean compileToXSD = false;
			String file = null;
			for (String arg : args) {
				if (arg.equals("-b")) 
					compileToBinary = true;
				//else if (arg.equals("-x")) 
				//	compileToXSD = true;
				else
					file = arg;
			}
			if (file != null) {
				compile(file, System.getProperty("user.dir"), compileToBinary);

				//if (compileToXSD)
				//	FlowSchemaCompiler.compile(file, System.getProperty("user.dir"));
			} else {
				System.out.println("Compiles flow XML to Java source.\n");
				System.out.println("Usage:");
				System.out.println("iristk cflow [OPTIONS] XML\n");
				System.out.println("Options:");
				System.out.println("-b  Also compile Java source to binary");
				//System.out.println("-x  Also compile templates to XSD Schema");
			}
		} catch (FlowCompilerException e) {
			System.err.println("Error on line " + e.getLineNumber() + ": " + e.getMessage());
		}
	}

	public void useUniqueNames(boolean b) {
		this.useUniqueNames   = b;
	}

	public String getFlowName() {
		return flowXml.getPackage() + "." + getLocalFlowName();
	}

	public String getLocalFlowName() {
		if (useUniqueNames)
			return flowXml.getName() + uniqueNameSuffix;
		else
			return flowXml.getName();
	}

}
