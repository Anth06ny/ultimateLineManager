package com.ultimatelinemanager.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ultimatelinemanager.bean.Role;
import com.ultimatelinemanager.metier.exception.LogicException;

import java.util.Date;
import java.util.Random;

import greendao.DaoMaster;
import greendao.PlayerBean;
import greendao.TeamBean;

/**
 * Created by amonteiro on 30/01/2015.
 */
public class MyOpenHelper extends DaoMaster.DevOpenHelper {

    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
        DaoMaster.dropAllTables(db, true);
        onCreate(db);

    }

    public void fillBDD() {

        Random random = new Random();

        //Une équipe
        TeamBean teamBean = new TeamBean();
        teamBean.setName("France");
        teamBean.setTournament("Talampaya");
        teamBean.setCreation(new Date());
        teamBean.setId(TeamDaoManager.getTeamDAO().insert(teamBean));

        //Joueur Talampaya
        PlayerBean[] list = new PlayerBean[] {
                //Homme
                //Handler
                new PlayerBean(null, "Alex", Role.Handler.name(), true, false, 4),
                new PlayerBean(null, "Junior", Role.Handler.name(), true, false, 10),
                new PlayerBean(null, "KhoaVu N'Guyen", Role.Handler.name(), true, false, 24),
                new PlayerBean(null, "Rut", Role.Handler.name(), true, false, 82),

                //Middle
                new PlayerBean(null, "Jo", Role.Middle.name(), true, false, 38),
                new PlayerBean(null, "Oliv", Role.Middle.name(), true, false, 23),
                new PlayerBean(null, "Vyns", Role.Middle.name(), true, false, 69),

                //Polyvalent
                new PlayerBean(null, "Paulo", Role.Both.name(), true, false, 27),
                new PlayerBean(null, "Gael Ancelin", Role.Both.name(), true, false, 77),
                new PlayerBean(null, "Ben", Role.Both.name(), true, false, 88),
                new PlayerBean(null, "PA", Role.Both.name(), true, false, 6),

                //Fille
                //Handleuse
                new PlayerBean(null, "Haude Hermand", Role.Handler.name(), false, false, 11),
                new PlayerBean(null, "Marta Suarez Barcena", Role.Handler.name(), false, false, 79),

                //Middle
                new PlayerBean(null, "Jenny Vallet", Role.Middle.name(), false, false, 32),
                new PlayerBean(null, "Celine Antoine", Role.Middle.name(), false, false, 8),
                new PlayerBean(null, "Moks", Role.Middle.name(), false, false, 16),
                new PlayerBean(null, "Cam", Role.Middle.name(), false, false, 14),
                new PlayerBean(null, "Mazette", Role.Middle.name(), false, false, 12),
                new PlayerBean(null, "Pauline Rigollier", Role.Middle.name(), false, false, 7),

                //polyvalent
                new PlayerBean(null, "Aurelie Bertin", Role.Both.name(), false, false, 2),

        };

        for (PlayerBean playerBean : list) {
            playerBean.setId(PlayerDaoManager.getPlayerDAO().insert(playerBean));
            try {
                TeamPlayerManager.addPlayerToTeam(teamBean.getId(), playerBean.getId());
            }
            catch (LogicException e) {
                e.printStackTrace();
            }
        }

        //
        //        //Match à l'quipe
        //        MatchBean matchBean = new MatchBean();
        //        matchBean.setName("Espagne");
        //        matchBean.setCurrentPoint(1);
        //        matchBean.setTeamBean(teamBean);
        //        matchBean.setId(MatchDaoManager.getMatchBeanDao().insert(matchBean));

    }

}
