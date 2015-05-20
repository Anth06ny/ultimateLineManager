package com.ultimatelinemanager;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class Constante {

    public static final String DB_TABLE_NAME = "ULM-db.sqlite";
    public final static String ERREUR_GENERIQUE = MyApplication.getInstance().getApplicationContext().getString(R.string.erreur_generique);
    public final static String EMPTY = "  -  ";

    // l'affichage du temps de jeu et non jou�. 1000 pour les secondes (pour les tests) et 60000 pour les minutes
    public final static int PLAYING_TIME_DIVISE = 1000;

    //Request Code
    public static final int PICK_PLAYER_REQ_CODE = 1;
    public static final int MATCH_REQ_CODE = 2;

    //EXTRA
    public final static String TEAM_EXTRA_ID = "TEAM_EXTRA_ID";
    public final static String PLAYER_EXTRA_ID = "PLAYER_EXTRA_ID";
    public final static String MATCH_EXTRA_ID = "MATCH_EXTRA_ID";
    public final static String POINT_EXTRA_ID = "POINT_EXTRA_ID";

}
