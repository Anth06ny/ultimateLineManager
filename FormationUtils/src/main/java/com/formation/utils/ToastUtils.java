/**
 * (C)opyright 2012 - UrbanPulse - All rights Reserved Released by CARDIWEB File : ToastUtils.java
 *
 * @date 13 juil. 2012
 * @author afouques
 */

package com.formation.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/** @author afouques */
public class ToastUtils {

    public static Toast toast;
    public static int lastToastTaskId;


    /**
     * Envoie sur lUIThread s'il y a un UIThread
     * @param context
     * @param message
     * @param length
     */
    public static void showToastOnUIThread(final Context context, final String message, final int length) {

        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    showToast(context, message, length);
                }
            });
        }
        else {
            showToast(context, message, length);
        }


    }

    public static void showToast(final Context context, final String message, final int length){
        //on efface l'ancien pour eviter d'en avoir 50 en attente
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, message, length);
        toast.show();
    }

    public static void showToastOnUIThread(final Context context, final int messageId) {
        showToastOnUIThread(context, context.getResources().getString(messageId), Toast.LENGTH_LONG);
    }

    public static void showToastOnUIThread(final Context context, final String message) {
        showToastOnUIThread(context, message, Toast.LENGTH_LONG);
    }

    public static void showToastOnUIThread(final Context context, final int messageId, final int length) {
        showToastOnUIThread(context, context.getResources().getString(messageId), length);
    }

}
