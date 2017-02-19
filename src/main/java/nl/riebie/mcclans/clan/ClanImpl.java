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

package nl.riebie.mcclans.clan;

import nl.riebie.mcclans.ClansImpl;
import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.api.ClanPlayer;
import nl.riebie.mcclans.api.Rank;
import nl.riebie.mcclans.api.Result;
import nl.riebie.mcclans.api.enums.KillDeathFactor;
import nl.riebie.mcclans.api.events.ClanOwnerChangeEvent;
import nl.riebie.mcclans.api.events.ClanSetHomeEvent;
import nl.riebie.mcclans.api.exceptions.NotDefaultImplementationException;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.events.EventDispatcher;
import nl.riebie.mcclans.persistence.TaskForwarder;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.utils.ResultImpl;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.text.DateFormat;
import java.util.*;

/**
 * Created by Kippers on 19-1-2016.
 */
public class ClanImpl implements Clan, Cloneable {

    private int clanID;
    private String name;
    private ClanPlayerImpl owner;
    private Location<World> home;
    private int homeSetTimes = 0;
    private long homeLastSetTimeStamp = -1;
    private TextColor tagColor = Config.getColor(Config.CLAN_TAG_DEFAULT_COLOR);
    private String tag;
    private HashMap<String, RankImpl> ranks = new HashMap<>();
    private List<ClanPlayerImpl> members = new ArrayList<>();
    private List<ClanPlayerImpl> invitedMembers = new ArrayList<>();
    private List<ClanImpl> allies = new ArrayList<>();
    private List<ClanImpl> invitedAllies = new ArrayList<>();
    private ClanImpl invitingAlly;
    private boolean allowAllyInvites = true;
    private Date creationDate;
    private boolean ffProtection;
    private String bankId;

    private ClanImpl(Builder builder) {
        this.clanID = builder.clanID;
        this.tag = builder.tag;
        this.name = builder.name;
        this.owner = builder.owner;
        this.tagColor = builder.tagColor;
        this.allowAllyInvites = builder.acceptAllyInvites;
        this.home = builder.home;
        this.homeLastSetTimeStamp = builder.homeLastSetTimeStamp;
        this.homeSetTimes = builder.homeSetTimes;

        this.creationDate = builder.creationDate;
        this.ffProtection = builder.ffProtection;

        this.bankId = builder.bankId;
    }

    public int getID() {
        return clanID;
    }

    public ClanPlayerImpl getMember(String name) {
        for (ClanPlayerImpl clanPlayer : getMembersImpl()) {
            if (clanPlayer.getName().toLowerCase().equals(name.toLowerCase())) {
                return clanPlayer;
            }
        }
        return null;
    }

    @Override
    public ClanPlayerImpl getMember(UUID uuid) {
        for (ClanPlayerImpl clanPlayer : getMembersImpl()) {
            if (clanPlayer.getUUID().equals(uuid)) {
                return clanPlayer;
            }
        }
        return null;
    }

    public ClanPlayerImpl getInvited(String name) {
        for (ClanPlayerImpl invitedPlayer : getInvitedPlayersImpl()) {
            if (invitedPlayer.getName().toLowerCase().equals(name.toLowerCase())) {
                return invitedPlayer;
            }
        }
        return null;
    }

    public List<ClanPlayerImpl> getInvitedPlayersImpl() {
        return new ArrayList<ClanPlayerImpl>(invitedMembers);
    }

    @Override
    public List<ClanPlayer> getMembers() {
        return new ArrayList<ClanPlayer>(members);
    }

    public List<ClanPlayerImpl> getMembersImpl() {
        return new ArrayList<ClanPlayerImpl>(members);
    }

    @Override
    public boolean isPlayerMember(UUID uuid) {
        for (ClanPlayer clanPlayer : getMembers()) {
            if (clanPlayer.getUUID().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public boolean isPlayerMember(String playerName) {
        for (ClanPlayer clanPlayer : getMembers()) {
            if (clanPlayer.getName().toLowerCase().equals(playerName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public boolean isPlayerInvited(String playerName) {
        for (ClanPlayer clanPlayer : getInvitedPlayersImpl()) {
            if (clanPlayer.getName().toLowerCase().equals(playerName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isPlayerFriendlyToThisClan(ClanPlayer clanPlayer) {
        if (clanPlayer == null) {
            return false;
        }

        if (clanPlayer instanceof ClanPlayerImpl) {
            ClanPlayerImpl clanPlayerImpl = (ClanPlayerImpl) clanPlayer;
            ClanImpl clan = clanPlayerImpl.getClan();
            if (clan != null) {
                if (clan.getTag().equals(tag)) {
                    return true;
                }
                if (isClanAllyOfThisClan(clan)) {
                    return true;
                }
            }
            return false;
        } else {
            throw new NotDefaultImplementationException(clanPlayer.getClass());
        }
    }

    @Override
    public List<Rank> getRanks() {
        List<Rank> rankList = new ArrayList<Rank>(ranks.values());
        return rankList;
    }

    public List<RankImpl> getRankImpls() {
        List<RankImpl> rankList = new ArrayList<RankImpl>(ranks.values());
        return rankList;
    }

    @Override
    public Result<Location<World>> setHome(@Nullable Location<World> location) {
        ClanSetHomeEvent.Plugin event = EventDispatcher.getInstance().dispatchPluginSetHomeEvent(this, location);
        if (event.isCancelled()) {
            return ResultImpl.ofError(event.getCancelMessage());
        } else {
            setHomeInternal(location);
            return ResultImpl.ofResult(location);
        }
    }

    public void setHomeInternal(@Nullable Location<World> location) {
        home = location == null ? null : new Location<>(location.getExtent(), location.getPosition());
        TaskForwarder.sendUpdateClan(this);
    }

    @Override
    public Location<World> getHome() {
        return home;
    }

    public int getHomeSetTimes() {
        return homeSetTimes;
    }

    public long getHomeSetTimeStamp() {
        return homeLastSetTimeStamp;
    }

    public int increaseHomeSetTimes() {
        homeSetTimes++;
        TaskForwarder.sendUpdateClan(this);
        return homeSetTimes;
    }

    public void setHomeSetTimes(int homeSetTimes) {
        this.homeSetTimes = homeSetTimes;
        TaskForwarder.sendUpdateClan(this);
    }

    public void setHomeSetTimeStamp(long homeLastSetTimeStamp) {
        this.homeLastSetTimeStamp = homeLastSetTimeStamp;
        TaskForwarder.sendUpdateClan(this);
    }

    @Override
    public int getTotalKills() {
        int kills = 0;
        for (ClanPlayerImpl member : members) {
            kills += member.getKillDeath().getTotalKills();
        }
        return kills;
    }

    @Override
    public int getKills(KillDeathFactor killDeathFactor) {
        int kills = 0;
        for (ClanPlayerImpl member : members) {
            kills += member.getKillDeath().getKills(killDeathFactor);
        }
        return kills;
    }

    @Override
    public int getTotalDeaths() {
        int deaths = 0;
        for (ClanPlayerImpl member : members) {
            deaths += member.getKillDeath().getTotalDeaths();
        }
        return deaths;
    }

    @Override
    public int getDeaths(KillDeathFactor factor) {
        int deaths = 0;
        for (ClanPlayerImpl member : members) {
            deaths += member.getKillDeath().getDeaths(factor);
        }
        return deaths;
    }

    @Override
    public double getKDR() {
        double kdr;
        double killsWeighted = 0;
        double deathsWeighted = 0;
        for (ClanPlayerImpl member : members) {
            killsWeighted += member.getKillDeath().getKillsWeighted();
            deathsWeighted += member.getKillDeath().getDeathsWeighted();
        }
        if (members.size() > 0) {
            if (deathsWeighted < 1) {
                deathsWeighted = 1;
            }
            kdr = killsWeighted / deathsWeighted;
            int ix = (int) (kdr * 10.0); // scale it
            double dbl2 = ((double) ix) / 10.0;
            return dbl2;
        } else {
            return 0;
        }
    }

    @Override
    public Result<ClanPlayer> setOwner(ClanPlayer clanPlayer) {
        if (clanPlayer == null) {
            throw new IllegalArgumentException("clan player may not be null");
        }
        if (!(clanPlayer instanceof ClanPlayerImpl)) {
            throw new NotDefaultImplementationException(clanPlayer.getClass());
        }
        ClanPlayerImpl clanPlayerImpl = (ClanPlayerImpl) clanPlayer;

        if (clanPlayer.getClan() == null || !this.equals(clanPlayer.getClan())) {
            throw new IllegalArgumentException("player not a member of this clan");
        }
        if (owner.equals(clanPlayer)) {
            // Already the owner, yay! Do nothing.
            return ResultImpl.ofResult(clanPlayerImpl);
        }

        ClanOwnerChangeEvent event = EventDispatcher.getInstance().dispatchPluginClanOwnerChangeEvent(this, owner, clanPlayerImpl);
        if (event.isCancelled()) {
            return ResultImpl.ofError(event.getCancelMessage());
        } else {
            setOwnerInternal(clanPlayerImpl);
            return ResultImpl.ofResult(clanPlayerImpl);
        }

    }

    public void setOwnerInternal(ClanPlayerImpl clanPlayer) {
        this.owner.setRankInternal(getRank(RankFactory.getRecruitIdentifier()));
        clanPlayer.setRankInternal(getRank(RankFactory.getOwnerIdentifier()));
        this.owner = clanPlayer;
        TaskForwarder.sendUpdateClan(this);
    }


    // Use for restoring clan object from database/xml
    public void setLoadedOwner(ClanPlayerImpl clanPlayer) {
        this.owner = clanPlayer;
    }

    @Override
    public void setName(String name) {
        this.name = name;
        TaskForwarder.sendUpdateClan(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public Text getTagColored() {
        TextColor colonColor = TextColors.GRAY;
        if (Config.getBoolean(Config.USE_COLORED_TAGS_BASED_ON_CLAN_KDR)) {
            ClanImpl firstClan = ClansImpl.getInstance().getFirstClan();
            ClanImpl secondClan = ClansImpl.getInstance().getSecondClan();
            ClanImpl thirdClan = ClansImpl.getInstance().getThirdClan();
            if (firstClan != null && firstClan.getTag().equalsIgnoreCase(getTag())) {
                colonColor = TextColors.DARK_RED;
            } else if (secondClan != null && secondClan.getTag().equalsIgnoreCase(getTag())) {
                colonColor = TextColors.GOLD;
            } else if (thirdClan != null && thirdClan.getTag().equalsIgnoreCase(getTag())) {
                colonColor = TextColors.DARK_BLUE;
            }
        }

        return Text.join(
                Text.builder("[").color(colonColor).build(),
                Text.builder(tag).color(tagColor).build(),
                Text.builder("]").color(colonColor).build()
        );
    }

    @Override
    public RankImpl getRank(String rank) {
        return ranks.get(rank.toLowerCase());
    }

    public void addRank(String name) {
        RankImpl rank = new RankImpl.Builder(ClansImpl.getInstance().getNextAvailableRankID(), name).build();
        ranks.put(name.toLowerCase(), rank);
        TaskForwarder.sendInsertRank(getID(), rank);
        TaskForwarder.sendUpdateClan(this);
    }

    public void addRank(Rank rank) {
        if (rank == null) {
            return;
        }

        if (rank instanceof RankImpl) {
            RankImpl rankImpl = (RankImpl) rank;
            ranks.put(rank.getName().toLowerCase(), rankImpl);
            TaskForwarder.sendInsertRank(getID(), rankImpl);
            TaskForwarder.sendUpdateClan(this);
        } else {
            throw new NotDefaultImplementationException(rank.getClass());
        }
    }

    public void removeRank(String name) {
        RankImpl rank = getRank(name);
        if (rank != null) {
            ranks.remove(name.toLowerCase());
            TaskForwarder.sendDeleteRank(rank.getID());
        }
    }

    public void renameRank(String oldName, String newName) {
        RankImpl rank = getRank(oldName);
        ranks.remove(oldName.toLowerCase());
        rank.setName(newName);
        this.ranks.put(newName.toLowerCase(), rank);
        TaskForwarder.sendUpdateRank(rank);
    }

    @Override
    public boolean containsRank(String rankName) {
        return this.ranks.containsKey(rankName.toLowerCase());
    }

    public boolean containsRank(RankImpl rank) {
        return ranks.containsValue(rank);
    }

    public void addMember(ClanPlayer player) {
        if (player == null) {
            return;
        }

        if (player instanceof ClanPlayerImpl) {
            ClanPlayerImpl clanPlayerImpl = (ClanPlayerImpl) player;
            members.add(clanPlayerImpl);
            TaskForwarder.sendUpdateClanPlayer(clanPlayerImpl);
        } else {
            throw new NotDefaultImplementationException(player.getClass());
        }
    }

    public void removeMember(ClanPlayer player) {
        ClanPlayerImpl member = getMember(player.getUUID());
        if (member != null) {
            member.setClan(null);

            member.setRankInternal(null);

            members.remove(member);
            TaskForwarder.sendUpdateClanPlayer(member);
        }
    }

    public void addInvitedPlayer(ClanPlayer player) {
        if (player == null) {
            return;
        }

        if (player instanceof ClanPlayerImpl) {
            invitedMembers.add((ClanPlayerImpl) player);
        } else {
            throw new NotDefaultImplementationException(player.getClass());
        }
    }

    public void removeInvitedPlayer(String playerName) {
        ClanPlayerImpl invitedPlayer = getInvited(playerName);
        if (invitedPlayer != null) {
            invitedMembers.remove(invitedPlayer);
        }
    }

    @Override
    public ClanPlayerImpl getOwner() {
        return owner;
    }

    @Override
    public TextColor getTagColor() {
        return this.tagColor;
    }

    @Override
    public void setTagColor(TextColor tagColor) {
        this.tagColor = tagColor;
        TaskForwarder.sendUpdateClan(this);
    }

    public boolean setTag(String newTag) {
        // TODO Maybe not allow changing of tags as discussed
        TaskForwarder.sendUpdateClan(this);
        return false;
    }

    @Override
    public void sendMessage(Text... message) {
        for (ClanPlayerImpl clanPlayer : getMembersImpl()) {
            clanPlayer.sendMessage(message);
        }
    }

    @Override
    public void sendMessage(String permission, Text... message) {
        for (ClanPlayerImpl clanPlayer : getMembersImpl()) {
            if (clanPlayer.getRank().hasPermission(permission)) {
                clanPlayer.sendMessage(message);
            }
        }
    }

    public void sendClanMessage(String sendingPlayerName, String sendingPlayerRank, String message) {

        Text textMessage = Text.join(
                Text.builder("[").color(TextColors.GRAY).build(),
                Text.builder("CC").color(TextColors.DARK_GREEN).build(),
                Text.builder("] [").color(TextColors.GRAY).build(),
                Text.builder(sendingPlayerRank).color(TextColors.BLUE).build(),
                Text.builder("] ").color(TextColors.GRAY).build(),
                Text.builder(sendingPlayerName + ": ").color(TextColors.DARK_GREEN).build(),
                Text.builder(message).color(TextColors.YELLOW).build()
        );

        sendMessage(textMessage);
    }

    public void sendAllyMessage(String sendingPlayerName, Text sendingPlayerClanTagColored, String message) {

        Text textMessage = Text.join(
                Text.builder("[").color(TextColors.GRAY).build(),
                Text.builder("AC").color(TextColors.DARK_GREEN).build(),
                Text.builder("] [").color(TextColors.GRAY).build(),
                sendingPlayerClanTagColored,
                Text.builder("] ").color(TextColors.GRAY).build(),
                Text.builder(sendingPlayerName + ": ").color(TextColors.DARK_GREEN).build(),
                Text.builder(message).color(TextColors.GOLD).build()
        );

        sendMessage(textMessage);
    }

    @Override
    public List<Clan> getAllies() {
        return new ArrayList<>(allies);
    }

    public List<ClanImpl> getAlliesImpl() {
        return new ArrayList<>(allies);
    }

    public void addAlly(Clan clan) {
        if (clan == null) {
            return;
        }

        if (clan instanceof ClanImpl) {
            ClanImpl clanImpl = (ClanImpl) clan;
            if (!allies.contains(clanImpl)) {
                allies.add(clanImpl);
                TaskForwarder.sendInsertClanAlly(getID(), clanImpl.getID());
            }
        } else {
            throw new NotDefaultImplementationException(clan.getClass());
        }
    }

    @Override
    public void removeAlly(Clan ally) {
        if (ally == null) {
            return;
        }

        if (ally instanceof ClanImpl) {
            ClanImpl clanImpl = (ClanImpl) ally;
            allies.remove(clanImpl);
            TaskForwarder.sendDeleteClanAlly(getID(), clanImpl.getID());
        } else {
            throw new NotDefaultImplementationException(ally.getClass());
        }
    }

    public List<ClanImpl> getInvitedAlliesImpl() {
        return new ArrayList<>(invitedAllies);
    }

    public void setInvitingAlly(ClanImpl clan) {
        invitingAlly = clan;
    }

    public void resetInvitingAlly() {
        invitingAlly = null;
    }

    public ClanImpl getInvitingAlly() {
        return invitingAlly;
    }

    @Override
    public void setAllowingAllyInvites(boolean inviteable) {
        this.allowAllyInvites = inviteable;
        TaskForwarder.sendUpdateClan(this);
    }

    @Override
    public boolean isAllowingAllyInvites() {
        return allowAllyInvites;
    }

    @Override
    public boolean isClanAllyOfThisClan(Clan clan) {
        for (ClanImpl ally : getAlliesImpl()) {
            if (ally.equals(clan)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public String getCreationDateUserFriendly() {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
        return dateFormat.format(creationDate);
    }

    @Override
    public boolean isFfProtected() {
        return ffProtection;
    }

    @Override
    public void setFfProtection(boolean ffProtection) {
        this.ffProtection = ffProtection;
        TaskForwarder.sendUpdateClan(this);
    }

    @Override
    public String getBankId() {
        return bankId;
    }

    public void setupDefaultRanks() {
        RankImpl owner = RankFactory.getInstance().createOwner();
        RankImpl recruit = RankFactory.getInstance().createRecruit();
        ranks.put(recruit.getName().toLowerCase(), recruit);
        ranks.put(owner.getName().toLowerCase(), owner);
        RankFactory.getInstance().createDefaultRanks().forEach(this::addRank);
    }

    @Override
    public int getMemberCount() {
        return members.size();
    }

    @Override
    public ClanImpl clone() {
        ClanImpl clone = null;
        try {
            Object object = super.clone();
            if (object instanceof ClanImpl) {
                clone = (ClanImpl) object;
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

        ClanImpl clan = (ClanImpl) o;

        return tag.equals(clan.tag);
    }

    @Override
    public int hashCode() {
        return tag.hashCode();
    }

    public static class Builder {
        private int clanID;
        private String name;
        private ClanPlayerImpl owner;
        private Location<World> home;
        private int homeSetTimes = 0;
        private long homeLastSetTimeStamp = -1;
        private TextColor tagColor = Config.getColor(Config.CLAN_TAG_DEFAULT_COLOR);
        private String tag;
        private boolean acceptAllyInvites = true;
        private Date creationDate = new Date();
        private boolean ffProtection = true;
        private String bankId;

        public Builder(int clanID, String tag, String name) {
            this.tag = tag;
            this.name = name;
            this.clanID = clanID;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder owner(ClanPlayerImpl owner) {
            this.owner = owner;
            return this;
        }

        public Builder home(Location<World> home) {
            this.home = home;
            return this;
        }

        public Builder homeSetTimes(int times) {
            this.homeSetTimes = times;
            return this;
        }

        public Builder homeLastSetTimeStamp(long timeStamp) {
            this.homeLastSetTimeStamp = timeStamp;
            return this;
        }

        public Builder tagColor(TextColor tagColor) {
            this.tagColor = tagColor;
            return this;
        }

        public Builder acceptAllyInvites(boolean acceptAllyInvites) {
            this.acceptAllyInvites = acceptAllyInvites;
            return this;
        }

        public Builder creationTime(long creationTime) {
            this.creationDate = new Date(creationTime);
            return this;
        }

        public Builder ffProtection(boolean ffProtection) {
            this.ffProtection = ffProtection;
            return this;
        }

        public Builder bankId(String identifier) {
            this.bankId = identifier;
            return this;
        }

        public ClanImpl build() {
            if (bankId == null) {
                bankId = UUID.randomUUID().toString();
            }

            return new ClanImpl(this);
        }
    }
}
