package com.ultimatelinemanager.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.adapter.SelectAdapter;
import com.ultimatelinemanager.dao.PlayerDaoManager;
import com.ultimatelinemanager.dao.TeamDaoManager;
import com.ultimatelinemanager.metier.DialogUtils;
import com.ultimatelinemanager.metier.IntentHelper;

import java.util.ArrayList;

import greendao.PlayerBean;
import greendao.TeamBean;

public class ListPlayerActivity extends ActionBarActivity implements SelectAdapter.SelectAdapterI<PlayerBean> {

    //Composants graphiques
    private RecyclerView st_rv;
    private MaterialDialog dialog;
    private TextView st_empty;
    private TextView st_info;

    //Autre
    private SelectAdapter adapter;
    private TeamBean teamBean;
    private ArrayList<PlayerBean> playerBeanList;

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_player);

        //En fonction de si on a une equipe ou non l'ecran sera different
        teamBean = (TeamBean) getIntent().getSerializableExtra(IntentHelper.TEAM_EXTRA);

        //RecylceView
        st_empty = (TextView) findViewById(R.id.st_empty);
        st_rv = (RecyclerView) findViewById(R.id.st_rv);
        st_info = (TextView) findViewById(R.id.st_info);
        st_rv.setHasFixedSize(false);
        st_rv.setLayoutManager(new LinearLayoutManager(this));
        st_rv.setItemAnimator(new DefaultItemAnimator());

        adapter = new SelectAdapter(this, playerBeanList = new ArrayList<>(), SelectAdapter.TYPE.PLAYER, this);

        st_rv.setAdapter(adapter);

        //Titre en fonction
        setTitle(teamBean != null ? R.string.lpt_title : R.string.lpt_no_team_title);

        //Info
        st_info.setText(teamBean != null ? R.string.lpt_info : R.string.lpt_no_team_info);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //On met à jour la liste
        playerBeanList.clear();
        playerBeanList.addAll(TeamDaoManager.getPlayers(teamBean));

        adapter.notifyDataSetChanged();

        refreshView();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /* ---------------------------------
    // Callback List
    // -------------------------------- */
    @Override
    public void selectAdapter_onClick(PlayerBean bean) {
        //IntentHelper.goToTeamActivity(this, teamBean);

    }

    /* ---------------------------------
    // Menu
    // -------------------------------- */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player_team, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_add) {
            //On affiche la page de séléction des joueurs enregistré dans le téléphone
            if (teamBean != null) {
                IntentHelper.goToListPlayerActivity(this, null);
            }
            else {
                DialogUtils.getNewPlayerDialog(this, R.string.lpt_bt_new, R.string.add, new DialogUtils.NewPlayerPromptDialogCB() {
                    @Override
                    public void newPlayerpromptDialogCB_onPositiveClick(PlayerBean playerBean) {
                        //on ajoute le nouveau joueur
                        PlayerDaoManager.getPlayerDAO().insert(playerBean);
                        refreshView();
                    }
                }).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* ---------------------------------
    // Autre
    // -------------------------------- */

    private void refreshView() {
        if (playerBeanList.size() > 0) {
            st_empty.setVisibility(View.INVISIBLE);
            st_rv.setVisibility(View.VISIBLE);
        }
        else {
            st_empty.setVisibility(View.VISIBLE);
            st_rv.setVisibility(View.INVISIBLE);
        }
    }
}
