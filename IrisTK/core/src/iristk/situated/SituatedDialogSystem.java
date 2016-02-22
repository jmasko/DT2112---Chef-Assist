package iristk.situated;

import java.io.File;

import iristk.agent.face.FaceModule;
import iristk.audio.AudioChannel;
import iristk.audio.AudioPort;
import iristk.audio.Microphone;
import iristk.flow.FlowModule;
import iristk.kinect.CameraViewPanel;
import iristk.kinect.KinectAudioSource;
import iristk.kinect.KinectCameraView;
import iristk.kinect.KinectModule;
import iristk.kinect.KinectRecognizerModule;
import iristk.speech.Console;
import iristk.speech.EnergyVAD;
import iristk.speech.EnergyVADContainer;
import iristk.speech.Recognizer;
import iristk.speech.RecognizerFactory;
import iristk.speech.RecognizerModule;
import iristk.speech.Synthesizer;
import iristk.speech.SynthesizerModule;
import iristk.speech.EnergyVADPanel;
import iristk.speech.Voice.Gender;
import iristk.system.AbstractDialogSystem;
import iristk.system.EventMonitorPanel;
import iristk.system.InitializationException;
import iristk.system.IrisGUI;

public class SituatedDialogSystem extends AbstractDialogSystem {

	private Situation situation;
	private IrisGUI gui;
	private KinectModule kinectModule;
	private SynthesizerModule synth;
	private SituationPanel topPanel;
	private SituationPanel sidePanel;
	private SystemAgent systemAgent;

	public SituatedDialogSystem(Class<?> packageClass) throws Exception {
		super(packageClass);
	}
	
	public SituatedDialogSystem(String name, File packageDir) throws Exception {
		super(name, packageDir);
	}

	/**
	 * Adds a default system agent with the name "system"
	 * @return The created SystemAgentModule
	 */
	public SystemAgentFlow addSystemAgent() throws InitializationException {
		return addSystemAgent("system");
	}
	
	/**
	 * Adds a system agent with a specified name
	 * @return The created SystemAgentModule
	 */
	public SystemAgentFlow addSystemAgent(String name) throws InitializationException {
		onlyOnce("Agent '" + name + "'");
		SystemAgentModule systemAgentModule = new SystemAgentModule(name);
		systemAgent = systemAgentModule.getSystemAgent();
		situation = systemAgent.getSituation();
		systemAgent.setInteractionDistance(2);
		addModule(systemAgentModule);
		SystemAgentFlow situatedDialogFlow = new SystemAgentFlow(systemAgent);
		addModule(new FlowModule(situatedDialogFlow));
		return situatedDialogFlow;
	}
	
	public void setupGUI() throws InitializationException {
		onlyOnce("GUI");
		
		gui = new IrisGUI(this);
		new EventMonitorPanel(gui, this);

		// Add a panel to the GUI with a top view of the situation
		topPanel = new SituationPanel(gui, this, SituationPanel.TOPVIEW);
		sidePanel = new SituationPanel(gui, this, SituationPanel.SIDEVIEW);
		
		// Add a console
		Console console = new Console(gui);
		addModule(console);
	}

	public void setupKinect() throws InitializationException {
		onlyOnce("Kinect");
		
		// Add a Kinect module to the system
		kinectModule = new KinectModule();
		addModule(kinectModule);

		// Set the kinect position. Default is below/front of the monitor/face
		kinectModule.setPosition(new Location(0, -0.35, 0.15), new Rotation(347, 0, 0));
		
		if (gui != null) {
			// Add the Kinect camera view to the situation 
			new CameraViewPanel(gui, kinectModule.getKinect()).setDecorator(new KinectCameraView(kinectModule));
		}
	}
	
	/*
	public void setupRealSense() throws InitializationException {
		onlyOnce("RealSense");
		
		// Add a Kinect module to the system
		realSenseModule = new RealSenseModule();
		addModule(realSenseModule);

		// Set the kinect position. Default is below/front of the monitor/face
		realSenseModule.setPosition(new Location(0, -0.35, 0.15), new Rotation(347, 0, 0));
		
		if (gui != null) {
			new RealSenseCameraPanel(gui, realSenseModule);
		}
	}
	*/

	public void setupFace(String faceModelName, Synthesizer synthesizer, String agentName) throws Exception {
		onlyOnce("Face '" + agentName + "'");
		FaceModule face = new FaceModule(faceModelName);
		face.setAgentName(agentName);
		addModule(face);
		if (synthesizer.getVoice() == null)
			synthesizer.setVoice(getLanguage());
		synth = new SynthesizerModule(synthesizer);
		synth.setAgentName(agentName);
		// Turn on lipsync since we are using an animated agent. 
		synth.doLipsync(true);		
		addModule(synth);
		if (loggingModule != null)
			loggingModule.addAudioPort("system", synth.getAudioTarget());
	}
	
	public void setupFace(String faceModelName, Synthesizer synthesizer) throws Exception {
		setupFace(faceModelName, synthesizer, "system");
	}
	
	public void setupFace(Synthesizer synthesizer, Gender gender) throws Exception {
		setupFace(gender == Gender.FEMALE ? "female" : "male", synthesizer.setVoice(getLanguage(), gender), "system");
	}
	
	public void setupStereoMicrophones(RecognizerFactory recognizerFactory) throws Exception {
		setupStereoMicrophones(recognizerFactory, "Wireless");
	}
	
	public void setupStereoMicrophones(RecognizerFactory recognizerFactory, String stereoDeviceName) throws Exception {
		// Initialize recognizers for both microphones, let them read from different channels
		Microphone stereo = new Microphone(stereoDeviceName, 16000, 2);
		AudioPort leftChannel = new AudioChannel(stereo, 0);
		AudioPort rightChannel = new AudioChannel(stereo, 1);
		setupStereoMicrophones(recognizerFactory, leftChannel, rightChannel);
	}
	
	public void setupStereoMicrophones(RecognizerFactory recognizerFactory, String leftDeviceName, String rightDeviceName) throws Exception {
		AudioPort leftChannel = new Microphone(leftDeviceName, 16000, 1);
		AudioPort rightChannel = new Microphone(rightDeviceName, 16000, 1);
		setupStereoMicrophones(recognizerFactory, leftChannel, rightChannel);
	}
	
	public void setupStereoMicrophones(RecognizerFactory recognizerFactory, AudioPort leftChannel, AudioPort rightChannel) throws Exception {
		onlyOnce("Recognizer");
		RecognizerModule recognizerLeft = new RecognizerModule(
				recognizerFactory.newRecognizer(leftChannel));
		RecognizerModule recognizerRight = new RecognizerModule(
				recognizerFactory.newRecognizer(rightChannel));
		recognizerLeft.setSensor(new LeftRightSensor("mic-left", LeftRightSensor.LEFT), true);
		recognizerRight.setSensor(new LeftRightSensor("mic-right", LeftRightSensor.RIGHT), true);
		addModule(recognizerLeft);
		addModule(recognizerRight);
		if (loggingModule != null) {
			loggingModule.addAudioPort("mic-left", leftChannel);
			loggingModule.addAudioPort("mic-right", rightChannel);
		}
		addVADPanel(recognizerLeft.getRecognizer(), recognizerRight.getRecognizer());
	}
	
	public void setupMonoMicrophone(RecognizerFactory recognizerFactory) throws Exception {
		onlyOnce("Recognizer");
		Microphone mic = new Microphone();
		RecognizerModule recognizer = new RecognizerModule(recognizerFactory.newRecognizer(mic));
		//Associate the recognizer with the microphone
		recognizer.setSensor(new Sensor("mic"), true);
		// Add the microphone to the situation model so that the microphone can be associated with a user
		addModule(recognizer);
		if (loggingModule != null) {
			loggingModule.addAudioPort("user", mic);
		}
		addVADPanel(recognizer.getRecognizer());
	}
	
	public void setupKinectMicrophone(RecognizerFactory recognizerFactory) throws Exception {
		onlyOnce("Recognizer");
		// Use the Kinect sensor for speech recognition 
		RecognizerModule recognizer = new KinectRecognizerModule(kinectModule, recognizerFactory.newRecognizer(new KinectAudioSource(kinectModule.getKinect())));
		addModule(recognizer);
		addVADPanel(recognizer.getRecognizer());
	}
	
	private void addVADPanel(Recognizer... recognizers) {
		if (gui != null) {
			if (recognizers[0] instanceof EnergyVADContainer) {
				EnergyVAD vad = ((EnergyVADContainer)recognizers[0]).getEnergyVAD();
				if (vad != null) {
					EnergyVADPanel vadPanel = new EnergyVADPanel(true);
					vadPanel.setLeftVAD(vad);
					if (recognizers.length > 1) {
						EnergyVAD vad2 = ((EnergyVADContainer)recognizers[1]).getEnergyVAD();
						vadPanel.setRightVAD(vad2);
					}
					vadPanel.addToGUI(gui);
				}
			}
		}
	}

	public KinectModule getKinectModule() {
		return kinectModule;
	}

	public Situation getSituation() {
		return situation;
	}

	public void loadPositions(File positionsFile) {
		if (topPanel != null) {
			topPanel.setPositionsFile(positionsFile);
			sidePanel.setPositionsFile(positionsFile);
		}
		if (positionsFile.exists()) {
			SituationModule.loadPositions(this, positionsFile);
		}
	}

	public IrisGUI getGUI() {
		return gui;
	}


}
