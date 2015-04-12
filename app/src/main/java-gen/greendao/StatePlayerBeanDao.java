package greendao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.SqlUtils;
import de.greenrobot.dao.internal.DaoConfig;
import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

import greendao.StatePlayerBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table STATE_PLAYER_BEAN.
*/
public class StatePlayerBeanDao extends AbstractDao<StatePlayerBean, Long> {

    public static final String TABLENAME = "STATE_PLAYER_BEAN";

    /**
     * Properties of entity StatePlayerBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property PlayingTime = new Property(1, Long.class, "PlayingTime", false, "PLAYING_TIME");
        public final static Property RestTime = new Property(2, Long.class, "RestTime", false, "REST_TIME");
        public final static Property StateIndicator = new Property(3, Integer.class, "StateIndicator", false, "STATE_INDICATOR");
        public final static Property MatchId = new Property(4, long.class, "matchId", false, "MATCH_ID");
        public final static Property PlayerId = new Property(5, long.class, "playerId", false, "PLAYER_ID");
    };

    private DaoSession daoSession;

    private Query<StatePlayerBean> matchBean_StatePlayerBeanListQuery;

    public StatePlayerBeanDao(DaoConfig config) {
        super(config);
    }
    
    public StatePlayerBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'STATE_PLAYER_BEAN' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'PLAYING_TIME' INTEGER," + // 1: PlayingTime
                "'REST_TIME' INTEGER," + // 2: RestTime
                "'STATE_INDICATOR' INTEGER," + // 3: StateIndicator
                "'MATCH_ID' INTEGER NOT NULL ," + // 4: matchId
                "'PLAYER_ID' INTEGER NOT NULL );"); // 5: playerId
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'STATE_PLAYER_BEAN'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, StatePlayerBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Long PlayingTime = entity.getPlayingTime();
        if (PlayingTime != null) {
            stmt.bindLong(2, PlayingTime);
        }
 
        Long RestTime = entity.getRestTime();
        if (RestTime != null) {
            stmt.bindLong(3, RestTime);
        }
 
        Integer StateIndicator = entity.getStateIndicator();
        if (StateIndicator != null) {
            stmt.bindLong(4, StateIndicator);
        }
        stmt.bindLong(5, entity.getMatchId());
        stmt.bindLong(6, entity.getPlayerId());
    }

    @Override
    protected void attachEntity(StatePlayerBean entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public StatePlayerBean readEntity(Cursor cursor, int offset) {
        StatePlayerBean entity = new StatePlayerBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1), // PlayingTime
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // RestTime
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // StateIndicator
            cursor.getLong(offset + 4), // matchId
            cursor.getLong(offset + 5) // playerId
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, StatePlayerBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPlayingTime(cursor.isNull(offset + 1) ? null : cursor.getLong(offset + 1));
        entity.setRestTime(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setStateIndicator(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setMatchId(cursor.getLong(offset + 4));
        entity.setPlayerId(cursor.getLong(offset + 5));
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(StatePlayerBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(StatePlayerBean entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "statePlayerBeanList" to-many relationship of MatchBean. */
    public List<StatePlayerBean> _queryMatchBean_StatePlayerBeanList(long matchId) {
        synchronized (this) {
            if (matchBean_StatePlayerBeanListQuery == null) {
                QueryBuilder<StatePlayerBean> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.MatchId.eq(null));
                matchBean_StatePlayerBeanListQuery = queryBuilder.build();
            }
        }
        Query<StatePlayerBean> query = matchBean_StatePlayerBeanListQuery.forCurrentThread();
        query.setParameter(0, matchId);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getMatchBeanDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getPlayerBeanDao().getAllColumns());
            builder.append(" FROM STATE_PLAYER_BEAN T");
            builder.append(" LEFT JOIN MATCH_BEAN T0 ON T.'MATCH_ID'=T0.'_id'");
            builder.append(" LEFT JOIN PLAYER_BEAN T1 ON T.'PLAYER_ID'=T1.'_id'");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected StatePlayerBean loadCurrentDeep(Cursor cursor, boolean lock) {
        StatePlayerBean entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        MatchBean matchBean = loadCurrentOther(daoSession.getMatchBeanDao(), cursor, offset);
         if(matchBean != null) {
            entity.setMatchBean(matchBean);
        }
        offset += daoSession.getMatchBeanDao().getAllColumns().length;

        PlayerBean playerBean = loadCurrentOther(daoSession.getPlayerBeanDao(), cursor, offset);
         if(playerBean != null) {
            entity.setPlayerBean(playerBean);
        }

        return entity;    
    }

    public StatePlayerBean loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<StatePlayerBean> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<StatePlayerBean> list = new ArrayList<StatePlayerBean>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<StatePlayerBean> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<StatePlayerBean> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}