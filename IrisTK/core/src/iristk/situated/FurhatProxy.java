package iristk.situated;

import iristk.system.AbstractDialogSystem;
import iristk.system.Event;

public class FurhatProxy extends AbstractDialogSystem {

	public FurhatProxy(String address) throws Exception {
		super(FurhatProxy.class);
		connectToBroker("furhat", address);
	}
	
	public void say(String text) {
		Event event = new Event("action.speech");
		event.put("text", text);
		send(event, "system");
	}

	public void gesture(String name) {
		Event event = new Event("action.gesture");
		event.put("name", name);
		send(event, "system");
	}
	
	public void gaze(float x, float y, float z) {
		Event event = new Event("action.gaze");
		event.put("location", new Location(x, y, z));
		send(event, "system");
	}
	
	public void gaze(float x, float y, float z, int slack) {
		Event event = new Event("action.gaze");
		event.put("location", new Location(x, y, z));
		event.put("slack", slack);
		send(event, "system");
	}
	
	public void gaze(float x, float y, float z, String mode) {
		Event event = new Event("action.gaze");
		event.put("location", new Location(x, y, z));
		event.put("mode", mode);
		send(event, "system");
	}	
	
}
