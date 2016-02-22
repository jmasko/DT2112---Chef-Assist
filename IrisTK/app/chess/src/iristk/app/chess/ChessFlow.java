package iristk.app.chess;

import java.io.File;
import iristk.xml.XmlMarshaller.XMLLocation;
import iristk.system.Event;
import iristk.flow.*;
import iristk.util.Record;
import static iristk.util.Converters.*;

public class ChessFlow extends iristk.flow.Flow {

	private ChessGame chess;
	private Record move;

	private void initVariables() {
	}

	public Record getMove() {
		return this.move;
	}

	public void setMove(Record value) {
		this.move = value;
	}

	public ChessGame getChess() {
		return this.chess;
	}

	@Override
	public Object getVariable(String name) {
		if (name.equals("move")) return this.move;
		if (name.equals("chess")) return this.chess;
		return null;
	}


	public ChessFlow(ChessGame chess) {
		this.chess = chess;
		initVariables();
	}

	@Override
	protected State getInitialState() {return new Start();}

	public static String[] getPublicStates() {
		return new String[] {};
	}

	private class Game extends State {

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
			int count;
			count = getCount(1490180672) + 1;
			if (event.triggers("chess.restart")) {
				incrCount(1490180672);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					iristk.flow.DialogFlow.say state0 = new iristk.flow.DialogFlow.say();
					state0.setText("Okay, let's restart");
					if (!flowThread.callState(state0, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 11, 33)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					AwaitUser state1 = new AwaitUser();
					flowThread.gotoState(state1, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 13, 29)));
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


	private class Start extends Game {

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
				int count = getCount(250075633) + 1;
				incrCount(250075633);
				iristk.flow.DialogFlow.say state2 = new iristk.flow.DialogFlow.say();
				state2.setText("Okay, let's start");
				if (!flowThread.callState(state2, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 18, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				AwaitUser state3 = new AwaitUser();
				flowThread.gotoState(state3, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 20, 29)));
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


	private class AwaitUser extends Game {

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
				int count = getCount(914424520) + 1;
				incrCount(914424520);
				boolean chosen4 = false;
				boolean matching5 = true;
				while (!chosen4 && matching5) {
					int rand6 = random(110718392, 3, iristk.util.RandomList.RandomModel.DECK_RESHUFFLE_NOREPEAT);
					matching5 = false;
					if (true) {
						matching5 = true;
						if (rand6 >= 0 && rand6 < 1) {
							chosen4 = true;
							iristk.app.chess.ChessFlow.ask state7 = new ask();
							state7.setText("So, what is your move?");
							if (!flowThread.callState(state7, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 26, 12)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
					}
					if (true) {
						matching5 = true;
						if (rand6 >= 1 && rand6 < 2) {
							chosen4 = true;
							iristk.app.chess.ChessFlow.ask state8 = new ask();
							state8.setText("Your move");
							if (!flowThread.callState(state8, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 26, 12)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
					}
					if (true) {
						matching5 = true;
						if (rand6 >= 2 && rand6 < 3) {
							chosen4 = true;
							iristk.app.chess.ChessFlow.ask state9 = new ask();
							state9.setText("Your turn");
							if (!flowThread.callState(state9, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 26, 12)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
						}
					}
				}
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(425918570) + 1;
			if (event.triggers("chess.move.user")) {
				incrCount(425918570);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					chess.identifyMoves(move);
					if (chess.availableMoves() == 1) {
						PerformMove state10 = new PerformMove();
						flowThread.gotoState(state10, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 35, 32)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					} else if (chess.availableMoves() == 0) {
						iristk.flow.DialogFlow.say state11 = new iristk.flow.DialogFlow.say();
						state11.setText("Sorry, I can't do that");
						if (!flowThread.callState(state11, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 34, 43)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						AwaitUser state12 = new AwaitUser();
						flowThread.gotoState(state12, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 38, 31)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					} else {
						chess.displayAvailableMoves();
						Record clarify = chess.chooseClarification();
						if (clarify.has("steps")) {
							ClarifySteps state13 = new ClarifySteps();
							flowThread.gotoState(state13, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 43, 34)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						} else if (clarify.has("direction")) {
							ClarifyDirection state14 = new ClarifyDirection();
							flowThread.gotoState(state14, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 45, 38)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						} else if (clarify.has("piece")) {
							ClarifyPiece state15 = new ClarifyPiece();
							state15.setPiece(clarify.get("piece"));
							flowThread.gotoState(state15, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 47, 58)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						} else if (clarify.has("square")) {
							ClarifySquare state16 = new ClarifySquare();
							flowThread.gotoState(state16, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 49, 35)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
					}
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			count = getCount(932583850) + 1;
			if (event.triggers("sense.user.speak")) {
				if (event.has("sem:act_move")) {
					incrCount(932583850);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						chess.newMove();
						move = asRecord(event.get("sem"));
						Event raiseEvent17 = new Event("chess.move.user");
						if (flowThread.raiseEvent(raiseEvent17, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 56, 36))) == State.EVENT_ABORTED) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(305808283) + 1;
			if (event.triggers("sense.speech.partial")) {
				if (event.has("sem:act_move")) {
					incrCount(305808283);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						chess.displayAvailableMoves(asRecord(event.get("sem")));
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(292938459) + 1;
			if (event.triggers("sense.user.silence")) {
				incrCount(292938459);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 62, 15)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			count = getCount(405662939) + 1;
			if (event.triggers("sense.user.speak")) {
				incrCount(405662939);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					iristk.flow.DialogFlow.say state18 = new iristk.flow.DialogFlow.say();
					state18.setText("Sorry");
					if (!flowThread.callState(state18, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 64, 36)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 66, 15)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			count = getCount(1130478920) + 1;
			if (event.triggers("dialog.repeat")) {
				incrCount(1130478920);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 69, 15)));
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


	private class PerformMove extends Game {

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
				int count = getCount(123961122) + 1;
				incrCount(123961122);
				iristk.flow.DialogFlow.say state19 = new iristk.flow.DialogFlow.say();
				state19.setText("Okay");
				if (!flowThread.callState(state19, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 74, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				chess.performMove();
				AwaitSystem state20 = new AwaitSystem();
				flowThread.gotoState(state20, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 77, 32)));
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


	private class ClarifySteps extends AwaitUser {

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
				int count = getCount(1101288798) + 1;
				incrCount(1101288798);
				iristk.app.chess.ChessFlow.ask state21 = new ask();
				state21.setText("How many steps?");
				if (!flowThread.callState(state21, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 82, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(942731712) + 1;
			if (event.triggers("sense.user.speak")) {
				if (event.has("sem:number")) {
					incrCount(942731712);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						move.putIfNotNull("movement:steps", event.get("sem:number"));
						Event raiseEvent22 = new Event("chess.move.user");
						if (flowThread.raiseEvent(raiseEvent22, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 87, 36))) == State.EVENT_ABORTED) {
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


	private class ClarifyDirection extends AwaitUser {

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
				int count = getCount(758529971) + 1;
				incrCount(758529971);
				iristk.app.chess.ChessFlow.ask state23 = new ask();
				state23.setText("In which direction?");
				if (!flowThread.callState(state23, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 92, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(2104457164) + 1;
			if (event.triggers("sense.user.speak")) {
				if (event.has("sem:direction")) {
					incrCount(2104457164);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						move.putIfNotNull("movement:direction", event.get("sem:direction"));
						Event raiseEvent24 = new Event("chess.move.user");
						if (flowThread.raiseEvent(raiseEvent24, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 97, 36))) == State.EVENT_ABORTED) {
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


	private class ClarifyPiece extends AwaitUser {

		final State currentState = this;

		public String piece = null;

		public void setPiece(Object value) {
			if (value != null) {
				piece = asString(value);
				params.put("piece", value);
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
				int count = getCount(1617791695) + 1;
				incrCount(1617791695);
				iristk.app.chess.ChessFlow.ask state25 = new ask();
				state25.setText(concat("Which", piece, "?"));
				if (!flowThread.callState(state25, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 103, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(125993742) + 1;
			if (event.triggers("sense.user.speak")) {
				if (event.has("sem:piece")) {
					incrCount(125993742);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						move.putIfNotNull("piece", event.get("sem:piece"));
						Event raiseEvent26 = new Event("chess.move.user");
						if (flowThread.raiseEvent(raiseEvent26, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 108, 36))) == State.EVENT_ABORTED) {
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


	private class ClarifySquare extends AwaitUser {

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
				int count = getCount(537548559) + 1;
				incrCount(537548559);
				iristk.app.chess.ChessFlow.ask state27 = new ask();
				state27.setText("To which square?");
				if (!flowThread.callState(state27, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 113, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(237852351) + 1;
			if (event.triggers("sense.user.speak")) {
				if (event.has("sem:piece:square")) {
					incrCount(237852351);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						move.putIfNotNull("movement:square", event.get("sem:piece:square"));
						Event raiseEvent28 = new Event("chess.move.user");
						if (flowThread.raiseEvent(raiseEvent28, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 118, 36))) == State.EVENT_ABORTED) {
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


	private class AwaitSystem extends Game {

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
			int count;
			count = getCount(992136656) + 1;
			if (event.triggers("chess.move.system")) {
				incrCount(992136656);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					AwaitUser state29 = new AwaitUser();
					flowThread.gotoState(state29, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 124, 30)));
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


	private class ask extends State {

		final State currentState = this;

		public String text = null;
		public Float threshold = asFloat(0.7);

		public void setText(Object value) {
			if (value != null) {
				text = asString(value);
				params.put("text", value);
			}
		}

		public void setThreshold(Object value) {
			if (value != null) {
				threshold = asFloat(value);
				params.put("threshold", value);
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
				int count = getCount(1509514333) + 1;
				incrCount(1509514333);
				iristk.flow.DialogFlow.say state30 = new iristk.flow.DialogFlow.say();
				state30.setText(text);
				if (!flowThread.callState(state30, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 131, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				iristk.flow.DialogFlow.listen state31 = new iristk.flow.DialogFlow.listen();
				if (!flowThread.callState(state31, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 131, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(1556956098) + 1;
			if (event.triggers("sense.user.speak")) {
				if (threshold > asFloat(event.get("conf"))) {
					incrCount(1556956098);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						confirm state32 = new confirm();
						state32.setCevent(event);
						flowThread.gotoState(state32, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 136, 44)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(2036368507) + 1;
			if (event.triggers("sense.user.speak")) {
				incrCount(2036368507);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					Event returnEvent33 = new Event(event.getName());
					returnEvent33.copyParams(event);
					flowThread.returnFromCall(this, returnEvent33, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 139, 26)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			count = getCount(1552787810) + 1;
			if (event.triggers("sense.user.silence")) {
				incrCount(1552787810);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					Event returnEvent34 = new Event(event.getName());
					returnEvent34.copyParams(event);
					flowThread.returnFromCall(this, returnEvent34, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 142, 26)));
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


	private class confirm extends State {

		final State currentState = this;

		public Event cevent = null;

		public void setCevent(Object value) {
			if (value != null) {
				cevent = (Event) value;
				params.put("cevent", value);
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
				int count = getCount(914504136) + 1;
				incrCount(914504136);
				iristk.flow.DialogFlow.say state35 = new iristk.flow.DialogFlow.say();
				state35.setText(concat("Did you say", cevent.get("text")));
				if (!flowThread.callState(state35, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 148, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				iristk.flow.DialogFlow.listen state36 = new iristk.flow.DialogFlow.listen();
				if (!flowThread.callState(state36, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 148, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(824009085) + 1;
			if (event.triggers("sense.user.speak")) {
				if (event.has("sem:yes")) {
					incrCount(824009085);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						Event returnEvent37 = new Event(cevent.getName());
						returnEvent37.copyParams(cevent);
						flowThread.returnFromCall(this, returnEvent37, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 153, 27)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(248609774) + 1;
			if (event.triggers("sense.user.speak")) {
				if (event.has("sem:no")) {
					incrCount(248609774);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						Event returnEvent38 = new Event("dialog.repeat");
						flowThread.returnFromCall(this, returnEvent38, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 156, 35)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(1887400018) + 1;
			if (event.triggers("sense.user.speak sense.user.silence")) {
				incrCount(1887400018);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					Event returnEvent39 = new Event(event.getName());
					returnEvent39.copyParams(event);
					flowThread.returnFromCall(this, returnEvent39, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\chess\\src\\iristk\\app\\chess\\ChessFlow.xml"), 159, 26)));
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
