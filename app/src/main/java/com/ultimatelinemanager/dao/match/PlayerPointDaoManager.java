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

    /* ---------------------------------
    // Delete
    // -------------------------------- */

    /**
     * Supprime tous les playerPoint du point
     */
    public static void deleteMatchPoint(long pointId, boolean clearSession) {
        getPlayerPointDao().queryBuilder().where(PlayerPointDao.Properties.PointId.eq(pointId)).buildDelete().executeDeleteWithoutDetachingEntities();

        if (clearSession) {
            //pour bien le supprimer de la session
            MyApplication.getInstance().getDaoSession().clear();
        }

    }

}
