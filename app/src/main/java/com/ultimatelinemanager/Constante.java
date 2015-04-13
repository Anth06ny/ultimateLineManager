package com.ultimatelinemanager;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class Constante {

    public static final String DB_TABLE_NAME = "ULM-db.sqlite";
    public final static String ERREUR_GENERIQUE = MyApplication.getInstance().getApplicationContext().getString(R.string.erreur_generique);

    //Request Code
    public static final int PICK_PLAYER_REQ_CODE = 1;

    //EXTRA
    public final static String TEAM_EXTRA_ID = "TEAM_EXTRA_ID";
    public final static String PLAYER_EXTRA_ID = "PLAYER_EXTRA_ID";
    public final static String MATCH_EXTRA_ID = "MATCH_EXTRA_ID";

}
