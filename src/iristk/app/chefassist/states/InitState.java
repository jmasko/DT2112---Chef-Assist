package iristk.app.chefassist.states;

import iristk.app.chefassist.ChefFlow;
import iristk.speech.RecResult;
import iristk.system.Event;
import iristk.util.Record;

public class InitState implements ChefState {

	@Override
	public void handleEvent(ChefFlow flow, Event event) {
        String input = event.getString("text");
        Record variables = (Record)event.get("sem");

	}

}
