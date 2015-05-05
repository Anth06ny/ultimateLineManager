package com.ultimatelinemanager.dao;

import com.ultimatelinemanager.MyApplication;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.metier.exception.LogicException;

import greendao.TeamPlayer;
import greendao.TeamPlayerDao;

/**
 * Created by Anthony on 11/04/2015.
 */
public class TeamPlayerManager {

    /* ---------------------------------
    // Get
    // -------------------------------- */

    /**
     * Pour gerer la liaison joueur equipe
     * @return
     */
    public static TeamPlayerDao getTeamPlayerDAO() {
        return MyApplication.getInstance().getDaoSession().getTeamPlayerDao();
    }

    public static TeamPlayer getTeamPlayer(long teamId, long playerId) {
        return getTeamPlayerDAO().queryBuilder().where(TeamPlayerDao.Properties.TeamId.eq(teamId), TeamPlayerDao.Properties.PlayerId.eq(playerId))
                .unique();
    }

    /* ---------------------------------
    // Delete
    // -------------------------------- */

    /**
     * Supprime toute les relations de joueur de l'equipe
     * @param teamId
     */
    public static void deleteTeamPlayer(long teamId, boolean clearSession) {
        getTeamPlayerDAO().queryBuilder().where(TeamPlayerDao.Properties.TeamId.eq(teamId)).buildDelete().executeDeleteWithoutDetachingEntities();
        if (clearSession) {
            //pour bien le supprimer de la session
            MyApplication.getInstance().getDaoSession().clear();
        }
    }

    /* ---------------------------------
    // Autre
    // -------------------------------- */

    /**
     * Ajoute un joueur dans une équipe
     * @param teamId
     * @param playerId
     */
    public static void addPlayerToTeam(long teamId, long playerId) throws LogicException {
        TeamPlayer teamPlayer = getTeamPlayer(teamId, playerId);

        if (teamPlayer != null) {
            throw new LogicException(R.string.player_alreay_add);
        }

        teamPlayer = new TeamPlayer(null, teamId, playerId);
        getTeamPlayerDAO().insert(teamPlayer);
    }
}
