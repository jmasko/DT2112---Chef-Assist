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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import iristk.util.ProcessRunner;
import iristk.util.ProcessRunner.ProcessListener;
import iristk.util.StringUtils;
import iristk.util.Utils;
import iristk.util.ZipUtils;
import iristk.xml._package.Package.Classpath.Lib;
import iristk.xml._package.Package.Classpath.Src;
import iristk.xml._package.Package.Run.Command;

public class IrisCmd {

	private static String[] cropArgs(String[] args, int n) {
		if (n > args.length) n = args.length;
		String[] result = new String[args.length - n];
		System.arraycopy(args, n, result, 0, result.length);
		return result;
	}

	public static void main(String[] args) throws Throwable {
		try {
			if (args.length == 1 && args[0].equals("install")) {
				install();
				return;
			}
			//if (System.getenv("IrisTK") == null) {
			//	System.out.println("IrisTK not installed");
			//	install();
			//	return;
			//}
			if (args.length == 0) {
				listCommands();
			} else {
				runCommand(args[0], cropArgs(args, 1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void install() {
		try {
			if (!new File(new File(System.getProperty("user.dir")), "iristk.exe").exists()) {
				System.out.println("ERROR: Your must be in the IrisTK root folder when installing");
				System.exit(0);
			}
			System.out.println("Installing IrisTK...\n");
			System.out.println("Setting %IrisTK% to " + System.getProperty("user.dir") + "\n");
			setEnv("IrisTK", System.getProperty("user.dir"));
			String path = getEnv("PATH");
			if (path == null || !path.contains("%IrisTK%")) {
				System.out.println("Adding %IrisTK% to %PATH%\n");
				setEnv("PATH", path + ";%IrisTK%");
			}
			IrisUtils.setIristkPath(System.getProperty("user.dir"));
			setupEclipse(null);
			System.out.println("\nPress enter to continue...");
			Scanner keyboard = new Scanner(System.in);
			keyboard.nextLine();
		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
		}
	}

	public static String getEnv(String name) throws Exception {
		String value = ProcessRunner.eval("reg query HKCU\\Environment /v " + name).trim();
		for (String line : value.split("\n")) {
			Matcher m = Pattern.compile(name + " +[^ ]+ +(.*)", Pattern.CASE_INSENSITIVE).matcher(line.trim());
			if (m.matches())
				return m.group(1);
		}
		return null;
	}

	public static void setEnv(String name, String value) throws Exception {
		//value = value.replace("%", "~");
		//String result = getCmd("reg add HKCU\\Environment /f /v " + name + " /t " + type + " /d \"" + value + "\"").trim();
		String result = ProcessRunner.eval("setx " + name + " \"" + value + "\"").trim();
		//if (!result.contains("successfully")) {
		if (!result.contains("SUCCESS")) {
			throw new Exception(result);
		}
	}

	/*
	public static void setupEclipse(String[] buildPackages) {
		try {
			System.out.println("Setting up Eclipse classpath\n");
			for (String packName : IrisUtils.getPackageNames()) {
				iristk.xml._package.Package pack = IrisUtils.getPackage(packName);
				
				String path = "";
				File projPath;
				if (packName.equals(IrisUtils.CORE_PACKAGE)) {
					projPath = IrisUtils.getIristkPath();
					path = IrisUtils.CORE_PACKAGE + "/";
				} else {
					projPath = IrisUtils.getPackagePath(packName);
				}

				System.out.println(packName);
				if (pack.getClasspath() != null) {

					PrintWriter pj = new PrintWriter(new File(projPath, ".project")); 
					pj.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
					pj.println("<projectDescription>");
					pj.println("<name>IrisTK - " + packName + "</name>");
					pj.println("<comment></comment>");
					pj.println("<projects></projects>");
					pj.println("<buildSpec>");
					pj.println("<buildCommand>");
					pj.println("<name>org.eclipse.jdt.core.javabuilder</name>");
					pj.println("<arguments></arguments>");
					pj.println("</buildCommand>");
					pj.println("</buildSpec>");
					pj.println("<natures>");
					pj.println("<nature>org.eclipse.jdt.core.javanature</nature>");
					pj.println("</natures>");
					pj.println("</projectDescription>");
					pj.close();
					
					PrintWriter cp = new PrintWriter(new File(projPath, ".classpath")); 
					cp.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
					cp.println("<classpath>");
					cp.println("<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>");
					for (Object entry : pack.getClasspath().getLibOrSrcOrDll()) {
						if (entry instanceof Lib) {
							cp.println("<classpathentry exported=\"true\" kind=\"lib\" path=\"" + path + ((Lib)entry).getPath() + "\"/>");
							//System.out.println(((Lib)entry).getPath());
						} else if (entry instanceof Src) {
							boolean build = false;
							if (IrisUtils.getPackagePath(pack.getName(), "src").exists()) {
								build = true;
								if (buildPackages != null && buildPackages.length > 0) {
									build = false;
									for (String buildPackage : buildPackages) {
										if (buildPackage.equalsIgnoreCase(pack.getName())) {
											build = true;
											break;
										}
									}
								}
							}
							if (build) {
								cp.println("<classpathentry kind=\"src\" output=\"" + path + ((Src)entry).getOutput() + "\" path=\"" + path + ((Src)entry).getPath() + "\"/>");
								//System.out.println(pad(pack.getName(), 40) + "Source folder");
							} else {
								cp.println("<classpathentry kind=\"lib\" path=\"" + path + ((Src)entry).getOutput() + "\"/>");
								//System.out.println(pad(pack.getName(), 40) + "Class folder");
							}
						}
					}
					//cp.println("<classpathentry kind=\"output\" path=\"" + path + "bin\"/>");
					//cp.println("</classpath>");
					cp.close();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	 */
	
	public static void setupEclipse(String[] buildPackages) {
		try {
			System.out.println("Setting up Eclipse classpath\n");
			PrintWriter pw = new PrintWriter(new File(IrisUtils.getIristkPath(), ".classpath")); 
			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			pw.println("<classpath>");
			pw.println("<classpathentry kind=\"con\" path=\"org.eclipse.jdt.launching.JRE_CONTAINER\"/>");
			String root = IrisUtils.getIristkPath().getAbsolutePath() + "\\";
			for (iristk.xml._package.Package pack : IrisUtils.getPackages()) {
				//System.out.println(pack.getName());
				if (pack.getClasspath() != null) {
					if (!IrisUtils.getPackagePath(pack.getName()).getAbsolutePath().startsWith(root))
						continue;
					String path = IrisUtils.getPackagePath(pack.getName()).getAbsolutePath().replace(root, "").replace("\\", "/") + "/";
					for (Object entry : pack.getClasspath().getLibOrSrcOrDll()) {
						if (entry instanceof Lib) {
							pw.println("<classpathentry exported=\"true\" kind=\"lib\" path=\"" + path + ((Lib)entry).getPath() + "\"/>");
							//System.out.println(((Lib)entry).getPath());
						} else if (entry instanceof Src) {
							boolean build = false;
							if (IrisUtils.getPackagePath(pack.getName(), "src").exists()) {
								build = true;
								if (buildPackages != null && buildPackages.length > 0) {
									build = false;
									for (String buildPackage : buildPackages) {
										if (buildPackage.equalsIgnoreCase(pack.getName())) {
											build = true;
											break;
										}
									}
								}
							}
							if (build) {
								pw.println("<classpathentry kind=\"src\" output=\"" + path + ((Src)entry).getOutput() + "\" path=\"" + path + ((Src)entry).getPath() + "\"/>");
								//System.out.println(pad(pack.getName(), 40) + "Source folder");
							} else {
								pw.println("<classpathentry kind=\"lib\" path=\"" + path + ((Src)entry).getOutput() + "\"/>");
								//System.out.println(pad(pack.getName(), 40) + "Class folder");
							}
						}
					}
				}
			}
			pw.println("<classpathentry kind=\"output\" path=\"core/bin\"/>");
			pw.println("</classpath>");
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void compileFlow(String[] args) {
		try {
			//, new File(System.getProperty("user.dir")
			runJava(false, "iristk.flow.FlowCompiler", args, System.out, System.err, true, null, null);
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	public static void  zipPackage(String[] args) {
		if (args.length == 0) {
			System.err.println("No packages provided");
			System.exit(0);
		}
		List<String> packages = new ArrayList<>();
		List<File> packageFolders = new ArrayList<>();
		String filename = "";
		OUTER:
			for (String pack : args) {
				for (String _pack : IrisUtils.getPackageNames()) {
					if (pack.equalsIgnoreCase(_pack)) {
						packages.add(_pack);
						packageFolders.add(IrisUtils.getPackagePath(_pack));
						if (filename.length() > 0)
							filename += "_";
						filename += _pack;
						continue OUTER;
					}
				}
				System.err.println("Package " + pack + " not found");
				System.exit(0);
			}
		filename += ".zip";
		try {
			//TODO: zip all packages (not just the first)
			ZipUtils.zipDirectories(packageFolders, IrisUtils.getIristkPath(), filename);
			System.out.println("\nCreated zip: " + filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void listCommands() {
		System.out.println("IrisTK version " + IrisUtils.getVersion() + " installed at " + IrisUtils.getIristkPath());
		System.out.println();
		System.out.println("Usage:");
		System.out.println("iristk [command] [args]\n");
		System.out.println("Available commands in packages:");
		for (String pname : IrisUtils.getPackageNames()) {
			if (IrisUtils.getPackage(pname).getRun() != null) {
				System.out.println("\n" + pname + ":");
				for (Command command : IrisUtils.getPackage(pname).getRun().getCommand()) {
					String descr = "";
					if (command.getDescr() != null) descr = command.getDescr();
					System.out.println("  " + pad(command.getName(), 15) + descr);
				}
			}
		}
	}

	private static String pad(String str, int len) {
		for (int i = str.length(); i < len; i++) {
			str += " ";
		}
		return str;
	}

	public static void listPackages(String[] args) {
		for (String pname : IrisUtils.getPackageNames()) {
			System.out.println(pad(pname, 20) + IrisUtils.getPackagePath(pname));
		}
	}

	private static void runCommand(String progName, String[] args) throws Throwable {
		setupClasspath();

		Command command = findCommand(progName);
		if (command != null) {
			//File progDir = IrisUtils.getPackagePath(program.getName());
			if (command.getMethod() != null) {
				Class<?>[] paramTypes = new Class<?>[1];
				paramTypes[0] = String[].class; 
				Method method = Class.forName(command.getClazz()).getMethod(command.getMethod(), paramTypes);
				if (method != null) {
					try {
						method.invoke(null, (Object) args);
					} catch (InvocationTargetException e) {
						throw e.getTargetException();
					}
					return;
				}
			} else {
				//runJava(command.isArch32(), command.getClazz(), args, System.out, System.err, true, null);

				Class<?>[] paramTypes = new Class<?>[1];
				paramTypes[0] = String[].class;
				Method method = Class.forName(command.getClazz()).getMethod("main", paramTypes);
				if (method != null) {
					try {
						method.invoke(null, (Object) args);
					} catch (InvocationTargetException e) {
						throw e.getTargetException();
					}
					return;
				}

				return;

			}
		}

		//runJava(false, progName, args, System.out, System.err, true, null);
		System.out.println("No command with the name '" + progName + "' found");
	}

	private static void setupClasspath() throws Throwable {
		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class sysclass = URLClassLoader.class;
		Method method = sysclass.getDeclaredMethod("addURL", new Class[] {URL.class});
		method.setAccessible(true);
		for (iristk.xml._package.Package pack : IrisUtils.getPackages()) {
			if (pack.getClasspath() != null) {
				for (Object entry : pack.getClasspath().getLibOrSrcOrDll()) {
					if (entry instanceof Lib) {
						method.invoke(sysloader, new Object[] {new File(IrisUtils.getPackagePath(pack.getName()), ((Lib)entry).getPath()).toURI().toURL()});
					} else if (entry instanceof Src) {
						method.invoke(sysloader, new Object[] {new File(IrisUtils.getPackagePath(pack.getName()), ((Src)entry).getOutput()).toURI().toURL()});
					}
				} 
			}
		}
	}

	private static Command findCommand(String cmdName) {
		for (String packageName : IrisUtils.getPackageNames()) {
			if (IrisUtils.getPackage(packageName).getRun() != null) {
				for (Command cmd : IrisUtils.getPackage(packageName).getRun().getCommand()) {
					if (cmd.getName().equals(cmdName) || (packageName + "." + cmd.getName()).equals(cmdName)) {
						return cmd;
					}
				}
			}
		}
		return null;
	}

	private static String getJavaCmd(boolean force32) throws Exception {
		if (force32 && System.getProperty("sun.arch.data.model").equals("64")) {
			File javaDir = new File(System.getenv("ProgramFiles(x86)"),  "Java");
			if (javaDir.exists()) {
				String[] versions = new String[]{"jdk1.8", "jre8", "jre1.8"};
				for (String v : versions) {
					for (File f : javaDir.listFiles()) {
						if (f.getName().startsWith(v)) {
							File javaexe = new File(new File(f.getPath(), "bin"), "java.exe");
							if (javaexe.exists()) {
								return javaexe.getAbsolutePath();
							}
						}
					}
				}
			}
			throw new Exception("Could not find Java 32-bit runtime");
		} else {
			File javaexe = new File(new File(new File(System.getProperty("java.home")), "bin"), "java.exe");
			if (javaexe.exists()) {
				return javaexe.getAbsolutePath();
			}
			throw new Exception("Could not find Java runtime");
		}
	}

	public static void cleanTempFolder(String[] args) {
		if (IrisUtils.getTempDir().exists()) {
			if (Utils.cleanFolder(IrisUtils.getTempDir())) {
				System.out.println("Succeeded in cleaning folder: " + IrisUtils.getTempDir());
			} else {
				System.out.println("Did not succeed to clean folder: " + IrisUtils.getTempDir());
			}
		} else  {
			System.out.println("Temporary folder not found: " + IrisUtils.getTempDir());
		}
	}

	public static void createFromTemplate(String[] args) {
		try {
			ZipFile zipFile = new ZipFile(new File(IrisUtils.getPackageLibPath("core"), "templates.zip"));
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			if (args.length < 2) {
				System.out.println("Usage: iristk create [template] [name]\n");
				System.out.println("Available templates:");

				while(entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					if (entry.getName().matches("[^/]+/descr.txt")) {
						String templ = entry.getName().replace("/descr.txt", "");
						String descr = Utils.readTextFile(zipFile.getInputStream(entry));
						System.out.println("  " + pad(templ, 20) + descr);
					}
				}
			} else {
				String templateName = args[0];
				String templateIdLC = "$" + StringUtils.lcFirst(templateName) + "$";
				String templateIdUC = "$" + StringUtils.ucFirst(templateName) + "$";
				String name = args[1];
				boolean found = false;
				while(entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					if (entry.getName().startsWith(templateName + "/")) {
						found = true;
						String entryName = entry.getName().replaceFirst(templateName + "/", "");
						if (entryName.equals("descr.txt")) continue;
						entryName = entryName.replace(templateIdLC, StringUtils.lcFirst(name));
						entryName = entryName.replace(templateIdUC, StringUtils.ucFirst(name));
						//entryName = entryName.replace("/_", "/");
						if(entry.isDirectory()) {
							if (!IrisUtils.getIristkPath(entryName).exists()) {
								System.out.println("Creating directory: " + IrisUtils.getIristkPath(entryName).getAbsolutePath());
								IrisUtils.getIristkPath(entryName).mkdirs();
							}
						} else {
							System.out.println("Creating file: " + IrisUtils.getIristkPath(entryName).getAbsolutePath());
							InputStream in = zipFile.getInputStream(entry);
							Scanner s = new Scanner(in).useDelimiter("\\A");
							String content = s.hasNext() ? s.next() : "";
							content = content.replace(templateIdLC, StringUtils.lcFirst(name));
							content = content.replace(templateIdUC, StringUtils.ucFirst(name));
							OutputStream out = new BufferedOutputStream(new FileOutputStream(IrisUtils.getIristkPath(entryName)));
							out.write(content.getBytes());
							in.close();
							out.close();
						}
					}
				}
				zipFile.close();
				if (!found) {
					System.out.println("Couldn't find template '" + templateName + "'");
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return;
		}
	}

	public static ProcessRunner runJava(boolean force32, String mainClass, String[] args, OutputStream stdout, OutputStream stderr, boolean waitFor, ProcessListener listener, File workingDir) throws Exception {
		List<String> cmd = new ArrayList<>();
		cmd.add(getJavaCmd(force32));
		cmd.add("-cp");
		cmd.add(IrisUtils.getClasspath());
		cmd.add(mainClass);
		for (String arg : args) {
			cmd.add(arg);
		}
		//String cmd = "\"" + javaCmd + "\" -cp \"" + IrisUtils.getClasspath() + "\" " + mainClass + " " + StringUtils.join(args, " ");
		ProcessRunner proc = new ProcessRunner(cmd, stdout, stderr, listener, workingDir);
		if (waitFor)
			proc.waitFor();
		return proc;
	}

}
