package com.ultimatelinemanager.metier;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.formation.utils.LogUtils;
import com.formation.utils.ToastUtils;
import com.formation.utils.Utils;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.bean.Role;
import com.ultimatelinemanager.dao.PlayerDaoManager;

import org.apache.commons.lang3.StringUtils;

import greendao.PlayerBean;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class DialogUtils {

    /* ---------------------------------
    // génériqeue
    // -------------------------------- */

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

        //On desactive le button tant que la valeur entrée est vide
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

    public static MaterialDialog getConfirmDialog(Context context, int iconId, int titleId, String content, MaterialDialog.ButtonCallback callback) {
        Drawable d = context.getResources().getDrawable(iconId);
        d.setColorFilter(Utils.getColorFromTheme(context, com.formation.utils.R.attr.color_composant_main), PorterDuff.Mode.MULTIPLY);

        return new MaterialDialog.Builder(context).title(titleId).icon(d).positiveText(R.string.delete).negativeText(android.R.string.cancel)
                .content(content).callback(callback).build();

    }

    public static MaterialDialog getOkDialog(Context context, int iconId, int titleId, String content) {

        Drawable d = context.getResources().getDrawable(iconId);
        d.setColorFilter(Utils.getColorFromTheme(context, com.formation.utils.R.attr.color_composant_main), PorterDuff.Mode.MULTIPLY);

        return new MaterialDialog.Builder(context).title(titleId).icon(d).positiveText(com.formation.utils.R.string.ok).content(content).build();

    }

    /* ---------------------------------
    // Spéciphique
    // -------------------------------- */

    /**
     * Permet de créer un joueur
     * @param context
     * @param titleId
     * @param positiveTextId
     * @param callBack
     * @return
     */
    public static MaterialDialog getNewPlayerDialog(final Context context, int titleId, int positiveTextId, final NewPlayerPromptDialogCB callBack) {

        Drawable d = context.getResources().getDrawable(R.drawable.ic_action_user_add);
        d.setColorFilter(Utils.getColorFromTheme(context, com.formation.utils.R.attr.color_composant_main), PorterDuff.Mode.MULTIPLY);

        MaterialDialog dialog = new MaterialDialog.Builder(context).title(titleId).icon(d).customView(R.layout.dialog_new_player, false)
                .positiveText(positiveTextId).autoDismiss(false).negativeText(android.R.string.cancel).callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (callBack != null) {
                            PlayerBean playerBean = new PlayerBean();

                            EditText np_name = (EditText) dialog.getCustomView().findViewById(R.id.np_name);
                            playerBean.setName(np_name.getText().toString());

                            final CheckBox np_cb_handler = (CheckBox) dialog.getCustomView().findViewById(R.id.np_cb_handler);
                            final CheckBox np_cb_middle = (CheckBox) dialog.getCustomView().findViewById(R.id.np_cb_middle);

                            if (np_cb_handler.isChecked() && np_cb_middle.isChecked()) {
                                playerBean.setRole(Role.Both.name());
                            }
                            else if (np_cb_handler.isChecked()) {
                                playerBean.setRole(Role.Handler.name());
                            }
                            else if (np_cb_middle.isChecked()) {
                                playerBean.setRole(Role.Middle.name());
                            }

                            playerBean.setSexe(((RadioButton) dialog.getCustomView().findViewById(R.id.np_rb_boy)).isChecked());

                            try {

                                if (!PlayerDaoManager.existPlayer(playerBean.getName())) {
                                    callBack.newPlayerpromptDialogCB_onPositiveClick(playerBean);
                                    dialog.dismiss();
                                }
                                else {
                                    //Si le nom existe déjà.
                                    ToastUtils.showToastOnUIThread(context, R.string.lpt_player_exist);
                                }
                            }
                            catch (Throwable e) {
                                LogUtils.logException(getClass(), e, true);
                                ToastUtils.showToastOnUIThread(context, R.string.erreur_generique);
                                return;
                            }
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                }).build();

        //On desactive le button tant que la valeur entrée est vide
        final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        positiveAction.setEnabled(false);

        final EditText np_name = (EditText) dialog.getCustomView().findViewById(R.id.np_name);
        final CheckBox np_cb_handler = (CheckBox) dialog.getCustomView().findViewById(R.id.np_cb_handler);
        final CheckBox np_cb_middle = (CheckBox) dialog.getCustomView().findViewById(R.id.np_cb_middle);
        final RadioGroup np_rg_sexe = (RadioGroup) dialog.getCustomView().findViewById(R.id.np_rg_sexe);

        //l'edit text
        np_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                showPositiveButton(np_name, np_cb_handler, np_cb_middle, np_rg_sexe, positiveAction);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //les checkbox
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showPositiveButton(np_name, np_cb_handler, np_cb_middle, np_rg_sexe, positiveAction);
            }
        };
        np_cb_handler.setOnCheckedChangeListener(listener);
        np_cb_middle.setOnCheckedChangeListener(listener);
        np_rg_sexe.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                showPositiveButton(np_name, np_cb_handler, np_cb_middle, np_rg_sexe, positiveAction);
            }
        });

        return dialog;
    }

    /**
     * affiche ou non le bouton valider
     * @param name
     * @param handler
     * @param middle
     * @param radioButton
     * @param positiveAction
     */
    private static void showPositiveButton(EditText name, CheckBox handler, CheckBox middle, RadioGroup radioButton, View positiveAction) {

        boolean showPositiveButton = true;
        //Name
        if (StringUtils.isBlank(name.getText())) {
            showPositiveButton = false;
        }
        //Aucune checkBox séléctionné
        if (!handler.isChecked() && !middle.isChecked()) {
            showPositiveButton = false;
        }
        //Sexe
        if (radioButton.getCheckedRadioButtonId() == -1) {
            showPositiveButton = false;
        }

        positiveAction.setEnabled(showPositiveButton);

    }

    public interface NewPlayerPromptDialogCB {
        public void newPlayerpromptDialogCB_onPositiveClick(PlayerBean playerBean);
    }

    /* ---------------------------------
    // interface
    // -------------------------------- */

    public interface PromptDialogCB {
        public void promptDialogCB_onPositiveClick(String promptText);

    }
}
