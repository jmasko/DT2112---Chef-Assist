package iristk.speech;

import iristk.audio.AudioListener;
import iristk.audio.AudioPort;
import iristk.audio.Microphone;
import iristk.system.InitializationException;
import iristk.util.BlockingByteQueue;

import java.util.LinkedList;

import javax.sound.sampled.AudioFormat;

public class EnergyEndpointer implements Endpointer, AudioListener {

	private AudioFormat audioFormat;
	private EnergyVAD vad;
	private boolean listening;
	private RecognizerListeners listeners = new RecognizerListeners();
	private int noSpeechTimeout = 8000;
	//TODO: check for this
	private int maxSpeechTimeout = 8000;
	private int endSilTimeout = 500;
	private ListeningThread listeningThread;
	private int bufferSize;
	private LinkedList<byte[]> frames = new LinkedList<>();
	private BlockingByteQueue audioData = new BlockingByteQueue();
	private boolean liveMode = true;
	private AudioPort audioPort;
	
	/**
	 * An endpointer that operates on the default microphone (16Khz)
	 * @throws InitializationException
	 */
	public EnergyEndpointer() throws InitializationException {
		this(new Microphone(), true);
	}

	public EnergyEndpointer(AudioPort audioPort, boolean live) {
		this.audioPort = audioPort;
		this.audioFormat = audioPort.getAudioFormat();
		setLiveMode(live);
		this.bufferSize = (int) (audioFormat.getSampleRate() * audioFormat.getSampleSizeInBits() / 800);
		this.vad = new EnergyVAD(audioPort.getDeviceName(), audioFormat);
		audioPort.addAudioListener(this);
	}
	
	@Override
	public synchronized void startListen() throws RecognizerException {
		vad.reset();
		if (liveMode)
			audioData.reset();
		listening = true;
		listeners.initRecognition(audioFormat);
		listeningThread = new ListeningThread();
	}

	public void setLiveMode(boolean liveMode) {
		this.liveMode = liveMode;
	}
	
	private class ListeningThread implements Runnable {

		private Thread thread;
		private boolean running = true;
		private boolean inSpeech = false;
		private float timestamp = 0f;
		private long endSilMsec = 0;
		private long startSilMsec = 0;
		private long inSpeechMsec = 0;
		
		public ListeningThread() {
			thread = new Thread(this);
			thread.start();
		}
		
		@Override
		public void run() {
			try {
				while (running) {
					byte[] buffer = new byte[bufferSize];
					int n = audioData.read(buffer, 0, buffer.length);
					if (n == -1) {
						if (inSpeech) {
							listeners.endOfSpeech(timestamp);
							listeners.recognitionResult(new RecResult(RecResult.FINAL, RecResult.NOMATCH));
						} else {
							listeners.recognitionResult(new RecResult(RecResult.SILENCE));
						}
						return;
					}
					vad.processSamples(buffer, 0, buffer.length);
					if (!inSpeech) {
						frames.add(buffer);
						if (frames.size() > 20) {
							frames.remove(0);
						}
					} else {
						listeners.speechSamples(buffer, 0, buffer.length);
					}
					timestamp += 0.01;
					if (vad.isInSpeech() && !inSpeech) {
						listeners.startOfSpeech(timestamp - 0.2f);
						for (byte[] frame : frames) {
							listeners.speechSamples(frame, 0, frame.length);
						}
						inSpeech = true;
					} else if (!vad.isInSpeech() && inSpeech) {
						endSilMsec += 10;
						if (endSilMsec > endSilTimeout) {
							listeners.endOfSpeech(timestamp - 0.2f);
							listeners.recognitionResult(new RecResult(RecResult.FINAL, RecResult.NOMATCH));
							return;
						}
					} else if (vad.isInSpeech() && inSpeech) {
						endSilMsec = 0;
						inSpeechMsec += 10;
						if (inSpeechMsec > maxSpeechTimeout) {
							listeners.recognitionResult(new RecResult(RecResult.MAXSPEECH));
							return;
						}
					} else if (!vad.isInSpeech() && !inSpeech) {
						startSilMsec += 10;
						if (startSilMsec > noSpeechTimeout) {
							listeners.recognitionResult(new RecResult(RecResult.SILENCE));
							return;
						}
					}
				}
				listeners.recognitionResult(new RecResult(RecResult.FAILED));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public void stop() {
			running = false;
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@Override
	public synchronized boolean stopListen() throws RecognizerException {
		if (listening) {
			listeningThread.stop();
			listening = false;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void setNoSpeechTimeout(int msec) throws RecognizerException {
		this.noSpeechTimeout = msec;
	}

	@Override
	public void setEndSilTimeout(int msec) throws RecognizerException {
		this.endSilTimeout = msec;
	}

	@Override
	public void setMaxSpeechTimeout(int msec) throws RecognizerException {
		this.maxSpeechTimeout = msec;
	}

	@Override
	public AudioFormat getAudioFormat() {
		return audioFormat;
	}

	@Override
	public void addRecognizerListener(RecognizerListener listener, int priority) {
		listeners.add(listener, priority);
	}

	@Override
	public void listenAudio(byte[] buffer, int pos, int len) {
		if (liveMode) {
			if (listening) {
				audioData.write(buffer, pos, len);
			}
		} else {
			// Running in off-line mode, wait until the endpointer runs and the buffer is empty
			//System.out.println(audioData.available());
			while (!listening || audioData.available() > bufferSize * 2) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			audioData.write(buffer, pos, len);
		}
	}

	@Override
	public void startListening() {
		audioData.reset();
	}

	@Override
	public void stopListening() {
		audioData.endWrite();
	}

	public EnergyVAD getVAD() {
		return vad;
	}

	@Override
	public AudioPort getAudioPort() {
		return audioPort;
	}
	
}
