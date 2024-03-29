package greendao;

import java.util.List;
import greendao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table TEAM_BEAN.
 */
public class TeamBean implements java.io.Serializable {

    private Long id;
    /** Not-null value. */
    private String name;
    private String tournament;
    /** Not-null value. */
    private java.util.Date creation;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient TeamBeanDao myDao;

    private List<TeamPlayer> teamPlayerList;
    private List<MatchBean> matchBeanList;

    public TeamBean() {
    }

    public TeamBean(Long id) {
        this.id = id;
    }

    public TeamBean(Long id, String name, String tournament, java.util.Date creation) {
        this.id = id;
        this.name = name;
        this.tournament = tournament;
        this.creation = creation;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getTeamBeanDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** Not-null value. */
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(String name) {
        this.name = name;
    }

    public String getTournament() {
        return tournament;
    }

    public void setTournament(String tournament) {
        this.tournament = tournament;
    }

    /** Not-null value. */
    public java.util.Date getCreation() {
        return creation;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCreation(java.util.Date creation) {
        this.creation = creation;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<TeamPlayer> getTeamPlayerList() {
        if (teamPlayerList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TeamPlayerDao targetDao = daoSession.getTeamPlayerDao();
            List<TeamPlayer> teamPlayerListNew = targetDao._queryTeamBean_TeamPlayerList(id);
            synchronized (this) {
                if(teamPlayerList == null) {
                    teamPlayerList = teamPlayerListNew;
                }
            }
        }
        return teamPlayerList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetTeamPlayerList() {
        teamPlayerList = null;
    }

    /** To-many relationship, resolved on first access (and after reset). Changes to to-many relations are not persisted, make changes to the target entity. */
    public List<MatchBean> getMatchBeanList() {
        if (matchBeanList == null) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MatchBeanDao targetDao = daoSession.getMatchBeanDao();
            List<MatchBean> matchBeanListNew = targetDao._queryTeamBean_MatchBeanList(id);
            synchronized (this) {
                if(matchBeanList == null) {
                    matchBeanList = matchBeanListNew;
                }
            }
        }
        return matchBeanList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    public synchronized void resetMatchBeanList() {
        matchBeanList = null;
    }

    /** Convenient call for {@link AbstractDao#delete(Object)}. Entity must attached to an entity context. */
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.delete(this);
    }

    /** Convenient call for {@link AbstractDao#update(Object)}. Entity must attached to an entity context. */
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.update(this);
    }

    /** Convenient call for {@link AbstractDao#refresh(Object)}. Entity must attached to an entity context. */
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }    
        myDao.refresh(this);
    }

}
