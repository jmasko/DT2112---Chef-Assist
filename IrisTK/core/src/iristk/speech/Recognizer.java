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

public interface Recognizer extends Endpointer {
	
	/**
	 * Sets whether to generate partial results (default should be false)
	 */
	void setPartialResults(boolean cond);
	
	/**
	 * Recognizes a wave-file
	 * @param file the file to recognize
	 * @return a recognition result
	 * @throws RecognizerException
	 */
	RecResult recognizeFile(File file) throws RecognizerException;

	void setNbestLength(int length);
	
	void activateContext(String name, float weight) throws RecognizerException;
	
	void deactivateContext(String name) throws RecognizerException;
	
	RecognizerFactory getRecognizerFactory();

}
