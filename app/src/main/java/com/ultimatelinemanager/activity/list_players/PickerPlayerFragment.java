package com.ultimatelinemanager.activity.list_players;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.ultimatelinemanager.MyApplication;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.dao.PlayerDaoManager;
import com.ultimatelinemanager.dao.TeamPlayerManager;
import com.ultimatelinemanager.metier.DialogUtils;
import com.ultimatelinemanager.metier.exception.LogicException;

import greendao.PlayerBean;

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
                DialogUtils.getNewPlayerDialog(getActivity(), R.string.lpt_bt_new, R.string.add, new DialogUtils.NewPlayerPromptDialogCB() {
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

        }
        catch (LogicException e) {
            showError(e, true);
        }

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
