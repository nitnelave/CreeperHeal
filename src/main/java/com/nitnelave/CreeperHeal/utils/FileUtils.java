package com.nitnelave.CreeperHeal.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.nitnelave.CreeperHeal.CreeperHeal;

/**
 * A class to handle common file operations.
 */
public class FileUtils {

    /**
     * Creates a file, making parent directories if necessary.
     * 
     * @param file
     *            The file to be created.
     * @return True, if successful
     */
    public static boolean createNewFile(File file) {
        file.delete();
        file.getParentFile().mkdirs();
        try
        {
            file.createNewFile();
        } catch (IOException ex)
        {
            CreeperLog.warning("[CreeperHeal] Cannot create file " + file.getPath());
            return false;
        }
        return true;
    }

    /**
     * Copy a resource from the jar to the CreeperHeal folder.
     * 
     * @param file
     *            The file to be copied.
     */
    public static void copyJarConfig(File file) {
        copyJarConfig(file, file.getName());
    }

    /**
     * Copy a resource form the jar to the destinatino file.
     * 
     * @param file
     *            The destination file.
     * @param source
     *            The source file's name.
     */
    public static void copyJarConfig(File file, String source) {
        if (!createNewFile(file))
            return;
        InputStream in = CreeperHeal.getInstance().getResource(source);
        if (in == null)
            throw new IllegalArgumentException("The embedded resource '" + source
                                               + "' cannot be found.");
        try
        {
            OutputStream out = new FileOutputStream(file);

            int read;
            byte[] buf = new byte[1024];

            while ((read = in.read(buf)) != -1)
                out.write(buf, 0, read);

            out.flush();
            out.close();
            CreeperLog.logInfo("[CreeperHeal] Defaults loaded for file " + file.getPath(), 1);
        } catch (IOException ex)
        {
            CreeperLog.warning("Error copying file from jar : " + source);
            ex.printStackTrace();
        }

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
