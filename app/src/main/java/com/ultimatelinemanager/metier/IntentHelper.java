package com.ultimatelinemanager.metier;

import android.app.Activity;
import android.content.Intent;

import com.formation.utils.ToastUtils;
import com.ultimatelinemanager.Constante;
import com.ultimatelinemanager.activity.TeamActivity;
import com.ultimatelinemanager.activity.list_players.PickerPlayerActivity;
import com.ultimatelinemanager.activity.list_players.TeamPlayerActivity;
import com.ultimatelinemanager.activity.match.ListPointsActivity;
import com.ultimatelinemanager.activity.match.MatchActivity;
import com.ultimatelinemanager.activity.match.TeamMatchActivity;

/**
 * Created by Anthony on 02/03/2015.
 */
public class IntentHelper {

    public static void goToTeamActivity(Activity activity, Long teamBeanId) {
        Intent intent = new Intent(activity, TeamActivity.class);
        intent.putExtra(Constante.TEAM_EXTRA_ID, teamBeanId);
        activity.startActivity(intent);
    }

    /**
     * Page de liste player
     * @param activity
     */
    public static void goToListPlayerTeamActivity(Activity activity, Long teamBeanId) {
        Intent intent = new Intent(activity, TeamPlayerActivity.class);
        intent.putExtra(Constante.TEAM_EXTRA_ID, teamBeanId);
        activity.startActivity(intent);
    }

    /**
     * La page de picker de joueur
     * @param activity
     * @param requestCode
     */
    public static void goToPickPlayer(Activity activity, long teamPlayerId, int requestCode) {
        Intent intent = new Intent(activity, PickerPlayerActivity.class);
        intent.putExtra(Constante.TEAM_EXTRA_ID, teamPlayerId);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void goToPlayerPage(Activity activity, long playerId) {
        //TODO faire la page
        ToastUtils.showNotImplementedToast(activity);
    }

    public static void goToTeamMatch(Activity activity, long teamId) {
        Intent intent = new Intent(activity, TeamMatchActivity.class);
        intent.putExtra(Constante.TEAM_EXTRA_ID, teamId);
        activity.startActivity(intent);
    }

    /* ---------------------------------
    // Match
    // -------------------------------- */

    public static void goToMatch(Activity activity, long matchId, boolean startActivityForResult) {
        Intent intent = new Intent(activity, MatchActivity.class);
        intent.putExtra(Constante.MATCH_EXTRA_ID, matchId);
        if (startActivityForResult) {
            activity.startActivityForResult(intent, Constante.MATCH_REQ_CODE);
        }
        else {
            activity.startActivity(intent);
        }
    }

    public static void goToMatchStatePlayer(Activity activity, long matchId) {
        //TODO
        ToastUtils.showNotImplementedToast(activity);
    }

    public static void goToMatchPoint(Activity activity, long matchId) {
        Intent intent = new Intent(activity, ListPointsActivity.class);
        intent.putExtra(Constante.MATCH_EXTRA_ID, matchId);
        activity.startActivity(intent);
    }

    public static void goToMatchStatistic(Activity activity, long matchId) {
        //TODO
        ToastUtils.showNotImplementedToast(activity);
    }

}
