package iristk.system;

import java.awt.event.KeyEvent;
import java.util.Locale;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class KeyListenerModule extends IrisModule implements NativeKeyListener {

	private boolean sendKeyPressed = false;
	private boolean sendKeyReleased = false;
	private boolean sendKeyTyped = true;

	public KeyListenerModule() {
		try {
			Locale.setDefault(new Locale("en", "us"));
            GlobalScreen.registerNativeHook();
            GlobalScreen.getInstance().addNativeKeyListener(this);
	    } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
	    }
	}
	
	public void setSendKeyPressed(boolean value) {
		this.sendKeyPressed = value;
	}
	
	public void setSendKeyReleased(boolean value) {
		this.sendKeyReleased = value;
	}
	
	public void setSendKeyTyped(boolean value) {
		this.sendKeyTyped = value;
	}
	
	@Override
	public void onEvent(Event event) {
	}

	@Override
	public void init() throws InitializationException {
	}

	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		if (sendKeyPressed) {
			Event event = new Event("sense.key.pressed");
			event.put("text", KeyEvent.getKeyText(e.getKeyCode()));
			event.put("code", e.getKeyCode());
			send(event);
		}
	}

	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		if (sendKeyReleased) {
			Event event = new Event("sense.key.released");
			event.put("text", KeyEvent.getKeyText(e.getKeyCode()));
			event.put("code", e.getKeyCode());
			send(event);
		}
	}

	@Override
	public void nativeKeyTyped(NativeKeyEvent e) {
		if (sendKeyTyped) {
			Event event = new Event("sense.key.typed");
			event.put("text", "" + e.getKeyChar());
			send(event);
			//System.out.println(e.getKeyCode());
		}
	}
	
}
