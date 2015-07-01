package com.ultimatelinemanager.dao;

import com.formation.utils.LogUtils;
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
import greendao.TeamPlayerDao;

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

        //Sous requete contenant l'ensemble des joueurs de l'équipe
        String allTeamPlayer = "(Select * FROM " + TeamPlayerDao.TABLENAME + " TP WHERE TP." + TeamPlayerDao.Properties.TeamId.columnName + "=? )";

        //Jointure indiquant si les joueurs sont de cette equipe ou non
        String query = " LEFT JOIN " + allTeamPlayer + " TP ON TP." + TeamPlayerDao.Properties.PlayerId.columnName + " = T."
                + PlayerBeanDao.Properties.Id.columnName;

        //On ne prend que ceux qui ne le sont pas
        query += " WHERE TP." + TeamPlayerDao.Properties.PlayerId.columnName + " IS NULL \n";
        query += " ORDER BY " + PlayerBeanDao.Properties.Name.columnName;

        try {
            return getPlayerDAO().queryRawCreate(query, teamId).list();
        }
        catch (Throwable e) {
            LogUtils.logException(PlayerBeanDao.class, e, true);
            return new ArrayList<>();
        }

    }

    /**
     * Retourne la liste des joueuss de l'équipe par ordre alphabetique
     * Si la team est nulle renvoit tous les joueur de la base
     */
    public static List<PlayerBean> getPlayers(TeamBean teamBean) {
        return getPlayers(teamBean.getId());
    }

    /**
     * Recupere l'ensemble des joueurs de l'équipe à partir d'un matchId
     * @return
     */
    public static List<PlayerBean> getPlayers(long teamId) {

        String innerJoinTeamPlayer = " INNER JOIN " + TeamPlayerDao.TABLENAME + " TP ON TP." + TeamPlayerDao.Properties.PlayerId.columnName + " = T."
                + PlayerBeanDao.Properties.Id.columnName + "\n";
        String where = " WHERE TP." + TeamPlayerDao.Properties.TeamId.columnName + " = ?";
        String orderBy = " ORDER BY T." + PlayerBeanDao.Properties.Name.columnName + " \n";
        String query = innerJoinTeamPlayer + where + orderBy;

        try {
            return getPlayerDAO().queryRawCreate(query, teamId).list();
        }
        catch (Throwable e) {
            LogUtils.logException(PlayerBeanDao.class, e, true);
            return new ArrayList<>();
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
