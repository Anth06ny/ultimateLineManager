package com.ultimatelinemanager.dao.match;

import com.ultimatelinemanager.MyApplication;
import com.ultimatelinemanager.bean.PlayerPointBean;
import com.ultimatelinemanager.metier.exception.TechnicalException;

import java.util.List;

import greendao.MatchBean;
import greendao.PlayerPoint;
import greendao.PlayerPointDao;
import greendao.PointBean;
import greendao.PointBeanDao;

/**
 * Created by Anthony on 14/04/2015.
 */
public class PointDaoManager {

    public static PointBeanDao getPointBeanDao() {
        return MyApplication.getInstance().getDaoSession().getPointBeanDao();
    }

    public static void savePlayerPointList(PointBean pointBean, List<PlayerPointBean> playerPointBeanList) {

        //On efface tous les player point du point
        PlayerPointDaoManager.getPlayerPointDao().queryBuilder().where(PlayerPointDao.Properties.PointId.eq(pointBean.getId())).buildDelete()
                .executeDeleteWithoutDetachingEntities();

        //on ajoute tous les nouveaux
        for (PlayerPointBean playerPointBean : playerPointBeanList) {
            PlayerPoint pp = new PlayerPoint();
            pp.setPlayerBean(playerPointBean.getPlayerBean());
            pp.setPointBean(pointBean);
            pp.setRole(playerPointBean.getRoleInPoint().name());
            pp.setId(PlayerPointDaoManager.getPlayerPointDao().insert(pp));
        }

        //On invalide la liste de playerPoint
        pointBean.resetPlayerPointList();
        //ON clean la session
        MyApplication.getInstance().getDaoSession().clear();

    }


    /* ---------------------------------
    // Delete
    // -------------------------------- */

    public static void deletePoint(MatchBean matchBean, PointBean pointBean, boolean clearSession) {

        //On parcourt tous les points suivant pour décaler leur numero (le point 4 devient le numéro 3)
        for (PointBean pb : matchBean.getPointBeanList()) {
            if (pb.getNumber() > pointBean.getNumber()) {
                pb.setNumber(pb.getNumber() - 1);
                getPointBeanDao().update(pb);
            }
        }


        //On supprime tous les PlayerPoint
        PlayerPointDaoManager.getPlayerPointDao().deleteInTx(pointBean.getPlayerPointList());

        //enfin on supprime le point
        PointDaoManager.getPointBeanDao().delete(pointBean);
        matchBean.getPointBeanList().remove(pointBean);

        //On recalcule le point courant
        MatchDaoManager.recalculateCurrentPoint(matchBean);

        MatchDaoManager.getMatchBeanDao().update(matchBean);

        if (clearSession) {
            //pour bien le supprimer de la session
            MyApplication.getInstance().getDaoSession().clear();
        }

    }

    /**
     * Supprime tous les points d'un match
     *
     * @param matchBean
     */
    public static void deleteMatchPoint(MatchBean matchBean, boolean clearSession) {
        //On supprime tous les PlayerPoint de tous les matchs
        matchBean.resetPointBeanList();
        for (PointBean pointBean : matchBean.getPointBeanList()) {
            PlayerPointDaoManager.deleteMatchPoint(pointBean.getId(), false);
        }

        //On supprime les points du match
        getPointBeanDao().queryBuilder().where(PointBeanDao.Properties.MatchId.eq(matchBean.getId())).buildDelete()
                .executeDeleteWithoutDetachingEntities();

        if (clearSession) {
            //pour bien le supprimer de la session
            MyApplication.getInstance().getDaoSession().clear();
        }

    }

    /* ---------------------------------
    // Utils
    // -------------------------------- */
    public static PointBean getPointNumber(List<PointBean> list, int number) throws TechnicalException {
        //On cherche le point num�ro "number"
        for (PointBean pointBean : list) {
            if (pointBean.getNumber() == number) {
                return pointBean;
            }
        }

        throw new TechnicalException("Number non trouv� : " + number + " TailleList=" + list.size());

    }
}
