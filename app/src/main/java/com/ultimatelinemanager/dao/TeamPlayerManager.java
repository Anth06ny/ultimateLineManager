package com.ultimatelinemanager.dao;

import com.ultimatelinemanager.MyApplication;
import com.ultimatelinemanager.R;
import com.ultimatelinemanager.metier.exception.LogicException;

import java.util.List;

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

    public static List<TeamPlayer> getPlayerNotInteam(long teamId) {
        return TeamPlayerManager.getTeamPlayerDAO().queryBuilder().where(TeamPlayerDao.Properties.TeamId.notEq(teamId)).list();
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
