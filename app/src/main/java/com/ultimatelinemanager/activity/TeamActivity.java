package com.ultimatelinemanager.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.dao.TeamDaoManager;
import com.ultimatelinemanager.metier.DialogUtils;
import com.ultimatelinemanager.metier.IntentHelper;

import greendao.TeamBean;

public class TeamActivity extends ActionBarActivity implements View.OnClickListener {

    //Composant graphique
    private MaterialDialog dialog;
    private Button mt_bt_players, ta_bt_games;

    //autre
    private TeamBean teamBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        mt_bt_players = (Button) findViewById(R.id.mt_bt_players);
        ta_bt_games = (Button) findViewById(R.id.ta_bt_games);

        mt_bt_players.setOnClickListener(this);
        ta_bt_games.setOnClickListener(this);

        teamBean = (TeamBean) getIntent().getSerializableExtra(IntentHelper.TEAM_EXTRA);

        if (teamBean == null) {
            finish();
        }
        refreshTitle();

    }

    private void refreshTitle() {
        getActionBar().setTitle(teamBean.getName());
    }

    /* ---------------------------------
    // Menu
    // -------------------------------- */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_team, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_rename) {

            //on demande le nouveau nom
            dialog = DialogUtils.getPromptDialog(this, R.drawable.ic_action_add_group, R.string.st_ask_team_name, R.string.add, teamBean.getName(),
                    new DialogUtils.PromptDialogCB() {
                        @Override
                        public void promptDialogCB_onPositiveClick(String promptText) {
                            teamBean.setName(promptText);
                            TeamDaoManager.getTeamDAO().update(teamBean);
                            refreshTitle();
                        }
                    });
            dialog.show();

            return true;
        }
        else if (id == R.id.menu_delete) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* ---------------------------------
    // Click
    // -------------------------------- */

    @Override
    public void onClick(View v) {

    }
}
