package com.ultimatelinemanager;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.ultimatelinemanager.dao.MyOpenHelper;
import com.ultimatelinemanager.dao.TeamDaoManager;

import de.greenrobot.dao.query.QueryBuilder;
import greendao.DaoMaster;
import greendao.DaoSession;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class MyApplication extends Application {

    //TODO Faire Activité principale qui inclu le bandeau

    //TODO Faire confirmation suppression point
    //TODO Continuer ecran point
    //TODO Ecran controle point en cours

    //TODO Ajouter fleche sur les boutons de tri
    //TODO selection multiplayer
    //TODO importer joueur autre equipe

    private static MyApplication instance;
    private DaoSession daoSession;

    public static MyApplication getInstance() {
        return instance;
    }

    public MyApplication() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //On declare les constantes static
        new Constante();

        initDatabase();
    }

    private void initDatabase() {

        MyOpenHelper helper = new MyOpenHelper(this, Constante.DB_TABLE_NAME, null);
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

        if (TeamDaoManager.getLast50Team().isEmpty()) {
            helper.fillBDD();
        }

    }

    /* ---------------------------------
    // Getter /  Setter
    // -------------------------------- */

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
