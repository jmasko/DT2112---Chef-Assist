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

	public static Ingredient[] ingredientList = new Ingredient[]{
			new Ingredient("cream cheese", "softened", 250, "g"),
			new Ingredient("milk", 250, "ml"),
			new Ingredient("butter", "softened", 60, "g"),
			new Ingredient("egg yolks", 6),
			new Ingredient("cake flour", 55, "g"),
			new Ingredient("corn flour", 20, "g"),
			new Ingredient("lemon zest", 1),
			new Ingredient("egg whites", 6),
			new Ingredient("cream of tartar", 0.25, "tsp"),
			new Ingredient("sugar", 130, "g"),
	};

	public static String[] directions = new String[]{
			"Preheat oven to 150 degrees celcius",
			"Use a large bowl, pour in milk. Place the bowl over simmering water. Don’t let the bottom of the bowl touch the water. ",
			"Add cream cheese, stir occasionally until completely dissolved and the mixture turns smooth. Stir in butter, till dissolved. ",
			"Remove the mixture from heat and let it cool down a bit, then add the egg yolks and mix well.",
			"Mix the cake flour and corn flour. Sift in the flours into the cream cheese mixture, a small amount at a time. Mix well between every addition, and make sure there aren’t any flour lumps. Stir in freshly grated zest. Set aside.",
			"Place egg whites in a large clean bowl. Use an electric mixer to beat the egg whites for 3 minutes, then add cream of tartar and blend again. Pour sugar in the egg whites and blend until very the mixture becomes half solid. ",
			"Add the egg whites into the cream cheese mixture gently with a rubber spatula just until all ingredients are mixed well. Do not stir or beat. ",
			"Pour the mixture into the two baking pans. Place the pans into another larger baking tray. Add hot water in the tray up to halfway and bake for about 50 to 60 minutes. Test with a needle or skewer that comes out clean.",
			"Turn off the oven. Leave the oven door slightly open for 10 minutes. Remove from the oven and remove from the pans. Let cool completely on a wire rack. Chill in a fridge for about 3 hours then the cake will be ready to be served."
	};

	private Integer index, ingredientIndex = 0;

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
					else if (event.getString("text").equals("repeat")){
						iristk.flow.DialogFlow.say state3 = new iristk.flow.DialogFlow.say();
						Ingredient currentIngredient = ingredientList[ingredientIndex];
						state3.setText(currentIngredient.getAmount() + " " + UnitTranslator.translate(currentIngredient.getUnit()) + " of " + currentIngredient.getName());
						if (!flowThread.callState(state3, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 29, 15)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					else if (event.getString("text").equals("repeat all")){
						for (int i = 0; i < ingredientList.length; i++) {
							Ingredient currentIngredient = ingredientList[i];

							iristk.flow.DialogFlow.say state3 = new iristk.flow.DialogFlow.say();
							state3.setText(currentIngredient.getAmount() + " " + UnitTranslator.translate(currentIngredient.getUnit()) + " of " + currentIngredient.getName() );
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
						iristk.flow.DialogFlow.say state3 = new iristk.flow.DialogFlow.say();
						String inputText = event.getString("text");
						String testVar = inputText.substring(inputText.lastIndexOf(" ")+1, inputText.length());

						boolean isFound = false;
						for(int i = 0; i < ingredientList.length; i++){
							Ingredient currentIngredient = ingredientList[i];
							System.out.println(currentIngredient.getName() + " ----------- " + testVar);
							if(currentIngredient.getName().indexOf(testVar) != -1){
								state3.setText(currentIngredient.getAmount() + " " + UnitTranslator.translate(currentIngredient.getUnit()) + " of " + currentIngredient.getName());
								isFound = true;
							}
						}

						if(!isFound){
							state3.setText("ingredient you wanted to know is not found in the system. Please say it again.");
						}

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
						System.out.println("next: " + ingredientIndex);
						if(ingredientIndex < ingredientList.length)
						{
							ingredientIndex++;
							Ingredient currentIngredient = ingredientList[ingredientIndex];
							state3.setText(currentIngredient.getAmount() + " " + UnitTranslator.translate(currentIngredient.getUnit()) + " of " + currentIngredient.getName());

						}
						else
						{
							Ingredient currentIngredient = ingredientList[ingredientList.length-1];
							state3.setText("this is the last one. " + currentIngredient.getAmount() + " " + UnitTranslator.translate(currentIngredient.getUnit()) + " of " + currentIngredient.getName());
						}
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
						System.out.println("back: " + ingredientIndex);
						if(ingredientIndex == 0)
						{
							Ingredient currentIngredient = ingredientList[0];
							state3.setText("this is the first ingredient. " + currentIngredient.getAmount() + " " + UnitTranslator.translate(currentIngredient.getUnit()) + " of " + currentIngredient.getName());
						}
						else
						{
							ingredientIndex--;
							Ingredient currentIngredient = ingredientList[ingredientIndex];
							state3.setText(currentIngredient.getAmount() + " " + UnitTranslator.translate(currentIngredient.getUnit()) + " of " + currentIngredient.getName());
						}

						if (!flowThread.callState(state3, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 29, 15)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					else if (event.getString("text").indexOf("last") != -1){
						iristk.flow.DialogFlow.say state3 = new iristk.flow.DialogFlow.say();
						Ingredient currentIngredient = ingredientList[ingredientList.length-1];
						state3.setText(currentIngredient.getAmount() + " " + UnitTranslator.translate(currentIngredient.getUnit()) + " of " + currentIngredient.getName());
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
						state3.setText("I did not understand your input.");
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
					else if((event.getString("text").equals("repeat"))){
						iristk.flow.DialogFlow.say state5 = new iristk.flow.DialogFlow.say();
						state5.setText(directions[index]);
						if (!flowThread.callState(state5, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 29, 15)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					else if(event.getString("text").equals("repeat all step")){
						for(int i = 0; i < directions.length; i++)
						{
							iristk.flow.DialogFlow.say state3 = new iristk.flow.DialogFlow.say();
							state3.setText(directions[i]);
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
						if(index < directions.length){
							index++;
							state5.setText(directions[index]);
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
							state5.setText(directions[0]);
						}
						else
						{
							index--;
							state5.setText(directions[index]);
						}

						if (!flowThread.callState(state5, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
						flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 29, 15)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;
					}
					else if((event.getString("text").indexOf("ingredient") != -1))
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
				state0.setText("Welcome to Chef's assistant application. We have one recipe so "
						+ "it will start loading now. You will be in the ingredient state and you can start "
						+ "to navigate by saying ,for example, next  and back.  ");
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
