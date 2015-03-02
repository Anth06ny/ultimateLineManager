package com.ultimatelinemanager.metier;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.formation.utils.Utils;
import com.ultimatelinemanager.R;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class DialogUtils {

    /**
     * Affiche une fenetre de dialog pour demander qque chose
     * @param context
     * @param iconId
     * @param titleId
     * @param positiveTextId
     * @param callBack
     * @return
     */
    public static MaterialDialog getPromptDialog(Context context, int iconId, int titleId, int positiveTextId, String editTextValue,
            final PromptDialogCB callBack) {

        EditText dp_et;
        final View positiveAction;

        Drawable d = context.getResources().getDrawable(iconId);
        d.setColorFilter(Utils.getColorFromTheme(context, com.formation.utils.R.attr.color_composant_main), PorterDuff.Mode.MULTIPLY);

        MaterialDialog dialog = new MaterialDialog.Builder(context).title(titleId).icon(d).customView(R.layout.dialog_prompt, false)
                .positiveText(positiveTextId).negativeText(android.R.string.cancel).callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (callBack != null) {
                            EditText editText = (EditText) dialog.getCustomView().findViewById(R.id.dp_et);
                            callBack.promptDialogCB_onPositiveClick(editText.getText().toString());
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                    }
                }).build();

        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        positiveAction.setEnabled(false);

        dp_et = (EditText) dialog.getCustomView().findViewById(R.id.dp_et);
        dp_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        if (StringUtils.isNotBlank(editTextValue)) {
            dp_et.setText(editTextValue);
        }

        return dialog;
    }

    public interface PromptDialogCB {
        public void promptDialogCB_onPositiveClick(String promptText);

    }
}
