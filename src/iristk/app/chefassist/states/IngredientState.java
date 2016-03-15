package iristk.app.chefassist.states;

import iristk.flow.State;
import iristk.speech.RecResult;
import iristk.app.chefassist.ChefFlow;
import iristk.system.Event;
import iristk.util.Record;

public class IngredientState extends State implements ChefState {

	@Override
	public void handleEvent(ChefFlow flow, Event event) {
		// TODO Auto-generated method stub
		String input = event.getString("text");
		Record variables = (Record)event.get("sem");
		if (input != null && input.length() > 0) {
			Event output = new Event("action.speech");
			if (input.equals(RecResult.NOMATCH))
				output.put("text", "That's not a recipe I know of.");
			else
			{
				if(input.equals("next")){
					output.put("text", "Next");
				}
				else if(input.equals("back")){
					output.put("text", "Back");
				}
				else if(input.equals("repeat")){
					output.put("text", "Repeat");
				}
				else if(input.equals("repeat all steps")){
					output.put("text", "Repeat all steps");
				}
				else if(input.equals("repeat ingredient"))
				{
					output.put("text", "Repeat ingredient");
				}
				else if(input.equals("repeat all ingredient"))
				{
					output.put("text", "repeat all ingredient");
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
