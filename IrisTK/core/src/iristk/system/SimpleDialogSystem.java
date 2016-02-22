package iristk.system;

import iristk.audio.Microphone;
import iristk.speech.Console;
import iristk.speech.Recognizer;
import iristk.speech.RecognizerFactory;
import iristk.speech.RecognizerModule;
import iristk.speech.Synthesizer;
import iristk.speech.SynthesizerModule;
import iristk.speech.Voice.Gender;

public class SimpleDialogSystem extends AbstractDialogSystem {

	private IrisGUI gui;
	private RecognizerModule recognizerModule;
	private SynthesizerModule synthesizerModule;
	private Console console;
	
	public SimpleDialogSystem(Class<?> packageClass) throws Exception {
		super(packageClass);
	}
	
	public void setupGUI() throws InitializationException {
		onlyOnce("GUI");
		gui = new IrisGUI(this);
		new EventMonitorPanel(gui, this);
		// Add a console
		console = new Console(gui);
		addModule(console);
	}
	
	public void setupRecognizer(RecognizerFactory recognizerFactory) throws Exception {
		onlyOnce("Recognizer");
		Microphone mic = new Microphone();
		Recognizer recognizer = recognizerFactory.newRecognizer(mic);
		recognizerModule = new RecognizerModule(recognizer);
		addModule(recognizerModule);
		if (loggingModule != null && recognizer.getAudioPort() != null) {
			loggingModule.addAudioPort("user", mic);
		}
	}
	
	public void setupSynthesizer(Synthesizer synthesizer, Gender gender) throws Exception {
		setupSynthesizer(synthesizer.setVoice(getLanguage(), gender));
	}
	
	public void setupSynthesizer(Synthesizer synthesizer) throws Exception {
		onlyOnce("Synthesizer");
		if (synthesizer.getVoice() == null)
			synthesizer.setVoice(getLanguage());
		synthesizerModule = new SynthesizerModule(synthesizer);
		addModule(synthesizerModule);	
		if (loggingModule != null)
			loggingModule.addAudioPort("system", synthesizerModule.getAudioTarget());
	}

	public IrisGUI getGUI() {
		return gui;
	}
	
	public Console getConsole() {
		return console;
	}

	public RecognizerModule getRecognizerModule() {
		return recognizerModule;
	}
	
	public SynthesizerModule getSynthesizerModule() {
		return synthesizerModule;
	}

}
