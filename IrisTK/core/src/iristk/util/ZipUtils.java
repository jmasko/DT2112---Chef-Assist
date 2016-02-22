package iristk.util;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class ZipUtils {

	public static void zipDirectories(List<File> directories, File baseDirectory, String zipFile) throws IOException {
		zipDirectories(directories, baseDirectory, new FileOutputStream(zipFile), null);
	}
	
	public static void zipDirectories(List<File> directories, File baseDirectory, OutputStream out, String nameFilter) throws IOException {
		String basePath = baseDirectory.getAbsolutePath();
		
		List<String> fileList = new ArrayList<>();
		for (File directory : directories) {
			generateFileList(directory, fileList);
		}

		byte[] buffer = new byte[1024];

		ZipOutputStream zos = new ZipOutputStream(out);

		for(String file : fileList){
			
			if (!file.startsWith(basePath))
				throw new IOException("Base path '" + basePath + "' not found in '" + file + "'");
			
			if (nameFilter != null && new File(file).getName().matches(nameFilter))
				continue;
			
			String zipFile = file.substring(basePath.length() + 1, file.length());

			//System.out.println(zipFile);
			ZipEntry ze= new ZipEntry(zipFile);
			zos.putNextEntry(ze);

			FileInputStream in = new FileInputStream(file);

			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}

			in.close();
		}

		zos.closeEntry();
		zos.close();
	}

	private static void generateFileList(File node, List<String> fileList){

		if(node.isFile()){
			fileList.add(node.getAbsolutePath());
		}

		if(node.isDirectory()){
			String[] subNote = node.list();
			for(String filename : subNote){
				generateFileList(new File(node, filename), fileList);
			}
		}

	}

}
