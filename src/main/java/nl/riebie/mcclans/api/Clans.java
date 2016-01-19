package nl.riebie.mcclans.api;

import nl.riebie.mcclans.api.exceptions.NotDefaultImplementationException;

import java.util.List;
import java.util.UUID;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public interface Clans {
    /**
     * Create a Clan
     *
     * @param tag
     *            the tag of the new clan
     * @param name
     *            the name of the new clan
     * @param owner
     *            the owner of the new clan
     * @return the new Clan
     * @throws NotDefaultImplementationException
     *             if owner is not created by MCClans
     */
    public Clan createClan(String tag, String name, ClanPlayer owner);

    /**
     * Disband a clan
     *
     * @param tag
     *            the tag of the clan to be disbanded
     */
    public void disbandClan(String tag);

    /**
     * Get a Clan with the given tag
     *
     * @param tag
     *            the tag of a clan
     * @return a Clan with the given tag
     */
    public Clan getClan(String tag);

    /**
     * Create a ClanPlayer
     *
     * @param uuid
     *            the uuid of a player
     * @param name
     *            the name of a player
     * @return a new ClanPlayer
     */
    public ClanPlayer createClanPlayer(UUID uuid, String name);

    /**
     * Remove a ClanPlayer
     *
     * @param clanPlayer
     *            the clanPlayer to be removed
     * @throws NotDefaultImplementationException
     *             if clanPlayer is not created by MCClans
     */
    public void removeClanPlayer(ClanPlayer clanPlayer);

    /**
     * Get a ClanPlayer with the given tag
     *
     * @param name
     *            the name of a player
     * @return a ClanPlayer with the given UUID
     */
    public ClanPlayer getClanPlayer(UUID uuid);

    /**
     * Get a List containing all the registered Clans
     *
     * @return a List containing all the Clans
     */
    public List<Clan> getClans();

    /**
     * Check if the given clanTag is free
     *
     * @param tag
     *            a clanTag
     * @return true if the given clanTag is free
     */
    public boolean tagIsFree(String tag);

}
