package com.formation.utils;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.TypedValue;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class Utils {

    public static int getColorFromTheme(Context context, int attr_id) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr_id, typedValue, true);
        return typedValue.data;
    }

    public static void CopySQLiteBaseToDownload(Context context, String tableName) {

        File database = new File("data/data/" + context.getPackageName() + "/databases/" + tableName);
        File downloadDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "/" + tableName);

        try {

            if (database.exists()) {

                if (!downloadDirectory.exists()) {
                    downloadDirectory.createNewFile();
                }

                InputStream in = new FileInputStream(database);
                OutputStream out = new FileOutputStream(downloadDirectory);

                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                in.close();
                out.close();

                Toast.makeText(context, "Les fichiers ont été copiés", Toast.LENGTH_LONG).show();

            }
            else {
                Toast.makeText(context, "Erreur lors de la copie", Toast.LENGTH_LONG).show();
            }

            //copyDirectoryOneLocationToAnotherLocation(userDirectory, downloadDirectory);
            MediaScannerConnection.scanFile(context, new String[] { downloadDirectory.getAbsolutePath() }, null, null);

        }
        catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Erreur lors de la copie", Toast.LENGTH_LONG).show();
        }
    }
}
