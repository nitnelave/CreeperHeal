package com.nitnelave.CreeperHeal.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.nitnelave.CreeperHeal.CreeperHeal;

public class FileUtils {

    public static boolean createNewFile (File f) {
        f.getParentFile ().mkdirs ();
        try
        {
            f.createNewFile ();
        } catch (IOException ex)
        {
            CreeperLog.warning ("[CreeperHeal] Cannot create file " + f.getPath ());
            return false;
        }
        return true;
    }

    public static void copyJarConfig (File file) {
        copyJarConfig (file, file.getName ());
    }

    public static void copyJarConfig (File file, String source) {
        OutputStream outStream = null;
        try
        {
            if (createNewFile (file))
                return;
            InputStream templateIn = CreeperHeal.getInstance ().getResource (source);
            outStream = new FileOutputStream (file);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = templateIn.read (bytes)) != -1)
                outStream.write (bytes, 0, read);

            templateIn.close ();
            outStream.flush ();
            outStream.close ();
            CreeperLog.logInfo ("[CreeperHeal] Default config created for file " + file.getName (), 1);

        } catch (Exception e)
        {
            CreeperLog.severe ("[CreeperHeal] Failed to create file: " + file.getName ());
            e.printStackTrace ();
        }
    }
}
