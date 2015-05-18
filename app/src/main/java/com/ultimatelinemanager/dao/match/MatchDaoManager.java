package com.ultimatelinemanager.dao.match;

import com.ultimatelinemanager.MyApplication;

import java.util.Date;

import greendao.MatchBean;
import greendao.MatchBeanDao;
import greendao.PointBean;
import greendao.TeamBean;

/**
 * Created by amonteiro on 13/04/2015.
 */
public class MatchDaoManager {

    public static MatchBeanDao getMatchBeanDao() {
        return MyApplication.getInstance().getDaoSession().getMatchBeanDao();
    }



    /* ---------------------------------
    // Delete
    // -------------------------------- */

    /**
     * Supprime tous les matches de l'equipe
     *
     * @param teamBean
     */
    public static void deleteMatches(TeamBean teamBean, boolean clearSession) {
        //On met a jour la liste des match
        teamBean.resetMatchBeanList();

        //on supprime tous les points de tous les matches
        for (MatchBean matchBean : teamBean.getMatchBeanList()) {
            PointDaoManager.deleteMatchPoint(matchBean, false);
        }

        //on supprime tous les matches
        getMatchBeanDao().queryBuilder().where(MatchBeanDao.Properties.TeamId.eq(teamBean.getId())).buildDelete()
                .executeDeleteWithoutDetachingEntities();

        if (clearSession) {
            //pour bien le supprimer de la session
            MyApplication.getInstance().getDaoSession().clear();
        }

    }

    /**
     * Supprime 1 match
     *
     * @param matchBean
     * @param clearSession
     */
    public static void deleteMatch(MatchBean matchBean, boolean clearSession) {
        //on supprime tous les points en relation avec le match
        PointDaoManager.deleteMatchPoint(matchBean, false);
        //on supprime le match
        MatchDaoManager.getMatchBeanDao().delete(matchBean);

        if (clearSession) {
            //pour bien le supprimer de la session
            MyApplication.getInstance().getDaoSession().clear();
        }
    }


    /* ---------------------------------
    // Autre
    // -------------------------------- */

    /**
     * Termine tous les matches qui ont commencé depuis plus de 3H
     */
    public static void closeMatchNotEnd() {

        long threeHour = (3 * 60 * 60 * 1000);
        Date now = new Date();

        for (MatchBean matchBean : MatchDaoManager.getMatchBeanDao().queryBuilder().where(MatchBeanDao.Properties.Start.isNotNull(),
                MatchBeanDao.Properties.End.isNull()).build().list()) {
            if (matchBean.getStart().getTime() + threeHour < now.getTime()) {
                matchBean.setEnd(new Date(matchBean.getStart().getTime() + threeHour));
                MatchDaoManager.getMatchBeanDao().update(matchBean);
            }
        }


    }

    public static void recalculateCurrentPoint(MatchBean matchBean) {

        int lastEndPoint = 0;//Si aucun point terminé

        //on cherche le dernier point terminée
        for (PointBean pointBean : matchBean.getPointBeanList()) {
            if (pointBean.getStop() != null && pointBean.getNumber() > lastEndPoint) {
                lastEndPoint = pointBean.getNumber();
            }
        }

        //Si inferieur au dernier point terminée on le place sur le dernier point
        if (matchBean.getCurrentPoint() < lastEndPoint) {
            matchBean.setCurrentPoint(lastEndPoint);
        }

        //Si superieur au 1er point non commencé on le place sur celui ci
        else if (matchBean.getCurrentPoint() > lastEndPoint + 1) {
            matchBean.setCurrentPoint(lastEndPoint + 1);
        }

    }

}
