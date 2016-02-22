/*******************************************************************************
 * Copyright (c) 2014 Gabriel Skantze.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Gabriel Skantze - initial API and implementation
 ******************************************************************************/
package iristk.speech;

import java.util.*;

import javax.sound.sampled.AudioFormat;

public class RecognizerListeners implements RecognizerListener {

	public static final int PRIORITY_RECOGNIZER = -100;
	public static final int PRIORITY_PROSODY = -50;
	public static final int PRIORITY_SECONDARY_RECOGNIZER = 0;
	public static final int PRIORITY_SEMANTICS = 90;
	public static final int PRIORITY_FINAL = 100;
	
	ArrayList<RecognizerListener> listeners = new ArrayList<>();
	HashMap<RecognizerListener,Integer> priorities = new HashMap<>();
	
	/**
	 * @param priority The listeners with the lowest priority will be called first
	 */
	public void add(RecognizerListener listener, int priority) {
		priorities.put(listener, priority);
		for (int i = 0; i < listeners.size(); i++) {
			int lp = priorities.get(listeners.get(i));
			if (priority < lp) {
				listeners.add(i, listener);
				return;
			}
		}
		listeners.add(listener);
	}
	
	public boolean hasListener(RecognizerListener listener) {
		return listeners.contains(listener);
	}
	
	@Override
	public void startOfSpeech(float timestamp) {
		for (RecognizerListener listener : listeners) {
			listener.startOfSpeech(timestamp);
		}
	}

	@Override
	public void endOfSpeech(float timestamp) {
		for (RecognizerListener listener : listeners) {
			listener.endOfSpeech(timestamp);
		}
	}

	@Override
	public void speechSamples(byte[] samples, int pos, int len) {
		for (RecognizerListener listener : listeners) {
			listener.speechSamples(samples, pos, len);
		}
	}

	@Override
	public void recognitionResult(RecResult result) {
		for (RecognizerListener listener : listeners) {
			listener.recognitionResult(result);
		}
	}

	@Override
	public void initRecognition(AudioFormat format) {
		for (RecognizerListener listener : listeners) {
			listener.initRecognition(format);
		}
	}

	
}
