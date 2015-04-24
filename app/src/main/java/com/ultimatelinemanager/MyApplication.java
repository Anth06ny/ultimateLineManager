package com.ultimatelinemanager;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.ultimatelinemanager.dao.MyOpenHelper;
import com.ultimatelinemanager.dao.TeamDaoManager;
import com.ultimatelinemanager.dao.match.PointDaoManager;
import com.ultimatelinemanager.metier.GestionSharedPreference;

import de.greenrobot.dao.query.QueryBuilder;
import greendao.DaoMaster;
import greendao.DaoSession;
import greendao.PointBean;
import greendao.TeamBean;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class MyApplication extends Application {

    //TODO faire apparaitre le bandeau pour le match en cours
    //TODO Faire Activite principale qui inclu le bandeau

    //TODO Faire confirmation suppression point
    //TODO Continuer ecran point
    //TODO Ecran controle point en cours

    //TODO Ajouter fleche sur les boutons de tri
    //TODO selection multiplayer
    //TODO importer joueur autre equipe

    private static MyApplication instance;
    private DaoSession daoSession;

    //Data Centrale
    private TeamBean teamBean; //Equipe selectionne
    private PointBean livePoint; //Point en cours

    public static MyApplication getInstance() {
        return instance;
    }

    public MyApplication() {
        instance = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //On declare les constantes static
        new Constante();

        initDatabase();

        //On charge la derniere équipe
        teamBean = TeamDaoManager.getTeamDAO().load(GestionSharedPreference.getLastTeamId());
        //Et le point en cours
        livePoint = PointDaoManager.getPointBeanDao().load(GestionSharedPreference.getLastPointId());
    }

    private void initDatabase() {

        MyOpenHelper helper = new MyOpenHelper(this, Constante.DB_TABLE_NAME, null);
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

        if (TeamDaoManager.getLast50Team().isEmpty()) {
            helper.fillBDD();
        }

    }

    /* ---------------------------------
    // Getter /  Setter
    // -------------------------------- */

    public PointBean getLivePoint() {
        return livePoint;
    }

    public void setLivePoint(PointBean livePoint) {
        this.livePoint = livePoint;
    }

    public TeamBean getTeamBean() {
        return teamBean;
    }

    public void setTeamBean(TeamBean teamBean) {
        this.teamBean = teamBean;
        //On sauvegarde dans les préférences le choix de l'équipe
        GestionSharedPreference.setLastTeamId(teamBean != null ? teamBean.getId() : -1);
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
