package com.ultimatelinemanager.activity.list_players;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.ultimatelinemanager.Constante;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.dao.PlayerDaoManager;
import com.ultimatelinemanager.dao.TeamDaoManager;
import com.ultimatelinemanager.dao.TeamPlayerManager;
import com.ultimatelinemanager.metier.IntentHelper;
import com.ultimatelinemanager.metier.exception.ExceptionA;
import com.ultimatelinemanager.metier.exception.TechnicalException;

import greendao.PlayerBean;
import greendao.TeamBean;

/**
 * Created by Anthony on 11/04/2015.
 * Affiche la liste des joueurs de l'équipe
 */
public class TeamPlayerActivity extends ListPlayerActivity {

    private TeamBean teamBean;

    /* ---------------------------------
    // Activity
    // -------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        teamBean = TeamDaoManager.getTeamDAO().load(getIntent().getLongExtra(Constante.TEAM_EXTRA_ID, -1));

        //erreur
        if (teamBean == null) {
            showError(new TechnicalException("Aucune équipe transmise"), true);
            finish();
            return;
        }

        setTitle(getString(R.string.lpt_title, teamBean.getName()));
        st_info.setText(R.string.lpt_info);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constante.PICK_PLAYER_REQ_CODE && resultCode == Activity.RESULT_OK) {
            //On recupere l'id de notre joueur
            try {
                long newPlayer = data.getExtras().getLong(Constante.PLAYER_EXTRA_ID, -1);
                if (newPlayer != -1) {
                    //On ajoute notre nouveau joueur à l'equipe
                    TeamPlayerManager.addPlayerToTeam(teamBean.getId(), newPlayer);
                    //On met à jour la liste et la vue
                    refreshList();
                    refreshView();

                }
                else {
                    throw new TechnicalException("Le playerId n'a pas été transmit");
                }

            }
            catch (ExceptionA e) {
                showError(e, true);
            }

        }
    }

    /* ---------------------------------
    // Menu
    // -------------------------------- */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_add:
                IntentHelper.goToPickPlayer(this,teamBean.getId(), Constante.PICK_PLAYER_REQ_CODE);
                return true;
        }

        //Sinon  traiter au dessus
        return super.onOptionsItemSelected(item);
    }

    /* ---------------------------------
    // Callback List
    // -------------------------------- */
    @Override
    public void selectAdapter_onClick(PlayerBean playerBean) {
        //On va sur la page d'un joueur , pas encore faite.
        IntentHelper.goToPlayerPage(this, playerBean.getId());
    }

    /* ---------------------------------
    // private
    // -------------------------------- */
    protected void refreshList() {
        playerBeanList.clear();
        playerBeanList.addAll(PlayerDaoManager.getPlayers(teamBean));
    }

}
