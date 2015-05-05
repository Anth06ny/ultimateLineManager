package com.ultimatelinemanager.dao.match;

import com.ultimatelinemanager.MyApplication;
import com.ultimatelinemanager.bean.PlayerPointBean;

import java.util.List;

import greendao.MatchBean;
import greendao.PlayerPoint;
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
        PlayerPointDaoManager.getPlayerPointDao().deleteInTx(pointBean.getPlayerPointList());

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

    }

    public static void deletePoint(PointBean pointBean, boolean clearSession) {
        //On supprime tous les PlayerPoint
        PlayerPointDaoManager.getPlayerPointDao().deleteInTx(pointBean.getPlayerPointList());
        PointDaoManager.getPointBeanDao().delete(pointBean);

        if (clearSession) {
            //pour bien le supprimer de la session
            MyApplication.getInstance().getDaoSession().clear();
        }

    }

    /**
     * Supprime tous les points d'un match
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

}
