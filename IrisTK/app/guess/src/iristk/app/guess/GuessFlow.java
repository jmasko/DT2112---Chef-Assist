package iristk.app.guess;

import java.io.File;
import iristk.xml.XmlMarshaller.XMLLocation;
import iristk.system.Event;
import iristk.flow.*;
import static iristk.util.Converters.*;

public class GuessFlow extends iristk.flow.Flow {

	private Integer number;
	private Integer guesses;

	private void initVariables() {
	}

	public Integer getNumber() {
		return this.number;
	}

	public void setNumber(Integer value) {
		this.number = value;
	}

	public Integer getGuesses() {
		return this.guesses;
	}

	public void setGuesses(Integer value) {
		this.guesses = value;
	}

	@Override
	public Object getVariable(String name) {
		if (name.equals("number")) return this.number;
		if (name.equals("guesses")) return this.guesses;
		return null;
	}


	public GuessFlow() {
		initVariables();
	}

	@Override
	protected State getInitialState() {return new Start();}


	private class Start extends State {

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
				number = new java.util.Random().nextInt(10) + 1;
				guesses = 0;
				iristk.flow.DialogFlow.say state0 = new iristk.flow.DialogFlow.say();
				state0.setText("I am thinking of a number between 1 and 10, let's see if you can guess which one it is.");
				if (!flowThread.callState(state0, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 10, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				Guess state1 = new Guess();
				flowThread.gotoState(state1, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 14, 25)));
				eventResult = EVENT_ABORTED;
				break EXECUTION;
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			eventResult = super.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			eventResult = callerHandlers(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
			return EVENT_IGNORED;
		}

	}


	private class Guess extends Dialog {

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
				iristk.flow.DialogFlow.listen state2 = new iristk.flow.DialogFlow.listen();
				if (!flowThread.callState(state2, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 19, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			if (event.triggers("sense.user.speak")) {
				if (event.has("sem:number")) {
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						guesses++;
						if (asInteger(event.get("sem:number")) == number) {
							iristk.flow.DialogFlow.say state3 = new iristk.flow.DialogFlow.say();
							state3.setText(concat("That was correct, you only needed", guesses, "guesses."));
							if (!flowThread.callState(state3, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
							CheckAgain state4 = new CheckAgain();
							flowThread.gotoState(state4, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 26, 31)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						} else if (asInteger(event.get("sem:number")) > number) {
							iristk.flow.DialogFlow.say state5 = new iristk.flow.DialogFlow.say();
							state5.setText("That was too high, let's try again.");
							if (!flowThread.callState(state5, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
							flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 29, 15)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						} else {
							iristk.flow.DialogFlow.say state6 = new iristk.flow.DialogFlow.say();
							state6.setText("That was too low, let's try again.");
							if (!flowThread.callState(state6, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
							flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 32, 15)));
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

	}


	private class CheckAgain extends Dialog {

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
				iristk.flow.DialogFlow.say state7 = new iristk.flow.DialogFlow.say();
				state7.setText("Do you want to play again?");
				if (!flowThread.callState(state7, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 38, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				iristk.flow.DialogFlow.listen state8 = new iristk.flow.DialogFlow.listen();
				if (!flowThread.callState(state8, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 38, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			if (event.triggers("sense.user.speak")) {
				if (event.has("sem:yes")) {
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						iristk.flow.DialogFlow.say state9 = new iristk.flow.DialogFlow.say();
						state9.setText("Okay, let's play again.");
						if (!flowThread.callState(state9, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 42, 58)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						Start state10 = new Start();
						flowThread.gotoState(state10, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 44, 25)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			if (event.triggers("sense.user.speak")) {
				if (event.has("sem:no")) {
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						iristk.flow.DialogFlow.say state11 = new iristk.flow.DialogFlow.say();
						state11.setText("Okay, goodbye");
						if (!flowThread.callState(state11, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 46, 57)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						System.exit(0);
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


	private class Dialog extends State {

		final State currentState = this;


		@Override
		public void setFlowThread(FlowRunner.FlowThread flowThread) {
			super.setFlowThread(flowThread);
		}

		@Override
		public void onentry() {
			int eventResult;
			Event event = new Event("state.enter");
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			if (event.triggers("sense.user.silence")) {
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					iristk.flow.DialogFlow.say state12 = new iristk.flow.DialogFlow.say();
					state12.setText("I am sorry, I didn't hear anything.");
					if (!flowThread.callState(state12, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 53, 38)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 55, 14)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			if (event.triggers("sense.user.speak")) {
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					iristk.flow.DialogFlow.say state13 = new iristk.flow.DialogFlow.say();
					state13.setText("I am sorry, I didn't get that.");
					if (!flowThread.callState(state13, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 57, 36)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 59, 14)));
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
