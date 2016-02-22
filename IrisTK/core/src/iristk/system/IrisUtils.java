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
package iristk.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import iristk.util.NameFilter;
import iristk.xml.XmlMarshaller;
import iristk.xml._package.Package;
import iristk.xml._package.Package.Classpath.Dll;
import iristk.xml._package.Package.Classpath.Lib;
import iristk.xml._package.Package.Classpath.Src;

public class IrisUtils {
	
	public static final String CORE_PACKAGE = "core";
	
	private static HashMap<String,Package> packages;
	private static HashMap<String,File> packagePaths;
	private static HashSet<String> javaLibPaths = new HashSet<>();
	private static String iristkPath = null;
		
	private synchronized static void readPackages() {
		if (packages == null) {
			packages = new HashMap<>();
			packagePaths = new HashMap<>();
			final XmlMarshaller<Package> packageReader = new XmlMarshaller<>("iristk.xml._package");
			
			List<File> dirs = new ArrayList<>();
			dirs.add(new File(getIristkPath(), "core"));
			for (File dir : new File(getIristkPath(), "app").listFiles()) 
				if (dir.isDirectory()) dirs.add(dir);
			for (File dir : new File(getIristkPath(), "addon").listFiles()) 
				if (dir.isDirectory()) dirs.add(dir);
			String extraPaths = ".";
			if (System.getProperty("IrisTK_Extra") != null)
				extraPaths += System.getProperty("IrisTK_Extra");
			if (System.getenv("IrisTK_Extra") != null)
				extraPaths += ";" + System.getenv("IrisTK_Extra");
			for (String extraPath : extraPaths.split(";")) {
				extraPath = extraPath.trim();
				if (extraPath.length() > 0)
					dirs.add(new File(extraPath));
			}
			for (File dir : dirs) {
				File packFile = new File(dir, "package.xml");
				if (packFile.exists()) {
					try {
						Package pack = packageReader.unmarshal(packFile);
						if (pack.getName() != null && !packages.containsKey(pack.getName())) {
							packages.put(pack.getName(), pack);
							packagePaths.put(pack.getName(), packFile.getParentFile().getCanonicalFile());
							//System.out.println(pack.getName() + " " + packagePaths.get(pack.getName()));
						}
					} catch (JAXBException e) {
						e.printStackTrace();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void savePackage(String packageName) {
		final XmlMarshaller<Package> packageWriter = new XmlMarshaller<>("iristk.xml._package");
		File packageFile = getPackagePath(packageName, "package.xml");
		try {
			packageWriter.marshal(getPackage(packageName), packageFile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	public static void updatePackages() {
		packages = null;
		readPackages();
	}

	public static Collection<Package> getPackages() {
		readPackages();
		return packages.values();
	}
	
	public static List<String> getPackageNames() {
		readPackages();
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(packages.keySet());
		list.remove("core");
		list.add(0, "core");
		return list;
	}

	public static Package getPackage(String pname) {
		readPackages();
		return packages.get(pname);
	}
	
	public static File getPackagePath(Class<?> clazz) {
		return getPackagePath(clazz, null);
	}
	
	public static File getPackagePath(String packageName) {
		return getPackagePath(packageName, null);
	}
	
	public static File getPackagePath(String packageName, String path) {
		readPackages();
		if (!hasPackage(packageName))
			return null;
		if (path == null)
			return packagePaths.get(packageName);
		else
			return new File(packagePaths.get(packageName), path);
	}
	
	private static boolean hasPackage(String packageName) {
		return packages.containsKey(packageName);
	}

	public static File getPackagePath(Class<?> clazz, String path) {
		String packName = getPackageName(clazz);
		if (!hasPackage(packName))
			return null;
		if (packName != null)
			return getPackagePath(packName, path);
		else
			return null;
	}
	
	public static String getPackageName(Class<?> clazz) {
		readPackages();
		try {
			String clazzPath = clazz.getClassLoader().getResource(clazz.getName().replace('.', '/') + ".class").toURI().toString();
			if (clazzPath.startsWith("jar:"))
				clazzPath = clazzPath.replaceFirst("jar:", "");
			for (String packName : packagePaths.keySet()) {
				if (clazzPath.toUpperCase().startsWith(packagePaths.get(packName).toURI().toString().toUpperCase())) {
					return packName;
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static File getPackageLibPath(String packageName) {
		return getPackagePath(packageName, "lib");
	}
	
	public static File getPackageLibPath(Class<?> clazz) {
		return getPackagePath(clazz, "lib");
	}
	
	public static void setIristkPath(String path) {
		iristkPath  = path;
	}
	
	public static File getIristkPath() {
		if (iristkPath == null) {
			if (System.getProperty("IrisTK") != null)
				iristkPath = System.getProperty("IrisTK");
			else
				iristkPath = System.getenv("IrisTK");
		}
		if (iristkPath == null)
			throw new RuntimeException("Environment variable 'IrisTK' not set");
		else {
			File iristkPathF = new File(iristkPath);
			if (!iristkPathF.exists()) 
				throw new RuntimeException("Directory " + iristkPath + " does not exist");
			else
				return iristkPathF;
		}
	}
	
	public static File getIristkPath(String path) {
		File pathF = new File(getIristkPath(), path);
		//if (!pathF.exists()) 
		//	throw new RuntimeException("Directory " + pathF.getAbsolutePath() + " does not exist");
		//else
		return pathF;
	}
	
	public static boolean is64arch() {
		return System.getProperty("sun.arch.data.model").equals("64");
	}
	
	public static void loadPackageLib(String packageName, String dll) {
		System.load(new File(getPackageLibPath(packageName), dll).getAbsolutePath());
	}
	
	public static void loadPackageLib(Class<?> clazz, String dll) {
		System.load(new File(getPackageLibPath(clazz), dll).getAbsolutePath());
	}

	public static void loadPackageDlls(Class<?> clazz) {
		loadPackageDlls(getPackageName(clazz));
	}
	
	public static void loadPackageDlls(String packageName) {
		if (packageName == null) return;
		Package pack = getPackage(packageName);
		if (pack.getClasspath() != null) {
			for (Object entry : pack.getClasspath().getLibOrSrcOrDll()) {
				if (entry instanceof Dll) {
					Dll dll = (Dll)entry;
					if (dll.getArch() == null || dll.getArch().equals(System.getProperty("sun.arch.data.model"))) {
						System.load(new File(getPackagePath(packageName), dll.getPath()).getAbsolutePath());
					}
				}
			}
		}
	}
	
	public static String getClasspath() {
		String classpath = "";
		for (Package pack : getPackages()) {
			if (pack.getClasspath() != null) {
				for (Object entry : pack.getClasspath().getLibOrSrcOrDll()) {
					if (entry instanceof Lib) {
						classpath += new File(getPackagePath(pack.getName()), ((Lib)entry).getPath()).getAbsolutePath() + ";";
					} else if (entry instanceof Src) {
						classpath += new File(getPackagePath(pack.getName()), ((Src)entry).getOutput()).getAbsolutePath() + ";";
					}
				} 
			}
		}
		return classpath;
	}
	
	public static void addJavaLibPath(File path) {
		if (!javaLibPaths.contains(path.getAbsolutePath())) {
			try {
				System.setProperty("java.library.path", System.getProperty("java.library.path") + ";" + path.getAbsolutePath());
				Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
				fieldSysPath.setAccessible( true );
				fieldSysPath.set( null, null );
				javaLibPaths.add(path.getAbsolutePath());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		}
	}

	public static File getTempDir() {
		return new File(System.getProperty("java.io.tmpdir") + File.separator + "iristk");
	}
	
	public static File getTempDir(String name) {
		return new File(System.getProperty("java.io.tmpdir") + File.separator + "iristk" + File.separator + name);
	}

	public static List<String> getPackageProvides(String className) {
		NameFilter filter = NameFilter.compile(className);
		ArrayList<String> result = new ArrayList<>();
		for (iristk.xml._package.Package pack : IrisUtils.getPackages()) {
			if (pack.getProvide() != null) {
				for (iristk.xml._package.Package.Provide.Class clazz : pack.getProvide().getClazz()) {
					if (filter.accepts(clazz.getType())) {
						result.add(clazz.getName());
					}
				}
			}
		}
		return result;
	}
	
	public static List<String> getPackageProvides(String packName, String className) {
		NameFilter filter = NameFilter.compile(className);
		Package pack = IrisUtils.getPackage(packName);
		ArrayList<String> result = new ArrayList<>();
		if (pack.getProvide() != null) {
			for (iristk.xml._package.Package.Provide.Class clazz : pack.getProvide().getClazz()) {
				if (filter.accepts(clazz.getType())) {
					result.add(clazz.getName());
				}
			}
		}
		return result;
	}

	
	public static String getVersion() {
		Properties prop = new Properties();
		try {
			prop.load(new FileReader(getIristkPath("iristk.properties")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop.getProperty("version");
	}

	
	public static void addCoreLibPath() {
		if (IrisUtils.is64arch()) {
			IrisUtils.addJavaLibPath(IrisUtils.getPackagePath(IrisUtils.CORE_PACKAGE, "lib/x64"));
		} else {
			IrisUtils.addJavaLibPath(IrisUtils.getPackagePath(IrisUtils.CORE_PACKAGE, "lib/x86"));
		}
	}

}
