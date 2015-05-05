package com.ultimatelinemanager.metier;

import android.app.Activity;
import android.content.Intent;

import com.formation.utils.ToastUtils;
import com.ultimatelinemanager.Constante;
import com.ultimatelinemanager.activity.SelectTeamActivity;
import com.ultimatelinemanager.activity.TeamFragment;
import com.ultimatelinemanager.activity.list_players.PickerPlayerFragment;
import com.ultimatelinemanager.activity.match.MatchFragment;
import com.ultimatelinemanager.activity.match.PointFragment;

/**
 * Created by Anthony on 02/03/2015.
 */
public class IntentHelper {

    public static void goToSelectTeamActivity(Activity activity) {
        Intent intent = new Intent(activity, SelectTeamActivity.class);
        activity.startActivity(intent);
    }

    public static void goToTeamActivity(Activity activity) {
        Intent intent = new Intent(activity, TeamFragment.class);
        activity.startActivity(intent);
    }

    /**
     * La page de picker de joueur
     * @param activity
     * @param teamId : Permet de ne pas afficher les joueurs de cette equipe
     * @param requestCode
     */
    public static void goToPickPlayer(Activity activity, long teamId, int requestCode) {
        Intent intent = new Intent(activity, PickerPlayerFragment.class);
        intent.putExtra(Constante.TEAM_EXTRA_ID, teamId);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void goToPlayerPage(Activity activity, long playerId) {
        //TODO faire la page
        ToastUtils.showNotImplementedToast(activity);
    }

    /* ---------------------------------
    // Match
    // -------------------------------- */

    public static void goToMatch(Activity activity, long matchId, boolean startActivityForResult) {
        Intent intent = new Intent(activity, MatchFragment.class);
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

    public static void goToMatchStatistic(Activity activity, long matchId) {
        //TODO
        ToastUtils.showNotImplementedToast(activity);
    }

    /**
     * Le pointId sert aussi de requestCode
     * @param activity
     * @param pointId
     */
    public static void goToPointActivity(Activity activity, long pointId) {
        Intent intent = new Intent(activity, PointFragment.class);
        intent.putExtra(Constante.POINT_EXTRA_ID, pointId);
        activity.startActivityForResult(intent, (int) pointId);
    }
}
