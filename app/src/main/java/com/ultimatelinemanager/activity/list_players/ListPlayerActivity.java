package com.ultimatelinemanager.activity.list_players;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ultimatelinemanager.R;
import com.ultimatelinemanager.activity.GeneriqueActivity;
import com.ultimatelinemanager.adapter.SelectAdapter;

import java.util.ArrayList;

import greendao.PlayerBean;

/**
 * Classe permetant d'afficher une liste de joueur
 */
public abstract class ListPlayerActivity extends GeneriqueActivity implements SelectAdapter.SelectAdapterI<PlayerBean> {

    //Composants graphiques
    private RecyclerView st_rv;
    private TextView st_empty;
    protected TextView st_info;

    //Autre
    private SelectAdapter adapter;
    protected ArrayList<PlayerBean> playerBeanList;

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list_activity_layout);

        //RecylceView
        st_empty = (TextView) findViewById(R.id.st_empty);
        st_rv = (RecyclerView) findViewById(R.id.st_rv);
        st_info = (TextView) findViewById(R.id.st_info);
        st_rv.setHasFixedSize(false);
        st_rv.setLayoutManager(new LinearLayoutManager(this));
        st_rv.setItemAnimator(new DefaultItemAnimator());

        //Utils.getColorFromTheme(this, R.attr.color_application_bg)
        adapter = new SelectAdapter(this, playerBeanList = new ArrayList<>(), SelectAdapter.TYPE.PLAYER, this);

        st_rv.setAdapter(adapter);



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
        getMenuInflater().inflate(R.menu.menu_list_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //bouton fleche retour
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* ---------------------------------
    // Autre
    // -------------------------------- */

    protected void refreshView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();

                if (playerBeanList.size() > 0) {
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
}
