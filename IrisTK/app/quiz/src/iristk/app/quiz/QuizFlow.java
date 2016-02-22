package iristk.app.quiz;

import java.io.File;
import iristk.xml.XmlMarshaller.XMLLocation;
import iristk.system.Event;
import iristk.flow.*;
import static iristk.util.Converters.*;

public class QuizFlow extends iristk.flow.Flow {

	private QuestionSet questions;
	private iristk.situated.SystemAgentFlow agent;
	private iristk.situated.SystemAgent system;
	private Question question;
	private int guess;
	private int winningScore;

	private void initVariables() {
		system = agent.getSystemAgent();
		guess = asInteger(0);
		winningScore = asInteger(3);
	}

	public iristk.situated.SystemAgent getSystem() {
		return this.system;
	}

	public void setSystem(iristk.situated.SystemAgent value) {
		this.system = value;
	}

	public Question getQuestion() {
		return this.question;
	}

	public void setQuestion(Question value) {
		this.question = value;
	}

	public int getGuess() {
		return this.guess;
	}

	public void setGuess(int value) {
		this.guess = value;
	}

	public int getWinningScore() {
		return this.winningScore;
	}

	public void setWinningScore(int value) {
		this.winningScore = value;
	}

	public QuestionSet getQuestions() {
		return this.questions;
	}

	public iristk.situated.SystemAgentFlow getAgent() {
		return this.agent;
	}

	@Override
	public Object getVariable(String name) {
		if (name.equals("system")) return this.system;
		if (name.equals("question")) return this.question;
		if (name.equals("guess")) return this.guess;
		if (name.equals("winningScore")) return this.winningScore;
		if (name.equals("questions")) return this.questions;
		if (name.equals("agent")) return this.agent;
		return null;
	}


	public QuizFlow(QuestionSet questions, iristk.situated.SystemAgentFlow agent) {
		this.questions = questions;
		this.agent = agent;
		initVariables();
	}

	@Override
	protected State getInitialState() {return new Idle();}

	public static String[] getPublicStates() {
		return new String[] {};
	}

	private class Idle extends State {

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
				int count = getCount(1143839598) + 1;
				incrCount(1143839598);
				iristk.situated.SystemAgentFlow.attendNobody state0 = agent.new attendNobody();
				if (!flowThread.callState(state0, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 18, 12)))) {
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
			if (event.triggers("sense.user.enter")) {
				incrCount(358699161);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					iristk.situated.SystemAgentFlow.attend state1 = agent.new attend();
					state1.setTarget(event.get("user"));
					if (!flowThread.callState(state1, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 21, 36)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					iristk.situated.SystemAgentFlow.say state2 = agent.new say();
					state2.setText("Hi there");
					if (!flowThread.callState(state2, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 21, 36)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					RequestGame state3 = new RequestGame();
					flowThread.gotoState(state3, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 24, 31)));
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
			int count;
			count = getCount(110718392) + 1;
			if (event.triggers("sense.user.leave")) {
				if (system.isAttending(event)) {
					incrCount(110718392);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						if (system.hasUsers()) {
							iristk.situated.SystemAgentFlow.attendRandom state4 = agent.new attendRandom();
							if (!flowThread.callState(state4, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 30, 33)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
							flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 32, 15)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						} else {
							Goodbye state5 = new Goodbye();
							flowThread.gotoState(state5, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 34, 28)));
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(231685785) + 1;
			if (event.triggers("sense.user.speech.start")) {
				if (system.isAttending(event) && eq(event.get("speakers"), 1)) {
					incrCount(231685785);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						Event sendEvent6 = new Event("action.gesture");
						sendEvent6.putIfNotNull("name", "smile");
						flowRunner.sendEvent(sendEvent6, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 38, 51)));
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


	private class RequestGame extends Dialog {

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
				int count = getCount(32374789) + 1;
				incrCount(32374789);
				if (system.hasManyUsers()) {
					iristk.situated.SystemAgentFlow.attendAll state7 = agent.new attendAll();
					if (!flowThread.callState(state7, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 44, 37)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
				iristk.situated.SystemAgentFlow.say state8 = agent.new say();
				state8.setText("Do you want to play a game?");
				if (!flowThread.callState(state8, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 43, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				iristk.situated.SystemAgentFlow.listen state9 = agent.new listen();
				if (!flowThread.callState(state9, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 43, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(1023487453) + 1;
			if (event.triggers("sense.user.speak**")) {
				if (event.has("sem:yes")) {
					incrCount(1023487453);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						StartGame state10 = new StartGame();
						flowThread.gotoState(state10, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 51, 29)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(515132998) + 1;
			if (event.triggers("sense.user.speak**")) {
				if (event.has("sem:no")) {
					incrCount(515132998);
					eventResult = EVENT_CONSUMED;
					EXECUTION: {
						iristk.situated.SystemAgentFlow.say state11 = agent.new say();
						state11.setText("Okay, maybe another time then");
						if (!flowThread.callState(state11, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 53, 59)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						Goodbye state12 = new Goodbye();
						flowThread.gotoState(state12, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 55, 27)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					if (eventResult != EVENT_IGNORED) return eventResult;
				}
			}
			count = getCount(1365202186) + 1;
			if (event.triggers("sense.user.silence sense.user.speak**")) {
				incrCount(1365202186);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 58, 14)));
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


	private class StartGame extends Dialog {

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
				int count = getCount(932583850) + 1;
				incrCount(932583850);
				if (system.hasManyUsers()) {
					iristk.situated.SystemAgentFlow.say state13 = agent.new say();
					state13.setText(concat("Okay, let's play a game of quiz. The first to reach", winningScore, "points is the winner."));
					if (!flowThread.callState(state13, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 64, 37)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				} else {
					iristk.situated.SystemAgentFlow.say state14 = agent.new say();
					state14.setText(concat("Okay, let's play a game of quiz. Let's try to see if you can get", winningScore, "points."));
					if (!flowThread.callState(state14, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 64, 37)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
				NextQuestion state15 = new NextQuestion();
				flowThread.gotoState(state15, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 69, 32)));
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


	private class Goodbye extends State {

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
				int count = getCount(2111991224) + 1;
				incrCount(2111991224);
				iristk.situated.SystemAgentFlow.say state16 = agent.new say();
				state16.setText("Goodbye");
				if (!flowThread.callState(state16, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 74, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				Idle state17 = new Idle();
				flowThread.gotoState(state17, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 76, 24)));
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


	private class NextQuestion extends Dialog {

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
				int count = getCount(1993134103) + 1;
				incrCount(1993134103);
				question = questions.next(); guess = 0;
				if (system.hasManyUsers()) {
					boolean chosen18 = false;
					boolean matching19 = true;
					while (!chosen18 && matching19) {
						int rand20 = random(1130478920, 2, iristk.util.RandomList.RandomModel.DECK_RESHUFFLE_NOREPEAT);
						matching19 = false;
						if (true) {
							matching19 = true;
							if (rand20 >= 0 && rand20 < 1) {
								chosen18 = true;
								{
									iristk.situated.SystemAgentFlow.attendOther state21 = agent.new attendOther();
									if (!flowThread.callState(state21, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 85, 13)))) {
										eventResult = EVENT_ABORTED;
										break EXECUTION;
									}
									iristk.situated.SystemAgentFlow.say state22 = agent.new say();
									state22.setText("The next one is for you");
									if (!flowThread.callState(state22, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 85, 13)))) {
										eventResult = EVENT_ABORTED;
										break EXECUTION;
									}
								}
							}
						}
						if (true) {
							matching19 = true;
							if (rand20 >= 1 && rand20 < 2) {
								chosen18 = true;
								{
									iristk.situated.SystemAgentFlow.attendAll state23 = agent.new attendAll();
									if (!flowThread.callState(state23, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 89, 13)))) {
										eventResult = EVENT_ABORTED;
										break EXECUTION;
									}
									iristk.situated.SystemAgentFlow.say state24 = agent.new say();
									state24.setText("Let's see who answers first");
									if (!flowThread.callState(state24, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 89, 13)))) {
										eventResult = EVENT_ABORTED;
										break EXECUTION;
									}
								}
							}
						}
					}
				} else {
					iristk.situated.SystemAgentFlow.say state25 = agent.new say();
					state25.setText("Here comes the next question");
					if (!flowThread.callState(state25, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 83, 37)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
				ReadQuestion state26 = new ReadQuestion();
				flowThread.gotoState(state26, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 97, 32)));
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


	private class ReadQuestion extends AwaitAnswer {

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
				int count = getCount(971848845) + 1;
				incrCount(971848845);
				iristk.situated.SystemAgentFlow.say state27 = agent.new say();
				state27.setText(question.getFullQuestion());
				if (!flowThread.callState(state27, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 102, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				iristk.situated.SystemAgentFlow.listen state28 = agent.new listen();
				state28.setContext("default " + question.getId());
				if (!flowThread.callState(state28, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 102, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
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


	private class ReadOptions extends AwaitAnswer {

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
				int count = getCount(305623748) + 1;
				incrCount(305623748);
				iristk.situated.SystemAgentFlow.say state29 = agent.new say();
				state29.setText(question.getOptions());
				if (!flowThread.callState(state29, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 109, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				iristk.situated.SystemAgentFlow.listen state30 = agent.new listen();
				state30.setContext("default " + question.getId());
				if (!flowThread.callState(state30, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 109, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
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


	private class AwaitAnswer extends Dialog {

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
				int count = getCount(2104457164) + 1;
				incrCount(2104457164);
				iristk.situated.SystemAgentFlow.listen state31 = agent.new listen();
				state31.setContext("default " + question.getId());
				if (!flowThread.callState(state31, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 116, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
			}
		}

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			int count;
			count = getCount(1521118594) + 1;
			if (event.triggers("sense.user.speak.multi")) {
				incrCount(1521118594);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					if (question.isCorrect(event.get("all:0:sem:answer"))) {
						iristk.situated.SystemAgentFlow.attend state32 = agent.new attend();
						state32.setTarget(event.get("all:0:user"));
						if (!flowThread.callState(state32, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 120, 58)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						CorrectAnswer state33 = new CorrectAnswer();
						flowThread.gotoState(state33, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 122, 34)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					} else if (question.isCorrect(event.get("all:1:sem:answer"))) {
						iristk.situated.SystemAgentFlow.attend state34 = agent.new attend();
						state34.setTarget(event.get("all:1:user"));
						if (!flowThread.callState(state34, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 120, 58)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						CorrectAnswer state35 = new CorrectAnswer();
						flowThread.gotoState(state35, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 125, 34)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					} else {
						iristk.situated.SystemAgentFlow.say state36 = agent.new say();
						state36.setText("None of you were correct, let's try another question.");
						if (!flowThread.callState(state36, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 120, 58)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						NextQuestion state37 = new NextQuestion();
						flowThread.gotoState(state37, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 128, 33)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			count = getCount(1068824137) + 1;
			if (event.triggers("sense.user.speak")) {
				incrCount(1068824137);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					if (system.isAttendingAll()) {
						iristk.situated.SystemAgentFlow.attend state38 = agent.new attend();
						state38.setTarget(event.get("user"));
						if (!flowThread.callState(state38, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 132, 39)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
					}
					if (question.isCorrect(event.get("sem:answer"))) {
						CorrectAnswer state39 = new CorrectAnswer();
						flowThread.gotoState(state39, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 136, 34)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					} else {
						IncorrectAnswer state40 = new IncorrectAnswer();
						flowThread.gotoState(state40, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 138, 36)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			count = getCount(1451270520) + 1;
			if (event.triggers("sense.user.speak.side")) {
				incrCount(1451270520);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					iristk.situated.SystemAgentFlow.attendOther state41 = agent.new attendOther();
					state41.setMode("eyes");
					if (!flowThread.callState(state41, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 141, 41)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					iristk.situated.SystemAgentFlow.say state42 = agent.new say();
					state42.setText("You were not supposed to answer that");
					if (!flowThread.callState(state42, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 141, 41)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					iristk.situated.SystemAgentFlow.attendOther state43 = agent.new attendOther();
					state43.setMode("eyes");
					if (!flowThread.callState(state43, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 141, 41)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					iristk.situated.SystemAgentFlow.say state44 = agent.new say();
					state44.setText("So, what is your answer?");
					if (!flowThread.callState(state44, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 141, 41)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					AwaitAnswer state45 = new AwaitAnswer();
					flowThread.gotoState(state45, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 146, 31)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			count = getCount(992136656) + 1;
			if (event.triggers("sense.user.silence")) {
				incrCount(992136656);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					Event raiseEvent46 = new Event("skip");
					if (flowThread.raiseEvent(raiseEvent46, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 149, 25))) == State.EVENT_ABORTED) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
				if (eventResult != EVENT_IGNORED) return eventResult;
			}
			count = getCount(1297685781) + 1;
			if (event.triggers("skip")) {
				incrCount(1297685781);
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					iristk.situated.SystemAgentFlow.say state47 = agent.new say();
					state47.setText("Give me your best guess");
					if (!flowThread.callState(state47, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 151, 24)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					AwaitAnswer state48 = new AwaitAnswer();
					flowThread.gotoState(state48, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 153, 31)));
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


	private class CorrectAnswer extends Dialog {

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
				int count = getCount(1509514333) + 1;
				incrCount(1509514333);
				system.getCurrentUser().incrInteger("score");
				iristk.situated.SystemAgentFlow.say state49 = agent.new say();
				state49.setText(concat("That is correct, you now have a score of", system.getCurrentUser().get("score")));
				if (!flowThread.callState(state49, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 158, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				if (asInteger(system.getCurrentUser().get("score"), 0) >= winningScore) {
					Winner state50 = new Winner();
					flowThread.gotoState(state50, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 162, 27)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				} else {
					NextQuestion state51 = new NextQuestion();
					flowThread.gotoState(state51, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 164, 33)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
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


	private class IncorrectAnswer extends Dialog {

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
				int count = getCount(125130493) + 1;
				incrCount(125130493);
				iristk.situated.SystemAgentFlow.say state52 = agent.new say();
				state52.setText("That was wrong");
				if (!flowThread.callState(state52, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 170, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				if (system.hasManyUsers() && guess == 0) {
					guess++;
					iristk.situated.SystemAgentFlow.attendOther state53 = agent.new attendOther();
					if (!flowThread.callState(state53, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 172, 52)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					iristk.situated.SystemAgentFlow.say state54 = agent.new say();
					state54.setText("Maybe you know the answer?");
					if (!flowThread.callState(state54, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 172, 52)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					AwaitAnswer state55 = new AwaitAnswer();
					flowThread.gotoState(state55, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 176, 32)));
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				iristk.situated.SystemAgentFlow.say state56 = agent.new say();
				state56.setText(concat("The correct answer was", question.getCorrectAnswer()));
				if (!flowThread.callState(state56, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 170, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				NextQuestion state57 = new NextQuestion();
				flowThread.gotoState(state57, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 179, 32)));
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


	private class Winner extends Dialog {

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
				system.putUsers("score", 0);
				iristk.situated.SystemAgentFlow.say state58 = agent.new say();
				state58.setText("Congratulations, you are the winner");
				if (!flowThread.callState(state58, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 184, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				if (system.hasManyUsers()) {
					iristk.situated.SystemAgentFlow.attendOther state59 = agent.new attendOther();
					if (!flowThread.callState(state59, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 187, 37)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					iristk.situated.SystemAgentFlow.say state60 = agent.new say();
					state60.setText("I am sorry, but you lost.");
					if (!flowThread.callState(state60, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 187, 37)))) {
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
				}
				Goodbye state61 = new Goodbye();
				flowThread.gotoState(state61, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\quiz\\src\\iristk\\app\\quiz\\QuizFlow.xml"), 191, 27)));
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


}
