package iristk.app.chefassist.states;

import iristk.flow.State;
import iristk.speech.RecResult;
import iristk.app.chefassist.ChefFlow;
import iristk.system.Event;
import iristk.util.Record;

public class IngredientState extends State implements ChefState {

	private boolean start = true;
	private int currentIngredientIndex = -1;
	
	@Override
	public void handleEvent(ChefFlow flow, Event event) {
		// TODO Auto-generated method stub
		String input = event.getString("text");
        String command = "";
        if(event.get("sem") != null){
	        Record variables = (Record)event.get("sem");
	        if(variables.has("output")){
	        	command = (String)variables.get("output");
	        }
	        for(String s : variables.getFields()){
	        	System.out.println(s);
	        }
	    }
		
		if (input != null && input.length() > 0) {
			Event output = new Event("action.speech");
			if (input.equals(RecResult.NOMATCH))
				output.put("text", "Sorry, can you please repeat what you said?");
			else
			{
				if(command.equals("yes")){
					if(start){
						
					}
					
				}else if(command.equals("no")){
					if(start){
						output.put("text", "Loading recipe");
	                	flow.setState(ChefFlow.STATE_INGREDIENT);
					}
				}
				else{

				}
			}
			flow.send(output);
		}
		else{
			return;
		}

	}

}
