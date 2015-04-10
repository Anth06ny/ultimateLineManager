package com.ultimatelinemanager.metier;

import android.app.Activity;
import android.content.Intent;

import com.ultimatelinemanager.activity.ListPlayerActivity;
import com.ultimatelinemanager.activity.TeamActivity;

/**
 * Created by Anthony on 02/03/2015.
 */
public class IntentHelper {

    public static String TEAM_EXTRA_ID = "TEAM_EXTRA_ID";

    public static void goToTeamActivity(Activity activity, Long teamBeanId) {
        Intent intent = new Intent(activity, TeamActivity.class);
        intent.putExtra(TEAM_EXTRA_ID, teamBeanId);
        activity.startActivity(intent);
    }

    /**
     * Page de liste player
     * @param activity
     * @param teamBean
     */
    public static void goToListPlayerActivity(Activity activity, Long teamBeanId) {
        Intent intent = new Intent(activity, ListPlayerActivity.class);
        intent.putExtra(TEAM_EXTRA_ID, teamBeanId);
        activity.startActivity(intent);
    }

    /**
     * La page de picker de joueur
     * @param activity
     * @param requestCode
     */
    public static void goToPickPlayer(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, ListPlayerActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }
}
