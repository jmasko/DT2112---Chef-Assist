package iristk.situated;

import iristk.util.Language;
import iristk.util.StringUtils;

public class SkillRequirements {

	private boolean requireOpenVocabulary = false;
	private boolean requireSpeechGrammar = false;
	private boolean requireSpeedOverQuality = false;
	private Language requireLanguage = null;

	public void requireLanguage(Language language) {
		this.requireLanguage  = language;
	}

	public void requireSpeechGrammar(boolean b) {
		this.requireSpeechGrammar = b;
	}
	
	public void requireOpenVocabulary(boolean b) {
		this.requireOpenVocabulary = b;
	}
	
	public void requireSpeedOverQuality(boolean b) {
		this.requireSpeedOverQuality = b;
	}
	
	public Language requiresLanguage() {
		return requireLanguage;
	}
	
	public boolean requiresSpeechGrammar() {
		return requireSpeechGrammar;
	}
	
	public boolean requiresOpenVocabulary() {
		return requireOpenVocabulary;
	}
	
	public boolean requiresSpeedOverQuality() {
		return requireSpeedOverQuality;
	}

	@Override
	public String toString() {
		return StringUtils.enumerate(requireLanguage, (requireSpeechGrammar ? "Speech Grammar" : null), (requireOpenVocabulary ? " Open Vocabulary" : null));
	}
	
}
