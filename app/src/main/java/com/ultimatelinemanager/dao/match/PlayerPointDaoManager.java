package com.ultimatelinemanager.dao.match;

import com.ultimatelinemanager.MyApplication;

import greendao.PlayerPointDao;

/**
 * Created by Anthony on 14/04/2015.
 */
public class PlayerPointDaoManager {

    public static PlayerPointDao getPlayerPointDao() {
        return MyApplication.getInstance().getDaoSession().getPlayerPointDao();
    }

}
