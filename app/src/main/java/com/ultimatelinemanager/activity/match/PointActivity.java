package com.ultimatelinemanager.activity.match;

import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import com.formation.utils.LogUtils;
import com.ultimatelinemanager.Constante;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.activity.GeneriqueActivity;
import com.ultimatelinemanager.adapter.PlayerPointAdapter;
import com.ultimatelinemanager.adapter.PlayerPointWithHeaderAdapter;
import com.ultimatelinemanager.bean.PlayerPointBean;
import com.ultimatelinemanager.bean.Role;
import com.ultimatelinemanager.dao.PlayerDaoManager;
import com.ultimatelinemanager.dao.match.PointDaoManager;

import java.util.List;

import greendao.PlayerBean;
import greendao.PlayerPoint;
import greendao.PointBean;

/**
 * Created by amonteiro on 17/04/2015.
 */
public class PointActivity extends GeneriqueActivity implements PlayerPointAdapter.PlayerPointAdapterI {

    private static final String TAG = LogUtils.getLogTag(PointActivity.class);

    //Composant graphique
    private CardView paCvSearch;
    private SearchView paSearch;
    private TextView paTvBoy;
    private TextView paTvGirl;

    //recycleview
    private RecyclerView pa_rv_playing;
    private RecyclerView paRvAll;
    private LinearLayoutManager lmAll, lmPlaying;
    private PlayerPointAdapter noPlayingAdapter;
    private PlayerPointWithHeaderAdapter playerInPointAdapter;

    //data
    private PointBean pointBean;

    /* ---------------------------------
    // View
    // -------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        long pointId = getIntent().getLongExtra(Constante.POINT_EXTRA_ID, -1);
        pointBean = PointDaoManager.getPointBeanDao().load(pointId);

        if (pointBean == null) {
            LogUtils.logMessage(TAG, "PointBean à nulle, pointId=" + pointId);
            finish();
            return;
        }

        paCvSearch = (CardView) findViewById(R.id.pa_cv_search);
        paSearch = (SearchView) findViewById(R.id.pa_search);
        paRvAll = (RecyclerView) findViewById(R.id.pa_rv_all);
        paTvBoy = (TextView) findViewById(R.id.pa_tv_boy);
        paTvGirl = (TextView) findViewById(R.id.pa_tv_girl);
        pa_rv_playing = (RecyclerView) findViewById(R.id.pa_rv_playing);

        setTitle(getString(R.string.ma_title, pointBean.getMatchBean().getTeamBean().getName(), pointBean.getMatchBean().getName()));

        initRecycleView();
        initList();
        refreshView();
    }

    /* ---------------------------------
    // callback list
    // -------------------------------- */
    @Override
    public void playerPointAdapter_onClick(PlayerPointBean playerPointBean) {
        //Le joueur ne joue pas on le passe dans la liste des joueurs qui joue à son role de bas
        if (playerPointBean.getRoleInPoint() == null) {
            noPlayingAdapter.removeItem(playerPointBean.getPlayerBean().getId());
            playerInPointAdapter.addItem(playerPointBean, Role.getRole(playerPointBean.getPlayerBean().getRole()));
        }
        else {
            //Le joueur joue on le replace dans la liste des joueurs qui ne joue pas
            playerInPointAdapter.removeItem(playerPointBean.getPlayerBean().getId());
            noPlayingAdapter.addItem(playerPointBean);
        }

        refreshView();

    }

    /* ---------------------------------
    // private graphique
    // -------------------------------- */

    private void refreshView() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int nbBoy = 0, nbGirl = 0;
                for (PlayerPointBean playerPointBean : playerInPointAdapter.getDaoList()) {
                    if (playerPointBean.getPlayerBean().getSexe()) {
                        nbBoy++;
                    }
                    else {
                        nbGirl++;
                    }
                }

                paTvBoy.setText(nbBoy + "");
                paTvGirl.setText(nbGirl + "");

            }
        });

    }

    /* ---------------------------------
    // private
    // -------------------------------- */

    /**
     * retire de la liste le joueur et retourne la position de celui ci
     * @param playerId
     * @return
     */
    private static int removePlayerFromList(long playerId, List<PlayerPointBean> playerBeanList) {
        int i = 0;
        for (PlayerPointBean playerPointBean : playerBeanList) {
            if (playerPointBean.getPlayerBean().getId().equals(playerId)) {
                playerBeanList.remove(i);
                return i;
            }
            i++;
        }

        return -1;
    }

    /**
     * Initialise les liste en plancant chaque joueur a son poste pour le point
     */
    private void initList() {
        //Joueur jouant ce point
        List<PlayerPoint> playerPointList = pointBean.getPlayerPointList();

        //L'ensemble des joueur de l'equipe et On l'ajoute la liste qui lui correspond
        PlayerPoint temp;
        PlayerPointBean playerPointBean;
        for (PlayerBean playerBean : PlayerDaoManager.getPlayers(pointBean.getMatchBean().getTeamId())) {
            temp = null;
            playerPointBean = new PlayerPointBean(playerBean);
            //On regarde si le joueur joue quelque part
            for (PlayerPoint playerPoint : playerPointList) {
                if (playerPoint.getId().equals(playerBean.getId())) {
                    temp = playerPoint;
                    break;
                }
            }

            //Il ne joue pas
            if (temp == null) {
                noPlayingAdapter.getDaoList().add(playerPointBean);
            }
            else {
                //Il joue
                playerPointBean.setRoleInPoint(Role.valueOf(temp.getRole()));
                playerInPointAdapter.getDaoList().add(playerPointBean);
            }
        }

        //on trie la liste des joueur qui joue par role
        playerInPointAdapter.sortList();
        playerInPointAdapter.refreshList();

        noPlayingAdapter.notifyDataSetChanged();
        playerInPointAdapter.notifyDataSetChanged();
    }

    private void initRecycleView() {

        paRvAll.setHasFixedSize(false);
        paRvAll.setLayoutManager(lmAll = new LinearLayoutManager(this));
        paRvAll.setItemAnimator(new DefaultItemAnimator());

        pa_rv_playing.setHasFixedSize(false);
        pa_rv_playing.setLayoutManager(lmPlaying = new LinearLayoutManager(this));
        pa_rv_playing.setItemAnimator(new DefaultItemAnimator());

        paRvAll.setAdapter(noPlayingAdapter = new PlayerPointAdapter(this, this));
        pa_rv_playing.setAdapter(playerInPointAdapter = new PlayerPointWithHeaderAdapter(this, this));

    }

}
