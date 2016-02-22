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

import javax.sound.sampled.AudioFormat;

public interface RecognizerListener {

	void initRecognition(AudioFormat format);
	
	/**
	 * A start of speech event
	 * @param timestamp the number of seconds since listening started
	 */
	void startOfSpeech(float timestamp);

	/**
	 * An end of speech event
	 * @param timestamp the number of seconds since listening started
	 */
	void endOfSpeech(float timestamp);

	void speechSamples(byte[] samples, int pos, int len);

	void recognitionResult(RecResult result);

}
