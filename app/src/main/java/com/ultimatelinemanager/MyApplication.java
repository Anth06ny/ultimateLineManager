package com.ultimatelinemanager;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.ultimatelinemanager.dao.MyOpenHelper;

import greendao.DaoMaster;
import greendao.DaoSession;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class MyApplication extends Application {

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

        initDatabase();
    }

    private void initDatabase() {

        MyOpenHelper helper = new MyOpenHelper(this, Constante.DB_TABLE_NAME, null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

    }

    /* ---------------------------------
    // Getter /  Setter
    // -------------------------------- */

    public DaoSession getDaoSession() {
        return daoSession;
    }
}