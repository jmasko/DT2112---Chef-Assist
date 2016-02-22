package iristk.speech;

import iristk.util.Language;

public class OpenVocabularyContext extends Context {
	
	private OpenVocabularyContext() {
	}
	
	public OpenVocabularyContext(Language lang) {
		this.language = lang;
	}
	
	@Override
	public void load(RecognizerModule recognizerModule) throws RecognizerException {
		Recognizer recognizer = recognizerModule.getRecognizer();
		if (recognizer instanceof OpenVocabularyRecognizer) {
			OpenVocabularyRecognizer oRec = (OpenVocabularyRecognizer)recognizer;
			oRec.loadOpenVocabulary(getUniqueName(), language);
		} else {
			throw new RecognizerException("Open Vocabulary not supported");
		}
	}

	@Override
	public void unload(RecognizerModule recognizerModule) throws RecognizerException {
		Recognizer recognizer = recognizerModule.getRecognizer();
		if (recognizer instanceof OpenVocabularyRecognizer) {
			OpenVocabularyRecognizer oRec = (OpenVocabularyRecognizer)recognizer;
			oRec.unloadOpenVocabulary(getUniqueName());
		}
	}

	@Override
	public void activate(RecognizerModule recognizerModule) throws RecognizerException {
		Recognizer recognizer = recognizerModule.getRecognizer();
		if (recognizer instanceof OpenVocabularyRecognizer) {
			recognizer.activateContext(getUniqueName(), 1f);
		}
	}

	@Override
	public void deactivate(RecognizerModule recognizerModule) throws RecognizerException {
		Recognizer recognizer = recognizerModule.getRecognizer();
		if (recognizer instanceof OpenVocabularyRecognizer) {
			recognizer.deactivateContext(getUniqueName());
		}
	}

}
