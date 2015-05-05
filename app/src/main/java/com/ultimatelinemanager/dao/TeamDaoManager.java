package com.ultimatelinemanager.dao;

import com.ultimatelinemanager.MyApplication;
import com.ultimatelinemanager.dao.match.MatchDaoManager;

import java.util.List;

import greendao.TeamBean;
import greendao.TeamBeanDao;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class TeamDaoManager {

    //pour gerer les équipes
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

    public static void deleteTeam(TeamBean teamBean) {
        //On supprime tous les TeamPlayer
        TeamPlayerManager.deleteTeamPlayer(teamBean.getId(), false);
        //On supprime tous les matches de l'équipe
        MatchDaoManager.deleteMatches(teamBean, false);

        TeamDaoManager.getTeamDAO().delete(teamBean);
        //on suprime le TeamBean
        MyApplication.getInstance().setTeamBean(null);
        MyApplication.getInstance().setLivePoint(null);

        //pour bien le supprimer de la session
        MyApplication.getInstance().getDaoSession().clear();

    }

}
