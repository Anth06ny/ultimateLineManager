package com.ultimatelinemanager.dao;

import com.ultimatelinemanager.MyApplication;
import com.ultimatelinemanager.metier.exception.TechnicalException;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import greendao.PlayerBean;
import greendao.PlayerBeanDao;
import greendao.TeamBean;
import greendao.TeamPlayer;

/**
 * Created by Anthony on 06/04/2015.
 */
public class PlayerDaoManager {

    public static PlayerBeanDao getPlayerDAO() {
        return MyApplication.getInstance().getDaoSession().getPlayerBeanDao();
    }

    /* ---------------------------------
    // Get
    // -------------------------------- */

    public static List<PlayerBean> getAllPlayer() {
        return getPlayerDAO().queryBuilder().orderAsc(PlayerBeanDao.Properties.Name).list();
    }

    /**
     * Retourne la liste des joueurs non inscrit dans l'equipe
     * @param teamId
     * @return
     */
    public static List<PlayerBean> getPlayerNotInTeam(long teamId) {
        return convertToPlayerBean(TeamPlayerManager.getPlayerNotInteam(teamId));
    }

    /**
     * Retourne la liste des joueuss de l'équipe par ordre alphabetique
     * Si la team est nulle renvoit tous les joueur de la base
     */
    public static List<PlayerBean> getPlayers(TeamBean teamBean) {

        //On recupere l'ensemble des teamPlayer ascocié à l'equipe
        if (teamBean != null) {
            teamBean.resetTeamPlayerList();
            return convertToPlayerBean(teamBean.getTeamPlayerList());
        }
        else {
            //Sinon on retourne l'ensemble des joueurs
            return PlayerDaoManager.getAllPlayer();
        }
    }

    /* ---------------------------------
    // Autre
    // -------------------------------- */

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

    /**
     * Convertie une liste de Teamplayer en player
     * @param teamPlayerList
     * @return
     */
    public static ArrayList<PlayerBean> convertToPlayerBean(List<TeamPlayer> teamPlayerList) {
        ArrayList<PlayerBean> playerBeanArrayList = new ArrayList<>();

        if (teamPlayerList != null) {
            for (TeamPlayer teamPlayer : teamPlayerList) {
                playerBeanArrayList.add(teamPlayer.getPlayerBean());
            }

            //on trie la list par nom
            Collections.sort(playerBeanArrayList, new Comparator<PlayerBean>() {
                @Override
                public int compare(PlayerBean lhs, PlayerBean rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
        }

        return playerBeanArrayList;

    }

}
