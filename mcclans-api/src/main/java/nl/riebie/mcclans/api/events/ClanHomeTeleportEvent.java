package nl.riebie.mcclans.api.events;

import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.api.ClanPlayer;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;

/**
 * Event fired when a Player attempts to start a clan home teleport. Is fired before the countdown or checking the user's economy balance.
 * <p>
 * Created by Kippers on 05/02/2017.
 */
public class ClanHomeTeleportEvent extends CancellableClanEvent {

    private Clan clan;
    private ClanPlayer clanPlayer;

    public ClanHomeTeleportEvent(Clan clan, ClanPlayer clanPlayer) {
        super("Clan home teleport cancelled by an external plugin", Cause.of(NamedCause.owner(clanPlayer)));
        this.clan = clan;
        this.clanPlayer = clanPlayer;
    }

    /**
     * Get the clan whose home is being teleported to.
     */
    public Clan getClan() {
        return clan;
    }

    /**
     * Get the clan player who is starting a clan home teleport.
     */
    public ClanPlayer getClanPlayer() {
        return clanPlayer;
    }

    /**
     * The teleporting player used a user command to start the teleport.
     */
    public static class User extends ClanHomeTeleportEvent {
        public User(Clan clan, ClanPlayer clanPlayer) {
            super(clan, clanPlayer);
        }
    }

    /**
     * The teleporting player used an admin command to start the teleport.
     */
    public static class Admin extends ClanHomeTeleportEvent {
        public Admin(Clan clan, ClanPlayer clanPlayer) {
            super(clan, clanPlayer);
        }
    }
}
