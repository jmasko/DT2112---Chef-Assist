package iristk.app.chefassist;

import iristk.cfg.SRGSGrammar;
import iristk.speech.SpeechGrammarContext;
import iristk.speech.Voice.Gender;
import iristk.speech.windows.WindowsRecognizerFactory;
import iristk.speech.windows.WindowsSynthesizer;
import iristk.system.SimpleDialogSystem;
import iristk.util.Language;

public class ChefSystem {
	
	private SimpleDialogSystem system;
	private ChefController controller;

	public ChefSystem() throws Exception {
		//Setup IrisTK
		system = new SimpleDialogSystem(this.getClass());
		system.setLanguage(Language.ENGLISH_US);
		system.setupGUI();
		system.setupRecognizer(new WindowsRecognizerFactory());
		system.setupSynthesizer(new WindowsSynthesizer(), Gender.FEMALE);
	
		//Setup chef app
		controller = new ChefController(system);
		
		//Start IrisTK + chef app
		//system.addModule(new ChefFlow(controller));

		//this is the module that loads the newly implemented flow.
		system.addModule(new FlowModule(new GuessFlow()));

		system.loadContext("default", new SpeechGrammarContext(new SRGSGrammar(getClass().getResource("ChefGrammar.xml").toURI())));
		system.sendStartSignal();
	}

	public static void main(String[] args) {
		try {
			new ChefSystem();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
