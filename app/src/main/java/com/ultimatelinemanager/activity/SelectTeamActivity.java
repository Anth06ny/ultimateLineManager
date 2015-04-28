package com.ultimatelinemanager.activity;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.formation.utils.ToastUtils;
import com.ultimatelinemanager.MyApplication;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.adapter.SelectAdapter;
import com.ultimatelinemanager.dao.TeamDaoManager;
import com.ultimatelinemanager.metier.DialogUtils;
import com.ultimatelinemanager.metier.IntentHelper;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;

import greendao.TeamBean;

public class SelectTeamActivity extends GeneriqueActivity implements SelectAdapter.SelectAdapterI<TeamBean> {

    //Composants graphiques
    private RecyclerView st_rv;
    private MaterialDialog dialog;
    private TextView st_empty;

    //Autre
    private SelectAdapter adapter;
    private ArrayList<TeamBean> teamBeanList;

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //On regarde si on a déjà une equipe dans ce cas on vas direct sur l'équipe
        if (getTeamBean() != null) {
            IntentHelper.goToTeamActivity(this);
            finish();
            return;
        }

        setContentView(R.layout.activity_select_team);
        setTitle(R.string.st_title);

        //RecylceView
        st_empty = (TextView) findViewById(R.id.st_empty);
        st_rv = (RecyclerView) findViewById(R.id.st_rv);
        st_rv.setHasFixedSize(false);
        st_rv.setLayoutManager(new LinearLayoutManager(this));
        st_rv.setItemAnimator(new DefaultItemAnimator());

        adapter = new SelectAdapter(this, teamBeanList = new ArrayList<>(), SelectAdapter.TYPE.TEAM, this);

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
    // Callback List
    // -------------------------------- */
    @Override
    public void selectAdapter_onClick(TeamBean teamBean) {
        //On charge l'equipe en tant qu'equipe courante
        MyApplication.getInstance().setTeamBean(teamBean);
        IntentHelper.goToTeamActivity(this);
        finish();
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

        switch (item.getItemId()) {
            case R.id.menu_add:
                dialog = DialogUtils.getPromptDialog(this, R.drawable.ic_action_add_group, R.string.st_ask_team_name, R.string.add, "",
                        new DialogUtils.PromptDialogCB() {
                            @Override
                            public void promptDialogCB_onPositiveClick(String promptText) {
                                //On ajoute l'equipe
                                addTeam(StringUtils.capitalize(promptText));
                            }
                        });
                dialog.show();
                return true;



            default:
                return super.onOptionsItemSelected(item);
        }
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
