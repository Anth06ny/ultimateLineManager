package greendao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import greendao.TeamBean;
import greendao.PlayerBean;
import greendao.team_player;

import greendao.TeamBeanDao;
import greendao.PlayerBeanDao;
import greendao.team_playerDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig teamBeanDaoConfig;
    private final DaoConfig playerBeanDaoConfig;
    private final DaoConfig team_playerDaoConfig;

    private final TeamBeanDao teamBeanDao;
    private final PlayerBeanDao playerBeanDao;
    private final team_playerDao team_playerDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        teamBeanDaoConfig = daoConfigMap.get(TeamBeanDao.class).clone();
        teamBeanDaoConfig.initIdentityScope(type);

        playerBeanDaoConfig = daoConfigMap.get(PlayerBeanDao.class).clone();
        playerBeanDaoConfig.initIdentityScope(type);

        team_playerDaoConfig = daoConfigMap.get(team_playerDao.class).clone();
        team_playerDaoConfig.initIdentityScope(type);

        teamBeanDao = new TeamBeanDao(teamBeanDaoConfig, this);
        playerBeanDao = new PlayerBeanDao(playerBeanDaoConfig, this);
        team_playerDao = new team_playerDao(team_playerDaoConfig, this);

        registerDao(TeamBean.class, teamBeanDao);
        registerDao(PlayerBean.class, playerBeanDao);
        registerDao(team_player.class, team_playerDao);
    }
    
    public void clear() {
        teamBeanDaoConfig.getIdentityScope().clear();
        playerBeanDaoConfig.getIdentityScope().clear();
        team_playerDaoConfig.getIdentityScope().clear();
    }

    public TeamBeanDao getTeamBeanDao() {
        return teamBeanDao;
    }

    public PlayerBeanDao getPlayerBeanDao() {
        return playerBeanDao;
    }

    public team_playerDao getTeam_playerDao() {
        return team_playerDao;
    }

}
