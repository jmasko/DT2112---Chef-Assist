package iristk.app.guess;

import iristk.speech.RecResult;
import iristk.system.Event;
import iristk.system.InitializationException;
import iristk.system.IrisModule;

public class MyDialogManager extends IrisModule{
    public static String[] steps = {
            "step 1", "step 2", "step 3","step 4","step 5", "step 6",
            "step 7","step 8","step 9"
            ,"step 10"
    };
    public static String[] ingredients = {
            "step 1 ingredient","step 2 ingredient","step 3 ingredient",
            "step 4 ingredient","step 5 ingredient","step 6 ingredient",
            "step 7 ingredient","step 8 ingredient","step 9 ingredient",
            "step 10 ingredient"
    };

    public static int pointer = 0;
    @Override
    public void onEvent(Event event) {
        // TODO Auto-generated method stub
        // We have received an event from some other module,
        // check whether we should react to it
        if (event.triggers("monitor.system.start")) {
            // The system started, listen for speech
            Event entryMsg = new Event("action.speech");
            String msg = steps[0];
            entryMsg.put("text", msg);
            send(entryMsg);
            pointer = 0;
            listen();
        } else if (event.triggers("sense.speech.rec**")) {
            // We received a speech recognition result, get the text
            String text = event.getString("text");
            // Check whether there is any result
            if (text != null && text.length() > 0) {
                // Create an event for the speech synthesizer
                Event newEvent = new Event("action.speech");
                Event outputStep = new Event("action.speech");
                if (text.equals(RecResult.NOMATCH))
                    // We got a NOMATCH, inform the user
                    newEvent.put("text", "I didn't understand that");
                else{
                    // Add the parameter text, which will repeat the speech recognition result
                    newEvent.put("text", "You said " + text);
                    String repeatSteps = "";
                    if(text.equals("next"))
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
                            repeatSteps += steps[i]+ ".";
                        }
                        outputStep.put("text", repeatSteps);
                    }
                    else if(text.equals("repeat ingredient"))
                    {
                        outputStep.put("text", ingredients[pointer]);
                    }
                    else{
                        outputStep.put("text", "no can do ass hat");
                    }
                }
                send(outputStep);
                //send(newEvent);
            } else {
                // If not, listen again
                listen();
            }
        } else if (event.triggers("monitor.speech.end")) {
            // The synthesizer completed, listen for speech again
            listen();
        }
    }

    // Make the recognizer start listening
    private void listen() {
        send(new Event("action.listen"));
    }

    @Override
    public void init() throws InitializationException {
        // TODO Auto-generated method stub

    }

}
