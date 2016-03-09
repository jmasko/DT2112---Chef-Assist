package iristk.app.chefassist;

import java.util.Random;

import iristk.cfg.SRGSGrammar;
import iristk.flow.FlowModule;
import iristk.situated.SituatedDialogSystem;
import iristk.situated.SystemAgentFlow;
import iristk.speech.SpeechGrammarContext;
import iristk.speech.Voice.Gender;
import iristk.speech.windows.WindowsRecognizerFactory;
import iristk.speech.windows.WindowsSynthesizer;
import iristk.util.Language;

public class ChefSystem {
	
	private SituatedDialogSystem system;
	private SystemAgentFlow systemAgentFlow;
	private ChefController controller;

	public ChefSystem() throws Exception {
		//Setup IrisTK
		system = new SituatedDialogSystem(this.getClass());
		systemAgentFlow = system.addSystemAgent();
		system.setLanguage(Language.ENGLISH_US);
		system.setupGUI();
		system.setupMonoMicrophone(new WindowsRecognizerFactory());
		system.setupFace(new WindowsSynthesizer(), new Random().nextDouble() > 0.5 ? Gender.FEMALE : Gender.MALE);
	
		//Setup chef app
		controller = new ChefController(system);
		

		
		//Start IrisTK + chef app
		system.addModule(new FlowModule(new ChefFlow())); //Construct ChefFlow.xml first
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
