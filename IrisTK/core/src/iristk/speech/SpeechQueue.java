package iristk.speech;

import java.util.concurrent.ArrayBlockingQueue;

public class SpeechQueue {

	private ArrayBlockingQueue<SpeechAction> queue = new ArrayBlockingQueue<>(1000);

	public SpeechAction take() throws InterruptedException {
		return queue.take();
	}

	public void append(String action, String text, String audio, String display) {
		SpeechAction sa = new SpeechAction();
		sa.action = action;
		sa.text = text;
		sa.display = display;
		sa.audio = audio;
		queue.add(sa);
	}

	public void clear() {
		queue.clear();
	}
	
	public int size() {
		return queue.size();
	}

}
