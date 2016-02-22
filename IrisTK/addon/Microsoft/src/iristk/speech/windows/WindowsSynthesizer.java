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
package iristk.speech.windows;

import iristk.net.speech.*;
import iristk.audio.Sound;
import iristk.speech.Synthesizer;
import iristk.speech.Transcription;
import iristk.speech.Voice;
import iristk.speech.Voice.Gender;
import iristk.speech.VoiceList;
import iristk.speech.Phone;
import iristk.speech.VoiceNotFoundException;
import iristk.system.InitializationException;
import iristk.util.Language;
import iristk.util.Mapper;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

// TODO: should encode phones to IPA, now SAPI ID is used
// http://msdn.microsoft.com/en-us/library/hh361632(v=office.14).aspx

public class WindowsSynthesizer extends Synthesizer {
	
	static {
		WindowsSpeech.init();
	}

	private final DesktopSynthesizer synth;
	private Voice voice;
	private AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
	private static Mapper ipa2ups = new Mapper("ipa2ups", WindowsSynthesizer.class.getResourceAsStream("ipa2ups.map"));
	private static Mapper sapi2ups = new Mapper("sapi2ups", WindowsSynthesizer.class.getResourceAsStream("sapi2ups.map"));
	private static Mapper vis2ups = new Mapper("vis2ups", WindowsSynthesizer.class.getResourceAsStream("vis2ups.map"));
	
	private Language language = null;
	private Gender gender = null;
	private String voiceName = null;

	public WindowsSynthesizer() {
		synth = new DesktopSynthesizer();
		//this(Language.ENGLISH_US);
	}

	/*
	public WindowsSynthesizer(String voiceName) {
		synth = new DesktopSynthesizer();
		this.voiceName = voiceName;
		findVoice();
	}
	
	public WindowsSynthesizer(Language lang) {
		synth = new DesktopSynthesizer();
		this.language = lang;
		findVoice();
	}

	public WindowsSynthesizer(Gender gender, Language lang) {
		synth = new DesktopSynthesizer();
		this.language = lang;
		this.gender = gender;
		findVoice();
	}
	*/
	
	private void findVoice() throws VoiceNotFoundException {
		VoiceList voices = getVoices();
		if (language != null)
			voices = voices.getByLanguage(language);
		if (gender != null)
			voices = voices.getByGender(gender);
		if (voiceName != null)
			voices = voices.getByName(voiceName);
		for (Voice voice : voices) {
			try {
				setVoice(voice);
				return;
			} catch (Exception e) {
				System.err.println("WARNING: Could not select voice " + voice.getName());
			}
		}
		System.err.println("ERROR: Could not find preferred voice");
	}

	@Override
	public void setVoice(Voice voice) throws InitializationException {
		if (voice != null) {
			try {
				synth.setVoice(voice.getName());
				this.voice = voice;
				this.language = voice.getLanguage();
			} catch (Exception e) {
				throw new InitializationException("Could not set voice " + voice);
			}
		}
	}

	@Override
	public void init() throws InitializationException {
		if (voice == null) {
			setVoice(Language.ENGLISH_US);
		}
	}

	@Override
	public VoiceList getVoices() {
		VoiceList voices = new VoiceList();
		Voices voiceL = synth.getVoices();
		for (int i = 0; i < voiceL.getLength(); i++) {
			Voice.Gender gender = voiceL.getVoice(i).getGender().equals("Female") ? Voice.Gender.FEMALE : Voice.Gender.MALE;
			voices.add(new Voice(WindowsSynthesizer.class, voiceL.getVoice(i).getName(), gender, new Language(voiceL.getVoice(i).getLang())));
		}
		return voices;
	}
	
	private String labelToUnicode(String label) {
		try {
			byte[] b = label.getBytes("UTF-16");
			if (label.length() == 3) {
				return String.format("%02X%02X+%02X%02X", b[2], b[3], b[6], b[7]);
			} else {
				return String.format("%02X%02X", b[2], b[3]);
			} 
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
		return "";
	}
	
	private boolean isIPA(Phonemes phonemes) {
		int ipa = 0;
		int sapi = 0;
		for (int i = 0; i < phonemes.getLength(); i++) {
			Phoneme phon = phonemes.getPhoneme(i);
			String unicode = labelToUnicode(phon.getLabel());
			if (ipa2ups.containsKey(unicode))
				ipa++;
			if (sapi2ups.containsKey(unicode))
				sapi++;
		}
		return ipa > sapi;
	}
	
	private Transcription makeTrans(Phonemes phonemes) {
		if (phonemes.isVisemes()) {
			Mapper mapper = vis2ups;
			Transcription trans = new Transcription();
			float pos = 0.0f;
			for (int i = 0; i < phonemes.getLength(); i++) {
				Phoneme phon = phonemes.getPhoneme(i);
				String key = phon.getLabel();
				trans.add(mapper.map(key, Transcription.SILENCE), pos, pos + phon.getDuration());
				pos += phon.getDuration();
			}
			return trans;
		} else {
			Mapper mapper = (isIPA(phonemes) ? ipa2ups : sapi2ups);
			Transcription trans = new Transcription();
			float pos = 0.0f;
			boolean compound = false;
			String lastUnicode = "";
			for (int i = 0; i < phonemes.getLength(); i++) {
				Phoneme phon = phonemes.getPhoneme(i);
				String unicode = labelToUnicode(phon.getLabel());
				if (unicode.equals("0361")) {
					compound = true;
				} else if (compound) {
					Phone last = trans.getPhones().remove(trans.getPhones().size() - 1);
					String key = lastUnicode + "+" + unicode;
					trans.add(mapper.map(key, Transcription.SILENCE), last.start, pos + phon.getDuration());
					compound = false;
				} else {
					trans.add(mapper.map(unicode, Transcription.SILENCE), pos, pos + phon.getDuration());
					lastUnicode = unicode;
					compound = false;
				}
				pos += phon.getDuration();
			}
			return trans;
		}
	}
	
	private String makeSSML(String text) {
		//TODO: more sophisticated processing to remove non-SSML tags
		text = text.replaceAll("<.?spurt.*?>", "");
		text = text.replaceAll("&(?![a-z]+;)", "&amp;");
		String ssml = "<speak version=\"1.0\"";
		ssml += " xmlns=\"http://www.w3.org/2001/10/synthesis\"";
		ssml += " xml:lang=\"" + voice.getLanguage()+ "\">";
		ssml += text.trim();
		ssml += "</speak>";
		return ssml;
	}

	@Override
	public Transcription synthesize(String text, File audioFile) {
		String ssml = makeSSML(text);
		Phonemes phonemes = synth.synthesize(ssml, audioFile.getAbsolutePath());
		Transcription trans = makeTrans(phonemes);
		float trim = trans.trimSilence();
		if (trim > 0) {
			try {
				Sound sound = new Sound(audioFile);
				sound.cropSeconds(0, sound.getSecondsLength() - trim);
				sound.save(audioFile);
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return trans;
	}

	@Override
	public Transcription transcribe(String text) {
		String ssml = makeSSML(text);
		Phonemes phonemes = synth.transcribe(ssml);
		Transcription trans = makeTrans(phonemes);
		return trans;
	}

	@Override
	public AudioFormat getAudioFormat() {
		return audioFormat;
	}

	@Override
	public Voice getVoice() {
		return voice;
	}

	public void printVoices() {
		synth.printVoices();
	}

}
