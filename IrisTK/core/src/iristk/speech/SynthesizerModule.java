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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import iristk.audio.AudioTarget;
import iristk.audio.AudioUtil;
import iristk.audio.Sound;
import iristk.audio.SoundPlayer;
import iristk.audio.SoundPlayer.CallbackDelegate;
import iristk.audio.Speaker;
import iristk.speech.Voice.Gender;
import iristk.system.InitializationException;
import iristk.system.Event;
import iristk.system.IrisModule;
import iristk.system.IrisUtils;
import iristk.util.DelayedEvent;
import iristk.util.Language;
import iristk.util.Pair;
import iristk.util.Record;
import iristk.util.Record.JsonToRecordException;
import iristk.util.Utils;

public class SynthesizerModule extends IrisModule {

	private List<Synthesizer> synthesizers = new ArrayList<>();

	private Synthesizer synthesizer;
	private SoundPlayer player;
	private SpeechThread speechThread = null;
	ArrayList<String> monitorStates = new ArrayList<String>();
	private int endPosition;
	private boolean doLipsync = false;
	private String agentName = "system";
	private String preferredAudioDevice = null;
	private SpeechQueue speechQueue = new SpeechQueue();

	private HashMap<Synthesizer,SoundPlayer> soundPlayers = new HashMap<>();

	private boolean speaking = false;

	public SynthesizerModule() {
	}

	public SynthesizerModule(Synthesizer synth) throws InitializationException {
		setSynthesizer(synth);
	}

	public void addSynthesizer(Synthesizer synthesizer) throws InitializationException {
		synthesizers.add(synthesizer);
		synthesizer.init();
	}

	public void setSynthesizer(Synthesizer synthesizer) throws InitializationException {
		stopSpeaking();
		if (!synthesizers.contains(synthesizer))
			addSynthesizer(synthesizer);
		if (!soundPlayers.containsKey(synthesizer)) {
			this.player = new SoundPlayer(new Speaker(preferredAudioDevice, synthesizer.getAudioFormat()));
			soundPlayers.put(synthesizer, player);
		} else {
			this.player = soundPlayers.get(synthesizer);
		}
		this.synthesizer = synthesizer;
	}

	public void setPreferredAudioDevice(String deviceName) {
		this.preferredAudioDevice = deviceName;
	}

	public void doLipsync(boolean flag) {
		this.doLipsync = flag;
	}

	@Override
	public void init() throws InitializationException {
		subscribe("action.speech** monitor.lipsync** action.voice");
		speechThread = new SpeechThread();
	}

	public AudioFormat getAudioFormat() {
		return getSynthesizer().getAudioFormat();
	}

	public Synthesizer getSynthesizer() {
		return synthesizer;
	}

	public AudioTarget getAudioTarget() {
		return player.getAudioTarget();
	}

	/*
	public void say(String text) {
		sayAsync(text);
		try {
			speechThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void sayAsync(String text) {
		Event event = new Event("action.speech");
		event.put("text", text);
		if (speechThread != null && speechThread.isRunning()) {
			speechThread.stopRunning();
		}
		speechThread = new SpeechThread(event);
	}
	 */

	@Override
	public void onEvent(Event event) {
		if (event.has("agent") && !event.getString("agent").equals(agentName)) 
			return;
		if (event.triggers("action.speech")) {
			if (event.has("agent") && !event.getString("agent").equals(agentName))
				return;
			boolean ifsilent = event.getBoolean("ifsilent", false);
			boolean async = event.getBoolean("async", false);
			if (ifsilent && speaking) {
				if (!async)
					monitorDone();
				return;
			}
			String text = event.getString("text");
			String audio = event.getString("audio");
			String display = event.getString("display");
			boolean abort = event.getBoolean("abort", false);
			if (abort) 
				speechQueue.clear();
			speechQueue.append(event.getId(), text, audio, display);
			if (abort) 
				abortSpeechAction();

			//if (speechThread != null && speechThread.isRunning()) {
			//	speechThread.stopRunning();
			//}

			/*
			if (event.getString("text", "").replaceAll("\\s*<.*?>\\s*", "").trim().length() == 0) {
				// The event doesn't contain anything to synthesize, so just skip it
				Event offset = new Event("monitor.speech.end");
				offset.put("action", event.getId());
				if (event.has("agent"))
					offset.put("agent", event.getString("agent"));
				send(offset);
			} else {
				speechThread = new SpeechThread(event);
			}
			 */
		} else if (event.triggers("action.speech.stop")) {
			if (!event.has("agent") || event.getString("agent").equals(agentName)) {
				stopSpeaking();
			}
		} else if (event.triggers("monitor.lipsync.start")) {
			speechThread.lipsReady(event);
		} else if (event.triggers("action.voice")) {
			if (!event.has("agent") || event.getString("agent").equals(agentName)) { 
				try {
					if (event.has("name")) {
						setVoice(event.getString("name"));
					} else {
						VoiceList vlist = synthesizer.getVoices();
						if (event.has("lang"))
							vlist = vlist.getByLanguage(new Language(event.getString("lang")));
						if (event.has("gender")) 
							vlist = vlist.getByGender(Gender.fromString(event.getString("gender")));
						synthesizer.setVoice(vlist.getFirst());
					}
				} catch (VoiceNotFoundException e) {
					System.out.println(e.getMessage());
				} catch (InitializationException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void stopSpeaking() {
		speechQueue.clear();
		abortSpeechAction();
	}
	
	private void abortSpeechAction() {
		if (speechThread != null) {
			speechThread.abortSpeechAction();
			if (doLipsync && speechThread.sa != null) {
				Event lipsync = new Event("action.lipsync.stop");
				lipsync.put("action", speechThread.sa.action);
				send(lipsync);
			}
		}
	}

	protected static File getCachePath(Synthesizer synthesizer) {
		File cachePath = IrisUtils.getTempDir("Synthesizer/" + synthesizer.getClass().getSimpleName() + "/" + synthesizer.getVoice().getName());
		if (!cachePath.exists()) {
			cachePath.mkdirs();
		}
		return cachePath;
	}

	protected void addMonitorState(String state) {
		monitorStates.add(state);
		monitorState(monitorStates.toArray(new String[0]));
	}

	protected void removeMonitorState(String state) {
		monitorStates.remove(state);
		monitorState(monitorStates.toArray(new String[0]));
	}

	private class SpeechThread extends Thread {

		//private final Event speechEvent;
		//private boolean running;
		private boolean lipsReady = false;
		private boolean playing = false;
		private Object lock = new Object();
		public SpeechAction sa = null;
		private ArrayBlockingQueue<Event> lipsEvents = new ArrayBlockingQueue<>(1000);

		public SpeechThread() {
			super("SpeechThread");
			start();
		}

		public void abortSpeechAction() {
			if (sa != null) {
				sa.abort = true;
				if (playing)
					player.stop();
			}
		}

		@Override
		public void run() {
			try {
				while (true) {
					sa = null;
					sa = speechQueue.take();
					
					speaking = true;

					final int startMsec = 0; //speechEvent.getInteger("start", 0);

					Transcription trans = null;
					File wavFile = null;

					if (sa.audio != null) {
						Pair<Transcription,File> pair = findPrerec(sa.audio, sa.text, synthesizer);
						trans = pair.getFirst();
						wavFile = pair.getSecond();
					}

					if (wavFile == null) {
						Pair<Transcription,File> pair = synthesizeToCache(sa.text, synthesizer); 
						trans = pair.getFirst();
						wavFile = pair.getSecond();
					}

					if (doLipsync && trans != null) {

						Event lipsync = new Event("action.lipsync");
						lipsync.put("action", sa.action);
						if (startMsec > 0)
							lipsync.put("start", startMsec);
						lipsync.put("phones", trans);
						if (agentName != null)
							lipsync.put("agent", agentName);
						send(lipsync);
						AWAIT_LIPSYNC:
							while (true) {
								Event event = lipsEvents.poll(5000, TimeUnit.MILLISECONDS);
								if (event == null) {
									System.err.println("WARNING: No response for lipsync (no agent running?), turning lipsync off");
									doLipsync = false;
									break AWAIT_LIPSYNC;
								} else if (event.getString("action").equals(sa.action)) {
									break AWAIT_LIPSYNC;
								}
							}
					}

					if (sa.abort) continue;

					try {
						Sound sound = new Sound(wavFile);

						Integer prominence = null;
						if (trans != null) {
							for (Phone phone : trans.phones) {
								if (phone.prominent)
									prominence = (int)(phone.start * 1000.0);
							}	
						}

						playing = true;
						player.playAsync(sound, startMsec, new CallbackDelegate() {
							@Override
							public void callback(int pos) {
								endPosition = (int) (AudioUtil.byteLengthToSeconds(player.getAudioFormat(), pos) * 1000f);
								synchronized (lock) {
									playing = false;
									lock.notify();
								}
							}
						});

						final String stateLabel = "\"" + sa.text.replaceAll("<.*?>", "") + "\"";
						addMonitorState(stateLabel);

						Event onset = new Event("monitor.speech.start");
						onset.put("action", sa.action);
						onset.put("text", sa.text);
						if (startMsec > 0)
							onset.put("start", startMsec);
						onset.put("length", (int)(sound.getSecondsLength() * 1000f));
						if (prominence != null)
							onset.put("prominence", prominence);
						if (agentName != null)
							onset.put("agent", agentName);
						onset.putIfNotNull("display", sa.display);
						onset.putIfNotNull("audio", sa.audio);
						send(onset);

						DelayedEvent prominenceEvent = null;
						final String promaction = sa.action;
						if (prominence != null) {
							prominenceEvent = new DelayedEvent(prominence) {
								@Override
								public void run() {
									Event prom = new Event("monitor.speech.prominence");
									prom.put("action", promaction);
									send(prom);
								}
							};
						}

						while (playing) {
							synchronized (lock ) {
								try {
									lock.wait();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}

						if (prominenceEvent != null)
							prominenceEvent.forget();

						removeMonitorState(stateLabel);
						Event offset = new Event("monitor.speech.end");
						offset.put("action", sa.action);
						if (trans != null && endPosition < trans.length() - 100) {
							offset.put("stopped", endPosition);
						}
						if (agentName != null)
							offset.put("agent", agentName);
						send(offset);

						if (speechQueue.size() == 0) {
							speaking = false;
							monitorDone();
						}

					} catch (UnsupportedAudioFileException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}

			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		public void lipsReady(Event event) {
			//System.out.println("Lips ready! " + event.getString("action").equals(speechEvent.getId()));
			lipsEvents.add(event);
		}

		/*
		public void stopRunning() {
			player.stop();
			running = false;
			playing = false;
			synchronized (SpeechThread.this) {
				SpeechThread.this.notify();
			}
			try {
				join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public boolean isRunning() {
			return running;
		}
		 */

	}

	private static String cacheId(String text) {
		String id;
		try {
			id = URLEncoder.encode(text, "UTF-8");
			if (id.length() > 100) {
				id = id.substring(0, 100) + text.hashCode();
			}
		} catch (UnsupportedEncodingException e) {
			id = "" + text.hashCode();
		}
		return id;
	}

	public void monitorDone() {
		Event done = new Event("monitor.speech.done");
		if (agentName != null)
			done.put("agent", agentName);
		send(done);
	}

	public static Pair<Transcription,File> findPrerec(String audio, String text, Synthesizer synthesizer) {
		File synthPackage = IrisUtils.getPackagePath(synthesizer.getClass());
		//prerecPath = new File(prerecPath.getAbsolutePath().replace("%voice%", synthesizer.getVoice().getName()));
		File prerecPath = new File(synthPackage, "voices/" + synthesizer.getVoice().getName() + "/prerec");
		if (!audio.endsWith(".wav"))
			audio = audio + ".wav";
		File prerec = new File(prerecPath, audio);
		if (prerec.exists()) {
			try {
				Transcription trans;
				File wavFile = prerec;
				File prepho = new File(prerec.getAbsolutePath().replaceFirst("\\..*", "") + ".pho");
				if (prepho.exists()) {
					trans = (Transcription) Record.fromJSON(Utils.readTextFile(prepho));
				} else {
					File temppho = new File(getCachePath(synthesizer), prepho.getName());
					if (temppho.exists()) {
						trans = (Transcription) Record.fromJSON(Utils.readTextFile(temppho));
					} else {
						trans = synthesizer.transcribe(text);
						Utils.writeTextFile(temppho, trans.toJSON().toString());
					}
				}
				return new Pair<Transcription,File>(trans, wavFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JsonToRecordException e) {
				e.printStackTrace();
			}
		}
		return new Pair<Transcription,File>(null, null);
	}

	public static Pair<Transcription,File> synthesizeToCache(String synthText, Synthesizer synthesizer) {
		Transcription trans = null;
		String cacheId = cacheId(synthText);
		File cachePath = getCachePath(synthesizer);
		File phoFile = new File(cachePath, cacheId + ".pho");
		File wavFile = new File(cachePath, cacheId + ".wav");
		if (!wavFile.exists() || !phoFile.exists()) {
			// Synthesize
			trans = synthesizer.synthesize(synthText, wavFile);
			try {
				Sound sound = new Sound(wavFile);
				byte[] bytes = sound.getBytes();
				double[] samples = new double[bytes.length / 2];
				AudioUtil.bytesToDoubles(sound.getAudioFormat(), bytes, 0, bytes.length, samples, 0);
				double max = Double.MIN_VALUE;
				Phone maxphone = null;
				for (Phone phone : trans.phones) {
					if (phone.name.matches(".*[AOUEIY].*")) {
						double power = AudioUtil.power(samples, 
								AudioUtil.secondLengthToSamples(sound.getAudioFormat(), phone.start), 
								AudioUtil.secondLengthToSamples(sound.getAudioFormat(), phone.end - phone.start));
						if (power > max) {
							max = power;
							maxphone = phone;
						}
					}
				}
				if (maxphone != null)
					maxphone.prominent = true;
			} catch (UnsupportedAudioFileException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				Utils.writeTextFile(phoFile, trans.toJSON().toString());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				trans = (Transcription) Record.fromJSON(Utils.readTextFile(phoFile));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JsonToRecordException e) {
				e.printStackTrace();
			}
		}
		return new Pair<Transcription,File>(trans, wavFile);
	}

	public void setVoice(String name) {
		for (Synthesizer synth : synthesizers) {
			for (Voice voice : synth.getVoices()) {
				if (voice.toString().toUpperCase().contains(name.toUpperCase())) {
					try {
						synth.setVoice(voice);
						setSynthesizer(synth);
						return;
					} catch (InitializationException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public VoiceList getVoices() {
		VoiceList list = new VoiceList();
		for (Synthesizer synth : synthesizers) {
			list.addAll(synth.getVoices());
		}
		return list;
	}

	public Voice getVoice() {
		return synthesizer.getVoice();
	}

	public void setAgentName(String name) {
		this.agentName = name;
	}

	@Override
	public String getUniqueName() {
		return "synthesizer-" + agentName;
	}


}
