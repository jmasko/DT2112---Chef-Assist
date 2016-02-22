package iristk.speech.wit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;

import iristk.speech.RecResult;
import iristk.speech.RecognizerListener;
import iristk.util.Record;
import iristk.util.Record.JsonToRecordException;

public class WitListener implements RecognizerListener {

	private List<String> keys = new ArrayList<>();

	public WitListener() {
	}
	
	@Override
	public void initRecognition(AudioFormat format) {
	}

	@Override
	public void startOfSpeech(float timestamp) {
	}

	@Override
	public void endOfSpeech(float timestamp) {
	}

	@Override
	public void speechSamples(byte[] samples, int pos, int len) {
	}

	@Override
	public void recognitionResult(RecResult result) {
		if (result != null && result.text != null && keys.size() > 0) {
			try {
				Record json = Record.fromJSON(getResult(result.text, keys.get(0)));
				String intent = json.getString("outcomes:0:intent");
				// TODO: process outcomes:0:entities
				if (intent != null) {
					result.put("sem:intent", intent);
				}
			} catch (JsonToRecordException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static String getResult(String text, String key) throws IOException {
		long t = System.currentTimeMillis();
		StringBuilder result = new StringBuilder();
		URL url = new URL("https://api.wit.ai/message?v=20150929&q=" + URLEncoder.encode(text));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Authorization", "Bearer " + key);
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		System.out.println("Wit.ai time: " + (System.currentTimeMillis() - t));
		return result.toString();
	}
	
	private static String getResult2() throws IOException {
		long t = System.currentTimeMillis();
		StringBuilder result = new StringBuilder();
		URL url = new URL("http://www.google.com");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		//conn.setRequestProperty("Authorization", "Bearer " + key);
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		System.out.println("iristk time: " + (System.currentTimeMillis() - t));
		return result.toString();
	}

	public static void main(String[] args) throws IOException {
		System.out.println(getResult("turn on the lights", "ZHHJUFR6V7WMEAK2N2YH3TWOUO5Y3XUK"));
		System.out.println(getResult2());
	}

	public void activate(String key) {
		keys .add(key);
	}

	public void deactivate(String key) {
		keys.remove(key);
	}

}
