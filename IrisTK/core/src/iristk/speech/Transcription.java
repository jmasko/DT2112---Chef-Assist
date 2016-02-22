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

import java.util.ArrayList;
import java.util.List;
import iristk.util.Mapper;
import iristk.util.Record;

public class Transcription extends Record {

	public static Mapper UPS = new Mapper("ups", Transcription.class.getResourceAsStream("ups.map"));

	public static final String SILENCE = "_s";
	
	@RecordField(name="phones")
	public List<Phone> phones = new ArrayList<>();
	
	public Transcription() {
	}
	
	public int length() {
		float length = 0.0f;
		for (Phone p : phones) {
			if (!p.name.equals("sil")) {
				length = p.end;
			}
		}
		return (int) (length * 1000f);
	}

	public void add(String name, float start, float end) {
		Phone p = new Phone();
		if (!UPS.containsKey(name)) {
			System.err.println("WARNING: '" + name + "' is not a UPS phoneme");
		}
		p.name = name;
		p.start = start;
		p.end = end;
		phones.add(p);
	}

	public float trimSilence() {
		if (phones.size() > 0) {
			Phone lastPhone = phones.get(size()-1);
			// Trim silence at the end
			if (lastPhone.name.equals(SILENCE) && (lastPhone.end - lastPhone.end) > 0.1f) {
				float trim = (lastPhone.end - lastPhone.start) - 0.1f;
				lastPhone.end = lastPhone.start + 0.1f;
				return trim;
			}
		}
		return 0;
	}

	public static void main(String[] args) throws Exception {
		Transcription trans = new Transcription();
		trans.add("A", 0f, 0.5f);
		trans.add("B", 0.6f, 0.9f);
		String json = trans.toJSON().toString();
		System.out.println(json);
		System.out.println(Record.fromJSON(json));
	}

	public List<Phone> getPhones() {
		return phones;
	}
	
}
