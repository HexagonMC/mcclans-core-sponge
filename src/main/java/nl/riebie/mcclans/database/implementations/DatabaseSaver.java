package nl.riebie.mcclans.database.implementations;

import nl.riebie.mcclans.api.enums.Permission;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.clan.RankImpl;
import nl.riebie.mcclans.database.DatabaseConnectionOwner;
import nl.riebie.mcclans.database.DatabaseHandler;
import nl.riebie.mcclans.database.QueryGenerator;
import nl.riebie.mcclans.database.interfaces.DataSaver;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.PreparedStatement;
import java.util.UUID;

public class DatabaseSaver extends DataSaver {

    private final static DatabaseConnectionOwner databaseConnectionOwner = DatabaseConnectionOwner.getInstance();

    protected void saveClan(ClanImpl clan) throws Exception {
        PreparedStatement query = getInsertClanQuery(clan);
        databaseConnectionOwner.executeTransactionStatement(query);
    }

    protected void saveClanPlayer(ClanPlayerImpl cp) throws Exception {
        PreparedStatement query = getInsertClanPlayerQuery(cp);
        databaseConnectionOwner.executeTransactionStatement(query);
    }

    protected void saveRank(int clanID, RankImpl rank) throws Exception {
        PreparedStatement query = getInsertRankQuery(clanID, rank);
        databaseConnectionOwner.executeTransactionStatement(query);
    }

    protected void saveClanAlly(int clanID, int clanIDAlly) throws Exception {
        PreparedStatement query = getInsertClanAllyQuery(clanID, clanIDAlly);
        databaseConnectionOwner.executeTransactionStatement(query);
    }

    @Override
    protected void saveStarted() throws Exception {
        databaseConnectionOwner.startTransaction();
        DatabaseHandler.getInstance().truncateDatabase();
    }

    @Override
    protected void saveFinished() throws Exception {
        databaseConnectionOwner.commitTransaction();
    }

    @Override
    protected void saveCancelled() {
        databaseConnectionOwner.cancelTransaction();
    }

    public static PreparedStatement getInsertClanPlayerQuery(ClanPlayerImpl clanPlayer) {
        int clanPlayerID = clanPlayer.getID();
        UUID uuid = clanPlayer.getUUID();
        String name = clanPlayer.getName();
        int clanID = -1;
        int rankID = -1;
        if (clanPlayer.getClan() != null) {
            clanID = clanPlayer.getClan().getID();
            rankID = clanPlayer.getRank().getID();
        }
        int killsHigh = clanPlayer.getKillsHigh();
        int killsMedium = clanPlayer.getKillsMedium();
        int killsLow = clanPlayer.getKillsLow();
        int deathsHigh = clanPlayer.getDeathsHigh();
        int deathsMedium = clanPlayer.getDeathsMedium();
        int deathsLow = clanPlayer.getDeathsLow();

        boolean ffProtection = clanPlayer.isFfProtected();
        long lastOnlineTime = clanPlayer.getLastOnline().getTime();

        return QueryGenerator.createInsertQuery("mcc_clanplayers", databaseConnectionOwner.getConnection()).value("clanplayer_id", clanPlayerID)
                .value("uuid_most_sig_bits", uuid.getMostSignificantBits()).value("uuid_least_sig_bits", uuid.getLeastSignificantBits())
                .value("playername", name).value("clan_id", clanID).value("rank_id", rankID).value("kills_high", killsHigh)
                .value("kills_medium", killsMedium).value("kills_low", killsLow).value("deaths_high", deathsHigh)
                .value("deaths_medium", deathsMedium).value("deaths_low", deathsLow).value("last_online_time", lastOnlineTime)
                .value("ff_protection", ffProtection).create();

    }

    public static PreparedStatement getUpdateClanPlayerQuery(ClanPlayerImpl clanPlayer) {
        int clanPlayerID = clanPlayer.getID();
        UUID uuid = clanPlayer.getUUID();
        String name = clanPlayer.getName();
        int clanID = -1;
        int rankID = -1;
        if (clanPlayer.getClan() != null) {
            clanID = clanPlayer.getClan().getID();
            rankID = clanPlayer.getRank().getID();
        }
        int killsHigh = clanPlayer.getKillsHigh();
        int killsMedium = clanPlayer.getKillsMedium();
        int killsLow = clanPlayer.getKillsLow();
        int deathsHigh = clanPlayer.getDeathsHigh();
        int deathsMedium = clanPlayer.getDeathsMedium();
        int deathsLow = clanPlayer.getDeathsLow();

        boolean ffProtection = clanPlayer.isFfProtected();
        long lastOnlineTime = clanPlayer.getLastOnline().getTime();

        return QueryGenerator.createUpdateQuery("mcc_clanplayers", databaseConnectionOwner.getConnection())
                .value("uuid_most_sig_bits", uuid.getMostSignificantBits()).value("uuid_least_sig_bits", uuid.getLeastSignificantBits())
                .value("playername", name).value("clan_id", clanID).value("rank_id", rankID).value("kills_high", killsHigh)
                .value("kills_medium", killsMedium).value("kills_low", killsLow).value("deaths_high", deathsHigh)
                .value("deaths_medium", deathsMedium).value("deaths_low", deathsLow).value("last_online_time", lastOnlineTime)
                .value("ff_protection", ffProtection).where("clanplayer_id", clanPlayerID).create();

    }

    public static PreparedStatement getDeleteClanPlayerQuery(int clanPlayerID) {
        return QueryGenerator.createDeleteQuery("mcc_clanplayers", databaseConnectionOwner.getConnection()).where("clanplayer_id", clanPlayerID)
                .create();
    }

    public static PreparedStatement getInsertClanQuery(ClanImpl clan) {
        int clanID = clan.getID();
        String tag = clan.getTag();
        String name = clan.getName();
        int ownerID = clan.getOwner().getID();
        String tagColorId = clan.getTagColor().getId();
        boolean allowAllyInvites = clan.isAllowingAllyInvites();
        boolean ffProtection = clan.isFfProtected();
        long creationTime = clan.getCreationDate().getTime();
        Location<World> clanHome = clan.getHome();
        String clanHomeWorld = null;
        double clanHomeX = 0;
        double clanHomeY = 0;
        double clanHomeZ = 0;
        float clanHomeYaw = 0;
        float clanHomePitch = 0;
        if (clanHome != null) {
            clanHomeWorld = clanHome.getExtent().getUniqueId().toString();
            clanHomeX = clanHome.getX();
            clanHomeY = clanHome.getY();
            clanHomeZ = clanHome.getZ();
            // TODO SPONGE vector3d or something
//            clanHomeYaw = clanHome.getYaw();
//            clanHomePitch = clanHome.getPitch();
        }
        int homeSetTimes = clan.getHomeSetTimes();
        long homeLastSetTimeStamp = clan.getHomeSetTimeStamp();

        return QueryGenerator.createInsertQuery("mcc_clans", databaseConnectionOwner.getConnection()).value("clan_id", clanID).value("clantag", tag)
                .value("clanname", name).value("clanplayer_id_owner", ownerID).value("tagcolor", tagColorId)
                .value("allow_ally_invites", allowAllyInvites).value("clanhome_world", clanHomeWorld).value("clanhome_x", clanHomeX)
                .value("clanhome_y", clanHomeY).value("clanhome_z", clanHomeZ).value("clanhome_yaw", clanHomeYaw)
                .value("clanhome_pitch", clanHomePitch).value("clanhome_set_times", homeSetTimes)
                .value("clanhome_set_timestamp", homeLastSetTimeStamp).value("ff_protection", ffProtection).value("creation_time", creationTime)
                .create();
    }

    public static PreparedStatement getUpdateClanQuery(ClanImpl clan) {
        int clanID = clan.getID();
        String tag = clan.getTag();
        String name = clan.getName();
        int ownerID = clan.getOwner().getID();
//        String tagColor = clan.getTagColor();    //TODO tagcolor -> SPONGE!!
        boolean allowAllyInvites = clan.isAllowingAllyInvites();
        boolean ffProtection = clan.isFfProtected();
        long creationTime = clan.getCreationDate().getTime();
        Location<World> clanHome = clan.getHome();
        String clanHomeWorld = null;
        double clanHomeX = 0;
        double clanHomeY = 0;
        double clanHomeZ = 0;
        float clanHomeYaw = 0;
        float clanHomePitch = 0;
        if (clanHome != null) {
            clanHomeWorld = clanHome.getExtent().getUniqueId().toString();
            clanHomeX = clanHome.getX();
            clanHomeY = clanHome.getY();
            clanHomeZ = clanHome.getZ();
            // TODO SPONGE vector3d or something
//            clanHomeYaw = clanHome.getYaw();
//            clanHomePitch = clanHome.getPitch();
        }
        int homeSetTimes = clan.getHomeSetTimes();
        long homeLastSetTimeStamp = clan.getHomeSetTimeStamp();

        return QueryGenerator.createUpdateQuery("mcc_clans", databaseConnectionOwner.getConnection()).value("clantag", tag).value("clanname", name)
                .value("clanplayer_id_owner", ownerID).value("tagcolor", "")            //TODO TAGCOLOR -> SPONGE
                .value("allow_ally_invites", allowAllyInvites)
                .value("clanhome_world", clanHomeWorld).value("clanhome_x", clanHomeX).value("clanhome_y", clanHomeY).value("clanhome_z", clanHomeZ)
                .value("clanhome_yaw", clanHomeYaw).value("clanhome_pitch", clanHomePitch).value("clanhome_set_times", homeSetTimes)
                .value("clanhome_set_timestamp", homeLastSetTimeStamp).value("ff_protection", ffProtection).value("creation_time", creationTime)
                .where("clan_id", clanID).create();
    }

    public static PreparedStatement getDeleteClanQuery(int clanID) {
        return QueryGenerator.createDeleteQuery("mcc_clans", databaseConnectionOwner.getConnection()).where("clan_id", clanID).create();
    }

    public static PreparedStatement getInsertRankQuery(int clanID, RankImpl rank) {
        int rankID = rank.getID();
        String name = rank.getName();
        boolean changeable = rank.isChangeable();

        String permissions = rank.getPermissionsAsString();

        return QueryGenerator.createInsertQuery("mcc_ranks", databaseConnectionOwner.getConnection()).value("rank_id", rankID)
                .value("clan_id", clanID).value("rankname", name).value("permissions", permissions).value("changeable", changeable).create();
    }

    public static PreparedStatement getUpdateRankQuery(RankImpl rank) {
        int rankID = rank.getID();
        String name = rank.getName();
        boolean changeable = rank.isChangeable();

        String permissions = "";
        int i = 0;
        for (Permission perm : rank.getPermissions()) {
            if (i != 0) {
                permissions += ",";
            }
            permissions += perm.name();
            i++;
        }

        return QueryGenerator.createUpdateQuery("mcc_ranks", databaseConnectionOwner.getConnection()).value("rankname", name)
                .value("permissions", permissions).value("changeable", changeable).where("rank_id", rankID).create();
    }

    public static PreparedStatement getDeleteRankQuery(int rankID) {
        return QueryGenerator.createDeleteQuery("mcc_ranks", databaseConnectionOwner.getConnection()).where("rank_id", rankID).create();
    }

    public static PreparedStatement getInsertClanAllyQuery(int clanID, int clanID_ally) {
        return QueryGenerator.createInsertQuery("mcc_clans_allies", databaseConnectionOwner.getConnection()).value("clan_id", clanID)
                .value("clan_id_ally", clanID_ally).create();
    }

    public static PreparedStatement getDeleteClanAllyQuery(int clanID, int clanID_ally) {
        return QueryGenerator.createDeleteQuery("mcc_clans_allies", databaseConnectionOwner.getConnection()).where("clan_id", clanID)
                .and("clan_id_ally", clanID_ally).create();
    }
}
