package com.ultimatelinemanager.activity.match;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.formation.utils.LogUtils;
import com.formation.utils.Utils;
import com.ultimatelinemanager.Constante;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.activity.MainFragment;
import com.ultimatelinemanager.adapter.PlayerStatAdapter;
import com.ultimatelinemanager.bean.PlayerStatBean;
import com.ultimatelinemanager.dao.PlayerDaoManager;
import com.ultimatelinemanager.dao.match.PointDaoManager;

import java.util.ArrayList;
import java.util.List;

import greendao.MatchBean;
import greendao.PlayerBean;
import greendao.PointBean;

public class StatFragment extends MainFragment {

    private static final String TAG = LogUtils.getLogTag(StatFragment.class);

    //Composant graphique
    private ImageView ma_iv_win;
    private TextView ma_tv_playing_time;
    private TextView ma_tv_reel_playing_time;
    private TextView ma_tv_score;
    private TextView st_empty, stat_tv_lucky, stat_tv_unlucky;

    //RecycleView
    private RecyclerView st_rv;
    private LinearLayoutManager lm;
    private PlayerStatAdapter adapter;

    //Data
    private MatchBean matchBean;
    private List<PlayerStatBean> playerStatBeanList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_stat, container, false);

        if (matchBean == null) {
            LogUtils.logMessage(TAG, "matchBean à nulle ");
            getFragmentManager().popBackStack();
            return null;
        }

        ma_iv_win = (ImageView) view.findViewById(R.id.ma_iv_win);
        ma_tv_playing_time = (TextView) view.findViewById(R.id.ma_tv_playing_time);
        ma_tv_reel_playing_time = (TextView) view.findViewById(R.id.ma_tv_reel_playing_time);
        ma_tv_score = (TextView) view.findViewById(R.id.ma_tv_score);
        st_rv = (RecyclerView) view.findViewById(R.id.st_rv);
        st_empty = (TextView) view.findViewById(R.id.st_empty);
        stat_tv_lucky = (TextView) view.findViewById(R.id.stat_tv_lucky);
        stat_tv_unlucky = (TextView) view.findViewById(R.id.stat_tv_unlucky);


        ma_iv_win.setColorFilter(getResources().getColor(R.color.yellow));

        //recycleview
        st_rv.setHasFixedSize(false);
        st_rv.setLayoutManager(lm = new LinearLayoutManager(generiqueActivity));
        st_rv.setItemAnimator(new DefaultItemAnimator());

        adapter = new PlayerStatAdapter(generiqueActivity, playerStatBeanList = new ArrayList<>());
        st_rv.setAdapter(adapter);

        fillView();
        createList();

        return view;
    }


    /* ---------------------------------
    // Menu
    // -------------------------------- */

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        inflater.inflate(R.menu.menu_match, menu);
//        menu.findItem(R.id.mm_end_game).setVisible(matchBean.getStart() != null && matchBean.getEnd() == null);
//
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.mm_state_player:
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//
//    }




    /* ---------------------------------
    // private
    // -------------------------------- */

    private void fillView() {

        //titre
        generiqueActivity.setTitle(getString(R.string.stat_title));


        long reelTime = 0;
        int team = 0, opponent = 0;
        for (PointBean pointBean : matchBean.getPointBeanList()) {
            reelTime += pointBean.getLength();

            //Si le point est termine
            if (pointBean.getTeamGoal() != null) {
                if (pointBean.getTeamGoal()) {
                    team++;
                } else {
                    opponent++;
                }
            }
        }

        //Playing time
        ma_tv_playing_time.setText(Utils.timeToHHMM(matchBean.getEnd().getTime() - matchBean.getStart().getTime()));

        //Win
        ma_iv_win.setVisibility(team > opponent ? View.VISIBLE : View.INVISIBLE);

        //reel playing time
        ma_tv_reel_playing_time.setText(Utils.timeToHHMM(reelTime));

        //Score
        ma_tv_score.setText(team + " - " + opponent);

        //RecycleView
        if (matchBean.getPointBeanList().isEmpty()) {
            st_empty.setVisibility(View.VISIBLE);
            st_rv.setVisibility(View.INVISIBLE);
        } else {
            st_empty.setVisibility(View.INVISIBLE);
            st_rv.setVisibility(View.VISIBLE);
        }


    }

    private void createList() {

        List<PlayerBean> porteBohneur = new ArrayList<>();
        float porteBohneurNumberStat = 0;
        List<PlayerBean> porteMalheur = new ArrayList<>();
        float portemalheurStat = 100;

        //Pour chaque joueur
        for (PlayerBean playerBean : PlayerDaoManager.getPlayers(matchBean.getTeamBean())) {
            PlayerStatBean playerStatBean = new PlayerStatBean();
            playerStatBean.setPlayerBean(playerBean);

            //On parcourt chaque point du match ou il a joué dedans
            for (PointBean pointBean : PointDaoManager.getPointPlayerOfMatch(matchBean, playerBean.getId())) {
                //Uniquement les points terminée
                if (pointBean.getStop() != null) {
                    //Les points d'attaque
                    if (pointBean.getTeamOffense()) {
                        playerStatBean.setGoalAttaque(playerStatBean.getGoalAttaque() + 1);
                        if (pointBean.getTeamGoal()) {
                            playerStatBean.setGoalAttaqueSuccess(playerStatBean.getGoalAttaqueSuccess() + 1);
                        }
                    } else {
                        //Les points de défense
                        playerStatBean.setGoalDefense(playerStatBean.getGoalDefense() + 1);
                        if (pointBean.getTeamGoal()) {
                            playerStatBean.setGoalDefenseSuccess(playerStatBean.getGoalDefenseSuccess() + 1);
                        }
                    }
                }

                playerStatBean.setNumberPoint(playerStatBean.getNumberPoint() + 1);
                playerStatBean.setPlayingTime(playerStatBean.getPlayingTime() + (pointBean.getLength() / Constante
                        .PLAYING_TIME_DIVISE));
            }


            if (playerStatBean.getNumberPoint() > 0) {
                float playerReussite = ((playerStatBean.getGoalAttaqueSuccess() + playerStatBean
                        .getGoalDefenseSuccess()) * 100) / playerStatBean.getNumberPoint();

                //Porte bohneur
                if (playerReussite > porteBohneurNumberStat) {
                    porteBohneur.clear();
                    porteBohneur.add(playerBean);
                    porteBohneurNumberStat = playerReussite;
                } else if (playerReussite == porteBohneurNumberStat) {
                    porteBohneur.add(playerBean);
                }

                //Porte malheur
                if (playerReussite < portemalheurStat) {
                    porteMalheur.clear();
                    porteMalheur.add(playerBean);
                    portemalheurStat = playerReussite;
                } else if (playerReussite == portemalheurStat) {
                    porteMalheur.add(playerBean);
                }
            }

            playerStatBeanList.add(playerStatBean);

        }

        adapter.notifyDataSetChanged();

        //ON affiche nos joueurs chanceux et malchanceux
        String lucky_text = "", unluckyText = "";
        for (PlayerBean playerBean : porteBohneur) {
            lucky_text += playerBean.getName() + " ";
        }
        stat_tv_lucky.setText(lucky_text);

        //
        for (PlayerBean playerBean : porteMalheur) {
            unluckyText += playerBean.getName() + " ";
        }
        stat_tv_unlucky.setText(unluckyText);

    }


    /* ---------------------------------
    // Getter Setter
    // -------------------------------- */
    public void setMatchBean(MatchBean matchBean) {
        this.matchBean = matchBean;
    }
}
