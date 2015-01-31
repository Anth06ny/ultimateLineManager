package com.formation.utils;

/**
* (C)opyright 2012 - UrbanPulse - All rights Reserved Released by CARDIWEB File : OneTimePopup.java
*
* @date 30 mai 2012
* @author mbaroukh
*/

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/** Gestion des boite de dialogue. */
public class PopupsManager {

    public static Dialog createProgressPopup(final Activity activity, final String bodyText) {

        final Dialog dialog = new Dialog(activity);

        // Get the layout inflater
        final LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_progress, null);

        ((TextView) dialogView.findViewById(R.id.textView1)).setText(bodyText);

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(final DialogInterface arg0, final int keyCode, final KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                }
                return true;
            }
        });

        // Show Dialog
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(dialogView);



        return dialog;
    }

    public static void showPopup(final Activity activity, final String message, final View.OnClickListener oclButtonOk) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Dialog dialog = new Dialog(activity);
                final View promptsView = activity.getLayoutInflater().inflate(R.layout.my_alerte_dialog, null);

                final TextView tv_title = (TextView) promptsView.findViewById(R.id.tv_title);
                final TextView tv_message = (TextView) promptsView.findViewById(R.id.tv_message);
                final Button bt_valider = (Button) promptsView.findViewById(R.id.bt_valider);
                final Button bt_annuler = (Button) promptsView.findViewById(R.id.bt_annuler);
                final ImageView iv_retry = (ImageView) promptsView.findViewById(R.id.iv_retry);
                final ImageView iv_cancel = (ImageView) promptsView.findViewById(R.id.iv_cancel);

                tv_message.setText(message);

                bt_annuler.setVisibility(View.GONE);
                iv_cancel.setVisibility(View.GONE);

                bt_valider.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View v) {
                        if (oclButtonOk != null) {
                            oclButtonOk.onClick(v);
                        }
                        dialog.dismiss();

                    }
                });

                // create alert dialog and show it
                dialog.setCanceledOnTouchOutside(false);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(promptsView);
                dialog.show();
            }
        });

    }

}
