package com.nitnelave.CreeperHeal.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.nitnelave.CreeperHeal.CreeperHeal;

public class FileUtils {

    public static boolean createNewFile (File f) {
        f.delete ();
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
        if (!createNewFile (file))
            return;
        InputStream in = CreeperHeal.getInstance ().getResource (source);
        if (in == null)
            throw new IllegalArgumentException ("The embedded resource '" + source + "' cannot be found.");
        try
        {
            OutputStream out = new FileOutputStream (file);

            int read;
            byte[] buf = new byte[1024];

            while ((read = in.read (buf)) != -1)
                out.write (buf, 0, read);

            in.close ();
            out.flush ();
            out.close ();
            CreeperLog.logInfo ("[CreeperHeal] Defaults loaded for file " + file.getPath (), 1);
        } catch (IOException ex)
        {
            CreeperLog.warning ("Error copying file from jar : " + source);
            ex.printStackTrace ();
        }

    }
}
