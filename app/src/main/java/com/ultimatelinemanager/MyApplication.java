package com.ultimatelinemanager;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.squareup.otto.Bus;
import com.ultimatelinemanager.dao.MyOpenHelper;
import com.ultimatelinemanager.dao.TeamDaoManager;
import com.ultimatelinemanager.dao.match.MatchDaoManager;
import com.ultimatelinemanager.dao.match.PointDaoManager;
import com.ultimatelinemanager.metier.GestionSharedPreference;
import com.ultimatelinemanager.metier.exception.TechnicalException;

import de.greenrobot.dao.query.QueryBuilder;
import greendao.DaoMaster;
import greendao.DaoSession;
import greendao.MatchBean;
import greendao.PointBean;
import greendao.TeamBean;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class MyApplication extends Application {

    //TODO un click sur le logo defense/offense d'un point, change son statut
    //TODO M�me chose pour point pour et contre
    //TODO on pause de l'activit� sauvegarde le match
    //TODO Suppression de point, gerer si c'est le point courant

    //TODO Faire confirmation suppression point
    //TODO Continuer ecran point
    //TODO Ecran controle point en cours

    //TODO Ajouter fleche sur les boutons de tri
    //TODO selection multiplayer
    //TODO importer joueur autre equipe
    //TODO cloturer tous les match de plus de 3h en bdd

    private static MyApplication instance;
    private DaoSession daoSession;

    //Data Centrale
    private TeamBean teamBean; //Equipe selectionne

    private MatchBean liveMatch;// match courant

    private Bus bus;

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

        bus = new Bus();
        initDatabase();

        //ON termine les matches commencés depuis tres longtemps
        MatchDaoManager.closeMatchNotEnd();

        //On charge la derniere equipe
        teamBean = TeamDaoManager.getTeamDAO().load(GestionSharedPreference.getLastTeamId());
        //Et le point en cours
        liveMatch = MatchDaoManager.getMatchBeanDao().load(GestionSharedPreference.getLiveMatchId());

        //Si le match n'appartient pas a l'equipe on l'enleve
        if (liveMatch != null && liveMatch.getTeamId() != teamBean.getId()) {
            liveMatch = null;
            GestionSharedPreference.setLiveMatchId(-1);
        }

        if (teamBean != null) {
            teamBean.resetTeamPlayerList();
        }

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

    public MatchBean getLiveMatch() {
        return liveMatch;
    }

    /**
     * @return Retourne le point courant s'il y en a 1
     */
    public PointBean getLivePoint() throws TechnicalException {
        if (liveMatch != null && !liveMatch.getPointBeanList().isEmpty()) {
            return PointDaoManager.getPointNumber(liveMatch.getPointBeanList(), getLiveMatch().getCurrentPoint());
        } else {
            return null;
        }
    }

    public void setLiveMatch(MatchBean liveMatch) {
        this.liveMatch = liveMatch;
    }

    public TeamBean getTeamBean() {
        return teamBean;
    }

    public void setTeamBean(TeamBean teamBean) {
        this.teamBean = teamBean;
        //On sauvegarde dans les preferences le choix de l'equipe
        GestionSharedPreference.setLastTeamId(teamBean != null ? teamBean.getId() : -1);
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public Bus getBus() {
        return bus;
    }
}
