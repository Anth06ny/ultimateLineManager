package greendao;

import greendao.DaoSession;
import de.greenrobot.dao.DaoException;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit. 
/**
 * Entity mapped to table STATE_PLAYER_BEAN.
 */
public class StatePlayerBean {

    private Long id;
    private Long PlayingTime;
    private Long RestTime;
    private Integer StateIndicator;
    private long matchId;
    private long playerId;

    /** Used to resolve relations */
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    private transient StatePlayerBeanDao myDao;

    private MatchBean matchBean;
    private Long matchBean__resolvedKey;

    private PlayerBean playerBean;
    private Long playerBean__resolvedKey;


    public StatePlayerBean() {
    }

    public StatePlayerBean(Long id) {
        this.id = id;
    }

    public StatePlayerBean(Long id, Long PlayingTime, Long RestTime, Integer StateIndicator, long matchId, long playerId) {
        this.id = id;
        this.PlayingTime = PlayingTime;
        this.RestTime = RestTime;
        this.StateIndicator = StateIndicator;
        this.matchId = matchId;
        this.playerId = playerId;
    }

    /** called by internal mechanisms, do not call yourself. */
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getStatePlayerBeanDao() : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayingTime() {
        return PlayingTime;
    }

    public void setPlayingTime(Long PlayingTime) {
        this.PlayingTime = PlayingTime;
    }

    public Long getRestTime() {
        return RestTime;
    }

    public void setRestTime(Long RestTime) {
        this.RestTime = RestTime;
    }

    public Integer getStateIndicator() {
        return StateIndicator;
    }

    public void setStateIndicator(Integer StateIndicator) {
        this.StateIndicator = StateIndicator;
    }

    public long getMatchId() {
        return matchId;
    }

    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    /** To-one relationship, resolved on first access. */
    public MatchBean getMatchBean() {
        long __key = this.matchId;
        if (matchBean__resolvedKey == null || !matchBean__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MatchBeanDao targetDao = daoSession.getMatchBeanDao();
            MatchBean matchBeanNew = targetDao.load(__key);
            synchronized (this) {
                matchBean = matchBeanNew;
            	matchBean__resolvedKey = __key;
            }
        }
        return matchBean;
    }

    public void setMatchBean(MatchBean matchBean) {
        if (matchBean == null) {
            throw new DaoException("To-one property 'matchId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.matchBean = matchBean;
            matchId = matchBean.getId();
            matchBean__resolvedKey = matchId;
        }
    }

    /** To-one relationship, resolved on first access. */
    public PlayerBean getPlayerBean() {
        long __key = this.playerId;
        if (playerBean__resolvedKey == null || !playerBean__resolvedKey.equals(__key)) {
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            PlayerBeanDao targetDao = daoSession.getPlayerBeanDao();
            PlayerBean playerBeanNew = targetDao.load(__key);
            synchronized (this) {
                playerBean = playerBeanNew;
            	playerBean__resolvedKey = __key;
            }
        }
        return playerBean;
    }

    public void setPlayerBean(PlayerBean playerBean) {
        if (playerBean == null) {
            throw new DaoException("To-one property 'playerId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.playerBean = playerBean;
            playerId = playerBean.getId();
            playerBean__resolvedKey = playerId;
        }
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