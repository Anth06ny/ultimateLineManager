package com.ultimatelinemanager.dao;

import com.formation.utils.exceptions.TechnicalException;
import com.ultimatelinemanager.MyApplication;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import greendao.PlayerBean;
import greendao.PlayerBeanDao;

/**
 * Created by Anthony on 06/04/2015.
 */
public class PlayerDaoManager {

    public static PlayerBeanDao getPlayerDAO() {
        return MyApplication.getInstance().getDaoSession().getPlayerBeanDao();
    }

    public static List<PlayerBean> getAllPlayer() {
        return getPlayerDAO().queryBuilder().orderAsc(PlayerBeanDao.Properties.Name).list();
    }

    /**
     * Verifie si le joueur nexiste pas déjà
     * @param name
     * @return
     */
    public static boolean existPlayer(String name) throws TechnicalException {
        if (StringUtils.isBlank(name)) {
            throw new TechnicalException("L'arg en parametre est null");
        }
        return getPlayerDAO().queryBuilder().where(PlayerBeanDao.Properties.Name.eq(name)).unique() != null;
    }
}
