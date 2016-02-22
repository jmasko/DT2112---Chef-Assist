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
package iristk.app.quiz;

import iristk.situated.SituatedDialogSystem;
import iristk.situated.SystemAgentFlow;
import iristk.speech.SpeechGrammarContext;
import iristk.speech.Voice.Gender;
import iristk.speech.windows.WindowsRecognizerFactory;
import iristk.speech.windows.WindowsSynthesizer;
import iristk.util.Language;
import iristk.app.quiz.Question;
import iristk.app.quiz.QuestionSet;
import iristk.app.quiz.QuizFlow;
import iristk.cfg.SRGSGrammar;
import iristk.flow.FlowModule;

public class QuizSystem {

	public QuizSystem() throws Exception {
		SituatedDialogSystem system = new SituatedDialogSystem(this.getClass());
		SystemAgentFlow systemAgentFlow = system.addSystemAgent();
				
		system.setLanguage(Language.ENGLISH_US);
		
		//system.setupLogging(new File("c:/iristk_logging"), true);
		
		system.setupGUI();
		
		//system.setupKinect();
		
		system.setupStereoMicrophones(new WindowsRecognizerFactory());
		//system.setupKinectMicrophone(new KinectRecognizerFactory());
		
		//system.connectToBroker("furhat", "127.0.0.1");
		system.setupFace(new WindowsSynthesizer(), Gender.FEMALE);
		
		QuestionSet questions = new QuestionSet(getClass().getResourceAsStream("questions.txt"));
		system.addModule(new FlowModule(new QuizFlow(questions, systemAgentFlow)));
		
		system.loadContext("default", new SpeechGrammarContext(new SRGSGrammar(getClass().getResource("QuizGrammar.xml").toURI())));
		for (Question q : questions) {
			system.loadContext(q.getId(), new SpeechGrammarContext(q.getGrammar()));
		}
		system.setDefaultContext("default");
		
		system.loadPositions(system.getFile("situation.properties"));		
		system.sendStartSignal();
	}
	
	public static void main(String[] args) throws Exception {
		new QuizSystem();
	}

}
