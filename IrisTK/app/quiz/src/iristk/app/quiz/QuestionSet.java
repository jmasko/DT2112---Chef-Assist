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
package iristk.app.quiz;

import iristk.util.RandomList;

import java.io.*;
import java.util.*;

public class QuestionSet extends ArrayList<Question> {

	private int n = 0;
	
	public QuestionSet(InputStream questionFile) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(questionFile));
			String line = br.readLine();
			int qn = 0;
			int ln = 0;
			System.out.println("Reading questions");
			while ((line = br.readLine()) != null) {
				ln++;
				if (!line.matches("[A-Za-z0-9,\\.;\\?'\\- ]*")) {
					System.err.println("Illegal line " + ln + ": " + line);
					continue;
				}
				String[] cols = line.split(";");
				if (cols.length < 5) {
					System.err.println("Not enough columns in line " + ln + ": " + line);
					continue;
				} 
				Question q = new Question("q" + qn++, cols);
				add(q);
			}
			System.out.println(qn + " questions read");
			randimize();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public QuestionSet(File file) throws FileNotFoundException {
		this(new FileInputStream(file));
	}

	public void randimize() {
		RandomList.shuffle(this);
	}
	
	public Question next() {
		Question q = get(n);
		n++;
		if (n >= size()) {
			n = 0;
		}
		return q;
	}
	
}
