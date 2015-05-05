package com.ultimatelinemanager.dao.match;

import com.ultimatelinemanager.MyApplication;

import greendao.MatchBean;
import greendao.MatchBeanDao;
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

}
