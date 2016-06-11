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
import nl.riebie.mcclans.utils.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kippers on 19-1-2016.
 */
public class RankFactory {

    private static RankFactory instance;
    private final static String OWNER_IDENTIFIER = "Owner";
    private final static String RECRUIT_IDENTIFIER = "Recruit";

    private RankImpl owner;
    private RankImpl recruit;
    private List<Pair<String, List<String>>> defaultRankData = new ArrayList<>();

    private final static int OWNER_ID = -2;
    private final static int RECRUIT_ID = -3;

    public static RankFactory getInstance() {
        if (instance == null) {
            instance = new RankFactory();
        }
        return instance;
    }

    public RankImpl createOwner() {
        if (owner == null) {
            owner = new RankImpl.Builder(OWNER_ID, OWNER_IDENTIFIER).unchangeable().build();
        }
        return owner;
    }

    public RankImpl createRecruit() {
        if (recruit == null) {
            recruit = new RankImpl.Builder(RECRUIT_ID, RECRUIT_IDENTIFIER).unchangeable().build();
            recruit.addPermission("clanchat");
        }

        return recruit;
    }

    public void registerDefaultRanks(List<Pair<String, List<String>>> ranks) {
        defaultRankData.clear();
        for (Pair<String, List<String>> rankDataItem : ranks) {
            List<String> permissions = new ArrayList<>();
            for (String permission : rankDataItem.getValue()) {
                if (ClansImpl.getInstance().getClanPermissionManager().isActiveClanPermission(permission)) {
                    permissions.add(permission);
                }
            }
            defaultRankData.add(Pair.createPair(rankDataItem.getKey(), permissions));
        }
    }

    public List<RankImpl> createDefaultRanks() {
        List<RankImpl> ranks = new ArrayList<>();
        for (Pair<String, List<String>> rankDataItem : defaultRankData) {
            RankImpl rank = new RankImpl.Builder(ClansImpl.getInstance().getNextAvailableRankID(), rankDataItem.getKey()).build();
            for (String permission : rankDataItem.getValue()) {
                rank.addPermission(permission);
            }
            ranks.add(rank);
        }
        return ranks;
    }

    public RankImpl createNewRank(String rankName) {
        return new RankImpl.Builder(ClansImpl.getInstance().getNextAvailableRankID(), rankName).build();
    }

    public static String getOwnerIdentifier() {
        return OWNER_IDENTIFIER;
    }

    public static String getRecruitIdentifier() {
        return RECRUIT_IDENTIFIER;
    }

    public static int getOwnerID() {
        return OWNER_ID;
    }

    public static int getRecruitID() {
        return RECRUIT_ID;
    }
}
