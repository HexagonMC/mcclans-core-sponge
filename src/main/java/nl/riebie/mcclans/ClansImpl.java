package nl.riebie.mcclans;

import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.api.ClanPlayer;
import nl.riebie.mcclans.api.Clans;
import nl.riebie.mcclans.api.events.ClanMemberJoinEvent;
import nl.riebie.mcclans.api.events.ClanMemberLeaveEvent;
import nl.riebie.mcclans.api.exceptions.NotDefaultImplementationException;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.clan.RankFactory;
import nl.riebie.mcclans.clan.RankImpl;
import nl.riebie.mcclans.comparators.ClanKdrComparator;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.database.TaskForwarder;
import nl.riebie.mcclans.events.ClanPlayerKillEvent;
import nl.riebie.mcclans.events.EventDispatcher;
import nl.riebie.mcclans.player.ClanInvite;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.*;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public class ClansImpl implements Clans {

    private Map<String, ClanImpl> clans = new HashMap<String, ClanImpl>();
    private Map<UUID, ClanPlayerImpl> clanPlayers = new HashMap<UUID, ClanPlayerImpl>();

    private ClanImpl firstClan;
    private ClanImpl secondClan;
    private ClanImpl thirdClan;

    private int highestUsedClanID = -1;
    private int highestUsedClanPlayerID = -1;
    private int highestUsedRankID = -1;

    private static ClansImpl instance;

    public static ClansImpl getInstance() {
        if (instance == null) {
            instance = new ClansImpl();
        }
        return instance;
    }

    @Listener
    public void onClanPlayerKill(ClanPlayerKillEvent event) {
        updateClanTagCache();
    }

    @Listener
    public void onMemberJoin(ClanMemberJoinEvent event) {
        updateClanTagCache();
    }

    @Listener
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

    public void fillThisDummyDummy() {
        if (clans.size() < 30) {
            for (int i = 0; i < 500; i++) {
                ClanImpl clanImpl = new ClanImpl.Builder(ClansImpl.getInstance().getNextAvailableClanID(), "tc" + i, "test clan" + i).build();
                clanImpl.setupDefaultRanks();
                UUID ownerUUID = new UUID(-1, i);
                ClanPlayerImpl owner = new ClanPlayerImpl.Builder(ClansImpl.getInstance().getNextAvailableClanPlayerID(), ownerUUID, "TCOWNER" + i)
                        .clan(clanImpl).rank(clanImpl.getRank("Owner")).build();
                clanImpl.addMember(owner);
                clanImpl.setLoadedOwner(owner);
                clans.put(clanImpl.getTag().toLowerCase(), clanImpl);
                clanPlayers.put(ownerUUID, owner);
                for (int j = 0; j < 19; j++) {
                    UUID memberUID = new UUID(i, j);
                    ClanPlayerImpl member = new ClanPlayerImpl.Builder(ClansImpl.getInstance().getNextAvailableClanPlayerID(), memberUID, "TCMEMBER"
                            + i + "." + j).clan(clanImpl).rank(clanImpl.getRank("Member")).build();
                    clanImpl.addMember(member);
                    clanPlayers.put(memberUID, member);
                }
            }
        }
    }

    @Override
    public ClanImpl createClan(String tag, String name, ClanPlayer owner) {
        if (owner instanceof ClanPlayerImpl) {
            ClanPlayerImpl ownerImpl = (ClanPlayerImpl) owner;
            if (tagIsFree(tag)) {
                ClanImpl newClan = new ClanImpl.Builder(getNextAvailableClanID(), tag, name).owner(ownerImpl).build();
                newClan.setupDefaultRanks();
                newClan.addMember(ownerImpl);

                ownerImpl.setRank(newClan.getRank(RankFactory.getOwnerIdentifier()));
                ownerImpl.setClan(newClan);
                clans.put(tag.toLowerCase(), newClan);
                EventDispatcher.getInstance().dispatchClanCreateEvent(newClan);
                TaskForwarder.sendInsertClan(newClan);
                updateClanTagCache();
                return newClan;
            }
            return null;
        } else {
            throw new NotDefaultImplementationException(owner.getClass());
        }
    }

    @Override
    public void disbandClan(String tag) {
        ClanImpl clan = clans.get(tag.toLowerCase());

        for (ClanPlayerImpl clanPlayer : clan.getMembersImpl()) {
            clanPlayer.setClan(null);
            clanPlayer.setRank(null);
        }
        List<RankImpl> ranks = clan.getRankImpls();
        for (RankImpl rank : ranks) {
            clan.removeRank(rank.getName());
        }
        for (ClanPlayerImpl invitedPlayer : clan.getInvitedPlayersImpl()) {
            invitedPlayer.resetClanInvite();
        }
        for (ClanImpl ally : clan.getAlliesImpl()) {
            ally.removeAlly(clan.getTag());
        }
        for (ClanImpl invitedAlly : clan.getInvitedAlliesImpl()) {
            invitedAlly.resetInvitingAlly();
        }

        clans.remove(tag.toLowerCase());

        EventDispatcher.getInstance().dispatchClanDisbandEvent(clan);
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
        //TODO check if this works
        UserStorageService storage = Sponge.getGame().getServiceManager().provide(UserStorageService.class).get();
        Optional<User> user = storage.get(playerName);
        if (user.isPresent()) {
            return getClanPlayer(user.get().getUniqueId());
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
        if (clanPlayer instanceof ClanPlayerImpl) {
            ClanPlayerImpl clanPlayerImpl = (ClanPlayerImpl) clanPlayer;
            String playerName = clanPlayerImpl.getName();
            ClanImpl clan = clanPlayerImpl.getClan();
            ClanInvite clanInvite = clanPlayerImpl.getClanInvite();
            if (clan != null) {
                if (clan.getOwner().equals(clanPlayer)) {
                    disbandClan(clan.getTag());
                } else {
                    clan.removeMember(playerName);
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
        return new ArrayList<Clan>(clans.values());
    }

    public List<ClanImpl> getClanImpls() {
        return new ArrayList<ClanImpl>(clans.values());
    }

    public List<ClanPlayerImpl> getClanPlayerImpls() {
        List<ClanPlayerImpl> copiedClanPlayers = new ArrayList<ClanPlayerImpl>();
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
    public boolean tagIsFree(String tag) {
        return !clans.containsKey(tag.toLowerCase());
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
