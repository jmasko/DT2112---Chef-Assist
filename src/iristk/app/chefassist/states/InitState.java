package iristk.app.chefassist.states;

import iristk.app.chefassist.ChefFlow;
import iristk.flow.State;
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


                if(input.equals("steps"))
                {
                    DirectionsState state = new DirectionsState();
                    state.handleEvent(flow, event);
                }
                else if(input.equals("ingredients"))
                {
                    IngredientState ingredientState = new IngredientState();
                    ingredientState.handleEvent(flow, event);
                }
                else{
                    output.put("text", "You said " + input);

                }
            }
            flow.send(output);
        } else {
            // If not, listen again
            flow.listen();
        }
    }

}
