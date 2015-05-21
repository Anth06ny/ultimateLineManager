package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    //Diagramme
    //https://www.gliffy.com/go/html5/8011489?app=1b5094b0-6042-11e2-bcfd-0800200c9a66
    public static void main(String args[]) throws Exception {

        final int DAO_VERSION = 10;

        Schema schema = new Schema(DAO_VERSION, "greendao");

        /* ---------------------------------
        // Table Equipe
        // -------------------------------- */
        Entity teamBean = schema.addEntity("TeamBean");
        teamBean.addIdProperty().getProperty();
        teamBean.implementsSerializable();
        teamBean.addStringProperty("name").notNull();
        teamBean.addDateProperty("creation").notNull();

        /* ---------------------------------
        // Table Joueur
        // -------------------------------- */
        Entity playerBean = schema.addEntity("PlayerBean");
        playerBean.addIdProperty().getProperty();
        playerBean.implementsSerializable();
        playerBean.addStringProperty("name").notNull();
        playerBean.addStringProperty("role").notNull();
        playerBean.addBooleanProperty("sexe").notNull();
        playerBean.addIntProperty("number").notNull();

        /* ---------------------------------
        // Table Equipe - Joueur
        // -------------------------------- */
        //On ne peut pas mettre des primaryKey sur plusieurs valeurs
        Entity teamPlayer = schema.addEntity("TeamPlayer");
        teamPlayer.addIdProperty().getProperty();
        //Relation : Team* teamPlayer
        Property teamId = teamPlayer.addLongProperty("teamId").notNull().getProperty();
        teamBean.addToMany(teamPlayer, teamId);
        //Relation : TeamPlayer 1 TeamBean
        teamPlayer.addToOne(teamBean, teamId);
        //Relation : Player * teamPlayer
        Property playerId = teamPlayer.addLongProperty("playerId").notNull().getProperty();
        playerBean.addToMany(teamPlayer, playerId);
        //Relation : TeamPlayer 1 Player
        teamPlayer.addToOne(playerBean, playerId);

        /* ---------------------------------
        // Table Match
        // -------------------------------- */
        Entity matchBean = schema.addEntity("MatchBean");
        matchBean.addIdProperty().getProperty();
        matchBean.addStringProperty("name").notNull();
        matchBean.addDateProperty("start");
        matchBean.addDateProperty("end");
        matchBean.addIntProperty("currentPoint").notNull();

        //Relation : Team * Match
        teamId = matchBean.addLongProperty("teamId").notNull().getProperty();
        teamBean.addToMany(matchBean, teamId);
        //Relation : Match 1 Team
        matchBean.addToOne(teamBean, teamId);

        /* ---------------------------------
        // Point
        // -------------------------------- */
        Entity pointBean = schema.addEntity("PointBean");
        pointBean.addIdProperty().getProperty();
        pointBean.addIntProperty("number").notNull(); //numéro du point
        pointBean.addDateProperty("start");
        pointBean.addDateProperty("stop"); //fin du point
        pointBean.addLongProperty("length").notNull();//durée
        pointBean.addDateProperty("pauseTime");//date de la pause
        pointBean.addBooleanProperty("pause");//en pause
        pointBean.addBooleanProperty("teamOffense"); //attaque ou défense
        pointBean.addBooleanProperty("teamGoal");//but pour ou contre

        //Relation Match * Point
        Property matchId = pointBean.addLongProperty("matchId").notNull().getProperty();
        matchBean.addToMany(pointBean, matchId);
        //Relation Point  1  Match
        pointBean.addToOne(matchBean, matchId);

        /* ---------------------------------
        // PlayerPoint
        // -------------------------------- */
        Entity playerPoint = schema.addEntity("PlayerPoint");
        playerPoint.addIdProperty().getProperty();
        playerPoint.addStringProperty("role").notNull();

        //Relation Player * PlayerPoint  :  inutile car cela recuperera tous les points de tous les match
        playerId = playerPoint.addLongProperty("playerId").notNull().getProperty();
        //Relation PlayerPoint 1 Player
        playerPoint.addToOne(playerBean, playerId);
        //Relation Point * PlayerPoint
        Property pointId = playerPoint.addLongProperty("pointId").notNull().getProperty();
        pointBean.addToMany(playerPoint, pointId);
        //Relation PlayerPoint 1 Point
        playerPoint.addToOne(pointBean, pointId);


        new DaoGenerator().generateAll(schema, args[0]);
    }

}
