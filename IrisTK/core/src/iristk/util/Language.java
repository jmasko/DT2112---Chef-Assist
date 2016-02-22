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
package iristk.util;

public class Language {

	public static final Language SWEDISH = new Language("sv-SE");
	public static final Language ENGLISH_US = new Language("en-US");
	public static final Language ENGLISH_GB = new Language("en-GB");
	public static final Language GERMAN = new Language("de-DE");
	
	private String code;
	
	public Language(String code) {
		if (!code.matches("[a-z][a-z]-[A-Z][A-Z]"))
			throw new RuntimeException("Bad language code: " + code);
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	@Override
	public String toString() {
		return code;
	}
	
	@Override
	public boolean equals(Object lang) {
		if (lang instanceof Language) {
			return this.code.equalsIgnoreCase(((Language)lang).getCode());
		} else {
			return false;
		}
	}
	
	public boolean equalsIgnoreDialect(Object lang) {
		if (lang instanceof Language) {
			return this.getMain().equalsIgnoreCase(((Language)lang).getMain());
		} else {
			return false;
		}
	}
	
	public String getMain() {
		return getCode().substring(0, 2);
	}
	
	
}
