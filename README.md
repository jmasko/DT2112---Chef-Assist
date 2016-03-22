# DT2112---Chef-Assist
DT2112 Awesomeness

I wrote the code lines first then the descrptions under them!

GuessFlow.java file

		@Override
		public void onentry() 
		
this code is for when the state is entered. It speaks the shit in this stuff first

		@Override
		public int onFlowEvent(Event event) {
			int eventResult;
			if (event.triggers("sense.user.speak")) {
				eventResult = EVENT_CONSUMED;
				EXECUTION: {
				
these lines are where the magic happens. After these lines you can start "if-else"ing

    event.getString("text").indexOf("step")

this line will take the text. When you say "str1.indexOf(str2) != -1" you check the whole string for the occurence of str2 in str1. 
if the result is -1 that means str2 is not in str1. 


						iristk.flow.DialogFlow.say state3 = new iristk.flow.DialogFlow.say();
						state3.setText("going to the step state.");
						if (!flowThread.callState(state3, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 24, 53)))) {
							eventResult = EVENT_ABORTED;
							break EXECUTION;
						}
these lines will say "going to the step state." nothing special.

						Step state4 = new Step();
						flowThread.gotoState(state4, currentState, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 26, 31)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;

Now here after it speaks some shit, it will go to the class "Step" which is defined in the same java file. last two lines are necessary.

					flowThread.reentryState(this, new FlowEventInfo(currentState, event, new XMLLocation(new File("C:\\Dropbox\\iristk\\app\\guess\\src\\iristk\\app\\guess\\GuessFlow.xml"), 29, 15)));
						eventResult = EVENT_ABORTED;
						break EXECUTION;

If you dont want navigation, just use these lines instead. They will keep the app in the same place. I dont needed to change the file location.

Now comes the overall class struct 

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
	          //shit load of if-else as described above
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
	
above is the simplified desciption of a class. The basic structure is visible from there. Dialog is already defined in the same java file so
no need to define a Dialog class again.
							
