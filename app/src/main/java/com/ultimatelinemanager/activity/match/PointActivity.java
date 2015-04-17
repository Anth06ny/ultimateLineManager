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
import com.ultimatelinemanager.adapter.PlayerAdapter;
import com.ultimatelinemanager.bean.Role;
import com.ultimatelinemanager.dao.PlayerDaoManager;
import com.ultimatelinemanager.dao.match.PointDaoManager;
import com.ultimatelinemanager.metier.exception.TechnicalException;

import java.util.ArrayList;
import java.util.List;

import greendao.PlayerBean;
import greendao.PlayerPoint;
import greendao.PointBean;

/**
 * Created by amonteiro on 17/04/2015.
 */
public class PointActivity extends GeneriqueActivity implements PlayerAdapter.SelectAdapterI {

    private static final String TAG = LogUtils.getLogTag(PointActivity.class);

    //Composant graphique
    private CardView paCvSearch;
    private SearchView paSearch;
    private TextView paTvBoy;
    private TextView paTvGirl;
    private TextView paTvHandler;
    private TextView paTvMiddle;
    private TextView paTvBoth;

    //recycleview
    private RecyclerView paRvMiddle;
    private RecyclerView paRvHandler;
    private RecyclerView paRvAll;
    private RecyclerView paRvBoth;
    private LinearLayoutManager lmAll, lmHandler, lmMiddle, lmBoth;
    private PlayerAdapter allAdapter, handlerAdapter, middleAdapter, bothAdapter;

    //data
    private PointBean pointBean;
    private List<PlayerBean> noPlayingPlayer, handlerPlayer, middlePlayer, bothPlayer;

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
        paTvHandler = (TextView) findViewById(R.id.pa_tv_handler);
        paRvHandler = (RecyclerView) findViewById(R.id.pa_rv_handler);
        paTvMiddle = (TextView) findViewById(R.id.pa_tv_middle);
        paRvMiddle = (RecyclerView) findViewById(R.id.pa_rv_middle);
        paTvBoth = (TextView) findViewById(R.id.pa_tv_both);
        paRvBoth = (RecyclerView) findViewById(R.id.pa_rv_both);

        setTitle(getString(R.string.ma_title, pointBean.getMatchBean().getTeamBean().getName(), pointBean.getMatchBean().getName()));

        initList();
        initRecycleView();
        refreshView();
    }

    /* ---------------------------------
    // callback list
    // -------------------------------- */
    @Override
    public void selectAdapter_onClick(PlayerBean playerBean, PlayerAdapter.TYPE type) {

        int positionStart;
        //En cliquant sur une liste on migre le joueur sur l'autre liste et on met à jour l'ibnterface
        switch (type) {

            case NOT_PLAYING:
                positionStart = removePlayerFromList(playerBean.getId(), noPlayingPlayer);
                allAdapter.notifyItemRemoved(positionStart);
                //On le deplace vers son role
                switch (Role.getRole(playerBean.getRole())) {
                    case Handler:
                        handlerPlayer.add(playerBean);
                        handlerAdapter.notifyItemInserted(handlerPlayer.size() - 1);
                        break;
                    case Middle:
                        middlePlayer.add(playerBean);
                        middleAdapter.notifyItemInserted(middlePlayer.size() - 1);
                        break;
                    case Both:
                        bothPlayer.add(playerBean);
                        bothAdapter.notifyItemInserted(bothPlayer.size() - 1);
                        break;
                }
                break;

            case HANDLER:
                //On repositionne sur la liste principale
                positionStart = removePlayerFromList(playerBean.getId(), handlerPlayer);
                handlerAdapter.notifyItemRemoved(positionStart);
                allAdapter.notifyItemInserted(insertPlayerInOrder(playerBean));
                break;
            case MIDDLE:
                //On repositionne sur la liste principale
                positionStart = removePlayerFromList(playerBean.getId(), middlePlayer);
                middleAdapter.notifyItemRemoved(positionStart);
                allAdapter.notifyItemInserted(insertPlayerInOrder(playerBean));
                break;
            case BOTH:
                //On repositionne sur la liste principale
                positionStart = removePlayerFromList(playerBean.getId(), bothPlayer);
                bothAdapter.notifyItemRemoved(positionStart);
                allAdapter.notifyItemInserted(insertPlayerInOrder(playerBean));
                break;
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
                for (PlayerBean playerBean : handlerPlayer) {
                    if (playerBean.getSexe()) {
                        nbBoy++;
                    }
                    else {
                        nbGirl++;
                    }
                }
                for (PlayerBean playerBean : middlePlayer) {
                    if (playerBean.getSexe()) {
                        nbBoy++;
                    }
                    else {
                        nbGirl++;
                    }
                }
                for (PlayerBean playerBean : bothPlayer) {
                    if (playerBean.getSexe()) {
                        nbBoy++;
                    }
                    else {
                        nbGirl++;
                    }
                }

                paTvBoy.setText(nbBoy + "");
                paTvGirl.setText(nbGirl + "");
                paTvHandler.setText(handlerPlayer.size() + "");
                paTvMiddle.setText(middlePlayer.size() + "");
                paTvBoth.setText(bothPlayer.size() + "");

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
    private static int removePlayerFromList(long playerId, List<PlayerBean> playerBeanList) {
        int i = 0;
        for (PlayerBean playerBean : playerBeanList) {
            if (playerBean.getId().equals(playerId)) {
                playerBeanList.remove(i);
                return i;
            }
            i++;
        }

        return -1;
    }

    /**
     * insert a la correct place par odre aphabetique et retourne la position
     * @param playerBean
     * @return
     */
    private int insertPlayerInOrder(PlayerBean playerBean) {
        int size = noPlayingPlayer.size();
        int value = 0;
        for (int i = 0; i < size; i++) {
            if (playerBean.getName().compareTo(noPlayingPlayer.get(i).getName()) < 0) {
                value = i;
                break;
            }
        }
        noPlayingPlayer.add(value, playerBean);
        return value;
    }

    /**
     * Initialise les liste en plancant chaque joueur a son poste pour le point
     */
    private void initList() {
        //Joueur jouant ce point
        List<PlayerPoint> playerPointList = pointBean.getPlayerPointList();

        noPlayingPlayer = new ArrayList<>();
        handlerPlayer = new ArrayList<>();
        middlePlayer = new ArrayList<>();
        bothPlayer = new ArrayList<>();

        //On ajoute le joueur dans la liste qui lui correspond
        PlayerPoint temp;
        for (PlayerBean playerBean : PlayerDaoManager.getPlayers(pointBean.getMatchBean().getTeamId())) {
            temp = null;
            //On regarde si le joueur joue quelque part
            for (PlayerPoint playerPoint : playerPointList) {
                if (playerPoint.getId().equals(playerBean.getId())) {
                    temp = playerPoint;
                    break;
                }
            }

            //Il ne joue pas
            if (temp == null) {
                noPlayingPlayer.add(playerBean);
            }
            else {
                //Il joue
                Role role = Role.valueOf(temp.getRole());
                if (role == null) {
                    LogUtils.logException(TAG, new TechnicalException("role non trouvé : role = " + temp.getRole()), true);
                    role = Role.Both;
                }
                switch (role) {
                    case Handler:
                        handlerPlayer.add(playerBean);
                        break;
                    case Middle:
                        middlePlayer.add(playerBean);
                        break;
                    case Both:
                        bothPlayer.add(playerBean);
                        break;
                }
            }
        }

    }

    private void initRecycleView() {

        paRvAll.setHasFixedSize(false);
        paRvAll.setLayoutManager(lmAll = new LinearLayoutManager(this));
        paRvAll.setItemAnimator(new DefaultItemAnimator());

        paRvHandler.setHasFixedSize(false);
        paRvHandler.setLayoutManager(lmHandler = new LinearLayoutManager(this));
        paRvHandler.setItemAnimator(new DefaultItemAnimator());

        paRvMiddle.setHasFixedSize(false);
        paRvMiddle.setLayoutManager(lmMiddle = new LinearLayoutManager(this));
        paRvMiddle.setItemAnimator(new DefaultItemAnimator());

        paRvBoth.setHasFixedSize(false);
        paRvBoth.setLayoutManager(lmBoth = new LinearLayoutManager(this));
        paRvBoth.setItemAnimator(new DefaultItemAnimator());

        paRvAll.setAdapter(allAdapter = new PlayerAdapter(this, noPlayingPlayer, PlayerAdapter.TYPE.NOT_PLAYING, this));
        paRvHandler.setAdapter(handlerAdapter = new PlayerAdapter(this, handlerPlayer, PlayerAdapter.TYPE.HANDLER, this));
        paRvMiddle.setAdapter(middleAdapter = new PlayerAdapter(this, middlePlayer, PlayerAdapter.TYPE.MIDDLE, this));
        paRvBoth.setAdapter(bothAdapter = new PlayerAdapter(this, bothPlayer, PlayerAdapter.TYPE.BOTH, this));

        allAdapter.notifyDataSetChanged();
        handlerAdapter.notifyDataSetChanged();
        middleAdapter.notifyDataSetChanged();
        bothAdapter.notifyDataSetChanged();

    }

}
