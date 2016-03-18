package iristk.app.chefassist;

import iristk.app.chefassist.states.ChefState;
import iristk.app.chefassist.states.DirectionsState;
import iristk.app.chefassist.states.IngredientState;
import iristk.app.chefassist.states.InitState;
import iristk.system.Event;
import iristk.system.InitializationException;
import iristk.system.IrisModule;

public class ChefFlow extends IrisModule{

	public static final int STATE_INIT = 0, STATE_INGREDIENT = 1, STATE_DIRECTIONS = 2;
	
	private ChefController controller;
	private int currentState = STATE_INIT;
	private final ChefState[] states = buildChefStates();

    public ChefFlow(ChefController c){
    	controller = c;
    }
    
    private ChefState[] buildChefStates(){
    	ChefState[] c = new ChefState[3];
    	c[STATE_INIT] = new InitState();
    	c[STATE_INGREDIENT] = new IngredientState();
    	c[STATE_DIRECTIONS] = new DirectionsState();
    	return c;
    }
    
    @Override
    public void onEvent(Event event) {
    	/***********************************************
    	 ***************** APP START *******************
    	 ***********************************************/
    	// ((Record)event.get("sem")).getInteger("number"); <-- Get the out.blahblah values
    	
        if (event.triggers("monitor.system.start")) {
        	Event message;
            message = new Event("action.speech");
            message.put("text", "Welcome to the Chefs Assistant. What recipe would you like assistance with?");
            send(message);
            listen();
        } else if (event.triggers("sense.speech.rec**")) {
        	String input = event.getString("text");
            if (input != null && input.length() > 0) {
            	states[currentState].handleEvent(this, event);
            	listen();
            } else {
            	listen();
            }
        } else if (event.triggers("monitor.speech.end")) {
            // The synthesizer completed, listen for speech again
            listen();
        }
    }

    // Make the recognizer start listening
    public void listen() {
        send(new Event("action.listen"));
    }

    @Override
    public void init() throws InitializationException {
        // TODO Auto-generated method stub

    }

    public void setState(int state){
    	this.currentState = state;
    }
    
    public ChefController getController(){
    	return controller;
    }
    
}
