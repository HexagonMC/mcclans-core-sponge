package nl.riebie.mcclans.database.implementations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import nl.riebie.mcclans.MCClans;
import nl.riebie.mcclans.database.interfaces.DataLoader;
import nl.riebie.mcclans.utils.FileUtils;

public class XmlLoader extends DataLoader {

    private static final File recentDataFolder = new File(MCClans.getPlugin().getXmlDataFolder(), "recent");
    private static final File loadDataFolder = new File(MCClans.getPlugin().getXmlDataFolder(), "load");

    public static boolean recentFilesPresent() {
        File clansFile = new File(recentDataFolder, "clans.xml");
        File clanPlayersFile = new File(recentDataFolder, "clanPlayers.xml");
        File ranksFile = new File(recentDataFolder, "ranks.xml");
        File alliesFile = new File(recentDataFolder, "allies.xml");

        if (clansFile.exists() && clanPlayersFile.exists() && ranksFile.exists() && alliesFile.exists()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean loadFilesPresent() {
        loadDataFolder.mkdirs();
        File loadClansFile = new File(loadDataFolder, "clans.xml");
        File loadClanPlayersFile = new File(loadDataFolder, "clanPlayers.xml");
        File loadRanksFile = new File(loadDataFolder, "ranks.xml");
        File loadAlliesFile = new File(loadDataFolder, "allies.xml");

        if (loadClansFile.exists() && loadClanPlayersFile.exists() && loadRanksFile.exists() && loadAlliesFile.exists()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected boolean initialize() {
        recentDataFolder.mkdirs();
        loadDataFolder.mkdirs();

        File loadClansFile = new File(loadDataFolder, "clans.xml");
        File loadClanPlayersFile = new File(loadDataFolder, "clanPlayers.xml");
        File loadRanksFile = new File(loadDataFolder, "ranks.xml");
        File loadAlliesFile = new File(loadDataFolder, "allies.xml");

        File clansFile = new File(recentDataFolder, "clans.xml");
        File clanPlayersFile = new File(recentDataFolder, "clanPlayers.xml");
        File ranksFile = new File(recentDataFolder, "ranks.xml");
        File alliesFile = new File(recentDataFolder, "allies.xml");

        if (loadFilesPresent()) {
            MCClans.getPlugin().getLogger().info("Found data in 'load' folder. Loading this data instead", true);
            try {
                FileUtils.moveFile(loadClansFile, clansFile);
                FileUtils.moveFile(loadClanPlayersFile, clanPlayersFile);
                FileUtils.moveFile(loadRanksFile, ranksFile);
                FileUtils.moveFile(loadAlliesFile, alliesFile);

                loadClansFile.delete();
                loadClanPlayersFile.delete();
                loadRanksFile.delete();
                loadAlliesFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return recentFilesPresent();
    }

    @Override
    protected void loadClans() {
        int clanID = -1;
        String clanTag = null;
        String clanName = null;
        int ownerID = -1;
        String tagColor = null;
        boolean allowAllyInvites = true;
        boolean ffProtection = true;
        long creationTime = 0;

        String homeWorld = null;
        double homeX = 0;
        double homeY = 0;
        double homeZ = 0;
        float homeYaw = 0;
        float homePitch = 0;

        int homeSetTimes = 0;
        long homeLastSetTimeStamp = -1;

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(new File(recentDataFolder, "clans.xml")));

            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("clanID")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        clanID = Integer.parseInt(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("clanTag")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        clanTag = xmlEvent.asCharacters().getData();
                    } else if (startElement.getName().getLocalPart().equals("clanName")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        clanName = xmlEvent.asCharacters().getData();
                    } else if (startElement.getName().getLocalPart().equals("ownerID")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        ownerID = Integer.parseInt(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("tagColor")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        tagColor = xmlEvent.asCharacters().getData();
                    } else if (startElement.getName().getLocalPart().equals("allowAllyInvites")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        allowAllyInvites = Boolean.parseBoolean(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("ffProtection")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        ffProtection = Boolean.parseBoolean(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("creationTime")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        creationTime = Long.parseLong(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("homeWorld")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        if (xmlEvent.isCharacters()) {
                            homeWorld = xmlEvent.asCharacters().getData();
                        } else {
                            homeWorld = null;
                        }
                    } else if (startElement.getName().getLocalPart().equals("homeX")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        homeX = Double.parseDouble(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("homeY")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        homeY = Double.parseDouble(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("homeZ")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        homeZ = Double.parseDouble(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("homeYaw")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        homeYaw = Float.valueOf(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("homePitch")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        homePitch = Float.valueOf(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("homeSetTimes")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        homeSetTimes = Integer.parseInt(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("homeSetTimeStamp")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        homeLastSetTimeStamp = Long.parseLong(xmlEvent.asCharacters().getData());
                    }
                }

                if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals("clan")) {
                        super.loadedClan(clanID, clanTag, clanName, ownerID, tagColor, allowAllyInvites, ffProtection, creationTime, homeWorld,
                                homeX, homeY, homeZ, homeYaw, homePitch, homeSetTimes, homeLastSetTimeStamp);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void loadRanks() {
        int rankID = -1;
        int clanID = -1;
        String rankName = null;
        String permissions = null;
        boolean changeable = true;

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(new File(recentDataFolder, "ranks.xml")));

            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("rankID")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        rankID = Integer.parseInt(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("clanID")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        clanID = Integer.parseInt(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("rankName")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        rankName = xmlEvent.asCharacters().getData();
                    } else if (startElement.getName().getLocalPart().equals("permissions")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        if (xmlEvent.isCharacters()) {
                            permissions = xmlEvent.asCharacters().getData();
                        } else {
                            permissions = new String();
                        }

                    } else if (startElement.getName().getLocalPart().equals("changeable")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        changeable = Boolean.parseBoolean(xmlEvent.asCharacters().getData());
                    }
                }

                if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals("rank")) {
                        super.loadedRank(rankID, clanID, rankName, permissions, changeable);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void loadClanPlayers() {
        String playerName = null;
        int clanPlayerID = -1;
        long uuidMostSigBits = -1;
        long uuidLeastSigBits = -1;
        int clanID = -1;
        int rankID = -1;
        int killsHigh = 0;
        int killsMedium = 0;
        int killsLow = 0;
        int deathsHigh = 0;
        int deathsMedium = 0;
        int deathsLow = 0;
        boolean ffProtection = true;
        long lastOnlineTime = 0;

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(new File(recentDataFolder, "clanPlayers.xml")));

            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("clanPlayerID")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        clanPlayerID = Integer.parseInt(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("uuidMostSigBits")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        uuidMostSigBits = Long.parseLong(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("uuidLeastSigBits")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        uuidLeastSigBits = Long.parseLong(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("playerName")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        playerName = xmlEvent.asCharacters().getData();
                    } else if (startElement.getName().getLocalPart().equals("clanID")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        clanID = Integer.parseInt(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("rankID")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        rankID = Integer.parseInt(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("killsHigh")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        killsHigh = Integer.parseInt(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("killsMedium")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        killsMedium = Integer.parseInt(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("killsLow")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        killsLow = Integer.parseInt(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("deathsHigh")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        deathsHigh = Integer.parseInt(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("deathsMedium")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        deathsMedium = Integer.parseInt(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("deathsLow")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        deathsLow = Integer.parseInt(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("ffProtection")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        ffProtection = Boolean.parseBoolean(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("lastOnlineTime")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        lastOnlineTime = Long.parseLong(xmlEvent.asCharacters().getData());
                    }
                }

                if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals("clanPlayer")) {
                        super.loadedClanPlayer(clanPlayerID, uuidMostSigBits, uuidLeastSigBits, playerName, clanID, rankID, killsHigh, killsMedium,
                                killsLow, deathsHigh, deathsMedium, deathsLow, ffProtection, lastOnlineTime);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void loadAllies() {
        int clanID = -1;
        int clanIDAlly = -1;

        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        try {
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(new File(recentDataFolder, "allies.xml")));

            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("clanID")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        clanID = Integer.parseInt(xmlEvent.asCharacters().getData());
                    } else if (startElement.getName().getLocalPart().equals("clanIDAlly")) {
                        xmlEvent = xmlEventReader.nextEvent();
                        clanIDAlly = Integer.parseInt(xmlEvent.asCharacters().getData());
                    }
                }

                if (xmlEvent.isEndElement()) {
                    EndElement endElement = xmlEvent.asEndElement();
                    if (endElement.getName().getLocalPart().equals("ally")) {
                        super.loadedAlly(clanID, clanIDAlly);
                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }
}
