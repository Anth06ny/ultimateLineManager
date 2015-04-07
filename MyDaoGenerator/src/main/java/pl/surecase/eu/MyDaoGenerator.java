package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {

        final int DAO_VERSION = 2;

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

        new DaoGenerator().generateAll(schema, args[0]);
    }

}
