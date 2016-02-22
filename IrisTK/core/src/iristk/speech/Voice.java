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

import iristk.util.Language;

public class Voice {

	public enum Gender {
		MALE, FEMALE;

		public static Gender fromString(String string) {
			for (Gender gender : values()) {
				if (gender.name().equalsIgnoreCase(string))
					return gender;
			}
			return null;
		}
	}

	private final String name;
	private final Language language;
	private final Gender gender;
	private final Class<? extends Synthesizer> synthClass;

	public Voice(Class<? extends Synthesizer> synthClass, String name, Gender gender, Language language) {
		this.synthClass = synthClass;
		this.name = name;
		this.language = language;
		this.gender = gender;
	}

	public String getName() {
		return name;
	}

	public Language getLanguage() {
		return language;
	}

	public Gender getGender() {
		return gender;
	}

	@Override
	public String toString() {
		return synthClass.getSimpleName() + " - " + getName();
	}
}
