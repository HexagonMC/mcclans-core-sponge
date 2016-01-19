package nl.riebie.mcclans.utils;

import java.io.*;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public class FileUtils {

    public static void copyFile(File source, File dest) throws IOException {
        if (!source.exists()) {
            source.createNewFile();
        }
        if (!dest.exists()) {
            dest.createNewFile();
        }

        try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }

    public static void moveFile(File source, File dest) throws IOException {
        copyFile(source, dest);
        source.delete();
    }

    public static void removeFolder(File folder) {
        if (folder.isDirectory()) {
            if (folder.listFiles().length == 0) {
                folder.delete();
            } else {
                for (File file : folder.listFiles()) {
                    if (file.isDirectory()) {
                        removeFolder(file);
                    } else {
                        file.delete();
                    }
                }
                folder.delete();
            }
        }
    }

    public static File getLastModifiedFileInFolder(File folder) {
        File[] files = new File[0];
        files = folder.listFiles();

        File lastModifiedFile = null;

        if (files != null)
            for (File file : files) {
                if (lastModifiedFile == null || file.lastModified() > lastModifiedFile.lastModified()) {
                    lastModifiedFile = file;
                }
            }

        return lastModifiedFile;
    }
}
