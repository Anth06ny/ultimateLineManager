package com.ultimatelinemanager.activity.list_players;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ultimatelinemanager.R;
import com.ultimatelinemanager.activity.MainFragment;
import com.ultimatelinemanager.adapter.SelectAdapter;

import java.util.ArrayList;

import greendao.PlayerBean;

/**
 * Classe permetant d'afficher une liste de joueur
 */
public abstract class ListPlayerFragment extends MainFragment implements SelectAdapter.SelectAdapterI<PlayerBean> {

    //Composants graphiques
    private RecyclerView st_rv;
    private TextView st_empty;
    protected TextView st_info;

    //Autre
    protected SelectAdapter adapter;
    protected ArrayList<PlayerBean> playerBeanList;

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.list_activity_layout, container, false);

        //RecylceView
        st_empty = (TextView) view.findViewById(R.id.st_empty);
        st_rv = (RecyclerView) view.findViewById(R.id.st_rv);
        st_info = (TextView) view.findViewById(R.id.st_info);
        st_rv.setHasFixedSize(false);
        st_rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        st_rv.setItemAnimator(new DefaultItemAnimator());

        //Utils.getColorFromTheme(this, R.attr.color_application_bg)
        adapter = new SelectAdapter(getActivity(), playerBeanList = new ArrayList<>(), SelectAdapter.TYPE.PLAYER, this);

        st_rv.setAdapter(adapter);

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        refreshView();
    }

    /* ---------------------------------
    // Menu
    // -------------------------------- */

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_list_player, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    /* ---------------------------------
    // Autre
    // -------------------------------- */

    protected void refreshView() {
        getActivity().runOnUiThread(new Runnable() {
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
