package nl.riebie.mcclans.database;

import java.util.ArrayList;
import java.util.List;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.MCClans;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.database.implementations.DatabaseLoader;
import nl.riebie.mcclans.database.implementations.DatabaseSaver;
import nl.riebie.mcclans.database.implementations.XmlLoader;
import nl.riebie.mcclans.database.implementations.XmlSaver;
import nl.riebie.mcclans.database.interfaces.DataLoader;
import nl.riebie.mcclans.database.interfaces.DataSaver;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;

import javax.security.auth.login.Configuration;

public class DatabaseHandler {

    private List<ClanPlayerImpl> markedClanPlayers = new ArrayList<ClanPlayerImpl>();

    private final String CREATE_TABLE_CLANS_QUERY = "CREATE TABLE IF NOT EXISTS `mcc_clans` "
            + "( "
            + "`clan_id` INT(11) NOT NULL,`clantag` VARCHAR(255) NOT NULL, "
            + "`clanname` VARCHAR(255) NOT NULL,`clanplayer_id_owner` INT(11) NOT NULL, "
            + "`tagcolor` VARCHAR(255) NOT NULL,`allow_ally_invites` TINYINT(1) NOT NULL, "
            + "`clanhome_world` VARCHAR(255) NULL,`clanhome_x` DOUBLE NOT NULL, "
            + "`clanhome_y` DOUBLE NOT NULL,`clanhome_z` DOUBLE NOT NULL,`clanhome_yaw` FLOAT NOT NULL "
            + ",`clanhome_pitch` FLOAT NOT NULL,`clanhome_set_times` INT(11) NOT NULL,`clanhome_set_timestamp` BIGINT NOT NULL,`ff_protection` TINYINT(1) NOT NULL,`creation_time` BIGINT NOT NULL, "
            + "PRIMARY KEY (`clan_id`) " + ") ENGINE=InnoDB;";

    private final String CREATE_TABLE_CLANS_ALLIES_QUERY = "CREATE TABLE IF NOT EXISTS `mcc_clans_allies` " + "( "
            + "`clan_id` INT(11) NOT NULL,`clan_id_ally` INT(11) NOT NULL, " + "PRIMARY KEY (`clan_id`, `clan_id_ally`) " + ") ENGINE=InnoDB;";

    private final String CREATE_TABLE_CLANPLAYERS_QUERY = "CREATE TABLE IF NOT EXISTS `mcc_clanplayers` "
            + "( "
            + "`clanplayer_id` INT NOT NULL,`uuid_most_sig_bits` BIGINT NOT NULL,`uuid_least_sig_bits` BIGINT NOT NULL,`playername` VARCHAR(255) NOT NULL, "
            + "`clan_id` INT(11) NOT NULL,`rank_id` INT(11) NOT NULL,`kills_high` INT(11) NOT NULL "
            + ",`kills_medium` INT(11) NOT NULL,`kills_low` INT(11) NOT NULL, " + "`deaths_high` INT(11) NOT NULL,`deaths_medium` INT(11) NOT NULL, "
            + "`deaths_low` INT(11) NOT NULL,`ff_protection` TINYINT(1) NOT NULL,`last_online_time` BIGINT NOT NULL, "
            + "PRIMARY KEY (`clanplayer_id`) " + ") ENGINE=InnoDB;";

    private final String CREATE_TABLE_RANKS_QUERY = "CREATE TABLE IF NOT EXISTS `mcc_ranks` " + "( "
            + "`rank_id` INT(11) NOT NULL,`clan_id` INT(11) NOT NULL, " + "`rankname` VARCHAR(255) NOT NULL,`permissions` VARCHAR(255) NULL, "
            + "`changeable` TINYINT(1) NOT NULL, " + "PRIMARY KEY (`rank_id`) " + ") ENGINE=InnoDB;";

    private static DatabaseHandler instance;

    protected DatabaseHandler() {
    }

    public static DatabaseHandler getInstance() {
        if (instance == null) {
            instance = new DatabaseHandler();
        }
        return instance;
    }

    public boolean setupConnection() {
        return DatabaseConnectionOwner.getInstance().setupConnection();
    }

    public void setupDatabase() {
        DatabaseConnectionOwner databaseConnectionOwner = DatabaseConnectionOwner.getInstance();
        databaseConnectionOwner.executeStatement(CREATE_TABLE_CLANS_QUERY);
        databaseConnectionOwner.executeStatement(CREATE_TABLE_CLANS_ALLIES_QUERY);
        databaseConnectionOwner.executeStatement(CREATE_TABLE_CLANPLAYERS_QUERY);
        databaseConnectionOwner.executeStatement(CREATE_TABLE_RANKS_QUERY);
    }

    public void clearDatabase() {
        DatabaseConnectionOwner databaseConnectionOwner = DatabaseConnectionOwner.getInstance();
        databaseConnectionOwner.executeStatement("DROP TABLE mcc_clans");
        databaseConnectionOwner.executeStatement("DROP TABLE mcc_clans_allies");
        databaseConnectionOwner.executeStatement("DROP TABLE mcc_clanplayers");
        databaseConnectionOwner.executeStatement("DROP TABLE mcc_ranks");
    }

    public void truncateDatabase() {
        DatabaseConnectionOwner databaseConnectionOwner = DatabaseConnectionOwner.getInstance();
        databaseConnectionOwner.executeStatement("DELETE FROM mcc_clans");
        databaseConnectionOwner.executeStatement("DELETE FROM mcc_clans_allies");
        databaseConnectionOwner.executeStatement("DELETE FROM mcc_clanplayers");
        databaseConnectionOwner.executeStatement("DELETE FROM mcc_ranks");
    }

    public boolean save() {
        DataSaver dataSaver;
        if (Config.getBoolean(Config.USE_DATABASE)) {
            dataSaver = new DatabaseSaver();
        } else {
            dataSaver = new XmlSaver();
        }
        return dataSaver.save();
    }

    public boolean load() {
        DataLoader dataLoader;
        if (Config.getBoolean(Config.USE_DATABASE) && !XmlLoader.loadFilesPresent()) {
            dataLoader = new DatabaseLoader();
        } else {
            dataLoader = new XmlLoader();
        }
        if (dataLoader.load()) {
            markedClanPlayers = dataLoader.getMarkedClanPlayers();
            return true;
        }
        return false;
    }

    public void backup() {
        MCClans.getPlugin().getLogger().info("System backup commencing...", false);

        List<ClanImpl> retrievedClans = ClansImpl.getInstance().getClanImpls();
        List<ClanPlayerImpl> retrievedClanPlayers = ClansImpl.getInstance().getClanPlayerImpls();

        final List<ClanImpl> clans = new ArrayList<ClanImpl>();
        final List<ClanPlayerImpl> clanPlayers = new ArrayList<ClanPlayerImpl>();

        for (ClanImpl retrievedClan : retrievedClans) {
            clans.add(retrievedClan.clone());
        }

        for (ClanPlayerImpl retrievedclanPlayers : retrievedClanPlayers) {
            clanPlayers.add(retrievedclanPlayers.clone());
        }

        Task.Builder taskBuilder = Sponge.getScheduler().createTaskBuilder();
        taskBuilder.execute(new Runnable() {
            @Override
            public void run() {
                XmlSaver xmlSaver = new XmlSaver();
                xmlSaver.useBackupLocation();
                xmlSaver.save(clans, clanPlayers);
                MCClans.getPlugin().getLogger().info("System backup finished", false);
            }
        });
        taskBuilder.async().submit(MCClans.getPlugin());
    }

    public void removeMarkedInactiveClanPlayers() {
        for (ClanPlayerImpl clanPlayer : markedClanPlayers) {
            ClansImpl.getInstance().removeClanPlayer(clanPlayer);
        }
    }
}