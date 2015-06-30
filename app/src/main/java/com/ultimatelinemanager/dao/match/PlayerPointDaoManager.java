package com.ultimatelinemanager.dao.match;

import com.formation.utils.LogUtils;
import com.ultimatelinemanager.MyApplication;

import greendao.MatchBeanDao;
import greendao.PlayerBeanDao;
import greendao.PlayerPointDao;
import greendao.PointBeanDao;

/**
 * Created by Anthony on 14/04/2015.
 */
public class PlayerPointDaoManager {

    public static PlayerPointDao getPlayerPointDao() {
        return MyApplication.getInstance().getDaoSession().getPlayerPointDao();
    }

    /**
     *
     * @param playerId
     * @param teamId
     * @return true si le joueur joue au moins 1 point avec l'équipe
     */
    public static boolean isPlayerPlayedAPointWithTeam(long playerId, long teamId) {

        String AllPointFromPlayer = "(Select PB." + PointBeanDao.Properties.Id.columnName + " FROM " + PointBeanDao.TABLENAME + " PB\n";
        AllPointFromPlayer += " INNER JOIN " + MatchBeanDao.TABLENAME + " MB ON MB." + MatchBeanDao.Properties.Id.columnName + " = PB."
                + PointBeanDao.Properties.MatchId.columnName + " \n";
        AllPointFromPlayer += " WHERE MB." + MatchBeanDao.Properties.TeamId.columnName + "=?)  \n";

        String query = " INNER JOIN " + AllPointFromPlayer + " AP ON AP." + PointBeanDao.Properties.Id.columnName + "=T."
                + PlayerPointDao.Properties.PointId.columnName + " \n";
        query += " WHERE T." + PlayerPointDao.Properties.PlayerId.columnName + "=?";

        try {
            return !getPlayerPointDao().queryRawCreate(query, teamId, playerId).list().isEmpty();
        }
        catch (Throwable e) {
            LogUtils.logException(PlayerBeanDao.class, e, true);
            return true;
        }
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
