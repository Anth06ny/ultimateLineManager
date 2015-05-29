package com.formation.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.text.Html;
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

    public static String timeToHHMM(long time) {

        long minute = time / (1000 * 60);
        long hour = minute / 60;
        minute = minute % 60;

        return hour + "h" + (minute < 10 ? ("0" + minute) : ("" + minute));
    }

    public static String timeToMMSS(long time) {
        long seconde = time / 1000;
        long minute = seconde / 60;
        seconde = seconde % 60;

        return minute + "m" + (seconde < 10 ? ("0" + seconde) : ("" + seconde));
    }

    /**
     * Permet de lancer l'application mail avec des éléments prérempli
     */
    public static void sendMail(Context context, String subject, String text) {

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(text));

        try {
            context.startActivity(Intent.createChooser(i, "Send mail..."));
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
