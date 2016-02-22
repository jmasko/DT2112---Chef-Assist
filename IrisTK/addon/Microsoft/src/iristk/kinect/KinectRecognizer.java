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
package iristk.kinect;

import iristk.audio.AudioPort;
import iristk.net.speech.*;
import iristk.speech.RecognizerException;
import iristk.speech.windows.WindowsRecognizer;
import iristk.system.InitializationException;
import iristk.util.Language;

public class KinectRecognizer extends WindowsRecognizer {

	public KinectRecognizer(AudioPort audioPort) throws InitializationException {
		super(audioPort);
	}
	
	@Override
	protected IRecognizer createRecognizer(Language language) throws RecognizerException {
		if (!language.equals(Language.ENGLISH_US))
			throw new RecognizerException(language + " not supported");
		return new SpeechPlatformRecognizer(language.getCode(), true);
	}

}
