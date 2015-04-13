package com.ultimatelinemanager.activity.list_players;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.ultimatelinemanager.Constante;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.dao.PlayerDaoManager;
import com.ultimatelinemanager.metier.DialogUtils;

import greendao.PlayerBean;

/**
 * Created by Anthony on 11/04/2015.
 */
public class PickerPlayerActivity extends ListPlayerActivity {

    //Equipe dont on veut exclure les joueurs
    private long teamBeanId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        teamBeanId = getIntent().getLongExtra(Constante.TEAM_EXTRA_ID, -1);

        setTitle(R.string.lpt_no_team_title);
        st_info.setText(R.string.lpt_no_team_info);

        //Pour avoir la fleche de retour
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        refreshList();
    }

    /* ---------------------------------
    // Menu
    // -------------------------------- */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                //On affiche la page de séléction des joueurs enregistré dans le téléphone
                DialogUtils.getNewPlayerDialog(this, R.string.lpt_bt_new, R.string.add, new DialogUtils.NewPlayerPromptDialogCB() {
                    @Override
                    public void newPlayerpromptDialogCB_onPositiveClick(PlayerBean playerBean) {
                        //on ajoute le nouveau joueur
                        PlayerDaoManager.getPlayerDAO().insert(playerBean);
                        refreshList();
                        refreshView();
                    }
                }).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* ---------------------------------
    // CallBackList
    // -------------------------------- */
    @Override
    public void selectAdapter_onClick(PlayerBean bean) {
        //Le joueur a été séléctionné, on le renvoit et on termine l'activity
        Intent result = new Intent();
        result.putExtra(Constante.PLAYER_EXTRA_ID, bean.getId());
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    /* ---------------------------------
    // private
    // -------------------------------- */
    protected void refreshList() {
        playerBeanList.clear();
        playerBeanList.addAll(PlayerDaoManager.getPlayerNotInTeam(teamBeanId));
    }

}
