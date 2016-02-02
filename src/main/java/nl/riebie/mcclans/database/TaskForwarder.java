package nl.riebie.mcclans.database;

import java.sql.PreparedStatement;

import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.clan.RankImpl;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.database.implementations.DatabaseSaver;
import nl.riebie.mcclans.player.ClanPlayerImpl;

public class TaskForwarder {

    public static void sendInsertClanPlayer(ClanPlayerImpl clanPlayer) {
        if (TaskExecutor.getInstance().isRunning()) {
            queueTask(DatabaseSaver.getInsertClanPlayerQuery(clanPlayer));
        }
    }

    public static void sendUpdateClanPlayer(ClanPlayerImpl clanPlayer) {
        if (TaskExecutor.getInstance().isRunning()) {
            queueTask(DatabaseSaver.getUpdateClanPlayerQuery(clanPlayer));
        }
    }

    public static void sendDeleteClanPlayer(int clanPlayerID) {
        if (TaskExecutor.getInstance().isRunning()) {
            queueTask(DatabaseSaver.getDeleteClanPlayerQuery(clanPlayerID));
        }
    }

    public static void sendInsertClan(ClanImpl clan) {
        if (TaskExecutor.getInstance().isRunning()) {
            queueTask(DatabaseSaver.getInsertClanQuery(clan));
        }
    }

    public static void sendUpdateClan(ClanImpl clan) {
        if (TaskExecutor.getInstance().isRunning()) {
            queueTask(DatabaseSaver.getUpdateClanQuery(clan));
        }
    }

    public static void sendDeleteClan(int clanID) {
        if (TaskExecutor.getInstance().isRunning()) {
            queueTask(DatabaseSaver.getDeleteClanQuery(clanID));
        }
    }

    public static void sendInsertRank(int clanID, RankImpl rank) {
        if (TaskExecutor.getInstance().isRunning()) {
            queueTask(DatabaseSaver.getInsertRankQuery(clanID, rank));
        }
    }

    public static void sendUpdateRank(RankImpl rank) {
        if (TaskExecutor.getInstance().isRunning()) {
            queueTask(DatabaseSaver.getUpdateRankQuery(rank));
        }
    }

    public static void sendDeleteRank(int rankID) {
        if (TaskExecutor.getInstance().isRunning()) {
            queueTask(DatabaseSaver.getDeleteRankQuery(rankID));
        }
    }

    public static void sendInsertClanAlly(int clanID, int clanID_ally) {
        if (TaskExecutor.getInstance().isRunning()) {
            queueTask(DatabaseSaver.getInsertClanAllyQuery(clanID, clanID_ally));
        }
    }

    public static void sendDeleteClanAlly(int clanID, int clanID_ally) {
        if (TaskExecutor.getInstance().isRunning()) {
            queueTask(DatabaseSaver.getDeleteClanAllyQuery(clanID, clanID_ally));
        }
    }

    public static void queueTask(PreparedStatement query) {
        if (Config.getBoolean(Config.USE_DATABASE)) {
            MCClansDatabaseTask task = new MCClansDatabaseTask(query);
            TaskExecutor.getInstance().enqueue(task);
        }
    }
}