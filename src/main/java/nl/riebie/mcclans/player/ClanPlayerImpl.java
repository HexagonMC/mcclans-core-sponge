package nl.riebie.mcclans.player;

import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.api.ClanPlayer;
import nl.riebie.mcclans.api.Rank;
import nl.riebie.mcclans.api.exceptions.NotDefaultImplementationException;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.clan.RankImpl;
import nl.riebie.mcclans.enums.PlayerChatState;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;

import javax.security.auth.login.Configuration;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public class ClanPlayerImpl implements ClanPlayer, Cloneable {

    private static final float killsHighFactor = 2.0f;
    private static final float killsMediumFactor = 1.0f;
    private static final float killsLowFactor = 0.1f;
    private static final float deathsHighFactor = 2.0f;
    private static final float deathsMediumFactor = 1.0f;
    private static final float deathsLowFactor = 0.1f;

    private UUID uuid;
    private int clanPlayerID;
    private String lastKnownName;

    private RankImpl rank;
    private ClanImpl clan;

    private int killsHigh;
    private int killsMedium;
    private int killsLow;
    private int deathsHigh;
    private int deathsMedium;
    private int deathsLow;

    // TODO SPONGE: Command shit, FUCK ME
    // private LastExecutedPageCommand lastPageCommand;
    private ClanInvite clanInvite;
    private LastOnlineImpl lastOnline = new LastOnlineImpl();
    private PlayerChatState chatState = PlayerChatState.GLOBAL;
    private PlayerChatState tempChatState = null;
    private LastClanHomeTeleport lastClanHomeTeleport;
    private Location lastTeleportInitiationLocation;
    private LastPlayerDamage lastPlayerDamage;
    private boolean ffProtection;

    private ClanPlayerImpl(Builder builder) {
        this.clanPlayerID = builder.clanPlayerID;
        this.uuid = builder.uuid;
        this.clan = builder.clan;
        this.lastKnownName = builder.lastKnownName;

        this.killsHigh = builder.killsHigh;
        this.killsMedium = builder.killsMedium;
        this.killsLow = builder.killsLow;
        this.deathsHigh = builder.deathsHigh;
        this.deathsMedium = builder.deathsMedium;
        this.deathsLow = builder.deathsLow;

        this.lastOnline = builder.lastOnline;
        this.rank = builder.rank;
        this.ffProtection = builder.ffProtection;
    }

    public int getID() {
        return clanPlayerID;
    }

    // Don't call this method without changing the ClanPlayer hashmap key in
    // ClansImpl
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
        // TODO SPONGE:
//        TaskForwarder.sendUpdateClanPlayer(this);
    }

    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName = lastKnownName;
    }

    @Override
    public String getName() {
        // TODO SPONGE: Bukkit.getPlayer RWEALLY??
//        Player player = Bukkit.getPlayer(this.uuid);
//        if (player != null) {
//            lastKnownName = player.getName();
//            return player.getName();
//        } else {
//            return lastKnownName;
//        }
        return null;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public ClanImpl getClan() {
        return clan;
    }

    @Override
    public boolean isMemberOf(String clanTag) {
        if (this.clan != null) {
            return clan.getTag().toLowerCase().equals(clanTag.toLowerCase());
        }
        return false;
    }

    @Override
    public boolean isMemberOfAClan() {
        return clan != null;
    }

    @Override
    public int getKills() {
        return getKillsHigh() + getKillsMedium() + getKillsLow();
    }

    @Override
    public double getKillsWeighted() {
        return ((getKillsHigh() * killsHighFactor) + (getKillsMedium() * killsMediumFactor) + (getKillsLow() * killsLowFactor));
    }

    @Override
    public int getDeaths() {
        return getDeathsHigh() + getDeathsMedium() + getDeathsLow();
    }

    @Override
    public double getDeathsWeighted() {
        return ((getDeathsHigh() * deathsHighFactor) + (getDeathsMedium() * deathsMediumFactor) + (getDeathsLow() * deathsLowFactor));
    }

    @Override
    public double getKDR() {
        double kdr = 0;
        double killsWeighted = getKillsWeighted();
        double deathsWeighted = getDeathsWeighted();
        if (deathsWeighted < 1) {
            deathsWeighted = 1;
        }
        kdr = killsWeighted / deathsWeighted;

        int ix = (int) (kdr * 10.0); // scale it
        double dbl2 = ((double) ix) / 10.0;
        return dbl2;
    }

    @Override
    public RankImpl getRank() {
        return rank;
    }

    @Override
    public void setRank(Rank rank) {
        if (rank instanceof RankImpl) {
            this.rank = (RankImpl) rank;
            // TODO SPONGE:
//            TaskForwarder.sendUpdateClanPlayer(this);
        } else if (rank == null) {
            this.rank = null;
            // TODO SPONGE:
//            TaskForwarder.sendUpdateClanPlayer(this);
        } else {
            throw new NotDefaultImplementationException(rank.getClass());
        }
    }

    public void setClan(Clan clan) {
        if (clan instanceof ClanImpl) {
            this.clan = (ClanImpl) clan;
            // TODO SPONGE:
//            TaskForwarder.sendUpdateClanPlayer(this);
        } else if (clan == null) {
            this.clan = null;
            // TODO SPONGE:
//            TaskForwarder.sendUpdateClanPlayer(this);
        } else {
            throw new NotDefaultImplementationException(clan.getClass());
        }
    }

    public void inviteToClan(ClanImpl clan) {
        clanInvite = new ClanInvite(clan, this);
    }

    public void resetClanInvite() {
        clanInvite = null;
    }

    public ClanInvite getClanInvite() {
        return clanInvite;
    }

    public void sendMessage(Text... message) {
        Optional<Player> playerOpt = Sponge.getServer().getPlayer(uuid);
        if (playerOpt.isPresent() && playerOpt.get().isOnline()) {
            playerOpt.get().sendMessages(message);
        }
    }

    public int addKillHigh() {
        int kills = getKillsHigh();
        kills++;
        setKillsHigh(kills);
        return kills;
    }

    public int addKillMedium() {
        int kills = getKillsMedium();
        kills++;
        setKillsMedium(kills);
        return kills;
    }

    public int addKillLow() {
        int kills = getKillsLow();
        kills++;
        setKillsLow(kills);
        return kills;
    }

    @Override
    public void setKillsHigh(int kills) {
        this.killsHigh = kills;
        // TODO SPONGE:
//        TaskForwarder.sendUpdateClanPlayer(this);
    }

    @Override
    public void setKillsMedium(int kills) {
        this.killsMedium = kills;
        // TODO SPONGE:
//        TaskForwarder.sendUpdateClanPlayer(this);
    }

    @Override
    public void setKillsLow(int kills) {
        this.killsLow = kills;
        // TODO SPONGE:
//        TaskForwarder.sendUpdateClanPlayer(this);
    }

    @Override
    public int getKillsHigh() {
        return killsHigh;
    }

    @Override
    public int getKillsMedium() {
        return killsMedium;
    }

    @Override
    public int getKillsLow() {
        return killsLow;
    }

    @Override
    public int getDeathsHigh() {
        return deathsHigh;
    }

    @Override
    public int getDeathsMedium() {
        return deathsMedium;
    }

    @Override
    public int getDeathsLow() {
        return deathsLow;
    }

    public int addDeathHigh() {
        int deaths = getDeathsHigh();
        deaths++;
        setDeathsHigh(deaths);
        return deaths;
    }

    public int addDeathMedium() {
        int deaths = getDeathsMedium();
        deaths++;
        setDeathsMedium(deaths);
        return deaths;
    }

    public int addDeathLow() {
        int deaths = getDeathsLow();
        deaths++;
        setDeathsLow(deaths);
        return deaths;
    }

    @Override
    public void setDeathsHigh(int deathsHigh) {
        this.deathsHigh = deathsHigh;
        // TODO SPONGE:
//        TaskForwarder.sendUpdateClanPlayer(this);
    }

    @Override
    public void setDeathsMedium(int deathsMedium) {
        this.deathsMedium = deathsMedium;
        // TODO SPONGE:
//        TaskForwarder.sendUpdateClanPlayer(this);
    }

    @Override
    public void setDeathsLow(int deathsLow) {
        this.deathsLow = deathsLow;
        // TODO SPONGE:
//        TaskForwarder.sendUpdateClanPlayer(this);
    }

    @Override
    public LastOnlineImpl getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(LastOnlineImpl lastOnline) {
        this.lastOnline = lastOnline;
        // TODO SPONGE:
//        TaskForwarder.sendUpdateClanPlayer(this);
    }

    public PlayerChatState getChatState() {
        return chatState;
    }

    public void setChatState(PlayerChatState chatState) {
        this.chatState = chatState;
    }

    public PlayerChatState getTempChatState() {
        return tempChatState;
    }

    public void setTempChatState(PlayerChatState tempChatState) {
        this.tempChatState = tempChatState;
    }

    // TODO SPONGE: COMMAND SHIT
//    public void setLastPageCommand(PageableCommand pageableCommand, Parameters parameters) {
//        this.lastPageCommand = new LastExecutedPageCommand(pageableCommand, parameters);
//    }
//
//    public LastExecutedPageCommand getLastPageCommand() {
//        return lastPageCommand;
//    }

    public LastClanHomeTeleport getLastClanHomeTeleport() {
        return lastClanHomeTeleport;
    }

    public void setLastClanHomeTeleport(LastClanHomeTeleport lastClanHomeTeleport) {
        this.lastClanHomeTeleport = lastClanHomeTeleport;
    }

    public Location getLastTeleportInitiationLocation() {
        return lastTeleportInitiationLocation;
    }

    public void setLastTeleportInitiationLocation(Location lastTeleportInitiationLocation) {
        this.lastTeleportInitiationLocation = lastTeleportInitiationLocation;
    }

    public void setLastPlayerDamage(UUID damagerUUID) {
        lastPlayerDamage = new LastPlayerDamage(uuid, damagerUUID);
    }

    public LastPlayerDamage getLastPlayerDamage() {
        return lastPlayerDamage;
    }

    @Override
    public boolean isFfProtected() {
        return ffProtection;
    }

    @Override
    public void setFfProtection(boolean ffProtection) {
        this.ffProtection = ffProtection;
        // TODO SPONGE:
//        TaskForwarder.sendUpdateClanPlayer(this);
    }

    @Override
    public ClanPlayerImpl clone() {
        ClanPlayerImpl clone = null;
        try {
            Object object = super.clone();
            if (object instanceof ClanPlayerImpl) {
                clone = (ClanPlayerImpl) object;
            }
        } catch (CloneNotSupportedException e) {
            // TODO SPONGE: New config
//            if (Configuration.debugging) {
//                e.printStackTrace();
//            }
        }

        return clone;
    }

    public static class Builder {
        private int clanPlayerID;
        private UUID uuid;
        private String lastKnownName;
        private RankImpl rank;
        private ClanImpl clan;

        private LastOnlineImpl lastOnline = new LastOnlineImpl();

        private int killsHigh = 0;
        private int killsMedium = 0;
        private int killsLow = 0;
        private int deathsHigh = 0;
        private int deathsMedium = 0;
        private int deathsLow = 0;

        private boolean ffProtection = true;

        public Builder(int clanPlayerID, UUID uuid, String lastKnownName) {
            this.clanPlayerID = clanPlayerID;
            this.uuid = uuid;
            this.lastKnownName = lastKnownName;
        }

        public Builder clan(ClanImpl clan) {
            this.clan = clan;
            return this;
        }

        public Builder rank(RankImpl rank) {
            this.rank = rank;
            return this;
        }

        public Builder lastOnline(LastOnlineImpl lastOnline) {
            this.lastOnline = lastOnline;
            return this;
        }

        public Builder kills(int killsHigh, int killsMedium, int killsLow) {
            this.killsHigh = killsHigh;
            this.killsMedium = killsMedium;
            this.killsLow = killsLow;
            return this;
        }

        public Builder deaths(int deathsHigh, int deathsMedium, int deathsLow) {
            this.deathsHigh = deathsHigh;
            this.deathsMedium = deathsMedium;
            this.deathsLow = deathsLow;
            return this;
        }

        public Builder ffProtection(boolean ffProtection) {
            this.ffProtection = ffProtection;
            return this;
        }

        public ClanPlayerImpl build() {
            return new ClanPlayerImpl(this);
        }

    }
}
