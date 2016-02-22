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

import iristk.speech.Voice.Gender;
import iristk.system.InitializationException;
import iristk.util.Language;

import java.io.File;

import javax.sound.sampled.AudioFormat;

public abstract class Synthesizer {
	
	/**
	 * Initializes the synthesizer. This method will be called once after the synthesizer is constructed and specific parameters are set.
	 * @throws InitializationException
	 */
	public abstract void init() throws InitializationException;
	
	/**
	 * Synthesizes text and writes the audio to a wav-file
	 * @param text The text to synthesize
	 * @param file The wav-file to write
	 * @return The transcription of the synthesized text
	 */
	public abstract Transcription synthesize(String text, File file);
	
	/**
	 * Transcribes a text
	 * @param text The text to synthesize
	 * @return The transcription of the text if it would be synthesized
	 */
	public abstract Transcription transcribe(String text);

	/**
	 * @return the audio format of the wav-files that are produced by this synthesizer
	 */
	public abstract AudioFormat getAudioFormat();

	/**
	 * Sets the currently selected voice
	 * @throws InitializationException 
	 */
	public abstract void setVoice(Voice voice) throws InitializationException;
	
	/**
	 * @return the currently selected voice
	 */
	public abstract Voice getVoice();
	
	/**
	 * @return a list of voices supported by this synthesizer
	 */
	public abstract VoiceList getVoices();
	
	public Synthesizer setVoice(Language lang, Gender gender) throws InitializationException {
		for (Voice voice : getVoices().getByLanguage(lang).getByGender(gender)) {
			try {
				setVoice(voice);
				return this;
			} catch (InitializationException e) {
			}
		}
		throw new InitializationException("Cannot set voice with language " + lang + " and gender " + gender);
	}
	
	public Synthesizer setVoice(Language lang) throws InitializationException {
		for (Voice voice : getVoices().getByLanguage(lang)) {
			try {
				setVoice(voice);
				return this;
			} catch (InitializationException e) {
			}
		}
		throw new InitializationException("Cannot set voice with language " + lang);
	}
	
	public Synthesizer setVoice(String name) throws InitializationException {
		for (Voice voice : getVoices().getByName(name)) {
			try {
				setVoice(voice);
				return this;
			} catch (InitializationException e) {
			}
		}
		throw new InitializationException("Cannot set voice with name " + name);
	}
		
}
