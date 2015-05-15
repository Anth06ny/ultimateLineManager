package com.ultimatelinemanager.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ultimatelinemanager.bean.Role;
import com.ultimatelinemanager.dao.match.MatchDaoManager;
import com.ultimatelinemanager.metier.exception.LogicException;

import java.util.Date;
import java.util.Random;

import greendao.DaoMaster;
import greendao.MatchBean;
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
        teamBean.setCreation(new Date());
        teamBean.setId(TeamDaoManager.getTeamDAO().insert(teamBean));

        //Joueur
        for (int i = 0; i < 12; i++) {
            PlayerBean playerBean = new PlayerBean();
            playerBean.setName("Bob" + i);
            int role = random.nextInt(3);
            if (role == 0) {
                playerBean.setRole(Role.Middle.name());
            }
            else if (role == 1) {
                playerBean.setRole(Role.Handler.name());
            }
            else {
                playerBean.setRole(Role.Both.name());
            }
            playerBean.setSexe(random.nextBoolean());

            //On ajoute le joueur
            playerBean.setId(PlayerDaoManager.getPlayerDAO().insert(playerBean));
            //On l'ajoute à l'equipe
            //On n'ajoute pas tous les joueurs
            if (random.nextInt(10) < 8) {
                try {
                    TeamPlayerManager.addPlayerToTeam(teamBean.getId(), playerBean.getId());
                }
                catch (LogicException e) {
                    e.printStackTrace();
                }
            }
        }

        //Match à l'quipe
        MatchBean matchBean = new MatchBean();
        matchBean.setName("Espagne");
        matchBean.setCurrentPoint(1);
        matchBean.setTeamBean(teamBean);
        matchBean.setId(MatchDaoManager.getMatchBeanDao().insert(matchBean));

    }

}
