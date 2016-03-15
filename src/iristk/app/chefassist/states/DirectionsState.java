package iristk.app.chefassist.states;

import iristk.app.chefassist.ChefFlow;
import iristk.speech.RecResult;
import iristk.system.Event;
import iristk.util.Record;

public class DirectionsState implements ChefState {

    @Override
    public void handleEvent(ChefFlow flow, Event event) {
        // TODO Auto-generated method stub
        String input = event.getString("text");
        Record variables = (Record)event.get("sem");
        if (input != null && input.length() > 0) {
            Event output = new Event("action.speech");

            if (input.equals(RecResult.NOMATCH))
                output.put("text", "That's not a recipe I know of.");
            else{
                if(input.equals("next")){

                }
                else if(input.equals("back")){

                }
                else if(input.equals("repeat")){

                }
                else if(input.equals("repeat all steps")){

                }
                else{

                }
            }
            flow.send(output);
        }

    }

}
