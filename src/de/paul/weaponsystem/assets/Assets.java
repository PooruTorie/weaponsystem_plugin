package de.paul.weaponsystem.assets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Assets {
	
	public static InputStream getFile(String f) {
		return Assets.class.getResourceAsStream(f);
	}

	public static void copyFile(File f, String fileInJar) {
		try {
			if (!f.exists()) {
				f.getParentFile().mkdirs();
				f.createNewFile();
			}
			InputStream stream = getFile(fileInJar);
			if (stream != null) {
				OutputStream w = new FileOutputStream(f);
				
				while (stream.available() > 0) {
					w.write(stream.read());
				}
				
				stream.close();
				w.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void loadFolder(String jarFolderPath, File folder) {
		try {
			if (!jarFolderPath.startsWith("[\\/]")) {
				jarFolderPath = "/"+jarFolderPath;
			}
			
			File jarFile = new File(Assets.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			JarFile jar = new JarFile(jarFile);
		    Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				String name = entries.nextElement().getName();
				String classDir = Assets.class.getPackage().getName().replace('.', '/');
				String path = classDir+jarFolderPath;
				if (name.startsWith(path)) {
					if (!name.endsWith("/")) {
						copyFile(new File(folder, "/"+name.replace(path, "")), name.replace(classDir, "").substring(1));
					}
				}
			}
		    jar.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
