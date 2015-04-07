package com.ultimatelinemanager.dao;

import com.ultimatelinemanager.MyApplication;

import java.util.ArrayList;
import java.util.List;

import greendao.PlayerBean;
import greendao.TeamBean;
import greendao.TeamBeanDao;
import greendao.TeamPlayer;

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

    /**
     * Retourne la liste des joueuss de l'équipe
     * Si la team est nulle renvoit tous les joueur de la base
     */
    public static List<PlayerBean> getPlayers(TeamBean teamBean) {

        //On recupere l'ensemble des teamPlayer ascocié à l'equipe
        if (teamBean != null) {
            ArrayList<PlayerBean> playerBeanArrayList = new ArrayList<>();
            teamBean.refresh();
            for (TeamPlayer teamPlayer : teamBean.getTeamPlayerList()) {
                playerBeanArrayList.add(teamPlayer.getPlayerBean());
            }
            return playerBeanArrayList;
        }
        else {
            //Sinon on retourne l'ensemble des joueur
            return PlayerDaoManager.getAllPlayer();
        }
    }

}
