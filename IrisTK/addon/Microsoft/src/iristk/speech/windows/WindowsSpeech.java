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
package iristk.speech.windows;

import iristk.system.IrisUtils;

import java.io.File;
import net.sf.jni4net.Bridge;

public class WindowsSpeech {

	private static final String PACKAGE = "Microsoft";
	
	private static boolean initialized = false;
	
	public static synchronized void init() {
		if (!initialized ) {
			try {
				IrisUtils.addJavaLibPath(IrisUtils.getPackageLibPath(PACKAGE));
				Bridge.init();
		        Bridge.LoadAndRegisterAssemblyFrom(new File(IrisUtils.getPackageLibPath(PACKAGE), "IrisTK.Net.Speech.j4n.dll"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			initialized = true;
		}
	}
	
	public static File getLibPath() {
		return IrisUtils.getPackageLibPath(PACKAGE);
	}
	
}
