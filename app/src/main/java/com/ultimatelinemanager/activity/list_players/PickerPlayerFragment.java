package com.ultimatelinemanager.activity.list_players;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.formation.utils.ToastUtils;
import com.ultimatelinemanager.MyApplication;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.dao.PlayerDaoManager;
import com.ultimatelinemanager.dao.TeamPlayerManager;
import com.ultimatelinemanager.metier.DialogUtils;
import com.ultimatelinemanager.metier.exception.LogicException;

import org.apache.commons.lang3.StringUtils;

import greendao.PlayerBean;
import greendao.TeamPlayer;

/**
 * Created by Anthony on 11/04/2015.
 */
public class PickerPlayerFragment extends ListPlayerFragment {

    //Equipe dont on veut exclure les joueurs
    private long teamBeanId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        getActivity().setTitle(R.string.lpt_no_team_title);
        st_info.setText(R.string.lpt_no_team_info);

        refreshList();

        return view;
    }

    /* ---------------------------------
    // Menu
    // -------------------------------- */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                //On affiche la page de séléction des joueurs enregistré dans le téléphone
                DialogUtils.getNewPlayerDialog(getActivity(), null, R.string.lpt_bt_new, R.string.add, new DialogUtils.NewPlayerPromptDialogCB() {
                    @Override
                    public void newPlayerpromptDialogCB_onPositiveClick(PlayerBean playerBean) {
                        //on ajoute le nouveau joueur
                        PlayerDaoManager.getPlayerDAO().insert(playerBean);
                        refreshList();
                        refreshView();
                    }
                }).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /* ---------------------------------
    // CallBackList
    // -------------------------------- */
    @Override
    public void selectAdapter_onClick(PlayerBean bean) {
        //On l'ajoute à l'équipe
        try {
            TeamPlayerManager.addPlayerToTeam(MyApplication.getInstance().getTeamBean().getId(), bean.getId());
            generiqueActivity.onBackPressed();

        } catch (LogicException e) {
            showError(e, true);
        }

    }

    @Override
    public void selectAdapter_onDeleteClick(final PlayerBean playerBean) {
        DialogUtils.getConfirmDialog(getActivity(), R.drawable.ic_action_delete, R.string.lpt_delete_player,
                getString(R.string.lpt_delete_player_text), new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);

                        //On supprime le joueur de l'équipe
                        if (playerBean.getTeamPlayerList().size() > 0) {
                            String teamName = "";
                            for (TeamPlayer teamPlayer : playerBean.getTeamPlayerList()) {
                                teamName += " - " + teamPlayer.getTeamBean().getName() + StringUtils.stripToEmpty(teamPlayer.getTeamBean().getTournament()) + "\n";
                            }

                            ToastUtils.showToastOnUIThread(getActivity(), getString(R.string
                                    .lpt_delete_player_refused, teamName), Toast.LENGTH_LONG);
                        } else {
                            //on peut supprimer le joueur
                            PlayerDaoManager.getPlayerDAO().delete(playerBean);
                            int index = playerBeanList.indexOf(playerBean);
                            if (index >= 0) {
                                playerBeanList.remove(index);
                                adapter.notifyItemRemoved(index);
                            }
                        }

                    }
                }).show();
    }

    /* ---------------------------------
    // private
    // -------------------------------- */
    protected void refreshList() {
        playerBeanList.clear();
        playerBeanList.addAll(PlayerDaoManager.getPlayerNotInTeam(teamBeanId));
    }

    /* ---------------------------------
    // Getter/ Setter
    // -------------------------------- */

    public long getTeamBeanId() {
        return teamBeanId;
    }

    public void setTeamBeanId(long teamBeanId) {
        this.teamBeanId = teamBeanId;
    }
}
