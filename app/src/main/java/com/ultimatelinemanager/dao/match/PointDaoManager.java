package com.ultimatelinemanager.dao.match;

import com.ultimatelinemanager.MyApplication;

import greendao.PointBeanDao;

/**
 * Created by Anthony on 14/04/2015.
 */
public class PointDaoManager {

    public static PointBeanDao getPointBeanDao() {
        return MyApplication.getInstance().getDaoSession().getPointBeanDao();
    }

}
