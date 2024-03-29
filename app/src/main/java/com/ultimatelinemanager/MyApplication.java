package com.ultimatelinemanager;

import android.app.Application;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.squareup.otto.Bus;
import com.ultimatelinemanager.dao.MyOpenHelper;
import com.ultimatelinemanager.dao.PlayerDaoManager;
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
import io.fabric.sdk.android.Fabric;

/**
 * Created by amonteiro on 13/02/2015.
 */
public class MyApplication extends Application {

    //A continuer
    //TODO ajouter tous les �venements de flurry dans le code

    //Appli
    //TODO Indiquer la mi temps.

    // differents endroit
    //TODO ecran about
    //TODO ecran settings

    //Parametre
    //TODO ecran reglage appli : Email, choix des filtres, matche mixte
    //TODO ecran parametre : temps alerte joueur pas jouer.

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

        initAnalytics();

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

    private void initAnalytics() {
        //CrashLytics
        Fabric.with(this, new Crashlytics());

        // Flurry
        try {
            FlurryAgent.setVersionName(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        }
        catch (PackageManager.NameNotFoundException ignored) {
        }

        FlurryAgent.setCaptureUncaughtExceptions(false);
        FlurryAgent.init(this, getString(R.string.flurry_id));

    }

    private void initDatabase() {

        MyOpenHelper helper = new MyOpenHelper(this, Constante.DB_TABLE_NAME, null);
        QueryBuilder.LOG_SQL = true;
        QueryBuilder.LOG_VALUES = true;
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

        if (PlayerDaoManager.getPlayerDAO().count() == 0) {
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
        }
        else {
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
