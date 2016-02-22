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
package iristk.speech;

import iristk.audio.AudioListener;
import iristk.audio.AudioPort;
import iristk.audio.AudioUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import javax.sound.sampled.AudioFormat;

import iristk.system.IrisUtils;
import iristk.util.Record;
import iristk.util.Utils;

public class EnergyVAD implements AudioListener {

	// The window size (in frames), which smoothes the result, but gives a lag
	public static final int WINSIZE = 21;
	// How quickly the endpointer adapts the silence level, the lower value, the quicker
	private static final double ADAPT_RATE = 400;

	private static final int SPEECH = 1;
	private static final int SILENCE = 0;
	
	private static int defaultSilenceLevel = 20;
	private static int defaultDeltaSpeech = 20;
	private static int defaultDeltaSil = 10;
	private static boolean defaultAdaptive = true;
	private static File storeDirectory = IrisUtils.getTempDir();
	
	private int silenceLevel;
	private int deltaSpeech;
	private int deltaSil;
	private boolean adaptive;

	private int[] stateWindow = new int[WINSIZE];
	{
		Arrays.fill(stateWindow, SILENCE);
	}
	private int stateWindowPos = 0;

	private double prevSample = 0.0;

	private boolean calibrating = false;

	private long streamPos = 0;

	public int[] histogram = new int[100];
	{
		Arrays.fill(histogram, 0);
	}

	private ArrayList<VADListener> vadListeners = new ArrayList<VADListener>();

	private int state = SILENCE;
	private final AudioFormat audioFormat;
	private double[] inputSamples = null;
	private double[] frameBuffer = new double[2048];
	private int frameBufferPos = 0;
	private final int sampleSize;
	private double[] frame;
	private String deviceName;
	
	public EnergyVAD(AudioFormat audioFormat) {
		this(null, audioFormat);
	}
	
	public EnergyVAD(AudioPort audioPort) {
		this(audioPort.getDeviceName(), audioPort.getAudioFormat());
		audioPort.addAudioListener(this);
	}
	
	public EnergyVAD(String deviceName, AudioFormat audioFormat) {
		this.deviceName = deviceName;
		this.audioFormat = audioFormat;
		this.sampleSize = audioFormat.getSampleSizeInBits() / 8;
		frame = new double[(int) (audioFormat.getSampleRate() / 100)];
		if (deviceName != null) {
			File file = getStoreFile();
			if (file.exists()) {
				try {
					System.out.println("Reading VAD parameters from " + file.getAbsolutePath());
					Record props = Record.fromProperties(file);
					silenceLevel = props.getInteger("silenceLevel", defaultSilenceLevel);
					deltaSpeech = props.getInteger("deltaSpeech", defaultDeltaSpeech);
					deltaSil = props.getInteger("deltaSil", defaultDeltaSil);
					adaptive = props.getBoolean("adaptive", defaultAdaptive);
					System.out.println("Adaptive: " + adaptive + ", SilLevel: " + silenceLevel + ", DeltaSpeech: " + deltaSpeech + ", DeltaSil: " + deltaSil);
					return;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		silenceLevel = defaultSilenceLevel;
		deltaSpeech = defaultDeltaSpeech;
		deltaSil = defaultDeltaSil;
		adaptive = defaultAdaptive;
	}
	
	public static void setStoreDirectory(File directory) {
		storeDirectory = directory;
	}
	
	public String getDeviceName() {
		return deviceName;
	}
	
	public void storeParameters() {
		if (deviceName != null) {
			storeDirectory.mkdirs();
			Record props = new Record();
			props.put("silenceLevel", silenceLevel);
			props.put("deltaSpeech", deltaSpeech);
			props.put("deltaSil", deltaSil);
			props.put("adaptive", adaptive);
			try {
				File file = getStoreFile();
				props.toProperties(file);
				System.out.println("VAD parameters stored to " + file.getAbsolutePath());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.err.println("Cannot store vad parameters without a device name");
		}
	}
	
	private File getStoreFile() {
		return new File(storeDirectory, "EnergyVAD/" + URLEncoder.encode(deviceName) + ".properties");
	}

	public static void setDefaultSilenceLevel(int value) {
		defaultSilenceLevel = value;
	}
	
	public static void setDefaultDeltaSpeech(int value) {
		defaultDeltaSpeech = value;
	}
	
	public static void setDefaultDeltaSil(int value) {
		defaultDeltaSil = value;
	}
	
	public static void setDefaultAdaptive(boolean value) {
		defaultAdaptive = value;
	}

	public void setSilenceLevel(int value) {
		silenceLevel = value;
	}
	
	public int getSilenceLevel() {
		return silenceLevel;
	}

	public void setDeltaSpeech(int value) {
		deltaSpeech = value;
	}
	
	public int getDeltaSpeech() {
		return deltaSpeech;
	}
	
	public void setDeltaSil(int value) {
		deltaSil = value;
	}
	
	public int getDeltaSil() {
		return deltaSil;
	}
	
	public void setAdaptive(boolean value) {
		adaptive = value;
	}
	
	public boolean isAdaptive() {
		return adaptive;
	}
	
	public void addVADListener(VADListener vadListener) {
		this.vadListeners.add(vadListener);
	}

	private int power(double[] samples) {
		double sumOfSquares = 0.0f;
		for (int i = 0; i < samples.length; i++) {
			double sample = samples[i] - prevSample;
			sumOfSquares += (sample * sample);
			prevSample = samples[i];
		}
		double power = (10.0 * (Math.log10(sumOfSquares) - Math.log10(frame.length))) + 0.5;
		if (power < 0) power = 1.0;
		return (int) power;
	}

	private void updateThresholds(int power) {
		histogram[power] += 1;
		if (histogram[power] > ADAPT_RATE) {
			for (int i = 0; i < histogram.length; i++) {
				histogram[i] /= 2;
			}
		}
		
		int ind = Utils.maxIndex(histogram);
		if (ind > -1 && histogram[ind] > 10 && getSilenceLevel() != ind) {
			silenceLevel = ind;
			//System.out.println(silenceLevel);
		}
	}

	public void processSamples(double[] samples, boolean scale) {
		System.arraycopy(samples, 0, frameBuffer, frameBufferPos, samples.length);
		frameBufferPos += samples.length;
		while (frameBufferPos >= frame.length) {    	
			System.arraycopy(frameBuffer, 0, frame, 0, frame.length);
			System.arraycopy(frameBuffer, frame.length, frameBuffer, 0, frameBufferPos - frame.length);
			frameBufferPos -= frame.length;
			if (scale)
				AudioUtil.scaleDoubles(frame, Short.MAX_VALUE);
			processFrame();
		}
	}

	public void processSamples(byte[] samples, int pos, int length) {
		if (inputSamples  == null || inputSamples.length != length / sampleSize) {
			inputSamples = new double[length / sampleSize];
		}
		AudioUtil.bytesToDoubles(audioFormat, samples, pos, length, inputSamples, 0);
		processSamples(inputSamples, true);
	}

	private void processFrame() {
		streamPos += frame.length;

		int power = power(frame);
		
		int newState;
		if (state == SPEECH) {
			if (power <= (getSilenceLevel() + getDeltaSil())) {
				newState = SILENCE;
			} else {
				newState = SPEECH;
			}
		} else {
			if (power > (getSilenceLevel() + getDeltaSpeech())) {
				newState = SPEECH;
			} else {
				newState = SILENCE;
			}			   
		}
		
		if (isAdaptive())
			updateThresholds(power);
		stateWindow[stateWindowPos] = newState;
		stateWindowPos++;
		if (stateWindowPos >= WINSIZE)
			stateWindowPos = 0;

		int[] stateCount = new int[2];
		stateCount[SPEECH] = 0;
		stateCount[SILENCE] = 0;
		for (int i = 0; i < WINSIZE; i++) {
			stateCount[stateWindow[i]] ++;
		}
		//System.out.println(stateCount[SPEECH] + " " + stateCount[SILENCE]);
		if (stateCount[SPEECH] > stateCount[SILENCE]) {
			state  = SPEECH;
			//silFrames  = 0;
		} else {
			state = SILENCE;
			//silFrames++;
		}
		
		//System.out.println(state);

		for (VADListener listener : vadListeners) {
			listener.vadEvent(streamPos, state == SPEECH, power);
		}

	}

	public boolean isInSpeech() {
		//return (!calibrating && (state == SPEECH || silFrames < endSil));
		return (!calibrating && (state == SPEECH));
	}

	public void reset() {
		Arrays.fill(stateWindow, SILENCE);
		state = SILENCE;
		stateWindowPos = 0;
		streamPos = 0;
	}

	public interface VADListener {

		/**
		 * 
		 * @param streamPos The position in the stream (counted in samples)
		 * @param inSpeech Voice activity at position
		 * @param energy Average energy at position
		 */
		public void vadEvent(long streamPos, boolean inSpeech, int energy);

	}

	@Override
	public void listenAudio(byte[] buffer, int pos, int len) {
		processSamples(buffer, pos, len);
	}

	@Override
	public void startListening() {
	}

	@Override
	public void stopListening() {
	}
}
