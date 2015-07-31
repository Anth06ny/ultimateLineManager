package com.ultimatelinemanager.metier.utils;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.formation.utils.DateUtils;
import com.ultimatelinemanager.MyApplication;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import greendao.MatchBean;
import greendao.PlayerPoint;
import greendao.PointBean;
import greendao.TeamBean;

/**
 * Created by amonteiro on 03/03/2015.
 */
public class AnalyticsUtils {

    private static StringBuilder sb = new StringBuilder();

    /* ---------------------------------
    // Erreur
    // -------------------------------- */

    /* ---------------------------------
    // Refresh
    // -------------------------------- */

    public static void refreshAnalyticsData() {
        MatchBean matchBean = MyApplication.getInstance().getLiveMatch();
        //Match
        if (matchBean != null) {

            Crashlytics.setString("MatchName", matchBean.getName());
            if (matchBean.getStart() != null) {
                Crashlytics.setString("Match Start", dateToString(matchBean.getStart()));

            }
            if (matchBean.getEnd() != null) {
                Crashlytics.setString("Match End", dateToString(matchBean.getEnd()));
            }
            Crashlytics.setInt("CurrentPoint", matchBean.getCurrentPoint());

        }
        else {
            Crashlytics.setString("MatchBean", "MatchBean  = null");
        }

        //Team
        TeamBean teamBean = MyApplication.getInstance().getTeamBean();
        if (teamBean != null) {
            Crashlytics.setUserName(teamBean.getName());
            Crashlytics.setString("Tournament", teamBean.getTournament());
            Crashlytics.setString("Team Creation", dateToString(teamBean.getCreation()));

        }
        else {
            Crashlytics.setString("TeamBean", "TeamBean  = null");
        }
    }

    /* ---------------------------------
    // LogEvent Team
    // -------------------------------- */
    public static void logNewTeam(String teamName, String tournament) {
        Map<String, String> param = new HashMap<>();
        param.put("teamName", teamName);
        param.put("tournament", tournament);
        FlurryAgent.logEvent("New Team", param);
    }

    /* ---------------------------------
    // Match
    // -------------------------------- */
    public static void logNewMatch(String opponentName) {
        Map<String, String> param = new HashMap<>();
        param.put("opponentName", opponentName);
        FlurryAgent.logEvent("New Match", param);
    }

    public static void logClicOnFilter(String filterName) {
        FlurryAgent.logEvent(filterName);
    }

    public static void logClicOnSort(String sortName) {
        FlurryAgent.logEvent(sortName);
    }

    public static void logStartPoint(PointBean pointBean) {
        Map<String, String> param = new HashMap<>();
        param.put("Number", pointBean.getNumber() + "");
        param.put("Start", dateToString(pointBean.getStart()));

        if (pointBean.getPlayerPointList() != null) {
            sb.setLength(0);

            for (PlayerPoint playerPoint : pointBean.getPlayerPointList()) {
                sb.append(playerPoint.getPlayerBean().getName()).append(" ");
            }
            param.put("Players", sb.toString());
        }
        param.put("Team Offense", pointBean.getTeamOffense() + "");

        FlurryAgent.logEvent("New Point", param);
    }

    public static void logEndPoint(PointBean pointBean) {
        Map<String, String> param = new HashMap<>();
        param.put("Number", pointBean.getNumber() + "");
        param.put("Start", dateToString(pointBean.getStart()));
        param.put("End", dateToString(pointBean.getStop()));

        if (pointBean.getPlayerPointList() != null) {
            sb.setLength(0);

            for (PlayerPoint playerPoint : pointBean.getPlayerPointList()) {
                sb.append(playerPoint.getPlayerBean().getName()).append(" ");
            }
            param.put("Players", sb.toString());
        }
        param.put("Team Offense", pointBean.getTeamOffense() + "");
        param.put("Team Goal", pointBean.getTeamGoal() + "");
        param.put("Length", pointBean.getLength() + "");

        FlurryAgent.logEvent("End Point", param);
    }

    public static void logEndMatch(MatchBean matchBean, String score) {

        Map<String, String> param = new HashMap<>();
        param.put("Score", matchBean.getTeamBean().getName() + " " + score + " " + matchBean.getName());
        FlurryAgent.logEvent("EndMatch", param);
    }

    public static void logStatistic(String stat) {
        Map<String, String> param = new HashMap<>();
        param.put("stat", stat);
        FlurryAgent.logEvent("ClicOnStat", param);
    }

    public static void logClicSendByMail() {
        FlurryAgent.logEvent("ClicSendByMail");
    }

    /* ---------------------------------
    // Player
    // -------------------------------- */

    public static void logPlayerInjured(boolean injured) {
        if (injured) {
            FlurryAgent.logEvent("Player Injured");
        }
        else {
            FlurryAgent.logEvent("Player UnInjured");
        }

    }

    /* ---------------------------------
    // Private
    // -------------------------------- */

    private static String dateToString(Date date) {
        if (date == null) {
            return " - ";
        }

        return DateUtils.dateToString(date,
                DateUtils.getFormat(MyApplication.getInstance().getApplicationContext(), DateUtils.DATE_FORMAT.ddMMyyyy_HHmm));
    }
}
