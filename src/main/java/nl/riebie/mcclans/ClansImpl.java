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

package nl.riebie.mcclans;

import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.api.ClanPlayer;
import nl.riebie.mcclans.api.ClanService;
import nl.riebie.mcclans.api.Result;
import nl.riebie.mcclans.api.events.ClanCreateEvent;
import nl.riebie.mcclans.api.events.ClanDisbandEvent;
import nl.riebie.mcclans.api.events.ClanMemberJoinEvent;
import nl.riebie.mcclans.api.events.ClanMemberLeaveEvent;
import nl.riebie.mcclans.api.exceptions.NotDefaultImplementationException;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.clan.RankFactory;
import nl.riebie.mcclans.clan.RankImpl;
import nl.riebie.mcclans.comparators.ClanKdrComparator;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.events.ClanPlayerKillEvent;
import nl.riebie.mcclans.events.EventDispatcher;
import nl.riebie.mcclans.messages.Messages;
import nl.riebie.mcclans.permissions.ClanPermissionManagerImpl;
import nl.riebie.mcclans.persistence.TaskForwarder;
import nl.riebie.mcclans.player.ClanInvite;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.utils.ResultImpl;
import nl.riebie.mcclans.utils.UUIDUtils;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.util.Tristate;

import java.util.*;

/**
 * Created by Kippers on 19-1-2016.
 */
public class ClansImpl implements ClanService {

    private Map<String, ClanImpl> clans = new HashMap<String, ClanImpl>();
    private Map<UUID, ClanPlayerImpl> clanPlayers = new HashMap<UUID, ClanPlayerImpl>();

    private ClanImpl firstClan;
    private ClanImpl secondClan;
    private ClanImpl thirdClan;

    private int highestUsedClanID = -1;
    private int highestUsedClanPlayerID = -1;
    private int highestUsedRankID = -1;

    private ClanPermissionManagerImpl clanPermissionManager = new ClanPermissionManagerImpl();

    private static ClansImpl instance;

    public static ClansImpl getInstance() {
        if (instance == null) {
            instance = new ClansImpl();
        }
        return instance;
    }

    @Listener(order = Order.POST)
    public void onClanPlayerKill(ClanPlayerKillEvent event) {
        updateClanTagCache();
    }

    @IsCancelled(Tristate.FALSE)
    @Listener(order = Order.POST)
    public void onMemberJoin(ClanMemberJoinEvent event) {
        updateClanTagCache();
    }

    @IsCancelled(Tristate.FALSE)
    @Listener(order = Order.POST)
    public void onMemberLeave(ClanMemberLeaveEvent event) {
        updateClanTagCache();
    }

    public void updateClanTagCache() {
        firstClan = null;
        secondClan = null;
        thirdClan = null;
        List<ClanImpl> clans = getClansRankedByKDR();
        if (clans.size() > 0) {
            firstClan = clans.get(0);
        }
        if (clans.size() > 1) {
            secondClan = clans.get(1);
        }
        if (clans.size() > 2) {
            thirdClan = clans.get(2);
        }
    }

    public ClanImpl getFirstClan() {
        return firstClan;
    }

    public ClanImpl getSecondClan() {
        return secondClan;
    }

    public ClanImpl getThirdClan() {
        return thirdClan;
    }

    public void setHighestUsedClanID(int highestUsedClanID) {
        this.highestUsedClanID = highestUsedClanID;
    }

    public void setHighestUsedClanPlayerID(int highestUsedClanPlayerID) {
        this.highestUsedClanPlayerID = highestUsedClanPlayerID;
    }

    public void setHighestUsedRankID(int highestUsedRankID) {
        this.highestUsedRankID = highestUsedRankID;
    }

    public int getHighestUsedClanID() {
        return highestUsedClanID;
    }

    public int getHighestUsedClanPlayerID() {
        return highestUsedClanPlayerID;
    }

    public int getHighestUsedRankID() {
        return highestUsedRankID;
    }

    public int getNextAvailableClanID() {
        return ++highestUsedClanID;
    }

    public int getNextAvailableClanPlayerID() {
        return ++highestUsedClanPlayerID;
    }

    public int getNextAvailableRankID() {
        return ++highestUsedRankID;
    }

    @Override
    public Result<Clan> createClan(String tag, String name, ClanPlayer owner) {
        if (tag == null || name == null || owner == null) {
            throw new IllegalArgumentException("arguments may not be null");
        }
        if (!(owner instanceof ClanPlayerImpl)) {
            throw new NotDefaultImplementationException(owner.getClass());
        }
        ClanPlayerImpl ownerImpl = (ClanPlayerImpl) owner;

        if (ownerImpl.getClan() != null) {
            throw new IllegalArgumentException("provided player may not already be in a clan");
        }
        if (!isTagAvailable(tag)) {
            throw new IllegalArgumentException("provided tag already taken");
        }

        ClanCreateEvent.Plugin clanCreateEvent = EventDispatcher.getInstance().dispatchPluginClanCreateEvent(tag, name, owner);
        if (clanCreateEvent.isCancelled()) {
            return ResultImpl.ofError(clanCreateEvent.getCancelMessage());
        } else {
            Clan clan = createClanInternal(tag, name, ownerImpl);
            return ResultImpl.ofResult(clan);
        }
    }

    public ClanImpl createClanInternal(String tag, String name, ClanPlayerImpl owner) {
        ClanImpl newClan = new ClanImpl.Builder(getNextAvailableClanID(), tag, name).owner(owner).build();
        newClan.setupDefaultRanks();
        newClan.addMember(owner);

        owner.setRankInternal(newClan.getRank(RankFactory.getOwnerIdentifier()));
        owner.setClan(newClan);
        clans.put(tag.toLowerCase(), newClan);
        TaskForwarder.sendInsertClan(newClan);
        updateClanTagCache();
        return newClan;
    }

    @Override
    public void disbandClan(Clan disbandedClan) {
        if (disbandedClan == null) {
            return;
        }

        if (disbandedClan instanceof ClanImpl) {
            ClanDisbandEvent.Plugin event = EventDispatcher.getInstance().dispatchPluginClanDisbandEvent(disbandedClan);
            if (event.isCancelled()) {
                disbandedClan.getOwner().sendMessage(Messages.getWarningMessage(event.getCancelMessage()));
            } else {
                disbandClanInternal((ClanImpl) disbandedClan);
            }
        } else {
            throw new NotDefaultImplementationException(disbandedClan.getClass());
        }
    }

    public void disbandClanInternal(ClanImpl clan) {
        for (ClanPlayerImpl clanPlayer : clan.getMembersImpl()) {
            clanPlayer.setClan(null);
            clanPlayer.setRankInternal(null);
        }
        List<RankImpl> ranks = clan.getRankImpls();
        for (RankImpl rank : ranks) {
            clan.removeRank(rank.getName());
        }
        for (ClanPlayerImpl invitedPlayer : clan.getInvitedPlayersImpl()) {
            invitedPlayer.resetClanInvite();
        }
        for (ClanImpl ally : clan.getAlliesImpl()) {
            ally.removeAlly(clan);
        }
        for (ClanImpl invitedAlly : clan.getInvitedAlliesImpl()) {
            invitedAlly.resetInvitingAlly();
        }

        clans.remove(clan.getTag().toLowerCase());

        TaskForwarder.sendDeleteClan(clan.getID());
        updateClanTagCache();
    }


    @Override
    public ClanImpl getClan(String tag) {
        return clans.get(tag.toLowerCase());
    }

    @Override
    public ClanPlayerImpl getClanPlayer(UUID uuid) {
        return clanPlayers.get(uuid);
    }

    public ClanPlayerImpl getClanPlayer(String playerName) {
        UUID uuid = UUIDUtils.getUUID(playerName);
        if (uuid != null) {
            return getClanPlayer(uuid);
        }
        return null;
    }

    public void transferClanPlayer(ClanPlayerImpl oldClanPlayer, UUID newClanPlayerUUID, String newClanPlayerName) {
        clanPlayers.remove(oldClanPlayer.getUUID());
        clanPlayers.remove(newClanPlayerUUID);
        oldClanPlayer.setUUID(newClanPlayerUUID);
        oldClanPlayer.setLastKnownName(newClanPlayerName);
        clanPlayers.put(newClanPlayerUUID, oldClanPlayer);
    }

    @Override
    public void removeClanPlayer(ClanPlayer clanPlayer) {
        if (clanPlayer == null) {
            return;
        }

        if (clanPlayer instanceof ClanPlayerImpl) {
            ClanPlayerImpl clanPlayerImpl = (ClanPlayerImpl) clanPlayer;
            String playerName = clanPlayerImpl.getName();
            ClanImpl clan = clanPlayerImpl.getClan();
            ClanInvite clanInvite = clanPlayerImpl.getClanInvite();
            if (clan != null) {
                if (clan.getOwner().equals(clanPlayerImpl)) {
                    disbandClanInternal(clan);
                } else {
                    clan.removeMember(clanPlayerImpl);
                }
            }
            if (clanInvite != null) {
                ClanImpl invitingClan = clanInvite.getClan();
                invitingClan.removeInvitedPlayer(playerName);
            }
            clanPlayers.remove(clanPlayerImpl.getUUID());
            TaskForwarder.sendDeleteClanPlayer(clanPlayerImpl.getID());
        } else {
            throw new NotDefaultImplementationException(clanPlayer.getClass());
        }

    }

    @Override
    public List<Clan> getClans() {
        return new ArrayList<>(clans.values());
    }

    public List<ClanImpl> getClanImpls() {
        return new ArrayList<>(clans.values());
    }

    public List<ClanPlayerImpl> getClanPlayerImpls() {
        List<ClanPlayerImpl> copiedClanPlayers = new ArrayList<>();
        for (ClanPlayerImpl clanPlayer : clanPlayers.values()) {
            if (!clanPlayer.getName().equals("CONSOLE")) {
                copiedClanPlayers.add(clanPlayer);
            }
        }
        return copiedClanPlayers;
    }

    @Override
    public ClanPlayerImpl createClanPlayer(UUID uuid, String name) {
        ClanPlayerImpl clanPlayer = new ClanPlayerImpl.Builder(getNextAvailableClanPlayerID(), uuid, name).build();
        this.clanPlayers.put(uuid, clanPlayer);
        if (Config.getBoolean(Config.USE_DATABASE)) {
            TaskForwarder.sendInsertClanPlayer(clanPlayer);
        }
        return clanPlayer;
    }

    @Override
    public boolean isTagAvailable(String tag) {
        return !clans.containsKey(tag.toLowerCase());
    }

    @Override
    public ClanPermissionManagerImpl getClanPermissionManager() {
        return clanPermissionManager;
    }

    public void setClans(Map<String, ClanImpl> clans) {
        this.clans = clans;
    }

    public void setClanPlayers(Map<UUID, ClanPlayerImpl> clanPlayers) {
        this.clanPlayers = clanPlayers;
    }

    public List<ClanImpl> getClansRankedByKDR() {
        List<ClanImpl> clans = getClanImpls();
        java.util.Collections.sort(clans, new ClanKdrComparator());
        return clans;
    }
}
