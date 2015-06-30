package greendao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.greenrobot.dao.AbstractDaoMaster;
import de.greenrobot.dao.identityscope.IdentityScopeType;

import greendao.TeamBeanDao;
import greendao.PlayerBeanDao;
import greendao.TeamPlayerDao;
import greendao.MatchBeanDao;
import greendao.PointBeanDao;
import greendao.PlayerPointDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * Master of DAO (schema version 14): knows all DAOs.
*/
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 14;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(SQLiteDatabase db, boolean ifNotExists) {
        TeamBeanDao.createTable(db, ifNotExists);
        PlayerBeanDao.createTable(db, ifNotExists);
        TeamPlayerDao.createTable(db, ifNotExists);
        MatchBeanDao.createTable(db, ifNotExists);
        PointBeanDao.createTable(db, ifNotExists);
        PlayerPointDao.createTable(db, ifNotExists);
    }
    
    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(SQLiteDatabase db, boolean ifExists) {
        TeamBeanDao.dropTable(db, ifExists);
        PlayerBeanDao.dropTable(db, ifExists);
        TeamPlayerDao.dropTable(db, ifExists);
        MatchBeanDao.dropTable(db, ifExists);
        PointBeanDao.dropTable(db, ifExists);
        PlayerPointDao.dropTable(db, ifExists);
    }
    
    public static abstract class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }
    
    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

    public DaoMaster(SQLiteDatabase db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(TeamBeanDao.class);
        registerDaoClass(PlayerBeanDao.class);
        registerDaoClass(TeamPlayerDao.class);
        registerDaoClass(MatchBeanDao.class);
        registerDaoClass(PointBeanDao.class);
        registerDaoClass(PlayerPointDao.class);
    }
    
    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }
    
    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }
    
}
