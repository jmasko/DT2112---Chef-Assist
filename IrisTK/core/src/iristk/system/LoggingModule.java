package iristk.system;

import iristk.audio.AudioPort;
import iristk.audio.AudioRecorder;
import iristk.flow.FlowEventInfo;
import iristk.flow.FlowListener;
import iristk.flow.FlowLogger;
import iristk.flow.FlowModule;
import iristk.flow.State;
import iristk.util.NameFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoggingModule extends IrisModule {

	FileOutputStream eventLog = null;
	private NameFilter filter;
	private File logDir;
	private Map<String,AudioRecorder> audioRecorders = new HashMap<>();
	private boolean logOnSystemStart = false;
	private long logStartTime;
	private List<FlowLogger> flowLoggers = new ArrayList<>();
	private List<FlowEventLogger> flowEventLoggers = new ArrayList<>();
	private Map<Object, Boolean> logEnabled = new HashMap<>();
	private boolean isLogging = false;
	
	public LoggingModule() {
		this(new File("c:/iristk_logging"), NameFilter.ALL, false);
	}
	
	public LoggingModule(File logDir, NameFilter filter, boolean logOnSystemStart) {
		this.logOnSystemStart = logOnSystemStart;
		this.logDir = logDir;
		this.filter = filter;
		if (!logDir.exists()) {
			logDir.mkdirs();
		}
		enableEventLogging(true);
		AudioRecorder.fixUncompletedLogs(logDir);
		Runtime.getRuntime().addShutdownHook(
				new Thread() {
					@Override
					public void run() {
						stopLogging();
					}
				});
	}	
	
	public void logOnSystemStart(boolean value) {
		this.logOnSystemStart = value;
	}
	
	@Override
	public void onEvent(Event event) {
		if (event.triggers("action.logging.start")) {
			startLogging(event.getString("timestamp"));
		} else if (event.triggers("action.logging.stop")) {
			stopLogging();
		} else if (filter.accepts(event.getName())) {
			logSystemEvent(event);
		}
	}
	
	private synchronized void logSystemEvent(Event event) {
		if (eventLog != null) {
			try {
				event.setTime(system.getTimestamp());
				eventLog.write(new String(event.toJSON() + "\n").getBytes());
				eventLog.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void startLogging() {
		startLogging(null);
	}
	
	public synchronized void startLogging(String timestamp) {
		logStartTime = System.currentTimeMillis();
		if (timestamp == null)
			timestamp = "" + logStartTime;
		stopLogging();
		try {
			isLogging = true;
			if (logEnabled.get(Event.class))
			eventLog = new FileOutputStream(new File(logDir, timestamp + ".txt"));
			for (String audioPortName : audioRecorders.keySet()) {
				if (logEnabled.get(audioRecorders.get(audioPortName).getAudioPort()))
					audioRecorders.get(audioPortName).startRecording(new File(logDir, timestamp + "." + audioPortName + ".wav"));
			}
			for (FlowLogger flowLogger : flowLoggers) {
				if (logEnabled.get(flowLogger.getFlowModule()))
					flowLogger.startLogging(new File(logDir, timestamp + "." + flowLogger.getFlowModule().getName() + ".html"));
			}
			for (FlowEventLogger flowLogger : flowEventLoggers) {
				if (logEnabled.get(flowLogger.getFlowModule()))	
					flowLogger.startLogging(new File(logDir, timestamp + "." + flowLogger.getFlowModule().getName() + ".txt"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}

	public synchronized void stopLogging() {
		if (eventLog != null) {
			try {
				eventLog.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			eventLog = null;
			for (String audioPortName : audioRecorders.keySet()) {
				audioRecorders.get(audioPortName).stopRecording();
			}
			for (FlowLogger flowLogger : flowLoggers) {
				flowLogger.stopLogging();
			}
			for (FlowEventLogger flowLogger : flowEventLoggers) {
				flowLogger.stopLogging();
			}
			isLogging = false;
		}
	}
	
	@Override
	public void init() throws InitializationException {
	}

	public void addAudioPort(String name, AudioPort audioPort) {
		audioRecorders.put(name, new AudioRecorder(audioPort));
		enableAudioLogging(audioPort, true);
	}

	public void addFlowModule(FlowModule flowModule) {
		flowEventLoggers.add(new FlowEventLogger(flowModule));
		flowLoggers.add(new FlowLogger(flowModule));
		enableFlowLogging(flowModule, true);
	}
	
	public void enableAudioLogging(AudioPort audioPort, boolean b) {
		logEnabled.put(audioPort, b);
	}
	
	public void enableEventLogging(boolean b) {
		logEnabled.put(Event.class, b);
	}
	
	public void enableFlowLogging(FlowModule flowModule, boolean b) {
		logEnabled.put(flowModule, b);
	}
	
	@Override
	protected void systemStarted() {
		super.systemStarted();
		if (logOnSystemStart)
			startLogging();
	}

	private class FlowEventLogger implements FlowListener {
		
		private FileOutputStream out;
		private FlowModule flowModule;

		public FlowEventLogger(FlowModule flowModule) {
			this.flowModule = flowModule;
			flowModule.addFlowListener(this);
		}

		public void stopLogging() {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			out = null;
		}

		public void startLogging(File file) throws FileNotFoundException {
			this.out = new FileOutputStream(file);
		}

		public FlowModule getFlowModule() {
			return flowModule;
		}

		@Override
		public void onFlowEvent(Event event, FlowEventInfo info) {
			if (out != null) {
				try {
					event.setTime(system.getTimestamp());
					out.write(new String(event.toJSON() + "\n").getBytes());
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	
		@Override
		public void onSendEvent(Event event, FlowEventInfo info) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void onGotoState(State toState, FlowEventInfo info) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void onCallState(State toState, FlowEventInfo info) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void onReturnState(State fromState, State toState, FlowEventInfo info) {
			// TODO Auto-generated method stub
			
		}
	
		@Override
		public void onFlowException(Exception e, FlowEventInfo flowEventInfo) {
			// TODO Auto-generated method stub
			
		}
	
	}

	public boolean isLogging() {
		return isLogging;
	}

	public int getLogTime() {
		return (int) (System.currentTimeMillis() - logStartTime);
	}

}
