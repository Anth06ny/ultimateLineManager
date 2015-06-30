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

import com.formation.utils.DateUtils;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import greendao.MatchBean;
import greendao.PlayerBean;
import greendao.PlayerPoint;
import greendao.PointBean;

public class StatFragment extends MainFragment implements View.OnClickListener {

    private static final String TAG = LogUtils.getLogTag(StatFragment.class);

    //Composant graphique
    private ImageView ma_iv_win;
    private TextView ma_tv_playing_time;
    private TextView ma_tv_reel_playing_time;
    private TextView ma_tv_score;
    private TextView st_empty, stat_tv_lucky, stat_tv_unlucky;
    private TextView stat_tv_mail;

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
        stat_tv_mail = (TextView) view.findViewById(R.id.stat_tv_mail);

        stat_tv_mail.setOnClickListener(this);

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
    // Clic
    // -------------------------------- */
    @Override
    public void onClick(View v) {
        if (v == stat_tv_mail) {
            Utils.sendMail(getActivity(), "sujet", createMailText());
        }
    }

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
                }
                else {
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
        }
        else {
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
                    }
                    else {
                        //Les points de défense
                        playerStatBean.setGoalDefense(playerStatBean.getGoalDefense() + 1);
                        if (pointBean.getTeamGoal()) {
                            playerStatBean.setGoalDefenseSuccess(playerStatBean.getGoalDefenseSuccess() + 1);
                        }
                    }
                }

                playerStatBean.setNumberPoint(playerStatBean.getNumberPoint() + 1);
                playerStatBean.setPlayingTime(playerStatBean.getPlayingTime() + (pointBean.getLength() / Constante.PLAYING_TIME_DIVISE));
            }

            //on ne compte pas les joueurs blessés dans le luncky et unlucky
            if (!playerBean.getInjured()) {
                if (playerStatBean.getNumberPoint() > 0) {
                    float playerReussite = ((playerStatBean.getGoalAttaqueSuccess() + playerStatBean.getGoalDefenseSuccess()) * 100)
                            / playerStatBean.getNumberPoint();

                    //Porte bohneur
                    if (playerReussite > porteBohneurNumberStat) {
                        porteBohneur.clear();
                        porteBohneur.add(playerBean);
                        porteBohneurNumberStat = playerReussite;
                    }
                    else if (playerReussite == porteBohneurNumberStat) {
                        porteBohneur.add(playerBean);
                    }

                    //Porte malheur
                    if (playerReussite < portemalheurStat) {
                        porteMalheur.clear();
                        porteMalheur.add(playerBean);
                        portemalheurStat = playerReussite;
                    }
                    else if (playerReussite == portemalheurStat) {
                        porteMalheur.add(playerBean);
                    }
                }
            }

            playerStatBeanList.add(playerStatBean);

        }

        //on trie les joueurs par sexe puis par temps de jeu
        Collections.sort(playerStatBeanList, new Comparator<PlayerStatBean>() {
            @Override
            public int compare(PlayerStatBean lhs, PlayerStatBean rhs) {
                //on trie d'abord par le sexe
                if (lhs.getPlayerBean().getSexe() && !rhs.getPlayerBean().getSexe()) {
                    return -1;
                }
                else if (!lhs.getPlayerBean().getSexe() && rhs.getPlayerBean().getSexe()) {
                    return 1;
                }
                //si le temps de jeu est identique on trie par le nom
                else if (lhs.getPlayingTime() == rhs.getPlayingTime()) {
                    return lhs.getPlayerBean().getName().compareTo(rhs.getPlayerBean().getName());
                }
                else {
                    //Si du même sexe par temps de jeu
                    return (int) (rhs.getPlayingTime() - lhs.getPlayingTime());
                }
            }
        });

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

    private String createMailText() {

        //on passe par une liste intermediaire pour ne pas changer l'ordre
        List<PointBean> temp = new ArrayList<>();
        temp.addAll(matchBean.getPointBeanList());

        //On parcourt tous les points du match trier par numéro
        Collections.sort(temp, new Comparator<PointBean>() {
            @Override
            public int compare(PointBean lhs, PointBean rhs) {
                return lhs.getNumber() - rhs.getNumber();
            }
        });
        String girlColor = "#FF358B";
        String boyColor = "#01B0F0";
        String titleOpen = "<big><big><font color='#106783'><u><b>";
        String titleClose = "</u></b></font></big></big>\n";
        String subTitleOpen = "<big><font color='#018bba'><b>";
        String boySubTitle = "<big><font color='" + boyColor + "'><b>";
        String girlSubTitle = "<big><font color='" + girlColor + "'><b>";
        String subTitleClose = "</b></font></big>\n";
        String label = "<font color='#00B9F1'>";
        String labelClose = "</font>";

        StringBuilder stringBuilder = new StringBuilder();

        //--------------------
        //Point
        //--------------------
        int attaqueNumber = 0, attaqueSuccessNumber = 0, defenseNumber = 0, defenseSuccesnumber = 0;
        StringBuilder pointBuilder = new StringBuilder();

        for (PointBean pointBean : temp) {
            //Que les points joués
            if (pointBean.getStop() != null) {
                //Point 3 : Defense Win
                pointBuilder.append(subTitleOpen).append("Point ").append(pointBean.getNumber()).append(" (");
                pointBuilder.append(Utils.timeToMMSS(pointBean.getLength())).append(") ");
                boolean offense;

                if (pointBean.getTeamOffense() != null && pointBean.getTeamOffense()) {
                    pointBuilder.append(getString(R.string.stat_offense_word));
                    attaqueNumber++;
                    offense = true;
                }
                else {
                    pointBuilder.append(getString(R.string.stat_defense_word));
                    defenseNumber++;
                    offense = false;
                }
                pointBuilder.append(" ");
                if (pointBean.getTeamGoal() != null && pointBean.getTeamGoal()) {
                    pointBuilder.append(getString(R.string.stat_win_word));
                    if (offense) {
                        attaqueSuccessNumber++;
                    }
                    else {
                        defenseSuccesnumber++;
                    }
                }

                pointBuilder.append(subTitleClose);

                //Les joueurs du points
                for (PlayerPoint pp : pointBean.getPlayerPointList()) {
                    pointBuilder.append("  -").append("<font color='").append(pp.getPlayerBean().getSexe() ? boyColor : girlColor).append("'>")
                            .append(pp.getPlayerBean().getName()).append("</font>\n");
                }

                pointBuilder.append("\n");
            }
        }
        pointBuilder.append("\n\n");

        //--------------------
        //Players
        //--------------------
        StringBuilder playerBuilder = new StringBuilder();
        playerBuilder.append(titleOpen).append("Players").append(titleClose);

        playerBuilder.append(label).append(getString(R.string.stat_lucky)).append(" ").append(labelClose).append(stat_tv_lucky.getText())
                .append("\n");

        playerBuilder.append(label).append(getString(R.string.stat_unlucky)).append(" ").append(labelClose).append(stat_tv_unlucky.getText())
                .append("\n\n");

        for (PlayerStatBean playerStatBean : playerStatBeanList) {
            //nom
            playerBuilder.append(playerStatBean.getPlayerBean().getSexe() ? boySubTitle : girlSubTitle).append(
                    playerStatBean.getPlayerBean().getName());
            //injured
            if (playerStatBean.getPlayerBean().getInjured()) {
                playerBuilder.append(" (").append(getString(R.string.injured)).append(")");
            }
            playerBuilder.append(subTitleClose);
            //temps de jeu 1min/1pts
            playerBuilder.append(label).append(getString(R.string.stat_playing_time)).append(" ").append(labelClose)
                    .append(getString(R.string.stat_playing_time_value, playerStatBean.getPlayingTime(), playerStatBean.getNumberPoint()))
                    .append("\n");
            //Attaque (success) 0/3
            playerBuilder.append(label).append(getString(R.string.stat_goal_attaque)).append(" ").append(labelClose)
                    .append(getString(R.string.stat_goal_attaque_value, playerStatBean.getGoalAttaqueSuccess(), playerStatBean.getGoalAttaque()))
                    .append("\n");
            //Defense 0/2
            playerBuilder.append(label).append(getString(R.string.stat_goal_defense)).append(" ").append(labelClose)
                    .append(getString(R.string.stat_goal_defense_value, playerStatBean.getGoalDefenseSuccess(), playerStatBean.getGoalDefense()))
                    .append("\n\n");
        }

        playerBuilder.append("\n\n");

        //--------------------
        //Team 3 - 2 Opponent
        //--------------------
        stringBuilder.append(titleOpen).append(matchBean.getTeamBean().getName()).append(" ").append(ma_tv_score.getText()).append(" ")
                .append(matchBean.getName()).append(titleClose);

        //date
        stringBuilder.append(label).append(getString(R.string.ma_date)).append(labelClose).append(" ")
                .append(DateUtils.dateToString(matchBean.getStart(), DateUtils.getFormat(getActivity(), DateUtils.DATE_FORMAT.ddMMyyyy_HHmm)))
                .append("\n");
        //PlayingTime
        stringBuilder.append(label).append(getString(R.string.ma_playing_time)).append(labelClose).append(" ").append(ma_tv_playing_time.getText())
                .append("\n");
        //ReelPlayingTime
        stringBuilder.append(label).append(getString(R.string.ma_reel_playing_time)).append(labelClose).append(ma_tv_reel_playing_time.getText())
                .append("\n");

        //Point attaque équipe
        stringBuilder.append(label).append(getString(R.string.stat_goal_attaque)).append(labelClose)
                .append(getString(R.string.stat_goal_attaque_value, attaqueSuccessNumber, attaqueNumber)).append("\n");
        //Point defense équipe
        stringBuilder.append(label).append(getString(R.string.stat_goal_defense)).append(labelClose)
                .append(getString(R.string.stat_goal_defense_value, defenseSuccesnumber, defenseNumber)).append("\n\n\n");

        //player
        stringBuilder.append(playerBuilder);
        stringBuilder.append("\n----------------------------------------------\n\n");
        //point
        stringBuilder.append(pointBuilder);

        return stringBuilder.toString().replace("\n", "<br />");
    }

    /* ---------------------------------
    // Getter Setter
    // -------------------------------- */
    public void setMatchBean(MatchBean matchBean) {
        this.matchBean = matchBean;
    }

}
