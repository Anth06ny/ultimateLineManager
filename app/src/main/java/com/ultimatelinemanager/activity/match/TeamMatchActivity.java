package com.ultimatelinemanager.activity.match;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ultimatelinemanager.Constante;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.activity.GeneriqueActivity;
import com.ultimatelinemanager.adapter.SelectAdapter;
import com.ultimatelinemanager.dao.TeamDaoManager;
import com.ultimatelinemanager.metier.IntentHelper;
import com.ultimatelinemanager.metier.exception.TechnicalException;

import java.util.ArrayList;

import greendao.MatchBean;
import greendao.TeamBean;

public class TeamMatchActivity extends GeneriqueActivity implements SelectAdapter.SelectAdapterI<MatchBean> {

    private RecyclerView st_rv;
    private TextView st_empty;
    protected TextView st_info;

    //Autre
    private SelectAdapter adapter;
    protected ArrayList<MatchBean> matchBeansList;
    private TeamBean teamBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_activity_layout);

        teamBean = TeamDaoManager.getTeamDAO().load(getIntent().getLongExtra(Constante.TEAM_EXTRA_ID, -1));

        //erreur
        if (teamBean == null) {
            showError(new TechnicalException("Aucune équipe transmise"), true);
            finish();
            return;
        }

        //RecylceView
        st_empty = (TextView) findViewById(R.id.st_empty);
        st_rv = (RecyclerView) findViewById(R.id.st_rv);
        st_info = (TextView) findViewById(R.id.st_info);
        st_rv.setHasFixedSize(false);
        st_rv.setLayoutManager(new LinearLayoutManager(this));
        st_rv.setItemAnimator(new DefaultItemAnimator());

        adapter = new SelectAdapter(this, matchBeansList = new ArrayList<>(), SelectAdapter.TYPE.MATCH, this);

        st_rv.setAdapter(adapter);

        setTitle(getString(R.string.ma_title, teamBean.getName()));
        st_info.setText(R.string.ma_info);

        refreshList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshView();
    }

    /* ---------------------------------
    // Menu
    // -------------------------------- */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_team_match, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        //bouton fleche retour
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_add:
                //TODO remplir la dialogue de demande de nom d'equipe
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* ---------------------------------
    // Callback Click
    // -------------------------------- */

    @Override
    public void selectAdapter_onClick(MatchBean matchBean) {
        IntentHelper.goToMatch(this, matchBean.getId());
    }

    /* ---------------------------------
    // private
    // -------------------------------- */

    protected void refreshView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();

                if (matchBeansList.size() > 0) {
                    st_empty.setVisibility(View.INVISIBLE);
                    st_rv.setVisibility(View.VISIBLE);
                }
                else {
                    st_empty.setVisibility(View.VISIBLE);
                    st_rv.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    protected void refreshList() {
        matchBeansList.clear();
        teamBean.resetMatchBeanList();
        matchBeansList.addAll(teamBean.getMatchBeanList());
    }

}