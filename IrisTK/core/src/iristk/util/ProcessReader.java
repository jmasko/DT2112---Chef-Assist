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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class ProcessReader {

	public static final String readRegistry(String location) {
		return readProcess("reg query " + '"' + location + '"');
	}
	
	public static final String readProcess(String cmd) {
		try {
			Process process = Runtime.getRuntime().exec(cmd);

			StreamReader reader = new StreamReader(process.getInputStream());
			reader.start();
			process.waitFor();
			reader.join();
			String output = reader.getResult();

			return output;

		} catch (Exception e) {
			return "";
		}
	}

	static class StreamReader extends Thread {
		private InputStream is;
		private StringWriter sw = new StringWriter();

		public StreamReader(InputStream is) {
			this.is = is;
		}

		@Override
		public void run() {
			try {
				int c;
				while ((c = is.read()) != -1)
					sw.write(c);
			} catch (IOException e) {
			}
		}

		public String getResult() {
			return sw.toString();
		}
	}

}
