package greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import greendao.PlayerBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table PLAYER_BEAN.
*/
public class PlayerBeanDao extends AbstractDao<PlayerBean, Long> {

    public static final String TABLENAME = "PLAYER_BEAN";

    /**
     * Properties of entity PlayerBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Name = new Property(1, String.class, "name", false, "NAME");
        public final static Property Firstname = new Property(2, String.class, "firstname", false, "FIRSTNAME");
        public final static Property Surname = new Property(3, String.class, "surname", false, "SURNAME");
        public final static Property Role = new Property(4, int.class, "role", false, "ROLE");
        public final static Property Sexe = new Property(5, boolean.class, "sexe", false, "SEXE");
    };


    public PlayerBeanDao(DaoConfig config) {
        super(config);
    }
    
    public PlayerBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "'PLAYER_BEAN' (" + //
                "'_id' INTEGER PRIMARY KEY ," + // 0: id
                "'NAME' TEXT NOT NULL ," + // 1: name
                "'FIRSTNAME' TEXT," + // 2: firstname
                "'SURNAME' TEXT," + // 3: surname
                "'ROLE' INTEGER NOT NULL ," + // 4: role
                "'SEXE' INTEGER NOT NULL );"); // 5: sexe
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "'PLAYER_BEAN'";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PlayerBean entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindString(2, entity.getName());
 
        String firstname = entity.getFirstname();
        if (firstname != null) {
            stmt.bindString(3, firstname);
        }
 
        String surname = entity.getSurname();
        if (surname != null) {
            stmt.bindString(4, surname);
        }
        stmt.bindLong(5, entity.getRole());
        stmt.bindLong(6, entity.getSexe() ? 1l: 0l);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public PlayerBean readEntity(Cursor cursor, int offset) {
        PlayerBean entity = new PlayerBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getString(offset + 1), // name
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // firstname
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // surname
            cursor.getInt(offset + 4), // role
            cursor.getShort(offset + 5) != 0 // sexe
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PlayerBean entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setName(cursor.getString(offset + 1));
        entity.setFirstname(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSurname(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setRole(cursor.getInt(offset + 4));
        entity.setSexe(cursor.getShort(offset + 5) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PlayerBean entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PlayerBean entity) {
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
    
}