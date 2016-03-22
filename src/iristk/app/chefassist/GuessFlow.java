package iristk.app.chefassist;

import java.io.File;
import iristk.xml.XmlMarshaller.XMLLocation;
import iristk.system.Event;
import iristk.flow.*;
import static iristk.util.Converters.*;

public class GuessFlow extends iristk.flow.Flow {

	private Integer number;
	private Integer guesses;
	
	public static String[] steps = {
			"step 1", "step 2", "step 3","step 4","step 5", "step 6",
			"step 7","step 8","step 9"
			,"step 10"
	};
	public static String[] ingredients = {
			"step 1 ingredient","step 2 ingredient","step 3 ingredient",
			"step 4 ingredient","step 5 ingredient","step 6 ingredient",
			"step 7 ingredient","step 8 ingredient","step 9 ingredient",
			"step 10 ingredient"
	};
	
	private Integer index;

	private void initVariables() {
	}

	public Integer getNumber() {
		return this.number;
	}

	public void setNumber(Integer value) {
		this.number = value;
	}
	
	public Integer getIndex(){
		return this.index;
	}

	public void setIndex(Integer value){
		this.index = value;
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
		if (name.equals("index")) return this.index;
		return null;
	}


	public GuessFlow() {
		initVariables();
	}

	@Override
	protected State getInitialState() {return new Start();}

	private class IngredientMode extends Dialog{
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
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					//indexOf != -1 means the given input string exists in the event string. 
					if (event.getString("text").indexOf("step") != -1
							|| event.getString("text").indexOf("direction") != -1){
						iristk.flow.DialogFlow.say state3 = new iristk.flow.DialogFlow.say();
						state3.setText("going to the step state.");
						if (!flowThread.callState(state3, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						Step state4 = new Step();
						flowThread.gotoState(state4, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 26, 31)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					else if (event.getString("text").indexOf("repeat all") != -1){
						for (int i = 0; i < ingredients.length; i++) {
							iristk.flow.DialogFlow.say state3 = new iristk.flow.DialogFlow.say();
							state3.setText(ingredients[i]);
							if (!flowThread.callState(state3, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
							flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 29, 15)));
						}
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					else if (event.getString("text").indexOf("how much") != -1
							|| event.getString("text").indexOf("how many") != -1
							|| event.getString("text").indexOf("what's the amount of") != -1)
					{
						String inputText = event.getString("text");
						String testVar = inputText.substring(inputText.lastIndexOf(" "), inputText.length());
						iristk.flow.DialogFlow.say state3 = new iristk.flow.DialogFlow.say();
						state3.setText("you want to know the quantity of: " + testVar);
						if (!flowThread.callState(state3, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 29, 15)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					else if (event.getString("text").indexOf("next") != -1){
						iristk.flow.DialogFlow.say state3 = new iristk.flow.DialogFlow.say();
						state3.setText("next ingredient.");
						if (!flowThread.callState(state3, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 29, 15)));						
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					else if (event.getString("text").indexOf("previous") != -1
							||event.getString("text").indexOf("back") != -1){
						iristk.flow.DialogFlow.say state3 = new iristk.flow.DialogFlow.say();
						state3.setText("previous ingredient.");
						if (!flowThread.callState(state3, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 29, 15)));						
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					else if (event.getString("text").equals("last")){
						iristk.flow.DialogFlow.say state3 = new iristk.flow.DialogFlow.say();
						state3.setText("last ingredient.");
						if (!flowThread.callState(state3, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 29, 15)));						
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					else if (event.getString("text").equals("end"))
					{
						iristk.flow.DialogFlow.say state3 = new iristk.flow.DialogFlow.say();
						state3.setText("going to the end.");
						if (!flowThread.callState(state3, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						CheckAgain state4 = new CheckAgain();
						flowThread.gotoState(state4, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 26, 31)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					else{
						iristk.flow.DialogFlow.say state3 = new iristk.flow.DialogFlow.say();
						state3.setText("No comprende amigo.");
						if (!flowThread.callState(state3, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 29, 15)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
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
	
	private class Step extends Dialog{
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
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
					if (event.getString("text").equals("end")){
						iristk.flow.DialogFlow.say state3 = new iristk.flow.DialogFlow.say();
						state3.setText(concat("Steps finished"));
						if (!flowThread.callState(state3, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						CheckAgain state4 = new CheckAgain();
						flowThread.gotoState(state4, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 26, 31)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					else if((event.getString("text").equals("repeat step"))){
						iristk.flow.DialogFlow.say state5 = new iristk.flow.DialogFlow.say();
						state5.setText(steps[index]);
						if (!flowThread.callState(state5, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 29, 15)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					else if((event.getString("text").equals("repeat all steps"))){
						for(int i = 0; i < steps.length; i++)
						{
							iristk.flow.DialogFlow.say state3 = new iristk.flow.DialogFlow.say();
							state3.setText(steps[i]);
							if (!flowThread.callState(state3, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
								eventResult = EVENT_ABORTED;
								break EXECUTION;
							}
							flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 29, 15)));
						}
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					else if((event.getString("text").equals("next"))){
						iristk.flow.DialogFlow.say state5 = new iristk.flow.DialogFlow.say();
						if(index < 9){
							index++;
							state5.setText(steps[index]);
						}
						if (!flowThread.callState(state5, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 29, 15)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					else if (event.getString("text").indexOf("previous") != -1
							||event.getString("text").indexOf("back") != -1){
						iristk.flow.DialogFlow.say state5 = new iristk.flow.DialogFlow.say();
						if(index == 0)
						{
							state5.setText(steps[0]);
						}
						else
						{
							index--;
							state5.setText(steps[index]);
						}
						state5.setText(steps[index]);
						if (!flowThread.callState(state5, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 29, 15)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					else if((event.getString("text").equals("ingredient")))
					{
						iristk.flow.DialogFlow.say state5 = new iristk.flow.DialogFlow.say();
						state5.setText("now going to ingredient state.");
						if (!flowThread.callState(state5, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						IngredientMode state4 = new IngredientMode();
						flowThread.gotoState(state4, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 26, 31)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					else
					{
						iristk.flow.DialogFlow.say state5 = new iristk.flow.DialogFlow.say();
						state5.setText("invalid input");
						if (!flowThread.callState(state5, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 29, 15)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
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
	
	//initial state. Your "InitState"
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
				index = 0;
				iristk.flow.DialogFlow.say state0 = new iristk.flow.DialogFlow.say();
				state0.setText("Welcome to Chef's assistant application. We have one recepie so "
						+ "it will start loading now.");
				if (!flowThread.callState(state0, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 10, 12)))) {
					eventResult = EVENT_ABORTED;
					break EXECUTION;
				}
				IngredientMode state1 = new IngredientMode();
				flowThread.gotoState(state1, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 14, 25)));
				eventResult = EVENT_ABORTED;
				break EXECUTION;
			}
		}

		@Override
		public int onFlowEvent(Event event) {
	
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
				state7.setText("Do you want to start cooking again?");
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
						state9.setText("Okay, let's cook then.");
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
