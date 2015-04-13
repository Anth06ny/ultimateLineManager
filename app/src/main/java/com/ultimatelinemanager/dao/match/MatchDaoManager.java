package com.ultimatelinemanager.dao.match;

import com.ultimatelinemanager.MyApplication;

import greendao.MatchBeanDao;

/**
 * Created by amonteiro on 13/04/2015.
 */
public class MatchDaoManager {

    public static MatchBeanDao getMatchBeanDao() {
        return MyApplication.getInstance().getDaoSession().getMatchBeanDao();
    }

}
