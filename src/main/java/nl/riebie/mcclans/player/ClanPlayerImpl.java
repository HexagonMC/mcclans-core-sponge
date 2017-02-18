/*
 * Copyright (c) 2016 riebie, Kippers <https://bitbucket.org/Kippers/mcclans-core-sponge>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package nl.riebie.mcclans.player;

import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.api.ClanPlayer;
import nl.riebie.mcclans.api.KillDeath;
import nl.riebie.mcclans.api.Rank;
import nl.riebie.mcclans.api.enums.KillDeathFactor;
import nl.riebie.mcclans.api.exceptions.NotDefaultImplementationException;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.clan.RankFactory;
import nl.riebie.mcclans.clan.RankImpl;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.enums.PlayerChatState;
import nl.riebie.mcclans.persistence.TaskForwarder;
import nl.riebie.mcclans.utils.UUIDUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by Kippers on 19-1-2016.
 */
public class ClanPlayerImpl implements ClanPlayer, Cloneable, CommandSender {

    private UUID uuid;
    private int clanPlayerID;
    private String lastKnownName;

    private RankImpl rank;
    private ClanImpl clan;

    private KillDeath killDeath;

    private ClanInvite clanInvite;
    private LastOnlineImpl lastOnline = new LastOnlineImpl();
    private PlayerChatState chatState = PlayerChatState.GLOBAL;
    private PlayerChatState tempChatState = null;
    private LastClanHomeTeleport lastClanHomeTeleport;
    private Location<World> lastTeleportInitiationLocation;
    private LastPlayerDamage lastPlayerDamage;
    private boolean ffProtection;
    private boolean ignoreClanChat;
    private boolean ignoreAllyChat;
    private boolean spy;

    private ClanPlayerImpl(Builder builder) {
        this.clanPlayerID = builder.clanPlayerID;
        this.uuid = builder.uuid;
        this.clan = builder.clan;
        this.lastKnownName = builder.lastKnownName;

        killDeath = new KillDeathImpl();
        killDeath.setKills(KillDeathFactor.HIGH, builder.killsHigh);
        killDeath.setKills(KillDeathFactor.MEDIUM, builder.killsMedium);
        killDeath.setKills(KillDeathFactor.LOW, builder.killsLow);
        killDeath.setDeaths(KillDeathFactor.HIGH, builder.deathsHigh);
        killDeath.setDeaths(KillDeathFactor.MEDIUM, builder.deathsMedium);
        killDeath.setDeaths(KillDeathFactor.LOW, builder.deathsLow);

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
        TaskForwarder.sendUpdateClanPlayer(this);
    }

    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName = lastKnownName;
    }

    @Override
    public String getName() {
        String name = UUIDUtils.getName(uuid);
        if (name == null) {
            name = lastKnownName;
        } else {
            lastKnownName = name;
        }
        return name;
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
    public boolean isMemberOfAClan() {
        return clan != null;
    }

    @Override
    public KillDeath getKillDeath() {
        return killDeath;
    }

    @Override
    public RankImpl getRank() {
        return rank;
    }

    @Override
    public void setRank(Rank rank) {
        if (rank == null) {
            throw new IllegalArgumentException("rank may not be null");
        } else if (!(rank instanceof RankImpl)) {
            throw new NotDefaultImplementationException(rank.getClass());
        }
        RankImpl newRank = (RankImpl) rank;

        if (clan == null) {
            throw new IllegalStateException("player not part of a clan");
        }
        if (!clan.containsRank(newRank)) {
            throw new IllegalArgumentException("rank not part of player's clan");
        }
        if (this.rank != null && this.rank.getName().equalsIgnoreCase(RankFactory.getOwnerIdentifier())) {
            throw new IllegalStateException("cannot overwrite owner's rank");
        }
        if (newRank.getName().equalsIgnoreCase(RankFactory.getOwnerIdentifier())) {
            throw new IllegalArgumentException("cannot set owner rank, use setowner");
        }

        setRankInternal(newRank);
    }

    public void setRankInternal(RankImpl rank) {
        this.rank = rank;
        TaskForwarder.sendUpdateClanPlayer(this);
    }

    public void setClan(Clan clan) {
        if (clan instanceof ClanImpl) {
            this.clan = (ClanImpl) clan;
            TaskForwarder.sendUpdateClanPlayer(this);
        } else if (clan == null) {
            this.clan = null;
            TaskForwarder.sendUpdateClanPlayer(this);
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

    @Override
    public void sendMessage(Text... message) {
        Optional<Player> playerOpt = Sponge.getServer().getPlayer(uuid);
        if (playerOpt.isPresent() && playerOpt.get().isOnline()) {
            playerOpt.get().sendMessages(message);
        }
    }

    @Override
    public boolean checkPermission(String permission) {
        Rank rank = getRank();
        if (rank != null) {
            return rank.hasPermission(permission);
        }
        return true;
    }

    @Override
    public LastOnlineImpl getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(LastOnlineImpl lastOnline) {
        this.lastOnline = lastOnline;
        TaskForwarder.sendUpdateClanPlayer(this);
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

    public Location<World> getLastTeleportInitiationLocation() {
        return lastTeleportInitiationLocation;
    }

    public void setLastTeleportInitiationLocation(Location<World> lastTeleportInitiationLocation) {
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
        TaskForwarder.sendUpdateClanPlayer(this);
    }

    public boolean getIgnoreClanChat() {
        return ignoreClanChat;
    }

    public void setIgnoreClanChat(boolean ignoreClanChat) {
        this.ignoreClanChat = ignoreClanChat;
    }

    public boolean getIgnoreAllyChat() {
        return ignoreAllyChat;
    }

    public void setIgnoreAllyChat(boolean ignoreAllyChat) {
        this.ignoreAllyChat = ignoreAllyChat;
    }

    public boolean isSpy() {
        return spy;
    }

    public void setSpy(boolean spy) {
        this.spy = spy;
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
            if (Config.getBoolean(Config.DEBUGGING)) {
                e.printStackTrace();
            }
        }

        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClanPlayerImpl that = (ClanPlayerImpl) o;

        return uuid.equals(that.uuid);

    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
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
