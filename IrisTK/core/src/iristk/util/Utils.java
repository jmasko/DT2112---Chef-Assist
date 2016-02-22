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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import sun.misc.Unsafe;

public class Utils {

	public static String readTextFile(File file) throws IOException {
		/*
		byte[] buffer = new byte[(int) file.length()];
		BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
		is.read(buffer);
		is.close();
		return new String(buffer);
		*/
		return readTextFile(new FileInputStream(file));
	}
	
	public static String readTextFile(InputStream in) throws IOException {
		return new Scanner(in,"UTF-8").useDelimiter("\\A").next();
	}
	
	public static void writeTextFile(File file, String string) throws IOException {
		if (file.getParentFile() != null && !file.getParentFile().exists())
			file.getParentFile().mkdirs();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
		bw.write(string);
		bw.close();
	}
	
	public static double mean(List<? extends Number> data) {
		double mean = 0;
		for (Number pd : data) {
			mean += pd.doubleValue() / data.size();
		}
		return mean;
	}

	public static double median(List<? extends Number> data) {
		ArrayList<Double> sorted = new ArrayList<Double>(data.size());
		for (int i = 0; i < data.size(); i++) {
			sorted.add(data.get(i).doubleValue());
		}
		Collections.sort(sorted);
		if (sorted.size() % 2 == 1) {
			return sorted.get(sorted.size() / 2);
		} else {
			int mid = sorted.size() / 2 - 1;
			return (sorted.get(mid) + sorted.get(mid+1)) / 2;
		}
	}
	
	public static double stdev(List<? extends Number> data, double mean) {
		float variance = 0;
		for (Number pd : data) {
			variance += Math.pow((pd.doubleValue() - mean), 2) / data.size();
		}
		return Math.sqrt(variance);
	}
	
	public static double stdev(List<Double> values) {
		return stdev(values, mean(values));
	}
	
	public static double min(List<Double> values) {
		double min = Double.MAX_VALUE;
		for (Double d : values) {
			min = Math.min(d, min);
		}
		return min;
	}
	
	public static double max(List<Double> values) {
		double max = Double.MIN_VALUE;
		for (Double d : values) {
			max = Math.max(d, max);
		}
		return max;
	}
	
	public static int maxIndex(int[] a) {
		int index = -1;
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < a.length; i++) {
			if (a[i] > max) {
				max = a[i];
				index = i;
			}
		}
		return index;
	}
	
	public static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}

	/*
	public static File resourceToFile(URL config) throws IOException {
		try {
			// See if we can read directly from the resource
			if (!config.getProtocol().equals("jar")) {
				File file = new File(config.toURI());
				if (file.exists()) {
					return file;
				}
			}
		} catch (URISyntaxException e) {
		}
		// That didn't work. Try to copy the resource to a temporary directory.
		File tempFile = new File(System.getProperty("java.io.tmpdir") + File.separator + "iristk" + File.separator + "nuance9" + File.separator + "config.xml");
		new File(tempFile.getParent()).mkdirs();
		InputStream is = config.openStream();
		FileOutputStream fos = new FileOutputStream(tempFile);
		byte[] buf = new byte[256];
		int read = 0;
		while ((read = is.read(buf)) > 0) {
			fos.write(buf, 0, read);
		}
		fos.close();
		is.close();
		return tempFile;
	}
	*/
	
	public static String listToString(List list, String glue) {
		String result = "";
		boolean first = true;
		for (Object item : list) {
			if (!first)
				result += glue;
			result += item.toString();
			first = false;
		}
		return result;
	}

	public static String listToString(List list) {
		return listToString(list, " ");
	}
	
	public static String fetchURL(String url) {
		String content = null;
		URLConnection connection = null;
		try {
		  connection =  new URL(url).openConnection();
		  Scanner scanner = new Scanner(connection.getInputStream());
		  scanner.useDelimiter("\\Z");
		  content = scanner.next();
		  return content;
		} catch ( Exception ex ) {
		    ex.printStackTrace();
		}
		return "";
	}
	
	public static Unsafe getUnsafe() {
	    try {
	            Field f = Unsafe.class.getDeclaredField("theUnsafe");
	            f.setAccessible(true);
	            return (Unsafe)f.get(null);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return null;
	    }
	}
	
	/**
	 * Deletes the folder and all of its contents
	 * @param folder the folder to delete
	 * @return true if successful
	 */
	public static boolean deleteFolder(File folder) {
		if (!folder.isDirectory())
			return false;
		if (!cleanFolder(folder))
			return false;
		return folder.delete();
	}
	
	/**
	 * Deletes all the contents of the folder (but preserves the empty folder)
	 * @param folder the folder to clean
	 * @return true if successful
	 */
	public static boolean cleanFolder(File folder) {
		if (!folder.isDirectory())
			return false;
	    File[] files = folder.listFiles();
	    if(files!=null) { 
	        for(File f: files) {
	            if (f.isDirectory()) {
	            	if (!deleteFolder(f))
	            		return false;
	            } else {
	            	if (!f.delete())
	            		return false;
	            }
	        }
	    }
	    return true;
	}

	public static boolean equals(Object o1, Object o2) {
		if (o1 == null && o2 == null)
			return true;
		else if (o1 == null || o2 == null)
			return false;
		else
			return o1.equals(o2);
	}
	
	public static long getFolderSize(File directory) {
	    long length = 0;
	    for (File file : directory.listFiles()) {
	        if (file.isFile())
	            length += file.length();
	        else
	            length += getFolderSize(file);
	    }
	    return length;
	}
	
	public static boolean eq(Object s1, Object s2) {
		return (s1 == s2 || (s1 != null && s2 != null && s1.equals(s2)));
	}
	
	public static boolean eqnn(Object s1, Object s2) {
		return (s1 != null && s2 != null && s1.equals(s2));
	}

	private static HashSet<String> loggedOnce = new HashSet<>();
	
	public synchronized static void logOnce(String string) {
		if (!loggedOnce.contains(string)) {
			loggedOnce.add(string);
			System.out.println(string);
		}
	}

}
