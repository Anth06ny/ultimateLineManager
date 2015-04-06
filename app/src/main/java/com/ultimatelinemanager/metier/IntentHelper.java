package com.ultimatelinemanager.metier;

import android.app.Activity;
import android.content.Intent;

import com.ultimatelinemanager.activity.ListPlayerActivity;
import com.ultimatelinemanager.activity.TeamActivity;

import greendao.TeamBean;

/**
 * Created by Anthony on 02/03/2015.
 */
public class IntentHelper {

    public static String TEAM_EXTRA = "TEAM_EXTRA";

    public static void goToTeamActivity(Activity activity, TeamBean teamBean) {
        Intent intent = new Intent(activity, TeamActivity.class);
        intent.putExtra(TEAM_EXTRA, teamBean);
        activity.startActivity(intent);
    }

    /**
     * Page de liste player
     * @param activity
     * @param teamBean
     */
    public static void goToListPlayerActivity(Activity activity, TeamBean teamBean) {
        Intent intent = new Intent(activity, ListPlayerActivity.class);
        intent.putExtra(TEAM_EXTRA, teamBean);
        activity.startActivity(intent);
    }
}
