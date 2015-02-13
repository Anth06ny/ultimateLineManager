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
import com.formation.utils.ToastUtils;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.adapter.SelectTeamAdapter;
import com.ultimatelinemanager.dao.TeamDaoManager;
import com.ultimatelinemanager.metier.DialogUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;

import greendao.TeamBean;

public class SelectTeamActivity extends ActionBarActivity {

    //Composants graphiques
    private RecyclerView st_rv;
    private MaterialDialog dialog;
    private TextView st_empty;

    //Autre
    private SelectTeamAdapter adapter;
    private ArrayList<TeamBean> teamBeanList;

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_team);

        //RecylceView
        st_empty = (TextView) findViewById(R.id.st_empty);
        st_rv = (RecyclerView) findViewById(R.id.st_rv);
        st_rv.setHasFixedSize(false);
        st_rv.setLayoutManager(new LinearLayoutManager(this));
        st_rv.setItemAnimator(new DefaultItemAnimator());

        adapter = new SelectTeamAdapter(this, teamBeanList = new ArrayList<>());
        st_rv.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //On met à jour la liste
        teamBeanList.clear();
        teamBeanList.addAll(TeamDaoManager.getLast50Team());

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
    // Menu
    // -------------------------------- */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_team, menu);

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
            dialog = DialogUtils.getPromptDialog(this, R.drawable.ic_action_add_group, R.string.st_ask_team_name, R.string.add,
                    new DialogUtils.PromptDialogCB() {
                        @Override
                        public void promptDialogCB_onPositiveClick(String promptText) {
                            //On ajoute l'equi
                            addTeam(promptText);
                        }
                    });
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* ---------------------------------
    // Autre
    // -------------------------------- */

    private void refreshView() {
        if (teamBeanList.size() > 0) {
            st_empty.setVisibility(View.INVISIBLE);
            st_rv.setVisibility(View.VISIBLE);
        }
        else {
            st_empty.setVisibility(View.VISIBLE);
            st_rv.setVisibility(View.INVISIBLE);
        }
    }

    private void addTeam(String teamName) {
        if (StringUtils.isBlank(teamName)) {
            return;
        }

        TeamBean teamBean = new TeamBean();
        teamBean.setCreation(new Date());
        teamBean.setName(teamName);

        teamBean.setId(TeamDaoManager.getTeamDAO().insert(teamBean));

        if (teamBean.getId() > 0) {
            //On met à jour la liste
            teamBeanList.add(0, teamBean);
            adapter.notifyItemInserted(0);
            refreshView();
        }
        else {
            ToastUtils.showToastOnUIThread(this, R.string.st_team_not_add);
        }

    }

}
