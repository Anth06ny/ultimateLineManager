package pl.surecase.eu;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class MyDaoGenerator {

    public static void main(String args[]) throws Exception {

        final int DAO_VERSION = 1;

        Schema schema = new Schema(DAO_VERSION, "greendao");

        /* ---------------------------------
        // Table Equipe
        // -------------------------------- */
        Entity team = schema.addEntity("TeamBean");
        team.addIdProperty().getProperty();
        team.implementsSerializable();
        team.addStringProperty("name").notNull();
        team.addDateProperty("creation").notNull();

        /* ---------------------------------
        // Table Joueur
        // -------------------------------- */
        Entity player = schema.addEntity("PlayerBean");
        player.addIdProperty().getProperty();
        player.implementsSerializable();
        player.addStringProperty("name").notNull();
        player.addStringProperty("role").notNull();
        player.addBooleanProperty("sexe").notNull();

        /* ---------------------------------
        // Table Equipe - Joueur
        // -------------------------------- */
        Entity teamPlayer = schema.addEntity("team_player");
        teamPlayer.addIdProperty();
        Property teamId = teamPlayer.addLongProperty("teamId").getProperty();
        teamPlayer.addToOne(team, teamId);
        Property playerId = teamPlayer.addLongProperty("playerId").getProperty();
        teamPlayer.addToOne(player, playerId);

        new DaoGenerator().generateAll(schema, args[0]);
    }

}
