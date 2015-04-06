package com.ultimatelinemanager.dao;

import com.ultimatelinemanager.MyApplication;

import greendao.PlayerBeanDao;

/**
 * Created by Anthony on 06/04/2015.
 */
public class PlayerDaoManager {

    public static PlayerBeanDao getPlayerDAO() {
        return MyApplication.getInstance().getDaoSession().getPlayerBeanDao();
    }

    public static boolean existPlayer(String name) {
        return getPlayerDAO().queryBuilder().where(PlayerBeanDao.Properties.Name.eq(name)).unique() != null;
    }
}
