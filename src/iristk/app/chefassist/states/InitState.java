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
        
        if (input != null && input.length() > 0) {
            Event output = new Event("action.speech");
            
            if (input.equals(RecResult.NOMATCH))
            	output.put("text", "That's not a recipe I know of.");
            else{
                // Add the parameter text, which will repeat the speech recognition result
                output.put("text", "You said " + input);
                flow.send(output);
                if(variables.getString("output").equals("next"))
                {
                    if(pointer+1 < steps.length)
                    {
                        outputStep.put("text", steps[pointer+1]);
                        pointer++;
                    }
                    else
                    {
                        outputStep.put("text", steps[steps.length-1]);
                    }
                }
                else if(text.equals("back"))
                {
                    if(pointer == 0){
                        outputStep.put("text", steps[0]);
                    }
                    else{
                        outputStep.put("text", steps[pointer-1]);
                        pointer--;
                    }
                }
                else if(text.equals("repeat"))
                {
                    outputStep.put("text", steps[pointer]);
                }
                else if(text.equals("repeat all steps"))
                {
                    for (int i = 0; i < steps.length; i++) {
                        repeatSteps += steps[i]+ " ";
                    }
                    outputStep.put("text", repeatSteps);
                }
                else if(text.equals("repeat ingredient"))
                {
                    outputStep.put("text", ingredients[pointer]);
                }
                else if(text.equals("repeat all ingredient"))
                {
                    String ingredientRepeat = "";
                    for (int i = 0; i < ingredients.length; i++) {
                        ingredientRepeat += ingredients[i] + " ";
                    }
                    outputStep.put("text", ingredientRepeat);
                }
                else if(text.equals("read directions"))
                {
                    outputStep.put("text", steps[0]);
                }
                else if(text.indexOf("how much") != -1)
                {
                    if(text.indexOf("lemon") != -1)
                        outputStep.put("text", "too much");
                    else if(text.indexOf("salt") != -1)
                        outputStep.put("text", "100 kilograms");
                    else if(text.indexOf("computer") != -1)
                        outputStep.put("text", "two computers");
                    else
                        outputStep.put("text", "ingredient does not exist");
                }
                else{
                    outputStep.put("text", "I did not understand that");
                }
            }
            flow.send(outputStep);
            //send(newEvent);
        } else {
            // If not, listen again
            flow.listen();
        }
	}

}
