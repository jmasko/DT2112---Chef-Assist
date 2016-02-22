package iristk.speech;

import iristk.util.Language;

public interface OpenVocabularyRecognizer {

	void loadOpenVocabulary(String contextName, Language language) throws RecognizerException;
	
	void unloadOpenVocabulary(String contextName) throws RecognizerException;
}
