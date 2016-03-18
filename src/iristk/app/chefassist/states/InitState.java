package iristk.app.chefassist.states;

import java.util.List;

import iristk.app.chefassist.ChefFlow;
import iristk.speech.RecResult;
import iristk.system.Event;
import iristk.util.Record;

public class InitState implements ChefState {

    @Override
    public void handleEvent(ChefFlow flow, Event event) {
        String input = event.getString("text");
        String command = "";
        int recipeID = -1;
        if(event.get("sem") != null){
	        Record variables = (Record)event.get("sem");
	        if(variables.has("output")){
	        	command = (String)variables.get("output");
	        }
	        if(variables.has("recipe")){
	        	recipeID = (Integer)variables.get("recipe");
	        }
	        for(String s : variables.getFields()){
	        	System.out.println(s);
	        }
	    }
	        
        if (input != null && input.length() > 0) {
            Event output = new Event("action.speech");
            if (input.equals(RecResult.NOMATCH))
                output.put("text", "That's not a recipe I know of.");
            else{
                // Add the parameter text, which will repeat the speech recognition result
            	System.out.println(command);
                if(command.equals("recipe")){
                	flow.getController().setCurrentRecipeIndex(recipeID);
                	output.put("text", "Loading recipe");
                	flow.setState(ChefFlow.STATE_INGREDIENT);
                }
                else{
                    output.put("text", "You said \"" + input + "\" and I have no idea what that means.");
                }
            }
            flow.send(output);
        } else {
            // If not, listen again
            flow.listen();
        }
    }
}
