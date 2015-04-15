package com.ultimatelinemanager.activity.match;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.formation.utils.LogUtils;
import com.ultimatelinemanager.Constante;
import com.ultimatelinemanager.MyApplication;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.activity.GeneriqueActivity;
import com.ultimatelinemanager.adapter.PointAdapter;
import com.ultimatelinemanager.dao.match.MatchDaoManager;
import com.ultimatelinemanager.dao.match.PointDaoManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import greendao.MatchBean;
import greendao.PointBean;

public class ListPointsActivity extends GeneriqueActivity implements PointAdapter.PointAdapterCB {

    private static final String TAG = LogUtils.getLogTag(ListPointsActivity.class);

    //Composants graphiques
    private TextView st_empty;

    //RecycleView
    private RecyclerView st_rv;
    private LinearLayoutManager lm;
    private PointAdapter adapter;

    //Data
    private ArrayList<PointBean> pointBeanList;
    private MatchBean matchBean;

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_points);

        long matchId = getIntent().getLongExtra(Constante.MATCH_EXTRA_ID, -1);
        matchBean = MatchDaoManager.getMatchBeanDao().load(matchId);

        if (matchBean == null) {
            LogUtils.logMessage(TAG, "matchBean à nulle, matchId=" + matchId);
            finish();
            return;
        }

        st_empty = (TextView) findViewById(R.id.st_empty);
        st_rv = (RecyclerView) findViewById(R.id.st_rv);
        st_rv.setHasFixedSize(false);
        st_rv.setLayoutManager(lm = new LinearLayoutManager(this));
        st_rv.setItemAnimator(new DefaultItemAnimator());

        pointBeanList = new ArrayList<>();
        pointBeanList.addAll(matchBean.getPointBeanList());
        sortList();

        adapter = new PointAdapter(this, pointBeanList, this);
        st_rv.setAdapter(adapter);

        setTitle(getString(R.string.lp_title, matchBean.getTeamBean().getName(), matchBean.getName()));

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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);

    }

    /* ---------------------------------
    // View callback
    // -------------------------------- */

    @Override
    public void pointAdapter_deletePoint(PointBean bean) {

        int position = -1;
        for (int i = 0; i < pointBeanList.size(); i++) {
            if (pointBeanList.get(i).getId() == bean.getId()) {
                position = i;
                break;
            }
        }

        //On le retire en base de donnée
        PointDaoManager.getPointBeanDao().delete(bean);
        //pour bien le supprimer de la session
        MyApplication.getInstance().getDaoSession().clear();

        //on essaye de le retirer en mode optimiser
        if (position >= 0) {
            pointBeanList.remove(position);
            adapter.notifyItemRemoved(position);
        }
        //Sinon de maniere normal
        else {
            pointBeanList.remove(bean);
            adapter.notifyDataSetChanged();
        }

    }

    /* ---------------------------------
    // private
    // -------------------------------- */
    private void sortList() {
        Collections.sort(pointBeanList, new Comparator<PointBean>() {
            @Override
            public int compare(PointBean p1, PointBean p2) {
                if (p1.getStart() == null && p2.getStart() == null) {
                    //Si les 2 sont nulle on garde l'autre des id
                    return (int) (p1.getId() - p2.getId());

                }
                else if (p1.getStart() == null) {
                    return -1;
                }
                else if (p2.getStart() == null) {
                    return 1;
                }
                else {
                    return (int) (p1.getStart().getTime() - p2.getStart().getTime());
                }
            }
        });
    }

    private void refreshList() {

        pointBeanList.clear();
        matchBean.resetPointBeanList();
        pointBeanList.addAll(matchBean.getPointBeanList());
        sortList();
        adapter.notifyDataSetChanged();
    }

    protected void refreshView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();

                if (pointBeanList.size() > 0) {
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
