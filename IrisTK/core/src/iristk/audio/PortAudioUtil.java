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
package iristk.audio;

import iristk.system.IrisUtils;
import com.portaudio.PortAudio;

public class PortAudioUtil {

	private static Boolean initialized = false;
	
	public static synchronized void initialize() throws Exception {
		if (!initialized) {
			IrisUtils.addCoreLibPath();
			if (IrisUtils.is64arch()) {
				IrisUtils.loadPackageLib(IrisUtils.CORE_PACKAGE, "x64/portaudio_x64.dll");
				IrisUtils.loadPackageLib(IrisUtils.CORE_PACKAGE, "x64/jportaudio_x64.dll");
			} else {
				IrisUtils.loadPackageLib(IrisUtils.CORE_PACKAGE, "x86/portaudio_x86.dll");
				IrisUtils.loadPackageLib(IrisUtils.CORE_PACKAGE, "x86/jportaudio_x86.dll");
			}
			PortAudio.initialize();
			initialized = true;
		}
	}
	

}
