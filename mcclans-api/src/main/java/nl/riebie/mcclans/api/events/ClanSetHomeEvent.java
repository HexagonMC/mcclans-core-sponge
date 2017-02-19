package nl.riebie.mcclans.api.events;

import nl.riebie.mcclans.api.Clan;
import nl.riebie.mcclans.api.ClanPlayer;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * An event which is fired when a clan home is set.
 * <p>
 * Created by riebie on 29/01/2017.
 */
public class ClanSetHomeEvent extends CancellableClanEvent {

    private final Clan clan;
    private final Location<World> location;

    private ClanSetHomeEvent(Cause cause, Clan clan, Location<World> location) {
        super("Clan set home cancelled by an external plugin", cause);
        this.clan = clan;
        this.location = location;
    }

    /**
     * Get the clan whose home location is being set.
     */
    public Clan getClan() {
        return clan;
    }

    /**
     * The new clan home location.
     */
    public Location<World> getLocation() {
        return location;
    }

    /**
     * A user command was used to set the clan home.
     */
    public static class User extends ClanSetHomeEvent {

        private final ClanPlayer clanPlayer;

        public User(ClanPlayer clanPlayer, Clan clan, Location<World> location) {
            super(Cause.of(NamedCause.source(clanPlayer)), clan, location);

            this.clanPlayer = clanPlayer;
        }

        /**
         * Get the player who set the clan home.
         */
        public ClanPlayer getClanPlayer() {
            return clanPlayer;
        }
    }

    /**
     * An admin command was used to set the clan home.
     */
    public static class Admin extends ClanSetHomeEvent {

        public Admin(CommandSource commandSource, Clan clan, Location<World> location) {
            super(Cause.of(NamedCause.source(commandSource)), clan, location);
        }
    }

    /**
     * An external plugin set the clan home.
     */
    public static class Plugin extends ClanSetHomeEvent {

        public Plugin(Clan clan, Location<World> location) {
            super(Cause.of(NamedCause.source(location)), clan, location);
        }
    }
}
