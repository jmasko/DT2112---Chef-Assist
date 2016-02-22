package iristk.situated;

import java.io.File;
import iristk.xml.XmlMarshaller.XMLLocation;
import iristk.system.Event;
import iristk.flow.*;
import iristk.util.Record;
import static iristk.util.Converters.*;

public class SystemAgentFlow extends iristk.flow.Flow {

	private SystemAgent systemAgent;
	private String agent;
	private Record gestures;
	private iristk.speech.SpeechTextProcessor speechTextProcessor;

	private void initVariables() {
		agent = asString(systemAgent.id);
		gestures = asRecord(new Record());
	}

	public String getAgent() {
		return this.agent;
	}

	public void setAgent(String value) {
		this.agent = value;
	}

	public Record getGestures() {
		return this.gestures;
	}

	public void setGestures(Record value) {
		this.gestures = value;
	}

	public iristk.speech.SpeechTextProcessor getSpeechTextProcessor() {
		return this.speechTextProcessor;
	}

	public void setSpeechTextProcessor(iristk.speech.SpeechTextProcessor value) {
		this.speechTextProcessor = value;
	}

	public SystemAgent getSystemAgent() {
		return this.systemAgent;
	}

	@Override
	public Object getVariable(String name) {
		if (name.equals("agent")) return this.agent;
		if (name.equals("gestures")) return this.gestures;
		if (name.equals("speechTextProcessor")) return this.speechTextProcessor;
		if (name.equals("systemAgent")) return this.systemAgent;
		return null;
	}


	public SystemAgentFlow(SystemAgent systemAgent) {
		this.systemAgent = systemAgent;
		initVariables();
	}

	@Override
	protected State getInitialState() {return new Idle();}

	public static String[] getPublicStates() {
		return new String[] {"attend", "attendNobody", "attendRandom", "attendOther", "attendAll", "fallAsleep", "prompt", "say", "listen"};
	}

	private class Base extends State {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
			flowThread.addEventClock(1000, 5000, "timer_231685785");
		}

		@Override
		public void onentry() {
			int eventResult;
			Event event = new Event("state.enter");
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(183264084) + 1;
			if (event.triggers("action.attend")) {
				if (eq(event.get("target"),"nobody") && eq(event.get("agent"), agent)) {
					incrCount(183264084);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						Idle state0 = new Idle();
						flowThread.gotoState(state0, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 16, 24)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(1490180672) + 1;
			if (event.triggers("action.attend.asleep")) {
				if (eq(event.get("agent"), agent)) {
					incrCount(1490180672);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						Asleep state1 = new Asleep();
						flowThread.gotoState(state1, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 19, 26)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(1919892312) + 1;
			if (event.triggers("action.attend.all")) {
				if (eq(event.get("agent"), agent)) {
					incrCount(1919892312);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						AttendingAll state2 = new AttendingAll();
						flowThread.gotoState(state2, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 22, 32)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(250075633) + 1;
			if (event.triggers("action.attend")) {
				if (event.has("location") && eq(event.get("agent"), agent)) {
					incrCount(250075633);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						AttendingLocation state3 = new AttendingLocation();
						state3.setMode(event.get("mode"));
						state3.setLocation(event.get("location"));
						flowThread.gotoState(state3, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 25, 85)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(517938326) + 1;
			if (event.triggers("action.attend")) {
				if (event.has("target") && systemAgent.hasUser(asString(event.get("target"))) && eq(event.get("agent"), agent)) {
					incrCount(517938326);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						AttendingAgent state4 = new AttendingAgent();
						state4.setMode(event.get("mode"));
						state4.setTarget(event.get("target"));
						flowThread.gotoState(state4, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 28, 78)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(110718392) + 1;
			if (event.triggers("action.attend")) {
				if (systemAgent.hasItem(asString(event.get("target")))) {
					incrCount(110718392);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						AttendingItem state5 = new AttendingItem();
						state5.setMode(event.get("mode"));
						state5.setTarget(event.get("target"));
						flowThread.gotoState(state5, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 31, 77)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(2143192188) + 1;
			if (event.triggers("monitor.speech.prominence")) {
				incrCount(2143192188);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					if (gestures.has("" + event.get("action"))) {
						Event sendEvent6 = new Event("action.gesture");
						sendEvent6.putIfNotNull("name", gestures.get("" + event.get("action")));
						flowRunner.sendEvent(sendEvent6, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 35, 68)));
					}
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			count = getCount(231685785) + 1;
			if (event.triggers("timer_231685785")) {
				incrCount(231685785);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					Event raiseEvent7 = new Event("blink");
					if (flowThread.raiseEvent(raiseEvent7, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 39, 26))) == State.EVENT_ABORTED) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			count = getCount(2110121908) + 1;
			if (event.triggers("blink")) {
				incrCount(2110121908);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					Event sendEvent8 = new Event("action.gesture");
					sendEvent8.putIfNotNull("agent", agent);
					sendEvent8.putIfNotNull("name", "blink");
					flowRunner.sendEvent(sendEvent8, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 42, 67)));
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class Idle extends Base {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() {
			int eventResult;
			Event event = new Event("state.enter");
			EXECUTION: {
				int count = getCount(1023487453) + 1;
				incrCount(1023487453);
				Event sendEvent9 = new Event("action.gaze");
				sendEvent9.putIfNotNull("mode", "headpose");
				sendEvent9.putIfNotNull("agent", agent);
				sendEvent9.putIfNotNull("location", systemAgent.getRelative(systemAgent.getNobody().getHeadLocation()));
				flowRunner.sendEvent(sendEvent9, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 48, 147)));
				Event sendEvent10 = new Event("monitor.attend");
				sendEvent10.putIfNotNull("agent", agent);
				sendEvent10.putIfNotNull("target", "nobody");
				flowRunner.sendEvent(sendEvent10, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 49, 70)));
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class AttendingAll extends Base {

		final State currentState = this;
		public Agent gazeTarget = systemAgent.getRandomUser();
		public Location middle;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
			flowThread.addEventClock(1000, 1000, "timer_917142466");
		}

		@Override
		public void onentry() {
			int eventResult;
			Event event = new Event("state.enter");
			EXECUTION: {
				int count = getCount(474675244) + 1;
				incrCount(474675244);
				Event raiseEvent11 = new Event("adjustHeadPose");
				if (flowThread.raiseEvent(raiseEvent11, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 57, 35))) == State.EVENT_ABORTED) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				Event sendEvent12 = new Event("monitor.attend.all");
				flowRunner.sendEvent(sendEvent12, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 58, 38)));
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(1579572132) + 1;
			if (event.triggers("adjustHeadPose")) {
				incrCount(1579572132);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					Location newMiddle = systemAgent.getUsersMiddleLocation();
					if (middle == null || newMiddle.distance(middle) > 0.2) {
						middle = newMiddle;
						Event sendEvent13 = new Event("action.gaze");
						sendEvent13.putIfNotNull("mode", "headpose");
						sendEvent13.putIfNotNull("location", systemAgent.getRelative(middle));
						flowRunner.sendEvent(sendEvent13, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 64, 97)));
					}
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			count = getCount(917142466) + 1;
			if (event.triggers("timer_917142466")) {
				incrCount(917142466);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					Event raiseEvent14 = new Event("adjustHeadPose");
					if (flowThread.raiseEvent(raiseEvent14, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 68, 35))) == State.EVENT_ABORTED) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					gazeTarget = systemAgent.getOtherUser(gazeTarget.id);
					Event sendEvent15 = new Event("action.gaze");
					sendEvent15.putIfNotNull("mode", "eyes");
					sendEvent15.putIfNotNull("location", systemAgent.getRelative(gazeTarget.getHeadLocation()));
					flowRunner.sendEvent(sendEvent15, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 70, 114)));
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class AttendingAgent extends Base {

		final State currentState = this;
		public Agent user;

		public String target = null;
		public String mode = asString("headpose");

		public void setTarget(Object value) {
			if (value != null) {
				target = asString(value);
				params.put("target", value);
			}
		}

		public void setMode(Object value) {
			if (value != null) {
				mode = asString(value);
				params.put("mode", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() {
			int eventResult;
			Event event = new Event("state.enter");
			EXECUTION: {
				int count = getCount(1227229563) + 1;
				incrCount(1227229563);
				user = systemAgent.getUser(target);
				Event sendEvent16 = new Event("action.gaze");
				sendEvent16.putIfNotNull("mode", mode);
				sendEvent16.putIfNotNull("agent", agent);
				sendEvent16.putIfNotNull("location", systemAgent.getRelative(user.getHeadLocation()));
				flowRunner.sendEvent(sendEvent16, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 80, 122)));
				if (eq(mode,"headpose")) {
					mode = "default";
				}
				Event sendEvent17 = new Event("monitor.attend");
				sendEvent17.putIfNotNull("agent", agent);
				sendEvent17.putIfNotNull("target", target);
				flowRunner.sendEvent(sendEvent17, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 84, 68)));
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(1910163204) + 1;
			if (event.triggers("sense.user.move")) {
				if (eq(event.get("agent"), agent)) {
					incrCount(1910163204);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						Event sendEvent18 = new Event("action.gaze");
						sendEvent18.putIfNotNull("mode", mode);
						sendEvent18.putIfNotNull("agent", agent);
						sendEvent18.putIfNotNull("location", systemAgent.getRelative(user.getHeadLocation()));
						flowRunner.sendEvent(sendEvent18, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 87, 122)));
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class AttendingItem extends Base {

		final State currentState = this;

		public String target = null;
		public String mode = asString("default");

		public void setTarget(Object value) {
			if (value != null) {
				target = asString(value);
				params.put("target", value);
			}
		}

		public void setMode(Object value) {
			if (value != null) {
				mode = asString(value);
				params.put("mode", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() {
			int eventResult;
			Event event = new Event("state.enter");
			EXECUTION: {
				int count = getCount(1940030785) + 1;
				incrCount(1940030785);
				if (systemAgent.hasItem(target)) {
					Event sendEvent19 = new Event("action.gaze");
					sendEvent19.putIfNotNull("mode", mode);
					sendEvent19.putIfNotNull("location", systemAgent.getRelative(systemAgent.getItem(target).location));
					flowRunner.sendEvent(sendEvent19, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 96, 121)));
				}
				Event sendEvent20 = new Event("monitor.attend");
				sendEvent20.putIfNotNull("target", target);
				flowRunner.sendEvent(sendEvent20, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 98, 52)));
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(125993742) + 1;
			if (event.triggers("sense.item.move")) {
				if (systemAgent.hasItem(target)) {
					incrCount(125993742);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						Event sendEvent21 = new Event("action.gaze");
						sendEvent21.putIfNotNull("location", systemAgent.getRelative(systemAgent.getItem(target).location));
						flowRunner.sendEvent(sendEvent21, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 101, 106)));
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class AttendingLocation extends Base {

		final State currentState = this;

		public Location location = null;
		public String mode = asString("default");

		public void setLocation(Object value) {
			if (value != null) {
				location = (Location) value;
				params.put("location", value);
			}
		}

		public void setMode(Object value) {
			if (value != null) {
				mode = asString(value);
				params.put("mode", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() {
			int eventResult;
			Event event = new Event("state.enter");
			EXECUTION: {
				int count = getCount(380894366) + 1;
				incrCount(380894366);
				systemAgent.setAttending(systemAgent.getNobody().id);
				Event sendEvent22 = new Event("action.gaze");
				sendEvent22.putIfNotNull("agent", agent);
				sendEvent22.putIfNotNull("location", systemAgent.getRelative(location));
				flowRunner.sendEvent(sendEvent22, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 110, 94)));
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class Asleep extends Base {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() {
			int eventResult;
			Event event = new Event("state.enter");
			EXECUTION: {
				int count = getCount(1608446010) + 1;
				incrCount(1608446010);
				Event sendEvent23 = new Event("action.gaze");
				sendEvent23.putIfNotNull("mode", "headpose");
				sendEvent23.putIfNotNull("agent", agent);
				sendEvent23.putIfNotNull("location", systemAgent.getRelative(systemAgent.getNobody().getHeadLocation()));
				flowRunner.sendEvent(sendEvent23, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 116, 147)));
				Event sendEvent24 = new Event("action.gesture");
				sendEvent24.putIfNotNull("agent", agent);
				sendEvent24.putIfNotNull("name", "sleep");
				flowRunner.sendEvent(sendEvent24, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 117, 67)));
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(1509514333) + 1;
			if (event.triggers("blink")) {
				incrCount(1509514333);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}
		@Override
		public void onexit() {
			int eventResult;
			Event event = new Event("state.exit");
			EXECUTION: {
				Event sendEvent25 = new Event("action.gesture");
				sendEvent25.putIfNotNull("agent", agent);
				sendEvent25.putIfNotNull("name", "blink");
				flowRunner.sendEvent(sendEvent25, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 120, 67)));
			}
			super.onexit();
		}

	}


	public class attend extends State {

		final State currentState = this;

		public String mode = asString("headpose");
		public String target = asString("nobody");
		public Location location = null;
		public String part = asString("head");

		public void setMode(Object value) {
			if (value != null) {
				mode = asString(value);
				params.put("mode", value);
			}
		}

		public void setTarget(Object value) {
			if (value != null) {
				target = asString(value);
				params.put("target", value);
			}
		}

		public void setLocation(Object value) {
			if (value != null) {
				location = (Location) value;
				params.put("location", value);
			}
		}

		public void setPart(Object value) {
			if (value != null) {
				part = asString(value);
				params.put("part", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() {
			int eventResult;
			Event event = new Event("state.enter");
			EXECUTION: {
				int count = getCount(1361960727) + 1;
				incrCount(1361960727);
				systemAgent.setAttending(target);
				if (location != null) {
					Event sendEvent26 = new Event("action.attend");
					sendEvent26.putIfNotNull("mode", mode);
					sendEvent26.putIfNotNull("agent", agent);
					sendEvent26.putIfNotNull("location", location);
					flowRunner.sendEvent(sendEvent26, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 133, 86)));
				} else {
					Event sendEvent27 = new Event("action.attend");
					sendEvent27.putIfNotNull("mode", mode);
					sendEvent27.putIfNotNull("agent", agent);
					sendEvent27.putIfNotNull("part", part);
					sendEvent27.putIfNotNull("target", target);
					flowRunner.sendEvent(sendEvent27, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 135, 96)));
				}
				flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 137, 13)));
				eventResult = EVENT_ABORTED;
				break EXECUTION;
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	public class attendNobody extends State {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() {
			int eventResult;
			Event event = new Event("state.enter");
			EXECUTION: {
				int count = getCount(2085857771) + 1;
				incrCount(2085857771);
				systemAgent.setAttending("nobody");
				Event sendEvent28 = new Event("action.attend");
				sendEvent28.putIfNotNull("agent", agent);
				sendEvent28.putIfNotNull("target", "nobody");
				flowRunner.sendEvent(sendEvent28, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 144, 69)));
				flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 145, 13)));
				eventResult = EVENT_ABORTED;
				break EXECUTION;
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	public class attendRandom extends State {

		final State currentState = this;
		public String random = asString(systemAgent.getRandomUser().id);

		public String mode = asString("headpose");

		public void setMode(Object value) {
			if (value != null) {
				mode = asString(value);
				params.put("mode", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() {
			int eventResult;
			Event event = new Event("state.enter");
			EXECUTION: {
				int count = getCount(716083600) + 1;
				incrCount(716083600);
				systemAgent.setAttending(random);
				Event sendEvent29 = new Event("action.attend");
				sendEvent29.putIfNotNull("mode", mode);
				sendEvent29.putIfNotNull("agent", agent);
				sendEvent29.putIfNotNull("target", random);
				flowRunner.sendEvent(sendEvent29, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 154, 81)));
				flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 155, 13)));
				eventResult = EVENT_ABORTED;
				break EXECUTION;
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	public class attendOther extends State {

		final State currentState = this;
		public String other = asString(systemAgent.getOtherUser().id);

		public String mode = asString("headpose");

		public void setMode(Object value) {
			if (value != null) {
				mode = asString(value);
				params.put("mode", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() {
			int eventResult;
			Event event = new Event("state.enter");
			EXECUTION: {
				int count = getCount(1908153060) + 1;
				incrCount(1908153060);
				systemAgent.setAttending(other);
				Event sendEvent30 = new Event("action.attend");
				sendEvent30.putIfNotNull("mode", mode);
				sendEvent30.putIfNotNull("agent", agent);
				sendEvent30.putIfNotNull("target", other);
				flowRunner.sendEvent(sendEvent30, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 164, 80)));
				flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 165, 13)));
				eventResult = EVENT_ABORTED;
				break EXECUTION;
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	public class attendAll extends State {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() {
			int eventResult;
			Event event = new Event("state.enter");
			EXECUTION: {
				int count = getCount(1627800613) + 1;
				incrCount(1627800613);
				if (systemAgent.hasManyUsers()) {
					systemAgent.setAttendingAll();
					Event sendEvent31 = new Event("action.attend.all");
					sendEvent31.putIfNotNull("agent", agent);
					flowRunner.sendEvent(sendEvent31, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 173, 54)));
				}
				flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 175, 13)));
				eventResult = EVENT_ABORTED;
				break EXECUTION;
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	public class fallAsleep extends State {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() {
			int eventResult;
			Event event = new Event("state.enter");
			EXECUTION: {
				int count = getCount(48612937) + 1;
				incrCount(48612937);
				systemAgent.setAttending("nobody");
				Event sendEvent32 = new Event("action.attend.asleep");
				sendEvent32.putIfNotNull("agent", agent);
				flowRunner.sendEvent(sendEvent32, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 182, 56)));
				flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 183, 13)));
				eventResult = EVENT_ABORTED;
				break EXECUTION;
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	public class prompt extends listen {

		final State currentState = this;

		public String text = null;

		public void setText(Object value) {
			if (value != null) {
				text = asString(value);
				params.put("text", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() {
			int eventResult;
			Event event = new Event("state.enter");
			EXECUTION: {
				int count = getCount(1782113663) + 1;
				incrCount(1782113663);
				Event sendEvent33 = new Event("action.speech");
				sendEvent33.putIfNotNull("agent", agent);
				sendEvent33.putIfNotNull("text", text);
				flowRunner.sendEvent(sendEvent33, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 190, 64)));
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(476800120) + 1;
			if (event.triggers("monitor.speech.start")) {
				incrCount(476800120);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					Event sendEvent34 = new Event("action.listen");
					sendEvent34.putIfNotNull("endSilTimeout", endSil);
					sendEvent34.putIfNotNull("context", context);
					sendEvent34.putIfNotNull("noSpeechTimeout", timeout + asInteger(event.get("length")));
					flowRunner.sendEvent(sendEvent34, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 193, 156)));
					listenActionId = sendEvent34.getId();
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			count = getCount(1254526270) + 1;
			if (event.triggers("sense.user.speech.start")) {
				if (eq(event.get("speakers"), 1)) {
					incrCount(1254526270);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						Event sendEvent35 = new Event("action.speech.stop");
						flowRunner.sendEvent(sendEvent35, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 196, 38)));
						eventResult = EVENT_IGNORED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	public class say extends State {

		final State currentState = this;
		public String action;

		public String text = null;
		public boolean async = false;
		public boolean ifsilent = false;
		public String gesture = asString("brow_raise");
		public String display = asString(null);
		public String audio = asString(null);
		public boolean abort = false;

		public void setText(Object value) {
			if (value != null) {
				text = asString(value);
				params.put("text", value);
			}
		}

		public void setAsync(Object value) {
			if (value != null) {
				async = (boolean) value;
				params.put("async", value);
			}
		}

		public void setIfsilent(Object value) {
			if (value != null) {
				ifsilent = (boolean) value;
				params.put("ifsilent", value);
			}
		}

		public void setGesture(Object value) {
			if (value != null) {
				gesture = asString(value);
				params.put("gesture", value);
			}
		}

		public void setDisplay(Object value) {
			if (value != null) {
				display = asString(value);
				params.put("display", value);
			}
		}

		public void setAudio(Object value) {
			if (value != null) {
				audio = asString(value);
				params.put("audio", value);
			}
		}

		public void setAbort(Object value) {
			if (value != null) {
				abort = (boolean) value;
				params.put("abort", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() {
			int eventResult;
			Event event = new Event("state.enter");
			EXECUTION: {
				int count = getCount(1395089624) + 1;
				incrCount(1395089624);
				if (speechTextProcessor != null) {
					text = speechTextProcessor.process(text);
					display = speechTextProcessor.process(display);
				}
				display = SystemAgent.processDisplay(text, display);
				Event sendEvent36 = new Event("action.speech");
				sendEvent36.putIfNotNull("async", async);
				sendEvent36.putIfNotNull("agent", agent);
				sendEvent36.putIfNotNull("abort", abort);
				sendEvent36.putIfNotNull("display", display);
				sendEvent36.putIfNotNull("text", text);
				sendEvent36.putIfNotNull("audio", audio);
				sendEvent36.putIfNotNull("ifsilent", ifsilent);
				flowRunner.sendEvent(sendEvent36, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 226, 23)));
				action = sendEvent36.getId();
				gestures.putIfNotNull("" + action, gesture);
				if (async) {
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 229, 14)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(1279149968) + 1;
			if (event.triggers("monitor.speech.done")) {
				if (eq(event.get("agent"), agent)) {
					incrCount(1279149968);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						Event returnEvent37 = new Event("monitor.speech.done");
						returnEvent37.putIfNotNull("agent", agent);
						flowThread.returnFromCall(this, returnEvent37, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 233, 57)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	public class listen extends State {

		final State currentState = this;
		public MultiSpeech multispeech = new MultiSpeech(systemAgent);
		public String listenActionId = asString("");

		public int timeout = asInteger(8000);
		public int endSil = asInteger(700);
		public int nbest = asInteger(1);
		public String context = null;

		public void setTimeout(Object value) {
			if (value != null) {
				timeout = asInteger(value);
				params.put("timeout", value);
			}
		}

		public void setEndSil(Object value) {
			if (value != null) {
				endSil = asInteger(value);
				params.put("endSil", value);
			}
		}

		public void setNbest(Object value) {
			if (value != null) {
				nbest = asInteger(value);
				params.put("nbest", value);
			}
		}

		public void setContext(Object value) {
			if (value != null) {
				context = asString(value);
				params.put("context", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() {
			int eventResult;
			Event event = new Event("state.enter");
			EXECUTION: {
				int count = getCount(429313384) + 1;
				incrCount(429313384);
				Event sendEvent38 = new Event("action.listen");
				sendEvent38.putIfNotNull("endSilTimeout", endSil);
				sendEvent38.putIfNotNull("context", context);
				sendEvent38.putIfNotNull("noSpeechTimeout", timeout);
				sendEvent38.putIfNotNull("nbest", nbest);
				flowRunner.sendEvent(sendEvent38, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 247, 146)));
				listenActionId = sendEvent38.getId();
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(1830712962) + 1;
			if (event.triggers("sense.speech.start")) {
				if (eq(event.get("action"), listenActionId)) {
					incrCount(1830712962);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						String agent = systemAgent.getUserId(event);
						boolean isAttendingSystem = systemAgent.getUser(agent).isAttending(systemAgent.id);
						multispeech.addStart(agent, isAttendingSystem);
						Event sendEvent39 = new Event("sense.user.speech.start");
						sendEvent39.putIfNotNull("agent", systemAgent.id);
						sendEvent39.putIfNotNull("attsys", isAttendingSystem);
						sendEvent39.putIfNotNull("speakers", multispeech.speakers);
						sendEvent39.putIfNotNull("sensor", event.get("sensor"));
						sendEvent39.putIfNotNull("user", agent);
						flowRunner.sendEvent(sendEvent39, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 253, 170)));
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(705265961) + 1;
			if (event.triggers("sense.user.attend")) {
				if (eq(event.get("target"), systemAgent.id) && multispeech.hasStarted(asString(event.get("user")))) {
					incrCount(705265961);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						multispeech.attendingSystem(asString(event.get("agent")));
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(317983781) + 1;
			if (event.triggers("sense.speech.end")) {
				if (eq(event.get("action"), listenActionId)) {
					incrCount(317983781);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						String agent = systemAgent.getUserId(event);
						multispeech.speakers--;
						Event sendEvent40 = new Event("sense.user.speech.end");
						sendEvent40.putIfNotNull("agent", systemAgent.id);
						sendEvent40.putIfNotNull("attsys", multispeech.someAttendingSystem());
						sendEvent40.putIfNotNull("speakers", multispeech.speakers);
						sendEvent40.putIfNotNull("sensor", event.get("sensor"));
						sendEvent40.putIfNotNull("user", agent);
						flowRunner.sendEvent(sendEvent40, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 261, 184)));
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(104739310) + 1;
			if (event.triggers("sense.speech.rec**")) {
				if (eq(event.get("action"), listenActionId)) {
					incrCount(104739310);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						multispeech.addRec(systemAgent.getUserId(event), event);
						if (multispeech.runningRecognizers == 0) {
							Event result = multispeech.getEvent();
							Event returnEvent41 = new Event(result.getName());
							returnEvent41.copyParams(result);
							flowThread.returnFromCall(this, returnEvent41, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 267, 28)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}
		@Override
		public void onexit() {
			int eventResult;
			Event event = new Event("state.exit");
			EXECUTION: {
				Event sendEvent42 = new Event("action.listen.stop");
				sendEvent42.putIfNotNull("action", listenActionId);
				flowRunner.sendEvent(sendEvent42, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\situated\\SystemAgentFlow.xml"), 271, 64)));
			}
			super.onexit();
		}

	}


}
