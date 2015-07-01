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
import android.widget.NumberPicker;
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
import com.ultimatelinemanager.dao.TeamDaoManager;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

import greendao.PlayerBean;
import greendao.TeamBean;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class DialogUtils {

    /* ---------------------------------
    // génériqeue
    // -------------------------------- */

    /**
     * Affiche une fenetre de dialog pour demander qque chose
     *
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
     *
     * @param context
     * @param titleId
     * @param positiveTextId
     * @param callBack
     * @return
     */
    public static MaterialDialog getNewPlayerDialog(final Context context, final PlayerBean existingPlayer, int titleId, int positiveTextId,
            final NewPlayerPromptDialogCB callBack) {

        Drawable d = context.getResources().getDrawable(R.drawable.ic_action_user_add);
        d.setColorFilter(Utils.getColorFromTheme(context, com.formation.utils.R.attr.color_composant_main), PorterDuff.Mode.MULTIPLY);

        MaterialDialog dialog = new MaterialDialog.Builder(context).title(titleId).icon(d).customView(R.layout.dialog_new_player, false)
                .positiveText(positiveTextId).autoDismiss(false).negativeText(android.R.string.cancel).callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (callBack != null) {

                            PlayerBean playerBean = existingPlayer != null ? existingPlayer : new PlayerBean();

                            //Nom
                            EditText np_name = (EditText) dialog.getCustomView().findViewById(R.id.np_name);
                            playerBean.setName(np_name.getText().toString());

                            //Role
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

                            //Sexe
                            playerBean.setSexe(((RadioButton) dialog.getCustomView().findViewById(R.id.np_rb_boy)).isChecked());

                            //Number
                            NumberPicker np_np_dizaine = (NumberPicker) dialog.findViewById(R.id.np_np_dizaine);
                            NumberPicker np_np_unite = (NumberPicker) dialog.findViewById(R.id.np_np_unite);

                            playerBean.setNumber(np_np_dizaine.getValue() * 10 + np_np_unite.getValue());

                            //Injured
                            CheckBox np_cb_injured = (CheckBox) dialog.getCustomView().findViewById(R.id.np_cb_injured);
                            playerBean.setInjured(np_cb_injured.isChecked());

                            try {
                                //Modification
                                if (existingPlayer != null) {
                                    callBack.newPlayerpromptDialogCB_onPositiveClick(existingPlayer);
                                    dialog.dismiss();
                                }
                                //Création
                                else if (!PlayerDaoManager.existPlayer(playerBean.getName())) {
                                    callBack.newPlayerpromptDialogCB_onPositiveClick(playerBean);
                                    dialog.dismiss();
                                }
                                else {
                                    //Si le nom existe déjà pour un création
                                    ToastUtils.showToastOnUIThread(context, R.string.lpt_player_exist);
                                }
                            }
                            catch (Throwable e) {
                                LogUtils.logException(getClass(), e, true);
                                ToastUtils.showToastOnUIThread(context, R.string.erreur_generique);
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
        final CheckBox np_cb_injured = (CheckBox) dialog.getCustomView().findViewById(R.id.np_cb_injured);
        final RadioGroup np_rg_sexe = (RadioGroup) dialog.getCustomView().findViewById(R.id.np_rg_sexe);

        NumberPicker np_np_dizaine = (NumberPicker) dialog.findViewById(R.id.np_np_dizaine);
        np_np_dizaine.setMaxValue(9);
        np_np_dizaine.setMinValue(0);
        NumberPicker np_np_unite = (NumberPicker) dialog.findViewById(R.id.np_np_unite);
        np_np_unite.setMaxValue(9);
        np_np_unite.setMinValue(0);

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

        //On préremplie si c'est une modification
        if (existingPlayer != null) {
            np_name.setText(existingPlayer.getName());
            switch (Role.getRole(existingPlayer.getRole())) {
                case Handler:
                    np_cb_handler.setChecked(true);
                    break;
                case Middle:
                    np_cb_middle.setChecked(true);
                    break;
                case Both:
                    np_cb_handler.setChecked(true);
                    np_cb_middle.setChecked(true);
                    break;
            }
            if (existingPlayer.getSexe()) {
                ((RadioButton) dialog.getCustomView().findViewById(R.id.np_rb_boy)).setChecked(true);
            }
            else {
                ((RadioButton) dialog.getCustomView().findViewById(R.id.np_rb_girl)).setChecked(true);
            }
            np_np_dizaine.setValue(existingPlayer.getNumber() / 10);
            np_np_unite.setValue(existingPlayer.getNumber() % 10);

            np_cb_injured.setChecked(existingPlayer.getInjured());

        }

        return dialog;
    }

    /**
     * Permet de créer un joueur
     *
     * @param context
     * @return
     */
    public static MaterialDialog getNewTeamDialog(final Context context, final NewTeamPromptDialogCB callBack) {

        Drawable d = context.getResources().getDrawable(R.drawable.ic_action_add_group);
        d.setColorFilter(Utils.getColorFromTheme(context, com.formation.utils.R.attr.color_composant_main), PorterDuff.Mode.MULTIPLY);

        final List<TeamBean> allTeam = TeamDaoManager.getLast30Team();

        MaterialDialog dialog = new MaterialDialog.Builder(context).title(R.string.st_menu_add).icon(d).customView(R.layout.dialog_new_team, false)
                .positiveText(R.string.create).autoDismiss(false).negativeText(android.R.string.cancel).callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (callBack != null) {

                            TeamBean teamBean = new TeamBean();
                            teamBean.setCreation(new Date());

                            //Nom
                            EditText nt_name = (EditText) dialog.getCustomView().findViewById(R.id.nt_name);
                            teamBean.setName(nt_name.getText().toString());

                            //Competition
                            EditText nt_competition_name = (EditText) dialog.getCustomView().findViewById(R.id.nt_competition_name);
                            teamBean.setTournament(nt_competition_name.getText().toString());

                            //Team dont on copie les joueurs
                            NumberPicker nt_team_picker = (NumberPicker) dialog.getCustomView().findViewById(R.id.nt_team_picker);
                            TeamBean copyTeamPlayer = null;
                            if (nt_team_picker.getValue() > 0) {
                                copyTeamPlayer = allTeam.get(nt_team_picker.getValue() - 1);
                            }

                            callBack.newTeampromptDialogCB_onPositiveClick(teamBean, copyTeamPlayer);
                            dialog.dismiss();
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

        final EditText nt_name = (EditText) dialog.getCustomView().findViewById(R.id.nt_name);
        //l'edit text
        nt_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(StringUtils.isNotBlank(nt_name.getText()));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        NumberPicker nt_team_picker = (NumberPicker) dialog.getCustomView().findViewById(R.id.nt_team_picker);
        String[] teamName = new String[allTeam.size() + 1];
        nt_team_picker.setMinValue(0);
        nt_team_picker.setMaxValue(teamName.length - 1);
        teamName[0] = "None";
        for (int i = 0; i < allTeam.size(); i++) {
            TeamBean teamBean = allTeam.get(i);
            if (StringUtils.isNotBlank(teamBean.getTournament())) {
                teamName[i + 1] = teamBean.getTournament() + " - " + teamBean.getName();
            }
            else {
                teamName[i + 1] = teamBean.getName();
            }
        }
        nt_team_picker.setDisplayedValues(teamName);

        return dialog;
    }

    /**
     * affiche ou non le bouton valider
     *
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

    /* ---------------------------------
    // interface
    // -------------------------------- */
    public interface NewPlayerPromptDialogCB {
        void newPlayerpromptDialogCB_onPositiveClick(PlayerBean playerBean);
    }

    public interface NewTeamPromptDialogCB {
        void newTeampromptDialogCB_onPositiveClick(TeamBean teamBean, TeamBean teamBeanToCopyPlayers);
    }

    public interface PromptDialogCB {
        void promptDialogCB_onPositiveClick(String promptText);

    }
}
