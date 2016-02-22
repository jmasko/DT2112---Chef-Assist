package iristk.speech;

import iristk.audio.AudioPort;
import iristk.util.Language;

public abstract class RecognizerFactory {

	public abstract Recognizer newRecognizer(AudioPort audioPort) throws RecognizerException;

	public void checkSupported() throws RecognizerException {
	}
	
	public abstract Language[] getSupportedLanguages();
	
	public abstract Class<? extends Recognizer> getRecognizerClass();
	
	public abstract String getName();
	
	public abstract boolean requiresInternet();

	public boolean supportsLanguage(Language language) {
		for (Language lang : getSupportedLanguages()) {
			if (lang.equals(language)) {
				return true;
			}
		}
		return false;
	}

	public boolean supportsSpeechGrammar() {
		return GrammarRecognizer.class.isAssignableFrom(getRecognizerClass());
	}
	
	public boolean supportsOpenVocabulary() {
		return OpenVocabularyRecognizer.class.isAssignableFrom(getRecognizerClass());
	}

}
