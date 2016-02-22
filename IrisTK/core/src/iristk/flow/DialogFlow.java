package iristk.flow;

import java.io.File;
import iristk.xml.XmlMarshaller.XMLLocation;
import iristk.system.Event;
import static iristk.util.Converters.*;

public class DialogFlow extends iristk.flow.Flow {


	private void initVariables() {
	}

	@Override
	public Object getVariable(String name) {
		return null;
	}


	public DialogFlow() {
		initVariables();
	}

	public static String[] getPublicStates() {
		return new String[] {"say", "listen", "prompt", "wait"};
	}

	public static class say extends State {

		final State currentState = this;
		public String action;

		public String text = null;
		public boolean async = false;
		public boolean ifsilent = false;
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
				int count = getCount(460332449) + 1;
				incrCount(460332449);
				Event sendEvent0 = new Event("action.speech");
				sendEvent0.putIfNotNull("async", async);
				sendEvent0.putIfNotNull("abort", abort);
				sendEvent0.putIfNotNull("display", display);
				sendEvent0.putIfNotNull("text", text);
				sendEvent0.putIfNotNull("audio", audio);
				sendEvent0.putIfNotNull("ifsilent", ifsilent);
				flowRunner.sendEvent(sendEvent0, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\flow\\DialogFlow.xml"), 22, 23)));
				action = sendEvent0.getId();
				if (async) {
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\flow\\DialogFlow.xml"), 24, 14)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(358699161) + 1;
			if (event.triggers("monitor.speech.done")) {
				incrCount(358699161);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					Event returnEvent1 = new Event("monitor.speech.done");
					flowThread.returnFromCall(this, returnEvent1, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\flow\\DialogFlow.xml"), 28, 41)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
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


	public static class listen extends State {

		final State currentState = this;
		public String actionId;

		public int timeout = asInteger(8000);
		public int endSil = asInteger(500);
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
				int count = getCount(231685785) + 1;
				incrCount(231685785);
				Event sendEvent2 = new Event("action.listen");
				sendEvent2.putIfNotNull("endSilTimeout", endSil);
				sendEvent2.putIfNotNull("context", context);
				sendEvent2.putIfNotNull("noSpeechTimeout", timeout);
				sendEvent2.putIfNotNull("nbest", nbest);
				flowRunner.sendEvent(sendEvent2, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\flow\\DialogFlow.xml"), 39, 140)));
				actionId = sendEvent2.getId();
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(2110121908) + 1;
			if (event.triggers("sense.speech.rec")) {
				if (eq(event.get("action"),actionId)) {
					incrCount(2110121908);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						Event returnEvent3 = new Event("sense.user.speak");
						returnEvent3.copyParams(event);
						flowThread.returnFromCall(this, returnEvent3, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\flow\\DialogFlow.xml"), 42, 51)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(1973538135) + 1;
			if (event.triggers("sense.speech.rec.silence")) {
				if (eq(event.get("action"),actionId)) {
					incrCount(1973538135);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						Event returnEvent4 = new Event("sense.user.silence");
						returnEvent4.copyParams(event);
						flowThread.returnFromCall(this, returnEvent4, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\flow\\DialogFlow.xml"), 45, 53)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(1865127310) + 1;
			if (event.triggers("sense.speech.rec.*")) {
				if (eq(event.get("action"),actionId)) {
					incrCount(1865127310);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						Event returnEvent5 = new Event("sense.user.speak");
						returnEvent5.copyParams(event);
						returnEvent5.putIfNotNull("text", iristk.speech.RecResult.NOMATCH);
						flowThread.returnFromCall(this, returnEvent5, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\flow\\DialogFlow.xml"), 48, 92)));
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
		@Override
		public void onexit() {
			int eventResult;
			Event event = new Event("state.exit");
			EXECUTION: {
				Event sendEvent6 = new Event("action.listen.stop");
				flowRunner.sendEvent(sendEvent6, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\flow\\DialogFlow.xml"), 51, 38)));
			}
			super.onexit();
		}

	}


	public static class prompt extends listen {

		final State currentState = this;
		public String sayActionId;

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
				int count = getCount(212628335) + 1;
				incrCount(212628335);
				Event sendEvent7 = new Event("action.speech");
				sendEvent7.putIfNotNull("text", text);
				flowRunner.sendEvent(sendEvent7, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\flow\\DialogFlow.xml"), 59, 69)));
				sayActionId = sendEvent7.getId();
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(359023572) + 1;
			if (event.triggers("monitor.speech.start")) {
				if (eq(sayActionId,event.get("action"))) {
					incrCount(359023572);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						Event sendEvent8 = new Event("action.listen");
						sendEvent8.putIfNotNull("endSilTimeout", endSil);
						sendEvent8.putIfNotNull("noSpeechTimeout", timeout + asInteger(event.get("length")));
						flowRunner.sendEvent(sendEvent8, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\flow\\DialogFlow.xml"), 62, 130)));
						actionId = sendEvent8.getId();
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(2111991224) + 1;
			if (event.triggers("sense.speech.start")) {
				if (eq(actionId,event.get("action"))) {
					incrCount(2111991224);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						Event sendEvent9 = new Event("action.speech.stop");
						sendEvent9.putIfNotNull("action", sayActionId);
						flowRunner.sendEvent(sendEvent9, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\flow\\DialogFlow.xml"), 65, 61)));
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


	public static class wait extends State {

		final State currentState = this;

		public Integer msec = asInteger(1000);

		public void setMsec(Object value) {
			if (value != null) {
				msec = asInteger(value);
				params.put("msec", value);
			}
		}

		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		iristk.util.DelayedEvent timer_405662939;
		@Override
		public void onentry() {
			int eventResult;
			Event event = new Event("state.enter");
			if (timer_405662939 != null) timer_405662939.forget();
			timer_405662939 = flowThread.raiseEvent(new Event("timer_405662939"), msec, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\flow\\DialogFlow.xml"), 71, 29)));
			forgetOnExit(timer_405662939);
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(405662939) + 1;
			if (event.triggers("timer_405662939")) {
				incrCount(405662939);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					flowThread.returnFromCall(this, null, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\core\\src\\iristk\\flow\\DialogFlow.xml"), 72, 13)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
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


}
