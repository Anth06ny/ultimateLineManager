package com.ultimatelinemanager.dao;

import com.ultimatelinemanager.MyApplication;

import java.util.List;

import greendao.TeamBean;
import greendao.TeamBeanDao;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class TeamDaoManager {

    public static TeamBeanDao getTeamDAO() {
        return MyApplication.getInstance().getDaoSession().getTeamBeanDao();
    }

    /**
     * retourne les 50 dernier trié par date
     * @return
     */
    public static List<TeamBean> getLast50Team() {

        return getTeamDAO().queryBuilder().orderDesc(TeamBeanDao.Properties.Creation).limit(50).list();
    }

}
