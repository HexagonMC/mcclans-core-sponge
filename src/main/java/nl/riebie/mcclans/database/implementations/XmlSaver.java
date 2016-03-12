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

package nl.riebie.mcclans.database.implementations;

import nl.riebie.mcclans.MCClans;
import nl.riebie.mcclans.clan.ClanImpl;
import nl.riebie.mcclans.clan.RankImpl;
import nl.riebie.mcclans.config.Config;
import nl.riebie.mcclans.database.interfaces.DataSaver;
import nl.riebie.mcclans.player.ClanPlayerImpl;
import nl.riebie.mcclans.utils.FileUtils;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;

public class XmlSaver extends DataSaver {

    private File saveDataFolder = new File(MCClans.getPlugin().getXmlDataFolder(), "recent");
    private File tempDataFolder = new File(MCClans.getPlugin().getXmlDataFolder(), "temp");

    private XMLStreamWriter clansWriter;
    private XMLStreamWriter clanPlayersWriter;
    private XMLStreamWriter ranksWriter;
    private XMLStreamWriter alliesWriter;

    public XmlSaver() {
        saveDataFolder.mkdirs();
        tempDataFolder.mkdirs();
    }

    public void useBackupLocation() {
        String backupName = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(System.currentTimeMillis());

        File backupFolder = new File(MCClans.getPlugin().getXmlDataFolder(), "backup");
        backupFolder.mkdirs();
        saveDataFolder = getBackupLocation(backupFolder, backupName, 0);
        saveDataFolder.mkdirs();

        int amountOfBackups = backupFolder.listFiles().length;
        int maxAmountOfBackups = Config.getInteger(Config.MAXIMUM_AMOUNT_OF_BACKUPS_BEFORE_REMOVING_OLDEST);
        if (amountOfBackups > maxAmountOfBackups) {
            removeOldestBackups(backupFolder, maxAmountOfBackups);
            MCClans.getPlugin().getLogger().info("Removed " + (amountOfBackups - maxAmountOfBackups) + " old backup(s)", true);
        }
    }

    private File getBackupLocation(File backupFolder, String backupName, int iteration) {
        File backupLocation;
        if (iteration == 0) {
            backupLocation = new File(backupFolder, backupName);
        } else {
            backupLocation = new File(backupFolder, backupName + " [" + iteration + "]");
        }

        if (backupLocation.exists()) {
            return getBackupLocation(backupFolder, backupName, ++iteration);
        } else {
            return backupLocation;
        }
    }

    private void removeOldestBackups(File directory, int maxBackups) {
        File[] files = directory.listFiles();
        int amountOfBackups = files.length;
        if (amountOfBackups == 0) {
            return;
        }
        if (amountOfBackups > maxBackups) {
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File o1, File o2) {
                    return new Long(o2.lastModified()).compareTo(o1.lastModified());
                }
            });
            for (int index = maxBackups; index < amountOfBackups; index++) {
                FileUtils.removeFolder(files[index]);
            }
        }
    }

    @Override
    protected void saveClan(ClanImpl clan) throws Exception {
        clansWriter.writeStartElement("clan");

        clansWriter.writeStartElement("clanID");
        clansWriter.writeCharacters(String.valueOf(clan.getID()));
        clansWriter.writeEndElement();

        clansWriter.writeStartElement("clanTag");
        clansWriter.writeCharacters(clan.getTag());
        clansWriter.writeEndElement();

        clansWriter.writeStartElement("clanName");
        clansWriter.writeCharacters(clan.getName());
        clansWriter.writeEndElement();

        clansWriter.writeStartElement("ownerID");
        clansWriter.writeCharacters(String.valueOf(clan.getOwner().getID()));
        clansWriter.writeEndElement();

        clansWriter.writeStartElement("tagColor");
        clansWriter.writeCharacters(clan.getTagColor().getId());
        clansWriter.writeEndElement();

        clansWriter.writeStartElement("allowAllyInvites");
        clansWriter.writeCharacters(String.valueOf(clan.isAllowingAllyInvites()));
        clansWriter.writeEndElement();

        clansWriter.writeStartElement("ffProtection");
        clansWriter.writeCharacters(String.valueOf(clan.isFfProtected()));
        clansWriter.writeEndElement();

        clansWriter.writeStartElement("creationTime");
        clansWriter.writeCharacters(String.valueOf(clan.getCreationDate().getTime()));
        clansWriter.writeEndElement();

        Location<World> homeLocation = clan.getHome();

        String homeWorld = null;
        double homeX = 0;
        double homeY = 0;
        double homeZ = 0;
        float homeYaw = 0;
        float homePitch = 0;

        if (homeLocation != null) {
            homeWorld = homeLocation.getExtent().getUniqueId().toString();
            homeX = homeLocation.getX();
            homeY = homeLocation.getY();
            homeZ = homeLocation.getZ();
            // TODO SPONGE vector or something
//			homeYaw = homeLocation.getYaw();
//			homePitch = homeLocation.getPitch();
        }

        clansWriter.writeStartElement("homeWorld");
        clansWriter.writeCharacters(homeWorld);
        clansWriter.writeEndElement();

        clansWriter.writeStartElement("homeX");
        clansWriter.writeCharacters(String.valueOf(homeX));
        clansWriter.writeEndElement();

        clansWriter.writeStartElement("homeY");
        clansWriter.writeCharacters(String.valueOf(homeY));
        clansWriter.writeEndElement();

        clansWriter.writeStartElement("homeZ");
        clansWriter.writeCharacters(String.valueOf(homeZ));
        clansWriter.writeEndElement();

        clansWriter.writeStartElement("homeYaw");
        clansWriter.writeCharacters(String.valueOf(homeYaw));
        clansWriter.writeEndElement();

        clansWriter.writeStartElement("homePitch");
        clansWriter.writeCharacters(String.valueOf(homePitch));
        clansWriter.writeEndElement();

        clansWriter.writeStartElement("homeSetTimes");
        clansWriter.writeCharacters(String.valueOf(clan.getHomeSetTimes()));
        clansWriter.writeEndElement();

        clansWriter.writeStartElement("homeSetTimeStamp");
        clansWriter.writeCharacters(String.valueOf(clan.getHomeSetTimeStamp()));
        clansWriter.writeEndElement();

        clansWriter.writeEndElement();
    }

    @Override
    protected void saveClanPlayer(ClanPlayerImpl cp) throws Exception {
        clanPlayersWriter.writeStartElement("clanPlayer");

        clanPlayersWriter.writeStartElement("clanPlayerID");
        clanPlayersWriter.writeCharacters(String.valueOf(cp.getID()));
        clanPlayersWriter.writeEndElement();

        clanPlayersWriter.writeStartElement("uuidMostSigBits");
        clanPlayersWriter.writeCharacters(String.valueOf(cp.getUUID().getMostSignificantBits()));
        clanPlayersWriter.writeEndElement();

        clanPlayersWriter.writeStartElement("uuidLeastSigBits");
        clanPlayersWriter.writeCharacters(String.valueOf(cp.getUUID().getLeastSignificantBits()));
        clanPlayersWriter.writeEndElement();

        clanPlayersWriter.writeStartElement("playerName");
        clanPlayersWriter.writeCharacters(cp.getName());
        clanPlayersWriter.writeEndElement();

        int clanID = -1;
        int rankID = -1;
        if (cp.getClan() != null) {
            clanID = cp.getClan().getID();
            rankID = cp.getRank().getID();
        }

        clanPlayersWriter.writeStartElement("clanID");
        clanPlayersWriter.writeCharacters(String.valueOf(clanID));
        clanPlayersWriter.writeEndElement();

        clanPlayersWriter.writeStartElement("rankID");
        clanPlayersWriter.writeCharacters(String.valueOf(rankID));
        clanPlayersWriter.writeEndElement();

        clanPlayersWriter.writeStartElement("killsHigh");
        clanPlayersWriter.writeCharacters(String.valueOf(cp.getKillsHigh()));
        clanPlayersWriter.writeEndElement();

        clanPlayersWriter.writeStartElement("killsMedium");
        clanPlayersWriter.writeCharacters(String.valueOf(cp.getKillsMedium()));
        clanPlayersWriter.writeEndElement();

        clanPlayersWriter.writeStartElement("killsLow");
        clanPlayersWriter.writeCharacters(String.valueOf(cp.getKillsLow()));
        clanPlayersWriter.writeEndElement();

        clanPlayersWriter.writeStartElement("deathsHigh");
        clanPlayersWriter.writeCharacters(String.valueOf(cp.getDeathsHigh()));
        clanPlayersWriter.writeEndElement();

        clanPlayersWriter.writeStartElement("deathsMedium");
        clanPlayersWriter.writeCharacters(String.valueOf(cp.getDeathsMedium()));
        clanPlayersWriter.writeEndElement();

        clanPlayersWriter.writeStartElement("deathsLow");
        clanPlayersWriter.writeCharacters(String.valueOf(cp.getDeathsLow()));
        clanPlayersWriter.writeEndElement();

        clanPlayersWriter.writeStartElement("ffProtection");
        clanPlayersWriter.writeCharacters(String.valueOf(cp.isFfProtected()));
        clanPlayersWriter.writeEndElement();

        clanPlayersWriter.writeStartElement("lastOnlineTime");
        clanPlayersWriter.writeCharacters(String.valueOf(cp.getLastOnline().getTime()));
        clanPlayersWriter.writeEndElement();

        clanPlayersWriter.writeEndElement();
    }

    @Override
    protected void saveRank(int clanID, RankImpl rank) throws Exception {
        ranksWriter.writeStartElement("rank");

        ranksWriter.writeStartElement("rankID");
        ranksWriter.writeCharacters(String.valueOf(rank.getID()));
        ranksWriter.writeEndElement();

        ranksWriter.writeStartElement("clanID");
        ranksWriter.writeCharacters(String.valueOf(clanID));
        ranksWriter.writeEndElement();

        ranksWriter.writeStartElement("rankName");
        ranksWriter.writeCharacters(String.valueOf(rank.getName()));
        ranksWriter.writeEndElement();

        ranksWriter.writeStartElement("changeable");
        ranksWriter.writeCharacters(String.valueOf(rank.isChangeable()));
        ranksWriter.writeEndElement();

        ranksWriter.writeStartElement("permissions");
        ranksWriter.writeCharacters(rank.getPermissionsAsString());
        ranksWriter.writeEndElement();

        ranksWriter.writeEndElement();
    }

    @Override
    protected void saveClanAlly(int clanID, int clanIDAlly) throws Exception {
        alliesWriter.writeStartElement("ally");

        alliesWriter.writeStartElement("clanID");
        alliesWriter.writeCharacters(String.valueOf(clanID));
        alliesWriter.writeEndElement();

        alliesWriter.writeStartElement("clanIDAlly");
        alliesWriter.writeCharacters(String.valueOf(clanIDAlly));
        alliesWriter.writeEndElement();

        alliesWriter.writeEndElement();
    }

    @Override
    protected void saveStarted() throws Exception {
        File clansFile = new File(saveDataFolder, "clans.xml");
        File clanPlayersFile = new File(saveDataFolder, "clanPlayers.xml");
        File ranksFile = new File(saveDataFolder, "ranks.xml");
        File alliesFile = new File(saveDataFolder, "allies.xml");

        File tempClansFile = new File(tempDataFolder, "clans.xml");
        File tempClanPlayersFile = new File(tempDataFolder, "clanPlayers.xml");
        File tempRanksFile = new File(tempDataFolder, "ranks.xml");
        File tempAlliesFile = new File(tempDataFolder, "allies.xml");

        FileUtils.copyFile(clansFile, tempClansFile);
        FileUtils.copyFile(clanPlayersFile, tempClanPlayersFile);
        FileUtils.copyFile(ranksFile, tempRanksFile);
        FileUtils.copyFile(alliesFile, tempAlliesFile);

        FileOutputStream fos = new FileOutputStream(clansFile);
        XMLOutputFactory xmlOutFact = XMLOutputFactory.newInstance();
        clansWriter = xmlOutFact.createXMLStreamWriter(fos, "UTF-8");

        fos = new FileOutputStream(clanPlayersFile);
        xmlOutFact = XMLOutputFactory.newInstance();
        clanPlayersWriter = xmlOutFact.createXMLStreamWriter(fos, "UTF-8");

        fos = new FileOutputStream(ranksFile);
        xmlOutFact = XMLOutputFactory.newInstance();
        ranksWriter = xmlOutFact.createXMLStreamWriter(fos, "UTF-8");

        fos = new FileOutputStream(alliesFile);
        xmlOutFact = XMLOutputFactory.newInstance();
        alliesWriter = xmlOutFact.createXMLStreamWriter(fos, "UTF-8");

        clansWriter.writeStartDocument("UTF-8", "1.0");
        clanPlayersWriter.writeStartDocument("UTF-8", "1.0");
        ranksWriter.writeStartDocument("UTF-8", "1.0");
        alliesWriter.writeStartDocument("UTF-8", "1.0");

        clansWriter.writeStartElement("clans");
        clanPlayersWriter.writeStartElement("clanPlayers");
        ranksWriter.writeStartElement("ranks");
        alliesWriter.writeStartElement("allies");
    }

    @Override
    protected void saveFinished() throws Exception {
        clansWriter.writeEndElement();
        clanPlayersWriter.writeEndElement();
        ranksWriter.writeEndElement();
        alliesWriter.writeEndElement();

        clansWriter.writeEndDocument();
        clanPlayersWriter.writeEndDocument();
        ranksWriter.writeEndDocument();
        alliesWriter.writeEndDocument();

        clansWriter.flush();
        clanPlayersWriter.flush();
        ranksWriter.flush();
        alliesWriter.flush();

        File tempClansFile = new File(tempDataFolder, "clans.xml");
        File tempClanPlayersFile = new File(tempDataFolder, "clanPlayers.xml");
        File tempRanksFile = new File(tempDataFolder, "ranks.xml");
        File tempAlliesFile = new File(tempDataFolder, "allies.xml");

        tempClansFile.delete();
        tempClanPlayersFile.delete();
        tempRanksFile.delete();
        tempAlliesFile.delete();
    }

    @Override
    protected void saveCancelled() {
        File clansFile = new File(saveDataFolder, "clans.xml");
        File clanPlayersFile = new File(saveDataFolder, "clanPlayers.xml");
        File ranksFile = new File(saveDataFolder, "ranks.xml");
        File alliesFile = new File(saveDataFolder, "allies.xml");

        File tempClansFile = new File(tempDataFolder, "clans.xml");
        File tempClanPlayersFile = new File(tempDataFolder, "clanPlayers.xml");
        File tempRanksFile = new File(tempDataFolder, "ranks.xml");
        File tempAlliesFile = new File(tempDataFolder, "allies.xml");

        try {
            FileUtils.copyFile(tempClansFile, clansFile);
            FileUtils.copyFile(tempClanPlayersFile, clanPlayersFile);
            FileUtils.copyFile(tempRanksFile, ranksFile);
            FileUtils.copyFile(tempAlliesFile, alliesFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}