package iristk.app.chefassist.states;

import iristk.app.chefassist.ChefFlow;

public interface ChefState {
	public void handleEvent(ChefFlow flow, iristk.system.Event event);
}
