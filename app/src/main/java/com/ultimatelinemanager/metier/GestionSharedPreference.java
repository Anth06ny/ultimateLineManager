package com.ultimatelinemanager.metier;

import android.content.Context;
import android.content.SharedPreferences;

import com.ultimatelinemanager.MyApplication;

/**
 * Created by amonteiro on 24/04/2015.
 */
public class GestionSharedPreference {

    public static final String MyPREFERENCES = "MyPrefs";

    private static SharedPreferences getSharedPreferences() {
        return MyApplication.getInstance().getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    /* ---------------------------------
    //  Recupere la derniere equipe choisie
    // -------------------------------- */
    private static final String LAST_TEAM_ID = "LAST_TEAM_ID";

    public static long getLastTeamId() {
        return getSharedPreferences().getLong(LAST_TEAM_ID, -1);
    }

    public static void setLastTeamId(long teamId) {

        if (teamId > 0) {
            getSharedPreferences().edit().putLong(LAST_TEAM_ID, teamId).commit();
        }
        else {
            getSharedPreferences().edit().remove(LAST_TEAM_ID);
        }
    }

    /* ---------------------------------
    //  Recupere le dernier point en live
    // -------------------------------- */
    private static final String LAST_POINT_ID = "LAST_POINT_ID";

    public static long getLastPointId() {
        return getSharedPreferences().getLong(LAST_POINT_ID, -1);
    }

    public static void setLastPointId(long pointId) {

        if (pointId > 0) {
            getSharedPreferences().edit().putLong(LAST_POINT_ID, pointId).commit();
        }
        else {
            getSharedPreferences().edit().remove(LAST_POINT_ID);
        }
    }

}
