package com.ultimatelinemanager.metier;

import android.content.Context;
import android.content.SharedPreferences;

import com.formation.utils.LogUtils;
import com.ultimatelinemanager.MyApplication;
import com.ultimatelinemanager.adapter.PlayerPointAdapter;
import com.ultimatelinemanager.bean.Role;
import com.ultimatelinemanager.metier.exception.TechnicalException;

/**
 * Created by amonteiro on 24/04/2015.
 */
public class GestionSharedPreference {

    public static final String MyPREFERENCES = "MyPrefs";

    private static SharedPreferences getSharedPreferences() {
        return MyApplication.getInstance().getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getEditor() {
        return getSharedPreferences().edit();
    }

    /* ---------------------------------
    //  Recupere la derniere equipe choisie
    // -------------------------------- */
    private static final String LAST_TEAM_ID = "LAST_TEAM_ID";

    public static long getLastTeamId() {
        return getSharedPreferences().getLong(LAST_TEAM_ID, -1);
    }

    public static void setLastTeamId(long teamId) {
        SharedPreferences.Editor editor = getEditor();

        if (teamId > 0) {
            editor.putLong(LAST_TEAM_ID, teamId);
        }
        else {
            editor.remove(LAST_TEAM_ID);
        }
        editor.apply();
    }

    /* ---------------------------------
    //  Recupere le dernier point en live
    // -------------------------------- */
    private static final String LIVE_MATCH_ID = "LIVE_MATCH_ID";

    public static long getLiveMatchId() {
        return getSharedPreferences().getLong(LIVE_MATCH_ID, -1);
    }

    public static void setLiveMatchId(long matchId) {
        SharedPreferences.Editor editor = getEditor();
        if (matchId > 0) {
            editor.putLong(LIVE_MATCH_ID, matchId);
        }
        else {
            editor.remove(LIVE_MATCH_ID);
        }

        editor.apply();
    }

    /* ---------------------------------
    // Sauvegarde les filtres et tries utilisés
    // -------------------------------- */
    private static final String ROLE_FILTRE = "ROLE_FILTRE";
    private static final String SEXE_FILTRE = "SEXE_FILTRE";
    private static final String SORT_ORDER = "SORT_ORDER";

    /**
     *
     * @return le dernier filtre utilisé pour le Role
     */
    public static Role getLastFiltreRole() {
        return Role.getRole(getSharedPreferences().getString(ROLE_FILTRE, Role.Both.name()));
    }

    public static void setLastFiltreRole(Role role) {
        if (role != null) {
            getEditor().putString(ROLE_FILTRE, role.name()).apply();
        }
    }

    /**
     *
     * @return le dernier filtre utilisé pour le Sexe
     */
    public static PlayerPointAdapter.FilterSexe getLastFiltreSexe() {
        String sexe = getSharedPreferences().getString(SEXE_FILTRE, PlayerPointAdapter.FilterSexe.BOTH.name());

        try {
            return PlayerPointAdapter.FilterSexe.valueOf(sexe);
        }
        catch (Exception e) {
            LogUtils.logException(PlayerPointAdapter.FilterSexe.class, new TechnicalException("Le sexe transmit " + "n'existe pas = " + sexe), true);
            return PlayerPointAdapter.FilterSexe.BOTH;
        }
    }

    public static void setLastFiltreSexe(PlayerPointAdapter.FilterSexe sexe) {
        if (sexe != null) {
            getEditor().putString(SEXE_FILTRE, sexe.name()).apply();
        }
    }

    /**
     *
     * @return le dernier trie utilisé
     */
    public static PlayerPointAdapter.SortOrder getLastSort() {
        String sortOrder = getSharedPreferences().getString(SORT_ORDER, PlayerPointAdapter.SortOrder.AZ.name());

        try {
            return PlayerPointAdapter.SortOrder.valueOf(sortOrder);
        }
        catch (Exception e) {
            LogUtils.logException(PlayerPointAdapter.SortOrder.class, new TechnicalException("L'ordre transmit " + "n'existe pas = " + sortOrder),
                    true);
            return PlayerPointAdapter.SortOrder.AZ;
        }
    }

    public static void setLastSort(PlayerPointAdapter.SortOrder order) {
        if (order != null) {
            getEditor().putString(SORT_ORDER, order.name()).apply();
        }
    }

}
